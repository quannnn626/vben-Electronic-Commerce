<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDatePicker,
  ElDialog,
  ElEmpty,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElSelect,
  ElOption,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';
import { requestClient } from '#/api/request';

interface OrderItemDto {
  id: number;
  skuId: number;
  productName: string;
  productImage: string;
  skuSpecName: string;
  price: number;
  quantity: number;
  totalPrice: number;
}

interface OrderRecord {
  id: number;
  orderNo: string;
  username: string;
  totalAmount: number;
  payAmount: number;
  status: number;
  receiverName: string;
  receiverPhone: string;
  receiverAddress: string;
  createTime: string;
  payTime: string | null;
  deliveryTime: string | null;
  finishTime: string | null;
  cancelTime: string | null;
  logisticsCompany: string;
  trackingNo: string;
  items: OrderItemDto[];
}

const loading = ref(false);
const list = ref<OrderRecord[]>([]);
const detailVisible = ref(false);
const currentOrder = ref<OrderRecord | null>(null);
const deliveryVisible = ref(false);
const deliveryLoading = ref(false);
const deliveryOrder = ref<OrderRecord | null>(null);
const itemDeliveries = reactive<Record<number, { logisticsCompany: string; trackingNo: string }>>({});

function initItemDeliveries(items: OrderItemDto[]) {
  for (const key of Object.keys(itemDeliveries)) delete itemDeliveries[key];
  for (const item of items) {
    itemDeliveries[item.id] = { logisticsCompany: '', trackingNo: '' };
  }
}

const queryForm = reactive({
  orderNo: '',
  username: '',
  status: null as number | null,
  createTime: null as string | null,
  endCreateTime: null as string | null,
});

const statusOptions = [
  { label: '全部', value: null },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '发货中', value: 5 },
  { label: '已发货', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 },
];

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待支付', type: 'warning' },
  1: { label: '已支付', type: '' },
  2: { label: '已发货', type: 'success' },
  3: { label: '已完成', type: 'success' },
  4: { label: '已取消', type: 'info' },
  5: { label: '发货中', type: 'warning' },
};

function normalizeImage(url?: string) {
  if (!url) return '';
  if (/^https?:\/\//.test(url)) return url;
  if (url.startsWith('/')) return `/api${url}`;
  return `/api/${url}`;
}

function formatDate(date: string | null | undefined) {
  if (!date) return null;
  return `${date} 00:00:00`;
}

async function loadList() {
  loading.value = true;
  try {
    const payload: Record<string, unknown> = {};
    if (queryForm.orderNo) payload.orderNo = queryForm.orderNo;
    if (queryForm.username) payload.username = queryForm.username;
    if (queryForm.status !== null) payload.status = queryForm.status;
    if (queryForm.createTime) payload.createTime = formatDate(queryForm.createTime);
    if (queryForm.endCreateTime) payload.endCreateTime = formatDate(queryForm.endCreateTime);
    list.value = await requestClient.post<OrderRecord[]>('/mall/order/admin/list', payload);
  } catch (e: any) {
    ElMessage.error(e?.message ?? '加载订单列表失败');
  } finally {
    loading.value = false;
  }
}

function handleReset() {
  queryForm.orderNo = '';
  queryForm.username = '';
  queryForm.status = null;
  queryForm.createTime = null;
  queryForm.endCreateTime = null;
  loadList();
}

function showDetail(row: OrderRecord) {
  currentOrder.value = row;
  detailVisible.value = true;
}

const deliveredIds = ref<number[]>([]);

async function showDelivery(row: OrderRecord) {
  deliveryOrder.value = row;
  // 查已发货的商品ID
  try {
    const ids = await requestClient.get<number[]>('/mall/order/delivery/items', { params: { orderId: row.id } });
    deliveredIds.value = Array.isArray(ids) ? ids : [];
  } catch { deliveredIds.value = []; }
  initItemDeliveries(row.items);
  deliveryVisible.value = true;
}

function isDelivered(itemId: number) {
  return deliveredIds.value.includes(itemId);
}

async function submitItemDelivery(item: OrderItemDto) {
  const d = itemDeliveries[item.id];
  if (!d.logisticsCompany) { ElMessage.warning('请填写物流公司'); return; }
  if (!d.trackingNo) { ElMessage.warning('请填写物流单号'); return; }
  deliveryLoading.value = true;
  try {
    await requestClient.post('/mall/order/delivery/create', {
      orderNo: deliveryOrder.value!.orderNo,
      orderItemId: item.id,
      logisticsCompany: d.logisticsCompany,
      trackingNo: d.trackingNo,
    });
    ElMessage.success(`"${item.productName}" 发货成功`);
    loadList();
    deliveryVisible.value = false;
  } catch (e: any) {
    ElMessage.error(e?.message ?? '发货失败');
  } finally {
    deliveryLoading.value = false;
  }
}

onMounted(() => {
  loadList();
});
</script>

<template>
  <Page description="查看和管理所有用户订单" title="订单管理">
    <ElCard shadow="never" class="search-card">
      <ElForm inline>
        <ElFormItem label="订单编号">
          <ElInput v-model="queryForm.orderNo" clearable />
        </ElFormItem>
        <ElFormItem label="用户昵称">
          <ElInput v-model="queryForm.username" clearable />
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSelect v-model="queryForm.status" placeholder="全部" clearable style="width:120px">
            <ElOption
              v-for="opt in statusOptions"
              :key="String(opt.value)"
              :label="opt.label"
              :value="opt.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="起始时间">
          <ElDatePicker
            v-model="queryForm.createTime"
            type="date"
            placeholder="选择起始日期"
            value-format="YYYY-MM-DD"
          />
        </ElFormItem>
        <ElFormItem label="结束时间">
          <ElDatePicker
            v-model="queryForm.endCreateTime"
            type="date"
            placeholder="选择结束日期"
            value-format="YYYY-MM-DD"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="loadList">查询</ElButton>
          <ElButton @click="handleReset">重置</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard shadow="never" class="mt-4">
      <ElTable :data="list" row-key="id" v-loading="loading" stripe>
        <ElTableColumn label="订单编号" prop="orderNo" min-width="180" />
        <ElTableColumn label="用户名" prop="username" width="120" />
        <ElTableColumn label="总金额" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="实付" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.payAmount?.toFixed(2) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="状态" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="statusMap[row.status]?.type">
              {{ statusMap[row.status]?.label ?? row.status }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="创建时间" prop="createTime" width="170" />
        <ElTableColumn label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <ElButton size="small" text type="primary" @click="showDetail(row)">
              详情
            </ElButton>
            <ElButton
              v-if="row.status === 1 || row.status === 5"
              size="small"
              text
              type="success"
              @click="showDelivery(row)"
            >
              发货
            </ElButton>
          </template>
        </ElTableColumn>

        <template #empty>
          <ElEmpty description="暂无订单" />
        </template>
      </ElTable>
    </ElCard>

    <ElDialog v-model="detailVisible" title="订单详情" width="700px" destroy-on-close>
      <template v-if="currentOrder">
        <div class="detail-section">
          <div class="detail-row">
            <span class="detail-label">订单编号</span>
            <span>{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">用户名</span>
            <span>{{ currentOrder.username }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">状态</span>
            <ElTag :type="statusMap[currentOrder.status]?.type" size="small">
              {{ statusMap[currentOrder.status]?.label }}
            </ElTag>
          </div>
          <div class="detail-row">
            <span class="detail-label">收货人</span>
            <span>{{ currentOrder.receiverName }} {{ currentOrder.receiverPhone }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">收货地址</span>
            <span>{{ currentOrder.receiverAddress }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">创建时间</span>
            <span>{{ currentOrder.createTime }}</span>
          </div>
          <div v-if="currentOrder.payTime" class="detail-row">
            <span class="detail-label">支付时间</span>
            <span>{{ currentOrder.payTime }}</span>
          </div>
          <div v-if="currentOrder.deliveryTime" class="detail-row">
            <span class="detail-label">发货时间</span>
            <span>{{ currentOrder.deliveryTime }}</span>
          </div>
          <div v-if="currentOrder.logisticsCompany" class="detail-row">
            <span class="detail-label">物流公司</span>
            <span>{{ currentOrder.logisticsCompany }}</span>
          </div>
          <div v-if="currentOrder.trackingNo" class="detail-row">
            <span class="detail-label">物流单号</span>
            <span>{{ currentOrder.trackingNo }}</span>
          </div>
          <div v-if="currentOrder.finishTime" class="detail-row">
            <span class="detail-label">完成时间</span>
            <span>{{ currentOrder.finishTime }}</span>
          </div>
        </div>
        <div class="detail-section">
          <h4>商品明细</h4>
          <div v-for="item in currentOrder.items" :key="item.id" class="goods-item">
            <img
              v-if="item.productImage"
              :src="normalizeImage(item.productImage)"
              class="goods-img"
              alt=""
            />
            <span v-else class="goods-img-placeholder">图</span>
            <div class="goods-info">
              <div class="goods-name">{{ item.productName }}</div>
              <div v-if="item.skuSpecName" class="goods-spec">{{ item.skuSpecName }}</div>
            </div>
            <div class="goods-right">
              <span>¥{{ item.price.toFixed(2) }} × {{ item.quantity }}</span>
              <span class="goods-subtotal">¥{{ item.totalPrice.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </template>
    </ElDialog>

    <ElDialog v-model="deliveryVisible" title="按商品发货" width="650px" destroy-on-close>
      <div v-if="deliveryOrder">
        <div class="delivery-order-info">
          订单编号：<strong>{{ deliveryOrder.orderNo }}</strong>
        </div>
        <div v-for="item in deliveryOrder.items" :key="item.id" v-show="!isDelivered(item.id)" class="delivery-item">
          <div class="delivery-item-header">
            <span class="font-medium">{{ item.productName }}</span>
            <span v-if="item.skuSpecName" class="text-gray-500 text-sm">{{ item.skuSpecName }}</span>
            <span class="text-gray-500 text-sm">×{{ item.quantity }}</span>
          </div>
          <div class="delivery-item-form">
            <ElInput v-model="itemDeliveries[item.id].logisticsCompany" placeholder="物流公司" size="small" style="width:140px" />
            <ElInput v-model="itemDeliveries[item.id].trackingNo" placeholder="物流单号" size="small" style="width:200px" />
            <ElButton size="small" type="primary" :loading="deliveryLoading" @click="submitItemDelivery(item)">发货</ElButton>
          </div>
        </div>
      </div>
    </ElDialog>
  </Page>
</template>

<style scoped>
.mt-4 {
  margin-top: 16px;
}

.search-card {
  margin-bottom: 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
}

.detail-row {
  align-items: center;
  display: flex;
  gap: 12px;
  padding: 6px 0;
}

.detail-label {
  color: var(--el-text-color-secondary);
  min-width: 80px;
}

.goods-item {
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-lighter);
  display: flex;
  gap: 12px;
  padding: 10px 0;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-img {
  border-radius: 4px;
  height: 48px;
  object-fit: cover;
  width: 48px;
}

.goods-img-placeholder {
  align-items: center;
  background: var(--el-fill-color-lighter);
  border-radius: 4px;
  color: var(--el-text-color-placeholder);
  display: flex;
  flex-shrink: 0;
  font-size: 12px;
  height: 48px;
  justify-content: center;
  width: 48px;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-spec {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  margin-top: 2px;
}

.goods-right {
  flex-shrink: 0;
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.goods-subtotal {
  font-weight: 600;
  color: var(--el-color-danger);
  min-width: 80px;
  text-align: right;
}

.delivery-order-info {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.delivery-item {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
}

.delivery-item-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}

.delivery-item-form {
  display: flex;
  gap: 8px;
  align-items: center;
}

.font-medium { font-weight: 500; }

.text-gray-500 { color: var(--el-text-color-secondary); }

.text-sm { font-size: 13px; }
</style>
