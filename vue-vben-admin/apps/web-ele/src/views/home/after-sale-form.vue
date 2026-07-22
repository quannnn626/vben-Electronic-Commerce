<script lang="ts" setup>
import type { UploadRequestOptions } from 'element-plus';

import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElCheckbox,
  ElEmpty,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElOption,
  ElRadio,
  ElRadioGroup,
  ElSelect,
  ElTag,
  ElUpload,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface ProductFile {
  fileName: string;
  filePath: string;
  fileType: string;
  id: number;
}

interface OrderItemDto {
  id: number;
  skuId: number;
  productName: string;
  productImage: string;
  price: number;
  quantity: number;
  totalPrice: number;
}

interface OrderDetail {
  id: number;
  orderNo: string;
  totalAmount: number;
  payAmount: number;
  status: number;
  items: OrderItemDto[];
}

const route = useRoute();
const router = useRouter();
const orderId = Number(route.query.orderId ?? 0);

const loading = ref(false);
const submitting = ref(false);
const order = ref<OrderDetail | null>(null);
const tempFileIds = ref<number[]>([]);
// 已申请数量
const claimedMap = ref<Record<number, number>>({});

// 已选商品 key=orderItemId, value=售后数量
const selectedItems = reactive<Record<number, number>>({});

// 每个订单项还可申请的数量
function availableQty(item: OrderItemDto): number {
  return Math.max(0, item.quantity - (claimedMap.value[item.id] ?? 0));
}

const selectedDetails = computed(() =>
  Object.entries(selectedItems)
    .filter(([, qty]) => qty > 0)
    .map(([itemId, qty]) => ({
      item: order.value?.items?.find((it) => it.id === Number(itemId))!,
      quantity: qty,
    }))
    .filter((d) => d.item),
);

const selectedCount = computed(() => selectedDetails.value.length);

const form = reactive({
  description: '',
  received: null as boolean | null,
  reason: null as number | null,
  type: null as number | null,
});

// 是否支持换货类型
const canExchange = computed(() => order.value?.status === 2 || order.value?.status === 3);

const refundTypes = [
  { label: '仅退款', value: 0 },
  { label: '退货退款', value: 1 },
  { label: '换货', value: 2 },
];

const allReasonOptions = [
  { label: '质量问题', value: 0 },
  { label: '商品与描述不符', value: 1 },
  { label: '发错货', value: 2 },
  { label: '商品破损', value: 3 },
  { label: '尺码/规格不合适', value: 4 },
  { label: '不想要了', value: 5 },
  { label: '其他', value: 6 },
];

// 未收到货、收到货
const availableReasons = computed(() => {
  if (form.received === null) return allReasonOptions;
  if (!form.received) {
    return allReasonOptions.filter((r) => r.value === 5 || r.value === 6);
  }
  return allReasonOptions;
});

const availableTypes = computed(() => {
  if (form.received === null) return refundTypes;
  if (!form.received) {
    return refundTypes.filter((t) => t.value === 0);
  }
  const types = [...refundTypes];
  if (!canExchange.value) types.splice(2, 1); // 仅已发货的订单才能换货
  return types;
});

function normalizeFileUrl(rawPath?: string) {
  if (!rawPath) return '';
  if (/^https?:\/\//.test(rawPath)) return rawPath;
  if (rawPath.startsWith('/api/')) return rawPath;
  if (rawPath.startsWith('/upload/')) return `/api${rawPath}`;
  if (rawPath.startsWith('/')) return `/api${rawPath}`;
  return `/api/${rawPath}`;
}

// 加载订单详情
async function loadOrderDetail() {
  loading.value = true;
  try {
    const [orderData, afterSales] = await Promise.all([
      requestClient.get<OrderDetail>('/mall/order/detail', { params: { orderId } }),
      requestClient.get<any[]>('/mall/afterSale/list').catch(() => [] as any[]),
    ]);
    order.value = orderData;
    // 计算每个订单项已申请数量
    const map: Record<number, number> = {};
    for (const as of (Array.isArray(afterSales) ? afterSales : [])) {
      if (as.orderId === orderId && [0, 1, 6, 3].includes(as.status)) {
        map[as.orderItemId] = (map[as.orderItemId] ?? 0) + (as.quantity ?? 0);
      }
    }
    claimedMap.value = map;
  } catch (e: any) {
    ElMessage.error(e?.message ?? '加载订单失败');
  } finally {
    loading.value = false;
  }
}

// 切换商品选中状态
function toggleItem(itemId: number) {
  if (selectedItems[itemId] !== undefined) {
    delete selectedItems[itemId];
  } else {
    selectedItems[itemId] = 1;
  }
}

function isItemSelected(itemId: number): boolean {
  return selectedItems[itemId] !== undefined;
}

// 校验表单
function validateForm(): boolean {
  if (selectedCount.value === 0) {
    ElMessage.warning('请选择售后商品');
    return false;
  }
  if (form.received === null) {
    ElMessage.warning('请选择是否收到货');
    return false;
  }
  if (form.type === null) {
    ElMessage.warning('请选择售后类型');
    return false;
  }
  if (form.reason === null) {
    ElMessage.warning('请选择售后原因');
    return false;
  }
  for (const d of selectedDetails.value) {
    if (!d.quantity || d.quantity <= 0) {
      ElMessage.warning(`"${d.item.productName}" 售后数量必须大于0`);
      return false;
    }
    const max = availableQty(d.item);
    if (d.quantity > max) {
      ElMessage.warning(`"${d.item.productName}" 最多可申请 ${max} 件`);
      return false;
    }
  }
  return true;
}

// 上传凭证文件
async function uploadFile(option: UploadRequestOptions) {
  const { file, onSuccess, onError } = option;
  const formData = new FormData();
  formData.append('files', file);
  try {
    const data = await requestClient.post<ProductFile[]>('/mall/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    const saved = Array.isArray(data) ? data[0] : null;
    if (!saved) throw new Error('上传失败');
    onSuccess?.(saved as any);
  } catch (e) {
    onError?.(e as any);
  }
}

function handleUploadSuccess(response: ProductFile) {
  tempFileIds.value.push(response.id);
}

function handleUploadRemove(file: any) {
  const id = Number(file.uid);
  if (Number.isNaN(id)) return;
  tempFileIds.value = tempFileIds.value.filter((fid) => fid !== id);
}

// 提交售后申请
async function submitForm() {
  if (!validateForm()) return;
  submitting.value = true;
  try {
    await requestClient.post('/mall/afterSale/create', {
      orderId,
      items: selectedDetails.value.map((d) => ({
        orderItemId: d.item.id,
        quantity: d.quantity,
      })),
      type: form.type,
      reason: form.reason,
      description: form.description.trim(),
      fileIds: tempFileIds.value,
    });
    ElMessage.success('售后申请已提交');
    router.back();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '提交失败');
  } finally {
    submitting.value = false;
  }
}

function goBack() {
  router.back();
}

onMounted(() => {
  if (!orderId) {
    ElMessage.error('缺少订单信息');
    router.back();
    return;
  }
  loadOrderDetail();
});
</script>

<template>
  <Page description="选择售后商品并提交申请" title="申请售后">
    <ElCard shadow="never">
      <ElButton @click="goBack">返回</ElButton>
    </ElCard>

    <ElCard v-if="order" class="mt-4" shadow="never">
      <div class="order-info">
        <span class="text-gray-500">订单编号：</span>
        <span class="font-medium">{{ order.orderNo }}</span>
      </div>
    </ElCard>

    <ElCard v-loading="loading" class="mt-4" shadow="never">
      <template #header>
        <div class="flex items-center justify-between">
          <span class="font-medium">选择售后商品（可多选）</span>
          <ElTag v-if="selectedCount > 0" type="primary" size="small">
            已选 {{ selectedCount }} 件商品
          </ElTag>
        </div>
      </template>

      <ElEmpty
        v-if="!loading && (!order?.items || order.items.length === 0)"
        description="暂无商品"
      />

      <div v-if="order?.items?.length" class="item-list">
        <div
          v-for="item in order.items"
          :key="item.id"
          class="item-card"
          :class="{ 'item-card--selected': isItemSelected(item.id) }"
        >
          <ElCheckbox
            :model-value="isItemSelected(item.id)"
            :disabled="availableQty(item) <= 0"
            class="item-checkbox"
            @change="toggleItem(item.id)"
          />

          <div class="item-image" @click="toggleItem(item.id)">
            <img
              v-if="item.productImage"
              :src="normalizeFileUrl(item.productImage)"
              alt=""
            />
            <span v-else class="item-image-placeholder">图</span>
          </div>

          <div class="item-info" @click="availableQty(item) > 0 ? toggleItem(item.id) : undefined">
            <div class="item-name">{{ item.productName }}</div>
            <div class="item-meta">
              <span class="text-gray-500">购买：{{ item.quantity }} 件</span>
              <span v-if="claimedMap[item.id]" class="text-orange-500">已申请：{{ claimedMap[item.id] }} 件</span>
              <span v-if="availableQty(item) <= 0" class="text-red-500">不可申请</span>
              <span class="text-gray-500">单价：¥{{ item.price?.toFixed(2) }}</span>
            </div>
          </div>

          <div v-if="isItemSelected(item.id)" class="item-qty" @click.stop>
            <span class="text-sm text-gray-500 mr-2">售后数量</span>
            <ElInputNumber
              v-model="selectedItems[item.id]"
              :min="1"
              :max="availableQty(item)"
              :step="1"
              size="small"
              style="width: 90px"
            />
            <span class="text-xs text-gray-400 ml-1">
              最多 {{ availableQty(item) }} 件
            </span>
          </div>
        </div>
      </div>
    </ElCard>

    <ElCard v-if="order" class="mt-4" shadow="never">
      <template #header>
        <div class="flex items-center gap-2 flex-wrap">
          <span class="font-medium">售后信息</span>
          <ElTag
            v-for="d in selectedDetails"
            :key="d.item.id"
            type="info"
            size="small"
          >
            {{ d.item.productName }} × {{ d.quantity }}
          </ElTag>
        </div>
      </template>

      <ElForm label-width="110px">
        <ElFormItem label="是否收到货" required>
          <ElRadioGroup v-model="form.received" @change="form.type = null; form.reason = null">
            <ElRadio :value="true">已收到货</ElRadio>
            <ElRadio :value="false">未收到货</ElRadio>
          </ElRadioGroup>
        </ElFormItem>

        <ElFormItem label="售后类型" required>
          <ElSelect v-model="form.type" placeholder="请选择售后类型">
            <ElOption
              v-for="t in availableTypes"
              :key="t.value"
              :label="t.label"
              :value="t.value"
            />
          </ElSelect>
          <span v-if="form.type === 2" class="text-gray-500 text-sm ml-2">
            换货无需退款，审核通过后将为您补发商品
          </span>
          <span v-else-if="form.type !== null" class="text-gray-500 text-sm ml-2">
            退款金额将按退款商品金额自动计算
          </span>
        </ElFormItem>

        <ElFormItem label="售后原因" required>
          <ElSelect v-model="form.reason" placeholder="请选择售后原因">
            <ElOption
              v-for="r in availableReasons"
              :key="r.value"
              :label="r.label"
              :value="r.value"
            />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="问题描述">
          <ElInput
            v-model="form.description"
            :rows="4"
            maxlength="500"
            placeholder="请描述您遇到的问题（选填）"
            show-word-limit
            type="textarea"
          />
        </ElFormItem>

        <ElFormItem label="凭证上传">
          <ElUpload
            :http-request="uploadFile"
            :on-remove="handleUploadRemove"
            :on-success="handleUploadSuccess"
            multiple
          >
            <ElButton type="primary">上传凭证</ElButton>
            <template #tip>
              <div class="text-gray-500">可上传订单截图、商品照片等凭证（选填）</div>
            </template>
          </ElUpload>
        </ElFormItem>

        <ElFormItem>
          <ElButton :loading="submitting" type="danger" @click="submitForm">
            提交申请
          </ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </Page>
</template>

<style scoped>
.order-info {
  font-size: 15px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border: 2px solid var(--el-border-color-lighter);
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.item-card:hover {
  border-color: var(--el-color-primary-light-3);
}

.item-card--selected {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.item-checkbox {
  flex-shrink: 0;
}

.item-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  background: var(--el-fill-color-lighter);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  cursor: pointer;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-image-placeholder {
  font-size: 14px;
  color: var(--el-text-color-placeholder);
}

.item-info {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.item-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  margin-top: 4px;
}

.item-qty {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}
</style>
