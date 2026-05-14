<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import { ElButton, ElCard, ElEmpty, ElInput, ElTag } from 'element-plus';

import { requestClient } from '#/api/request';

interface BackendOrderItem {
  id: number;
  productId: number;
  productName: string;
  productImage: string;
  price: number;
  quantity: number;
  totalPrice: number;
}

interface BackendOrder {
  id: number;
  orderNo: string;
  totalAmount: number;
  payAmount: number;
  status: number; // 0待支付 1已支付 2已发货 3已完成 4已取消
  createTime: string;
  payTime: string | null;
  deliveryTime: string | null;
  finishTime: string | null;
  items: BackendOrderItem[];
}

interface OrderItem {
  id: string;
  orderNo: string;
  goodsName: string;
  goodsCount: number;
  goodsImage: string;
  amount: number;
  actualAmount: number;
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled';
  createTime: string;
}

const backendStatusMap: Record<number, OrderItem['status']> = {
  0: 'pending',
  1: 'paid',
  2: 'shipped',
  3: 'completed',
  4: 'cancelled',
};

const statusMap: Record<OrderItem['status'], { label: string; type: string }> = {
  cancelled: { label: '已取消', type: 'info' },
  completed: { label: '已完成', type: 'success' },
  paid: { label: '待发货', type: 'warning' },
  pending: { label: '待付款', type: 'danger' },
  shipped: { label: '已发货', type: '' },
};

const keyword = ref('');
const loading = ref(false);

function normalizeFileUrl(rawPath?: string) {
  if (!rawPath) return '';
  if (/^https?:\/\//.test(rawPath)) return rawPath;
  if (rawPath.startsWith('/api/')) return rawPath;
  if (rawPath.startsWith('/upload/')) return `/api${rawPath}`;
  if (rawPath.startsWith('upload/')) return `/api/${rawPath}`;
  if (rawPath.startsWith('/')) return `/api${rawPath}`;
  return `/api/${rawPath}`;
}

function transformOrder(backend: BackendOrder): OrderItem {
  const firstItem = backend.items?.[0];
  const totalCount =
    backend.items?.reduce((sum, item) => sum + item.quantity, 0) ?? 0;
  return {
    id: String(backend.id),
    orderNo: backend.orderNo,
    goodsName: firstItem?.productName ?? '未知商品',
    goodsCount: totalCount,
    goodsImage: normalizeFileUrl(firstItem?.productImage),
    amount: backend.totalAmount,
    actualAmount: backend.payAmount,
    status: backendStatusMap[backend.status] ?? 'pending',
    createTime: backend.createTime,
  };
}

const orders = ref<OrderItem[]>([]);

async function loadOrders() {
  loading.value = true;
  try {
    const data = await requestClient.get<BackendOrder[]>('/mall/order/list');
    orders.value = (Array.isArray(data) ? data : []).map(transformOrder);
  } finally {
    loading.value = false;
  }
}

const tableData = computed(() => {
  const key = keyword.value.trim().toLowerCase();
  if (!key) return orders.value;
  return orders.value.filter(
    (item) =>
      item.orderNo.toLowerCase().includes(key) ||
      item.goodsName.toLowerCase().includes(key),
  );
});

function handlePay(row: OrderItem) {
  console.log('去支付', row.orderNo);
}

function handleCancel(row: OrderItem) {
  console.log('取消订单', row.orderNo);
}

function handleConfirm(row: OrderItem) {
  console.log('确认收货', row.orderNo);
}

function handleViewDetail(row: OrderItem) {
  console.log('查看详情', row.orderNo);
}

onMounted(() => {
  loadOrders();
});
</script>

<template>
  <Page description="管理您的所有订单" title="我的订单">
    <ElCard shadow="never">
      <ElInput
        v-model="keyword"
        clearable
        placeholder="搜索订单编号或商品名称"
        style="width: 300px"
      />
    </ElCard>

    <div v-loading="loading" class="mt-4 space-y-4">
      <template v-if="tableData.length > 0">
        <ElCard
          v-for="item in tableData"
          :key="item.id"
          shadow="never"
          class="order-card"
        >
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="flex items-center gap-4">
              <span class="order-no-label">订单号</span>
              <span class="order-no-value">{{ item.orderNo }}</span>
              <span class="order-time">{{ item.createTime }}</span>
            </div>
            <ElTag :type="statusMap[item.status].type" size="small">
              {{ statusMap[item.status].label }}
            </ElTag>
          </div>

          <!-- 订单商品 -->
          <div class="order-body">
            <div class="goods-image-box">
              <img
                v-if="item.goodsImage"
                :src="item.goodsImage"
                class="goods-img"
                alt=""
              />
              <span v-else class="goods-image-placeholder">图</span>
            </div>
            <div class="goods-info">
              <div class="goods-name">{{ item.goodsName }}</div>
              <div class="goods-qty">共 {{ item.goodsCount }} 件</div>
            </div>
            <div class="goods-amount">
              <div class="amount-label">商品金额</div>
              <div class="amount-value">¥{{ item.amount.toFixed(2) }}</div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="total-info">
              <span class="total-label">实付款：</span>
              <span class="total-price">
                {{
                  item.actualAmount > 0
                    ? `¥${item.actualAmount.toFixed(2)}`
                    : '-'
                }}
              </span>
            </div>
            <div class="order-actions">
              <ElButton
                v-if="item.status === 'pending'"
                type="danger"
                @click="handlePay(item)"
              >
                去支付
              </ElButton>
              <ElButton
                v-if="item.status === 'pending'"
                @click="handleCancel(item)"
              >
                取消
              </ElButton>
              <ElButton
                v-if="item.status === 'shipped'"
                type="primary"
                @click="handleConfirm(item)"
              >
                确认收货
              </ElButton>
              <ElButton
                v-if="
                  ['completed', 'paid', 'cancelled'].includes(item.status)
                "
                @click="handleViewDetail(item)"
              >
                查看详情
              </ElButton>
            </div>
          </div>
        </ElCard>
      </template>
      <ElEmpty v-else description="暂无订单" />
    </div>
  </Page>
</template>

<style scoped>
.order-card {
  border: 1px solid var(--el-border-color-lighter);
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.order-no-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.order-no-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-regular);
}

.order-time {
  font-size: 13px;
  color: var(--el-text-color-placeholder);
}

.order-body {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.goods-image-box {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  background: var(--el-fill-color-lighter);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-image-placeholder {
  font-size: 14px;
  color: var(--el-text-color-placeholder);
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-qty {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.goods-amount {
  text-align: right;
  flex-shrink: 0;
}

.amount-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.amount-value {
  font-size: 15px;
  font-weight: 500;
  color: var(--el-text-color-regular);
  margin-top: 2px;
}

.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
}

.total-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.total-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--el-color-danger);
}

.order-actions {
  display: flex;
  gap: 8px;
}
</style>
