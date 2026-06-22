<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import { ElButton, ElCard, ElEmpty, ElMessage } from 'element-plus';

import { requestClient } from '#/api/request';

const route = useRoute();
const router = useRouter();

const paymentNo = ref('');
const orderId = ref('');
const amount = ref('0.00');
const loading = ref(false);

function initFromQuery() {
  paymentNo.value = (route.query.paymentNo as string) ?? '';
  orderId.value = (route.query.orderId as string) ?? '';
  amount.value = (route.query.amount as string) ?? '0.00';
}

async function handleConfirmPay() {
  loading.value = true;
  try {
    await requestClient.post('/mall/payment/paymentCallback', {
      paymentNo: paymentNo.value,
      tradeNo: `SIM${Date.now()}`,
      amount: Number(amount.value),
    });
    ElMessage.success('支付成功');
    router.push({ name: 'myOrderList' });
  } catch (e: any) {
    ElMessage.error(e?.message ?? '支付失败');
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.back();
}

onMounted(() => {
  initFromQuery();
});
</script>

<template>
  <Page description="模拟第三方支付收银台" title="第三方支付">
    <div v-if="!paymentNo" class="flex items-center justify-center py-20">
      <ElEmpty description="缺少支付参数" />
    </div>

    <div v-else class="pay-wrapper">
      <ElCard shadow="never" class="pay-card">
        <div class="pay-icon">💳</div>
        <div class="pay-title">模拟支付收银台</div>
        <div class="pay-info">
          <div class="pay-row">
            <span class="pay-label">支付单号</span>
            <span class="pay-value">{{ paymentNo }}</span>
          </div>
          <div class="pay-row">
            <span class="pay-label">订单编号</span>
            <span class="pay-value">{{ orderId }}</span>
          </div>
          <div class="pay-row pay-total">
            <span class="pay-label">应付金额</span>
            <span class="pay-price">¥{{ Number(amount).toFixed(2) }}</span>
          </div>
        </div>
        <div class="pay-actions">
          <ElButton size="large" @click="goBack">取消</ElButton>
          <ElButton size="large" type="danger" :loading="loading" @click="handleConfirmPay">
            确认支付
          </ElButton>
        </div>
      </ElCard>
    </div>
  </Page>
</template>

<style scoped>
.pay-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}

.pay-card {
  width: 480px;
  text-align: center;
}

.pay-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.pay-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
}

.pay-info {
  text-align: left;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 24px;
}

.pay-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.pay-total {
  border-top: 1px solid var(--el-border-color-lighter);
  margin-top: 8px;
  padding-top: 12px;
}

.pay-label {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.pay-value {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.pay-price {
  font-size: 22px;
  font-weight: 700;
  color: var(--el-color-danger);
}

.pay-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}
</style>
