<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDivider,
  ElEmpty,
  ElMessage,
  ElRadio,
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
  totalPrice: number;
}

interface OrderDetail {
  id: number;
  orderNo: string;
  totalAmount: number;
  payAmount: number;
  status: number;
  createTime: string;
  payTime: string | null;
  deliveryTime: string | null;
  finishTime: string | null;
  cancelTime: string | null;
  items: OrderItem[];
}

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const submitLoading = ref(false);
const order = ref<OrderDetail | null>(null);

const payType = ref('alipay');
const paymentOptions = [
  { label: '支付宝', value: 'alipay' },
  { label: '微信支付', value: 'wechat' },
];

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
    const data = await requestClient.get<OrderDetail>('/mall/order/detail', {
      params: { orderId: Number(orderId) },
    });
    order.value = data;
  } catch (e: any) {
    ElMessage.error(e?.message ?? '加载订单详情失败');
  } finally {
    loading.value = false;
  }
}

async function handleConfirmPay() {
  if (!order.value) return;
  submitLoading.value = true;
  try {
    const payment = await requestClient.post('/mall/payment/create', {
      orderId: order.value.id,
      payType: payType.value,
    });
    router.push({
      name: 'SimulatedPay',
      query: {
        paymentNo: payment.paymentNo,
        orderId: String(order.value.id),
        amount: String(order.value.payAmount),
      },
    });
  } catch (e: any) {
    ElMessage.error(e?.message ?? '创建支付单失败');
  } finally {
    submitLoading.value = false;
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
  <Page description="确认支付信息并完成付款" title="订单支付">
    <div v-if="!route.query.orderId" class="flex items-center justify-center py-20">
      <ElEmpty description="缺少订单参数" />
    </div>

    <div v-else v-loading="loading" class="payment-layout">
      <div class="payment-main">
        <!-- 商品明细 -->
        <ElCard shadow="never">
          <template #header>
            <span class="section-title">
              <span class="step-num">1</span> 商品明细
            </span>
          </template>
          <div v-if="order" class="goods-list">
            <div v-for="item in order.items" :key="item.id" class="goods-item">
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
              </div>
              <div class="goods-price-col">
                <div class="goods-unit-price">¥{{ item.price.toFixed(2) }}</div>
                <div class="goods-qty">×{{ item.quantity }}</div>
              </div>
              <div class="goods-subtotal">¥{{ item.totalPrice.toFixed(2) }}</div>
            </div>
          </div>
          <ElEmpty v-else description="暂无商品信息" />
        </ElCard>

        <!-- 支付方式 -->
        <ElCard class="mt-4" shadow="never">
          <template #header>
            <span class="section-title">
              <span class="step-num">2</span> 支付方式
            </span>
          </template>
          <div class="payment-options">
            <div
              v-for="opt in paymentOptions"
              :key="opt.value"
              class="payment-item"
              :class="{ active: payType === opt.value }"
              @click="payType = opt.value"
            >
              <ElRadio v-model="payType" :value="opt.value" class="payment-radio" />
              <span class="payment-label">{{ opt.label }}</span>
            </div>
          </div>
        </ElCard>
      </div>

      <aside class="payment-sidebar">
        <ElCard shadow="never" class="summary-card">
          <h3 class="summary-title">订单摘要</h3>

          <div class="summary-row">
            <span class="summary-label">订单编号</span>
            <span class="summary-value summary-no">{{ order?.orderNo }}</span>
          </div>

          <div class="summary-row">
            <span class="summary-label">商品件数</span>
            <span class="summary-value">
              {{ order?.items.reduce((s, i) => s + i.quantity, 0) ?? 0 }} 件
            </span>
          </div>

          <div class="summary-row">
            <span class="summary-label">商品金额</span>
            <span class="summary-value">¥{{ order?.totalAmount?.toFixed(2) }}</span>
          </div>

          <ElDivider />

          <div class="summary-row summary-total-row">
            <span class="summary-label">应付金额</span>
            <span class="summary-total-price">¥{{ order?.payAmount?.toFixed(2) }}</span>
          </div>

          <ElDivider />

          <div class="summary-actions">
            <ElButton class="action-btn" @click="goBack">返回</ElButton>
            <ElButton
              :loading="submitLoading"
              class="action-btn action-submit"
              type="danger"
              @click="handleConfirmPay"
            >
              确认支付
            </ElButton>
          </div>
        </ElCard>
      </aside>
    </div>
  </Page>
</template>

<style scoped>
.payment-layout {
  display: grid;
  gap: 20px;
  grid-template-columns: minmax(0, 1fr) 340px;
  align-items: start;
}

.mt-4 {
  margin-top: 16px;
}

.step-num {
  align-items: center;
  background: var(--el-color-primary);
  border-radius: 50%;
  color: #fff;
  display: inline-flex;
  font-size: 13px;
  font-weight: 600;
  height: 22px;
  justify-content: center;
  margin-right: 8px;
  width: 22px;
}

.section-title {
  align-items: center;
  display: flex;
  font-size: 16px;
  font-weight: 600;
}

/* 商品列表 */
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

/* 支付方式 */
.payment-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.payment-item {
  align-items: center;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  gap: 8px;
  padding: 12px 20px;
  transition: all 0.2s;
}

.payment-item:hover {
  border-color: var(--el-border-color-hover);
}

.payment-item.active {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.payment-label {
  font-size: 15px;
  font-weight: 500;
}

/* 右侧摘要 */
.summary-card {
  position: sticky;
  top: 12px;
}

.summary-title {
  font-size: 17px;
  font-weight: 600;
  margin: 0 0 16px;
}

.summary-row {
  align-items: center;
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
}

.summary-label {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.summary-value {
  font-size: 14px;
  font-weight: 500;
}

.summary-no {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  word-break: break-all;
  text-align: right;
  max-width: 180px;
}

.summary-total-row {
  padding: 8px 0;
}

.summary-total-price {
  color: var(--el-color-danger);
  font-size: 22px;
  font-weight: 700;
}

.summary-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.action-btn {
  flex: 1;
}

.action-submit {
  flex: 2;
}

@media (max-width: 960px) {
  .payment-layout {
    grid-template-columns: 1fr;
  }

  .summary-card {
    position: static;
  }
}
</style>
