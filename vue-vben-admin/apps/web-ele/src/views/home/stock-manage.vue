<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElMessageBox,
  ElSpace,
  ElTable,
  ElTag,
} from 'element-plus';

import { requestClient } from '#/api/request';

type StockAction = 'decrease' | 'increase' | 'lock' | 'unlock';

interface StockItem {
  availableStock: number;
  lockedStock: number;
  productId: number;
  productName: string;
  totalStock: number;
  updateTime: string;
}

const stockApiBase = '/mall/stock';

const keyword = ref('');
const tableLoading = ref(false);
const dialogVisible = ref(false);
const submitLoading = ref(false);
const currentAction = ref<StockAction>('increase');
const stockList = ref<StockItem[]>([]);
const currentRow = ref<null | StockItem>(null);

const formModel = reactive({
  quantity: 1,
});

const actionTitleMap: Record<StockAction, string> = {
  decrease: '出库',
  increase: '入库',
  lock: '锁定库存',
  unlock: '解锁库存',
};

const tableData = computed(() => {
  const key = keyword.value.trim().toLowerCase();
  if (!key) {
    return stockList.value;
  }
  return stockList.value.filter((item) => item.productName.toLowerCase().includes(key));
});

async function loadStocks() {
  tableLoading.value = true;
  try {
    const data = await requestClient.get<StockItem[]>(`${stockApiBase}/list`, {
      params: { keyword: keyword.value.trim() || undefined },
    });
    stockList.value = Array.isArray(data) ? data : [];
  } finally {
    tableLoading.value = false;
  }
}

function openActionDialog(action: StockAction, row: StockItem) {
  currentAction.value = action;
  currentRow.value = row;
  formModel.quantity = 1;
  dialogVisible.value = true;
}

function validateOperate() {
  if (!currentRow.value) {
    return false;
  }
  if (!formModel.quantity || formModel.quantity <= 0) {
    ElMessage.warning('操作数量必须大于0');
    return false;
  }
  return true;
}

async function submitOperate() {
  if (!validateOperate() || !currentRow.value) {
    return;
  }
  submitLoading.value = true;
  try {
    await requestClient.post(`${stockApiBase}/${currentAction.value}`, {
      productId: currentRow.value.productId,
      quantity: formModel.quantity,
    });
    ElMessage.success(`${actionTitleMap[currentAction.value]}成功`);
    dialogVisible.value = false;
    await loadStocks();
  } finally {
    submitLoading.value = false;
  }
}

async function batchRefresh() {
  await loadStocks();
  ElMessage.success('库存已刷新');
}

onMounted(async () => {
  await loadStocks();
});
</script>

<template>
  <Page description="商品库存的入库、出库、锁定与解锁管理" title="库存管理">
    <ElCard shadow="never">
      <ElForm inline>
        <ElFormItem label="商品名称">
          <ElInput
            v-model="keyword"
            clearable
            placeholder="请输入商品名称"
            style="width: 220px"
          />
        </ElFormItem>
        <ElFormItem>
          <ElSpace>
            <ElButton type="primary" @click="loadStocks">查询</ElButton>
            <ElButton @click="keyword = ''">重置</ElButton>
            <ElButton type="success" @click="batchRefresh">刷新库存</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElTable :data="tableData" border row-key="productId" v-loading="tableLoading">
        <ElTable.TableColumn label="商品ID" min-width="90" prop="productId" />
        <ElTable.TableColumn label="商品名称" min-width="180" prop="productName" />
        <ElTable.TableColumn label="可用库存" min-width="100" prop="availableStock" />
        <ElTable.TableColumn label="锁定库存" min-width="100" prop="lockedStock" />
        <ElTable.TableColumn label="总库存" min-width="100" prop="totalStock" />
        <ElTable.TableColumn label="更新时间" min-width="180" prop="updateTime" />
        <ElTable.TableColumn fixed="right" label="操作" width="270">
          <template #default="{ row }">
            <ElSpace>
              <ElButton link type="success" @click="openActionDialog('increase', row)">
                入库
              </ElButton>
              <ElButton link type="warning" @click="openActionDialog('decrease', row)">
                出库
              </ElButton>
              <ElButton link type="primary" @click="openActionDialog('lock', row)">
                锁定
              </ElButton>
              <ElButton link type="info" @click="openActionDialog('unlock', row)">
                解锁
              </ElButton>
            </ElSpace>
          </template>
        </ElTable.TableColumn>
      </ElTable>
    </ElCard>

    <ElDialog
      v-model="dialogVisible"
      :title="`${actionTitleMap[currentAction]} - ${currentRow?.productName ?? ''}`"
      destroy-on-close
      width="460px"
    >
      <ElForm label-width="90px">
        <ElFormItem label="商品ID">
          <span>{{ currentRow?.productId }}</span>
        </ElFormItem>
        <ElFormItem label="操作数量" required>
          <ElInputNumber v-model="formModel.quantity" :min="1" :step="1" style="width: 220px" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElSpace>
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton :loading="submitLoading" type="primary" @click="submitOperate">
            确认
          </ElButton>
        </ElSpace>
      </template>
    </ElDialog>
  </Page>
</template>
