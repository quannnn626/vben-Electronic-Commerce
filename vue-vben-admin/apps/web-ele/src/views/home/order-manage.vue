<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDialog,
  ElEmpty,
  ElMessage,
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
  items: OrderItemDto[];
}

const loading = ref(false);
const list = ref<OrderRecord[]>([]);
const detailVisible = ref(false);
const currentOrder = ref<OrderRecord | null>(null);

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待支付', type: 'warning' },
  1: { label: '已支付', type: '' },
  2: { label: '已发货', type: 'success' },
  3: { label: '已完成', type: 'success' },
  4: { label: '已取消', type: 'info' },
};

function normalizeImage(url?: string) {
  if (!url) return '';
  if (/^https?:\/\//.test(url)) return url;
  if (url.startsWith('/')) return `/api${url}`;
  return `/api/${url}`;
}

async function loadList() {
  loading.value = true;
  try {
    list.value = await requestClient.get<OrderRecord[]>('/mall/order/admin/list');
  } catch (e: any) {
    ElMessage.error(e?.message ?? '加载订单列表失败');
  } finally {
    loading.value = false;
  }
}

function showDetail(row: OrderRecord) {
  currentOrder.value = row;
  detailVisible.value = true;
}

onMounted(() => {
  loadList();
});
</script>

<template>
  <Page description="查看和管理所有用户订单" title="订单管理">
    <ElCard shadow="never">
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
        <ElTableColumn label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <ElButton size="small" text type="primary" @click="showDetail(row)">
              查看详情
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
  </Page>
</template>

<style scoped>
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
</style>
