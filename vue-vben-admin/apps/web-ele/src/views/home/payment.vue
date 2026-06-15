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

const route = useRoute();
const router = useRouter();

const paymentNo = ref('');
const orderId = ref('');
const orderNo = ref('');
const amount = ref('0.00');
const loading = ref(false);

const payType = ref('alipay');
const paymentOptions = [
  { label: '支付宝', value: 'alipay' },
  { label: '微信支付', value: 'wechat' },
];

function initFromQuery() {
  paymentNo.value = (route.query.paymentNo as string) ?? '';
  orderId.value = (route.query.orderId as string) ?? '';
  orderNo.value = (route.query.orderNo as string) ?? '';
  amount.value = (route.query.amount as string) ?? '0.00';
}

function handleConfirmPay() {
  ElMessage.info('支付接口开发中');
}

function goBack() {
  router.back();
}

onMounted(() => {
  initFromQuery();
});
</script>

<template>
  <Page description="确认支付信息并完成付款" title="订单支付">
    <div v-if="!paymentNo" class="flex items-center justify-center py-20">
      <ElEmpty description="缺少支付参数，请从订单列表进入" />
    </div>

    <div v-else class="payment-layout">
      <div class="payment-main">
        <ElCard shadow="never">
          <template #header>
            <span class="section-title">
              <span class="step-num">1</span> 支付信息
            </span>
          </template>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">支付单号</span>
              <span class="info-value">{{ paymentNo }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">订单编号</span>
              <span class="info-value">{{ orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">应付金额</span>
              <span class="info-value price">¥{{ Number(amount).toFixed(2) }}</span>
            </div>
          </div>
        </ElCard>

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
          <h3 class="summary-title">支付摘要</h3>

          <div class="summary-row">
            <span class="summary-label">支付单号</span>
            <span class="summary-value summary-no">{{ paymentNo }}</span>
          </div>

          <div class="summary-row">
            <span class="summary-label">订单编号</span>
            <span class="summary-value summary-no">{{ orderNo }}</span>
          </div>

          <ElDivider />

          <div class="summary-row summary-total-row">
            <span class="summary-label">应付金额</span>
            <span class="summary-total-price">¥{{ Number(amount).toFixed(2) }}</span>
          </div>

          <ElDivider />

          <div class="summary-method">
            <span class="remark-label">支付方式：{{ paymentOptions.find(o => o.value === payType)?.label }}</span>
          </div>

          <div class="summary-actions">
            <ElButton class="action-btn" @click="goBack">返回</ElButton>
            <ElButton
              :loading="loading"
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

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  align-items: center;
  display: flex;
  gap: 12px;
}

.info-label {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  min-width: 80px;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.info-value.price {
  color: var(--el-color-danger);
  font-size: 20px;
  font-weight: 700;
}

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

.summary-method {
  margin-bottom: 8px;
}

.remark-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-regular);
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

/* 响应式 */
@media (max-width: 960px) {
  .payment-layout {
    grid-template-columns: 1fr;
  }

  .summary-card {
    position: static;
  }
}
</style>
