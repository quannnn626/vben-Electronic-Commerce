<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElImage,
  ElTag,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface OrderItemDto {
  id: number;
  skuId: number;
  productName: string;
  productImage: string;
  price: number;
  quantity: number;
  totalPrice: number;
}

interface AfterSaleDetail {
  id: string;
  afterSaleNo: string;
  type: number;
  status: number;
  reason: string;
  description: string;
  refundAmount: number;
  applyTime: string;
  auditTime: string;
  finishTime: string;
  orderNo: string;
  payAmount: number;
  receiverName: string;
  receiverPhone: string;
  receiverAddress: string;
  items: OrderItemDto[];
}

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const detail = ref<AfterSaleDetail | null>(null);

const typeMap: Record<number, string> = { 0: '仅退款', 1: '退货退款', 2: '换货' };
const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '申请中', type: 'warning' },
  1: { label: '审核中', type: 'warning' },
  2: { label: '已通过', type: 'success' },
  3: { label: '已拒绝', type: 'danger' },
  4: { label: '退款中', type: 'warning' },
  5: { label: '已完成', type: 'success' },
  6: { label: '已取消', type: 'info' },
};

function normalizeFileUrl(rawPath?: string) {
  if (!rawPath) return '';
  if (/^https?:\/\//.test(rawPath)) return rawPath;
  if (rawPath.startsWith('/api/')) return rawPath;
  if (rawPath.startsWith('/upload/')) return `/api${rawPath}`;
  if (rawPath.startsWith('/')) return `/api${rawPath}`;
  return `/api/${rawPath}`;
}

async function loadDetail() {
  const id = route.query.id as string;
  if (!id) {
    router.replace('/order/list');
    return;
  }
  loading.value = true;
  try {
    const data = await requestClient.get<AfterSaleDetail>('/mall/afterSale/detail', { params: { id } });
    detail.value = data;
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.back();
}

onMounted(() => { loadDetail(); });
</script>

<template>
  <Page description="查看售后单详情" title="售后详情">
    <ElCard shadow="never">
      <ElButton @click="goBack">返回</ElButton>
    </ElCard>

    <ElCard v-loading="loading" class="mt-4" shadow="never">
      <div v-if="detail" class="detail-wrap">
        <!-- 售后信息 -->
        <div class="section">
          <div class="section-title">售后信息</div>
          <div class="info-grid">
            <span class="info-label">售后单号</span><span>{{ detail.afterSaleNo }}</span>
            <span class="info-label">售后类型</span><span>{{ typeMap[detail.type] ?? '-' }}</span>
            <span class="info-label">当前状态</span>
            <span><ElTag :type="statusMap[detail.status]?.type ?? 'info'">{{ statusMap[detail.status]?.label ?? '-' }}</ElTag></span>
            <span class="info-label">申请原因</span><span>{{ detail.reason || '-' }}</span>
            <span class="info-label">问题描述</span><span>{{ detail.description || '-' }}</span>
            <span class="info-label">退款金额</span>
            <span class="price-text">¥{{ (detail.refundAmount ?? 0).toFixed(2) }}</span>
            <span class="info-label">申请时间</span><span>{{ detail.applyTime || '-' }}</span>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="section">
          <div class="section-title">关联订单</div>
          <div class="info-grid">
            <span class="info-label">订单编号</span><span>{{ detail.orderNo || '-' }}</span>
            <span class="info-label">实付金额</span><span>¥{{ (detail.payAmount ?? 0).toFixed(2) }}</span>
            <span class="info-label">收货人</span><span>{{ detail.receiverName || '-' }}</span>
            <span class="info-label">联系电话</span><span>{{ detail.receiverPhone || '-' }}</span>
            <span class="info-label">收货地址</span><span>{{ detail.receiverAddress || '-' }}</span>
          </div>
        </div>

        <!-- 商品明细 -->
        <div class="section">
          <div class="section-title">商品明细</div>
          <div v-for="item in detail.items" :key="item.id" class="item-row">
            <ElImage
              v-if="item.productImage"
              :src="normalizeFileUrl(item.productImage)"
              fit="cover"
              class="item-img"
            />
            <div class="item-info">
              <div>{{ item.productName }}</div>
              <div class="text-gray">单价 ¥{{ item.price.toFixed(2) }} × {{ item.quantity }}</div>
            </div>
            <div class="item-total">¥{{ item.totalPrice.toFixed(2) }}</div>
          </div>
        </div>
      </div>
    </ElCard>
  </Page>
</template>

<style scoped>
.detail-wrap { max-width: 720px; }

.section {
  border-bottom: 1px solid #eee;
  padding: 16px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: 80px 1fr;
  gap: 10px 16px;
  font-size: 14px;
}

.info-label { color: #999; }

.price-text { color: var(--el-color-danger); font-weight: 600; }

.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.item-img { width: 56px; height: 56px; border-radius: 4px; flex-shrink: 0; }

.item-info { flex: 1; }

.item-total { font-weight: 600; }

.text-gray { color: #999; font-size: 12px; }
</style>
