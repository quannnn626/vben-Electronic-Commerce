<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElCheckbox,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElSelect,
  ElOption,
  ElSpace,
  ElTable,
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

interface AfterSaleItem {
  id: string;
  afterSaleNo: string;
  orderNo: string;
  username: string;
  type: number;
  status: number;
  refundAmount: number;
  createTime: string;
  items: OrderItemDto[];
}

const router = useRouter();
const loading = ref(false);
const list = ref<AfterSaleItem[]>([]);
const keyword = ref('');
const statusFilter = ref<number | null>(null);
const selectedIds = ref<Set<string>>(new Set());

function toggleSelect(id: string) {
  const s = new Set(selectedIds.value);
  if (s.has(id)) { s.delete(id); } else { s.add(id); }
  selectedIds.value = s;
}

function selectable(row: AfterSaleItem) {
  return row.status === 0;
}

const selectableIds = computed(() =>
  tableData.value.filter(selectable).map((r) => r.id),
);

const isAllSelected = computed(() =>
  selectableIds.value.length > 0 && selectableIds.value.every((id) => selectedIds.value.has(id)),
);

function toggleAll() {
  const s = new Set(selectedIds.value);
  if (isAllSelected.value) {
    for (const id of selectableIds.value) s.delete(id);
  } else {
    for (const id of selectableIds.value) s.add(id);
  }
  selectedIds.value = s;
}

function goBatchAudit() {
  if (selectedIds.value.size === 0) {
    ElMessage.warning('请选择售后单');
    return;
  }
  const ids = Array.from(selectedIds.value);
  router.push({ path: '/after-sale/batch-audit', query: { ids: ids.join(',') } });
}

const typeMap: Record<number, string> = { 0: '仅退款', 1: '退货退款', 2: '换货' };
const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '申请中', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已拒绝', type: 'danger' },
  3: { label: '退款中', type: 'warning' },
  4: { label: '已完成', type: 'success' },
  5: { label: '已取消', type: 'info' },
  6: { label: '待退货', type: 'warning' },
};

const filterStatusOptions = [
  { label: '全部', value: null },
  { label: '申请中', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已拒绝', value: 2 },
  { label: '退款中', value: 3 },
  { label: '已完成', value: 4 },
  { label: '待退货', value: 6 },
];

const tableData = computed(() => {
  let data = list.value;
  if (statusFilter.value !== null) {
    data = data.filter((item) => item.status === statusFilter.value);
  }
  const key = keyword.value.trim().toLowerCase();
  if (key) {
    data = data.filter(
      (item) =>
        item.afterSaleNo.toLowerCase().includes(key) ||
        item.orderNo.toLowerCase().includes(key) ||
        (item.username || '').toLowerCase().includes(key),
    );
  }
  return data;
});

async function loadList() {
  loading.value = true;
  try {
    const data = await requestClient.get<AfterSaleItem[]>('/mall/afterSale/admin/list');
    list.value = Array.isArray(data) ? data : [];
  } finally {
    loading.value = false;
  }
}

function viewDetail(row: AfterSaleItem) {
  router.push({ path: '/order/after-sale/detail', query: { id: row.id } });
}

onMounted(() => { loadList(); });
</script>

<template>
  <Page description="审核管理用户的售后申请" title="售后管理">
    <ElCard shadow="never">
      <ElForm inline>
        <ElFormItem label="状态">
          <ElSelect v-model="statusFilter" placeholder="全部" style="width: 140px">
            <ElOption
              v-for="opt in filterStatusOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="关键词">
          <ElInput
            v-model="keyword"
            clearable
            placeholder="售后单号/订单号/用户名"
            style="width: 240px"
          />
        </ElFormItem>
        <ElFormItem>
          <ElSpace>
            <ElButton type="primary" @click="loadList">查询</ElButton>
            <ElButton @click="keyword = ''; statusFilter = null">重置</ElButton>
            <ElButton type="warning" @click="goBatchAudit">批量审核</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElTable :data="tableData" border row-key="id" v-loading="loading">
        <ElTable.TableColumn width="55" align="center">
          <template #header>
            <ElCheckbox
              :model-value="isAllSelected"
              :indeterminate="selectedIds.size > 0 && !isAllSelected"
              @change="toggleAll"
            />
          </template>
          <template #default="{ row }">
            <ElCheckbox
              v-if="selectable(row)"
              :model-value="selectedIds.has(row.id)"
              @change="toggleSelect(row.id)"
            />
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="售后单号" min-width="200" prop="afterSaleNo" />
        <ElTable.TableColumn label="订单号" min-width="180" prop="orderNo" />
        <ElTable.TableColumn label="用户名" min-width="100" prop="username" />
        <ElTable.TableColumn label="商品" min-width="150">
          <template #default="{ row }">
            {{ row.items?.[0]?.productName ?? '-' }}
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="类型" min-width="100">
          <template #default="{ row }">
            {{ typeMap[row.type] ?? '-' }}
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="状态" min-width="100">
          <template #default="{ row }">
            <ElTag :type="statusMap[row.status]?.type ?? 'info'" size="small">
              {{ statusMap[row.status]?.label ?? '-' }}
            </ElTag>
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="退款金额" min-width="110">
          <template #default="{ row }">
            ¥{{ (row.refundAmount ?? 0).toFixed(2) }}
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="申请时间" min-width="170" prop="createTime" />
        <ElTable.TableColumn fixed="right" label="操作" width="100">
          <template #default="{ row }">
            <ElButton link type="primary" @click="viewDetail(row)">查看</ElButton>
          </template>
        </ElTable.TableColumn>
      </ElTable>
    </ElCard>
  </Page>
</template>
