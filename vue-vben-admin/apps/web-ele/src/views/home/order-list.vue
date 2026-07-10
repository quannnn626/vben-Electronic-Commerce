<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import { ElButton, ElCard, ElEmpty, ElInput, ElMessage, ElMessageBox, ElTabPane, ElTabs, ElTag } from 'element-plus';

import { requestClient } from '#/api/request';

interface BackendOrderItem {
  id: number;
  skuId: number;
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
  status: number; // 对应 OrderStatusEnum: 0待支付 1已支付 2已发货 3已完成 4已取消
  createTime: string;
  payTime: string | null;
  deliveryTime: string | null;
  finishTime: string | null;
  logisticsCompany: string;
  trackingNo: string;
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
  logisticsCompany: string;
  trackingNo: string;
  orderItemId?: number;
}

// 订单状态码常量
const ORDER_STATUS = {
  WAIT_PAY: 0,   // 待支付
  PAID: 1,       // 已支付
  SHIPPED: 2,    // 已发货
  COMPLETED: 3,  // 已完成
  CANCELLED: 4,  // 已取消
} as const;

const backendStatusMap: Record<number, OrderItem['status']> = {
  [ORDER_STATUS.WAIT_PAY]: 'pending',
  [ORDER_STATUS.PAID]: 'paid',
  [ORDER_STATUS.SHIPPED]: 'shipped',
  [ORDER_STATUS.COMPLETED]: 'completed',
  [ORDER_STATUS.CANCELLED]: 'cancelled',
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
const activeTab = ref('all');

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
    logisticsCompany: backend.logisticsCompany || '',
    trackingNo: backend.trackingNo || '',
    orderItemId: firstItem?.id,
  };
}

interface AfterSaleItem {
  id: string;
  afterSaleNo: string;
  orderId: string;
  orderItemId: string;
  type: number;
  status: number;
  reason: number;
  reasonDesc: string;
  refundAmount: number;
  applyTime: string;
  items: { productName: string; productImage: string; quantity: number; price: number }[];
}

const orders = ref<OrderItem[]>([]);
const afterSales = ref<AfterSaleItem[]>([]);

// 活跃售后状态：申请中0、审核中1、已通过2、退款中4（与后端createAfterSale校验逻辑一致）
const ACTIVE_AFTER_SALE_STATUSES = new Set([0, 1, 2, 4]);

// 已存在活跃售后申请的 orderItemId 集合
const activeAfterSaleOrderItemIds = computed(() => {
  const ids = new Set<string>();
  for (const item of afterSales.value) {
    if (item.orderItemId && ACTIVE_AFTER_SALE_STATUSES.has(item.status)) {
      ids.add(String(item.orderItemId));
    }
  }
  return ids;
});

async function loadAfterSales() {
  loading.value = true;
  try {
    const data = await requestClient.get<AfterSaleItem[]>('/mall/afterSale/list');
    afterSales.value = Array.isArray(data) ? data : [];
  } finally {
    loading.value = false;
  }
}

const afterSaleStatusMap: Record<number, { label: string; type: string }> = {
  0: { label: '申请中', type: 'warning' },
  1: { label: '审核中', type: 'warning' },
  2: { label: '已通过', type: 'success' },
  3: { label: '已拒绝', type: 'info' },
  4: { label: '退款中', type: 'warning' },
  5: { label: '已完成', type: 'success' },
  6: { label: '已取消', type: 'info' },
};

const afterSaleTypeMap: Record<number, string> = {
  0: '仅退款',
  1: '退货退款',
  2: '换货',
};

async function loadOrders(status?: number) {
  loading.value = true;
  try {
    const params: Record<string, any> = {};
    if (status !== undefined) {
      params.status = status;
    }
    const data = await requestClient.get<BackendOrder[]>('/mall/order/list', { params });
    orders.value = (Array.isArray(data) ? data : []).map(transformOrder);
  } finally {
    loading.value = false;
  }
}

watch(activeTab, (tab) => {
  keyword.value = '';
  if (tab === 'after-sale') {
    loadAfterSales();
  } else if (tab === 'all') {
    loadOrders();
  } else {
    loadOrders(Number(tab));
  }
});

const tableData = computed(() => {
  const key = keyword.value.trim().toLowerCase();
  if (!key) return orders.value;
  return orders.value.filter(
    (item) =>
      item.orderNo.toLowerCase().includes(key) ||
      item.goodsName.toLowerCase().includes(key),
  );
});

const router = useRouter();
const actionLoading = ref<Record<string, boolean>>({});

function goToPayment(row: OrderItem) {
  if (row.status !== 'pending') return;
  router.push({ name: 'OrderPayment', query: { orderId: row.id } });
}

function handlePay(row: OrderItem) {
  goToPayment(row);
}

async function handleCancel(row: OrderItem) {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '返回',
      type: 'warning',
    });
  } catch {
    return;
  }
  actionLoading.value[row.id] = true;
  try {
    await requestClient.post('/mall/order/cancel', null, {
      params: { orderId: Number(row.id) },
    });
    ElMessage.success('订单已取消');
    await loadOrders(activeTab.value === 'all' ? undefined : Number(activeTab.value));
  } catch (e: any) {
    ElMessage.error(e?.message ?? '取消失败');
  } finally {
    actionLoading.value[row.id] = false;
  }
}

async function handleConfirm(row: OrderItem) {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '确认收货', {
      confirmButtonText: '确定',
      cancelButtonText: '返回',
      type: 'success',
    });
  } catch {
    return;
  }
  actionLoading.value[row.id] = true;
  try {
    await requestClient.post('/mall/order/finish', null, {
      params: { orderId: Number(row.id) },
    });
    ElMessage.success('确认收货成功');
    await loadOrders(activeTab.value === 'all' ? undefined : Number(activeTab.value));
  } catch (e: any) {
    ElMessage.error(e?.message ?? '确认失败');
  } finally {
    actionLoading.value[row.id] = false;
  }
}

function handleAfterSale(row: OrderItem) {
  if (!row.orderItemId) {
    ElMessage.warning('无法获取商品信息');
    return;
  }
  router.push({
    path: '/order/after-sale/create',
    query: { orderId: row.id, orderItemId: row.orderItemId },
  });
}

function handleViewDetail(row: OrderItem) {
  const backendId = Number(row.id);
  router.push({ path: '/product/detail', params: { id: backendId } });
}

onMounted(() => {
  loadOrders();
  loadAfterSales();
});
</script>

<template>
  <Page description="管理您的所有订单" title="我的订单">
    <ElTabs v-model="activeTab" class="order-tabs">
      <ElTabPane label="全部订单" name="all" />
      <ElTabPane label="待付款" name="0" />
      <ElTabPane label="待发货" name="1" />
      <ElTabPane label="待收货" name="2" />
      <ElTabPane label="退换/售后" name="after-sale" />
    </ElTabs>

    <ElCard shadow="never">
      <ElInput
        v-model="keyword"
        clearable
        placeholder="搜索订单编号或商品名称"
        style="width: 300px"
      />
    </ElCard>

    <!-- 退换/售后列表 -->
    <div v-if="activeTab === 'after-sale'" v-loading="loading" class="mt-4 space-y-4">
      <template v-if="afterSales.length > 0">
        <ElCard
          v-for="item in afterSales"
          :key="item.id"
          shadow="never"
          class="order-card cursor-pointer"
          @click="router.push({ path: '/order/after-sale/detail', query: { id: item.id } })"
        >
          <div class="order-header">
            <div class="flex items-center gap-4">
              <span class="order-no-label">售后单号</span>
              <span class="order-no-value">{{ item.afterSaleNo }}</span>
              <span class="order-time">{{ item.applyTime }}</span>
            </div>
            <ElTag :type="afterSaleStatusMap[item.status]?.type ?? 'info'" size="small">
              {{ afterSaleStatusMap[item.status]?.label ?? '未知' }}
            </ElTag>
          </div>
          <div class="order-body">
            <div class="goods-info">
              <div class="goods-name">{{ item.items?.[0]?.productName ?? '未知商品' }}</div>
              <div class="goods-qty">{{ afterSaleTypeMap[item.type] ?? '售后' }} · {{ item.reasonDesc ?? '' }}</div>
            </div>
          </div>
          <div class="order-footer">
            <div class="total-info">
              <span class="total-label">退款金额：</span>
              <span class="total-price">
                {{ item.refundAmount ? `¥${item.refundAmount.toFixed(2)}` : '待定' }}
              </span>
            </div>
          </div>
        </ElCard>
      </template>
      <ElEmpty v-else description="暂无退换/售后订单" />
    </div>

    <!-- 订单列表 -->
    <div v-else v-loading="loading" class="mt-4 space-y-4">
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
            <div class="goods-image-box" @click="goToPayment(item)">
              <img
                v-if="item.goodsImage"
                :src="item.goodsImage"
                class="goods-img"
                alt=""
              />
              <span v-else class="goods-image-placeholder">图</span>
            </div>
            <div class="goods-info">
              <div class="goods-name" @click="goToPayment(item)">{{ item.goodsName }}</div>
              <div class="goods-qty">共 {{ item.goodsCount }} 件</div>
            </div>
            <div class="goods-amount">
              <div class="amount-label">商品金额</div>
              <div class="amount-value">¥{{ item.amount.toFixed(2) }}</div>
            </div>
          </div>

          <!-- 物流信息 -->
          <div
            v-if="(item.status === 'shipped' || item.status === 'completed') && item.logisticsCompany"
            class="order-delivery"
          >
            <span class="delivery-label">物流</span>
            <span>{{ item.logisticsCompany }}</span>
            <span class="delivery-no">{{ item.trackingNo }}</span>
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
                v-if="['paid', 'shipped', 'completed'].includes(item.status) && item.orderItemId && !activeAfterSaleOrderItemIds.has(String(item.orderItemId))"
                type="warning"
                @click="handleAfterSale(item)"
              >
                申请售后
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
.order-tabs {
  margin-bottom: 4px;
}

.order-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

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
  cursor: pointer;
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
  cursor: pointer;
}

.goods-name:hover {
  color: var(--el-color-primary);
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

.order-delivery {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.delivery-label {
  color: var(--el-text-color-secondary);
  margin-right: 4px;
}

.delivery-no {
  color: var(--el-color-primary);
  margin-left: auto;
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
