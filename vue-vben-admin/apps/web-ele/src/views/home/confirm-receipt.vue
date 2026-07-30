<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElCheckbox,
  ElEmpty,
  ElMessage,
  ElMessageBox,
  ElTag,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface OrderItem {
  id: number;
  skuId: number;
  productName: string;
  productImage: string;
  skuSpecName: string;
  price: number;
  quantity: number;
  itemStatus: number;
  totalPrice: number;
  logisticsCompany: string;
  trackingNo: string;
}

interface OrderDetail {
  id: number;
  orderNo: string;
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
  items: OrderItem[];
}

const itemStatusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待发货', type: 'info' },
  1: { label: '已发货', type: '' },
  2: { label: '运输中', type: 'warning' },
  3: { label: '已收货', type: 'success' },
  4: { label: '已完成', type: 'success' },
  5: { label: '售后中', type: 'danger' },
};

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const confirmLoading = ref(false);
const order = ref<OrderDetail | null>(null);

// 是否指定了单个商品
const targetItemId = computed(() => {
  const id = route.query.orderItemId as string;
  return id ? Number(id) : null;
});

const isSingleItem = computed(() => targetItemId.value !== null);

// 可确认收货的商品（已发货或运输中），如果指定了 orderItemId 则只显示该商品
const confirmableItems = computed(() => {
  const items = order.value?.items.filter((i) => i.itemStatus === 1 || i.itemStatus === 2) ?? [];
  if (targetItemId.value) {
    return items.filter((i) => i.id === targetItemId.value);
  }
  return items;
});

const selectedIds = ref<Set<number>>(new Set());

const allSelected = computed(() => {
  const ids = confirmableItems.value.map((i) => i.id);
  return ids.length > 0 && ids.every((id) => selectedIds.value.has(id));
});

const isIndeterminate = computed(() => {
  const ids = confirmableItems.value.map((i) => i.id);
  const count = ids.filter((id) => selectedIds.value.has(id)).length;
  return count > 0 && count < ids.length;
});

function handleSelectAll() {
  if (allSelected.value) {
    selectedIds.value.clear();
  } else {
    confirmableItems.value.forEach((i) => selectedIds.value.add(i.id));
  }
}

function handleSelect(id: number) {
  if (selectedIds.value.has(id)) {
    selectedIds.value.delete(id);
  } else {
    selectedIds.value.add(id);
  }
}

function normalizeImage(url?: string) {
  if (!url) return '';
  if (/^https?:\/\//.test(url)) return url;
  if (url.startsWith('/')) return `/api${url}`;
  return `/api/${url}`;
}

async function loadOrderDetail() {
  const orderId = route.query.orderId as string;
  if (!orderId) return;

  loading.value = true;
  try {
    order.value = await requestClient.get<OrderDetail>('/mall/order/detail', {
      params: { orderId: Number(orderId) },
    });
    // 单个商品模式：自动选中
    if (targetItemId.value && confirmableItems.value.length === 1) {
      selectedIds.value.add(confirmableItems.value[0].id);
    }
  } catch (e: any) {
    ElMessage.error(e?.message ?? '加载订单详情失败');
  } finally {
    loading.value = false;
  }
}

async function handleBatchConfirm() {
  const ids = [...selectedIds.value];
  if (ids.length === 0) return;
  try {
    await ElMessageBox.confirm(
      `确认已收到所选 ${ids.length} 件商品吗？`,
      '确认收货',
      { confirmButtonText: '确定', cancelButtonText: '返回', type: 'success' },
    );
  } catch {
    return;
  }
  confirmLoading.value = true;
  try {
    // 逐个确认
    for (const itemId of ids) {
      await requestClient.post('/mall/order/item/finish', null, {
        params: { orderItemId: itemId },
      });
    }
    ElMessage.success(`已确认收货 ${ids.length} 件商品`);
    selectedIds.value.clear();
    await loadOrderDetail();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '确认失败');
  } finally {
    confirmLoading.value = false;
  }
}

function goBack() {
  router.back();
}

onMounted(() => {
  loadOrderDetail();
});
</script>

<template>
  <Page description="确认已收到商品" title="确认收货">
    <div v-if="!route.query.orderId" class="flex items-center justify-center py-20">
      <ElEmpty description="缺少订单参数" />
    </div>

    <div v-else v-loading="loading" class="receipt-layout">
      <!-- 订单信息 -->
      <ElCard v-if="order" shadow="never" class="mb-4">
        <div class="order-info">
          <div class="info-row">
            <span class="info-label">订单编号</span>
            <span class="info-value">{{ order.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">收货人</span>
            <span class="info-value">{{ order.receiverName }} {{ order.receiverPhone }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">收货地址</span>
            <span class="info-value">{{ order.receiverAddress }}</span>
          </div>
        </div>
      </ElCard>

      <!-- 可确认收货的商品 -->
      <ElCard shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <template v-if="isSingleItem">确认收货</template>
              <template v-else>可确认收货的商品（{{ confirmableItems.length }} 件）</template>
            </span>
            <div class="card-actions">
              <ElCheckbox
                v-if="confirmableItems.length > 0"
                :model-value="allSelected"
                :indeterminate="isIndeterminate"
                @change="handleSelectAll"
              >
                全选
              </ElCheckbox>
              <ElButton
                v-if="selectedIds.size > 0"
                type="primary"
                :loading="confirmLoading"
                @click="handleBatchConfirm"
              >
                确认收货（{{ selectedIds.size }}）
              </ElButton>
            </div>
          </div>
        </template>

        <div v-if="confirmableItems.length > 0" class="goods-list">
          <div v-for="item in confirmableItems" :key="item.id" class="goods-item">
            <ElCheckbox
              :model-value="selectedIds.has(item.id)"
              class="goods-checkbox"
              @change="handleSelect(item.id)"
            />
            <div class="goods-image-box">
              <img
                v-if="item.productImage"
                :src="normalizeImage(item.productImage)"
                class="goods-img"
                alt=""
              />
              <span v-else class="goods-image-placeholder">图</span>
            </div>
            <div class="goods-info">
              <div class="goods-name">{{ item.productName }}</div>
              <div v-if="item.skuSpecName" class="goods-spec">{{ item.skuSpecName }}</div>
              <ElTag
                :type="itemStatusMap[item.itemStatus]?.type ?? 'info'"
                size="small"
                class="mt-1"
              >
                {{ itemStatusMap[item.itemStatus]?.label ?? '-' }}
              </ElTag>
              <div
                v-if="(item.itemStatus === 1 || item.itemStatus === 2) && (item.logisticsCompany || item.trackingNo)"
                class="goods-delivery"
              >
                物流：{{ item.logisticsCompany }}{{ item.logisticsCompany && item.trackingNo ? ' · ' : '' }}{{ item.trackingNo }}
              </div>
            </div>
            <div class="goods-price-col">
              <div class="goods-unit-price">¥{{ item.price.toFixed(2) }}</div>
              <div class="goods-qty">×{{ item.quantity }}</div>
            </div>
            <div class="goods-subtotal">¥{{ item.totalPrice.toFixed(2) }}</div>
          </div>
        </div>
        <ElEmpty v-else description="暂无待收货商品，所有商品已确认收货" />
      </ElCard>

      <!-- 已收货/其他状态商品（多商品模式才显示） -->
      <ElCard v-if="order && !isSingleItem" shadow="never" class="mt-4">
        <template #header>
          <span class="card-title">其他商品</span>
        </template>
        <div v-if="order.items.filter((i) => i.itemStatus !== 1 && i.itemStatus !== 2).length > 0" class="goods-list">
          <div
            v-for="item in order.items.filter((i) => i.itemStatus !== 1 && i.itemStatus !== 2)"
            :key="item.id"
            class="goods-item"
          >
            <div class="goods-image-box">
              <img
                v-if="item.productImage"
                :src="normalizeImage(item.productImage)"
                class="goods-img"
                alt=""
              />
              <span v-else class="goods-image-placeholder">图</span>
            </div>
            <div class="goods-info">
              <div class="goods-name">{{ item.productName }}</div>
              <div v-if="item.skuSpecName" class="goods-spec">{{ item.skuSpecName }}</div>
              <ElTag
                :type="itemStatusMap[item.itemStatus]?.type ?? 'info'"
                size="small"
                class="mt-1"
              >
                {{ itemStatusMap[item.itemStatus]?.label ?? '-' }}
              </ElTag>
              <div
                v-if="(item.itemStatus === 1 || item.itemStatus === 2) && (item.logisticsCompany || item.trackingNo)"
                class="goods-delivery"
              >
                物流：{{ item.logisticsCompany }}{{ item.logisticsCompany && item.trackingNo ? ' · ' : '' }}{{ item.trackingNo }}
              </div>
            </div>
            <div class="goods-price-col">
              <div class="goods-unit-price">¥{{ item.price.toFixed(2) }}</div>
              <div class="goods-qty">×{{ item.quantity }}</div>
            </div>
            <div class="goods-subtotal">¥{{ item.totalPrice.toFixed(2) }}</div>
          </div>
        </div>
        <ElEmpty v-else description="暂无其他商品" />
      </ElCard>

      <div class="mt-4">
        <ElButton @click="goBack">返回</ElButton>
      </div>
    </div>
  </Page>
</template>

<style scoped>
.receipt-layout {
  max-width: 900px;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-1 {
  margin-top: 4px;
}

.mt-4 {
  margin-top: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  gap: 12px;
}

.info-label {
  color: var(--el-text-color-secondary);
  min-width: 80px;
}

.info-value {
  font-weight: 500;
}

.goods-list {
  display: flex;
  flex-direction: column;
}

.goods-item {
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-lighter);
  display: flex;
  gap: 16px;
  padding: 16px 0;
}

.goods-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.goods-item:first-child {
  padding-top: 0;
}

.goods-checkbox {
  flex-shrink: 0;
}

.goods-image-box {
  flex-shrink: 0;
  align-items: center;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  display: flex;
  height: 80px;
  justify-content: center;
  overflow: hidden;
  width: 80px;
}

.goods-img {
  height: 100%;
  object-fit: cover;
  width: 100%;
}

.goods-image-placeholder {
  color: var(--el-text-color-placeholder);
  font-size: 14px;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-spec {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  margin-top: 4px;
}

.goods-delivery {
  font-size: 12px;
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  border-radius: 4px;
  padding: 2px 8px;
  margin-top: 4px;
  display: inline-block;
}

.goods-price-col {
  flex-shrink: 0;
  text-align: right;
  min-width: 80px;
}

.goods-unit-price {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.goods-qty {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  margin-top: 2px;
}

.goods-subtotal {
  color: var(--el-text-color-primary);
  flex-shrink: 0;
  font-size: 16px;
  font-weight: 600;
  min-width: 90px;
  text-align: right;
}
</style>
