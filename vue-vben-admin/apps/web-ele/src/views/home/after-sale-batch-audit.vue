<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElInput,
  ElMessage,
  ElRadio,
  ElRadioGroup,
  ElTag,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface AfterSaleItem {
  id: string;
  afterSaleNo: string;
  orderNo: string;
  username: string;
  type: number;
  status: number;
  refundAmount: number;
  items: { productName: string }[];
}

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const submitting = ref(false);
const list = ref<AfterSaleItem[]>([]);

// 每行的审核状态：2=通过, 3=不通过(默认)
const auditStatus = ref<Record<string, number>>({});
const auditRemarks = ref<Record<string, string>>({});

function initAuditRow(id: string) {
  if (auditStatus.value[id] === undefined) {
    auditStatus.value[id] = 3; // 默认不通过
    auditRemarks.value[id] = '';
  }
}

function isApproved(id: string) {
  return auditStatus.value[id] === 2;
}

const rejectCount = computed(() =>
  Object.values(auditStatus.value).filter((v) => v === 3).length,
);

async function loadItems() {
  const ids = (route.query.ids as string || '').split(',').filter(Boolean);
  if (ids.length === 0) {
    ElMessage.warning('未选择售后单');
    router.back();
    return;
  }
  loading.value = true;
  try {
    const data = await requestClient.get<AfterSaleItem[]>('/mall/afterSale/admin/list');
    const all = Array.isArray(data) ? data : [];
    list.value = all.filter((item) => ids.includes(item.id));
    for (const item of list.value) {
      initAuditRow(item.id);
    }
  } finally {
    loading.value = false;
  }
}

async function submitBatch() {
  // 校验：不通过必须填原因
  for (const item of list.value) {
    if (auditStatus.value[item.id] === 3 && !(auditRemarks.value[item.id] || '').trim()) {
      ElMessage.warning(`售后单 ${item.afterSaleNo} 不通过原因必填`);
      return;
    }
  }
  const items = list.value.map((item) => ({
    id: item.id,
    status: auditStatus.value[item.id],
    auditRemark: auditRemarks.value[item.id] || '',
  }));
  submitting.value = true;
  try {
    await requestClient.post('/mall/afterSale/audit/batch', { items });
    ElMessage.success('批量审核完成');
    router.back();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '审核失败');
  } finally {
    submitting.value = false;
  }
}

function goBack() {
  router.back();
}

onMounted(() => { loadItems(); });
</script>

<template>
  <Page description="批量审核售后申请" title="批量审核">
    <ElCard shadow="never">
      <ElButton @click="goBack">返回</ElButton>
      <span class="ml-4 text-sm text-gray-500">
        共 {{ list.length }} 单，其中 {{ rejectCount }} 单不通过
      </span>
    </ElCard>

    <ElCard v-loading="loading" class="mt-4" shadow="never">
      <div v-for="item in list" :key="item.id" class="audit-card">
        <div class="audit-header">
          <span class="font-medium">{{ item.afterSaleNo }}</span>
          <span class="text-gray-500 text-sm ml-4">{{ item.orderNo }}</span>
          <ElTag size="small" type="info" class="ml-2">{{ item.username }}</ElTag>
        </div>
        <div class="audit-body">
          <span class="text-sm">{{ item.items?.[0]?.productName ?? '-' }}</span>
          <span class="text-gray-500 text-sm ml-4">退款 ¥{{ (item.refundAmount ?? 0).toFixed(2) }}</span>
        </div>
        <div class="audit-actions">
          <ElRadioGroup v-model="auditStatus[item.id]" class="mr-4">
            <ElRadio :value="2">通过</ElRadio>
            <ElRadio :value="3">不通过</ElRadio>
          </ElRadioGroup>
          <ElInput
            v-if="!isApproved(item.id)"
            v-model="auditRemarks[item.id]"
            placeholder="不通过原因（必填）"
            style="width: 260px"
            size="small"
          />
        </div>
      </div>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElButton type="primary" :loading="submitting" @click="submitBatch">
        提交审核
      </ElButton>
    </ElCard>
  </Page>
</template>

<style scoped>
.audit-card {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 12px;
}

.audit-header {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}

.audit-body {
  margin-bottom: 10px;
}

.audit-actions {
  display: flex;
  align-items: center;
}
</style>
