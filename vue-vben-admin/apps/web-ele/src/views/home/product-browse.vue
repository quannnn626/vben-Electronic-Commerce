<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElInput,
  ElSpace,
  ElTable,
  ElTag,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface ProductItem {
  categoryIds: number[];
  createTime: string;
  description: string;
  id: number;
  name: string;
  price: number;
  status: number;
  stock: number;
}

const productApiBase = '/mall/product';
const keyword = ref('');
const loading = ref(false);
const products = ref<ProductItem[]>([]);
const router = useRouter();

const tableData = computed(() => {
  const key = keyword.value.trim().toLowerCase();
  if (!key) {
    return products.value;
  }
  return products.value.filter((item) => item.name.toLowerCase().includes(key));
});

async function loadProducts() {
  loading.value = true;
  try {
    const data = await requestClient.get<ProductItem[]>(`${productApiBase}/list`);
    products.value = Array.isArray(data) ? data : [];
  } finally {
    loading.value = false;
  }
}

function openDetail(id: number) {
  router.push(`/product/browse/${id}`);
}

onMounted(async () => {
  await loadProducts();
});
</script>

<template>
  <Page description="浏览商品列表并查看详情信息" title="商品浏览">
    <ElCard shadow="never">
      <div class="flex items-center justify-between">
        <ElInput
          v-model="keyword"
          clearable
          placeholder="请输入商品名称"
          style="width: 260px"
        />
        <ElSpace>
          <ElButton type="primary" @click="loadProducts">刷新</ElButton>
        </ElSpace>
      </div>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElTable :data="tableData" border row-key="id" v-loading="loading">
        <ElTable.TableColumn label="商品ID" min-width="90" prop="id" />
        <ElTable.TableColumn label="商品名称" min-width="180" prop="name" />
        <ElTable.TableColumn label="价格" min-width="100" prop="price" />
        <ElTable.TableColumn label="库存" min-width="90" prop="stock" />
        <ElTable.TableColumn label="状态" min-width="90">
          <template #default="{ row }">
            <ElTag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </ElTag>
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="创建时间" min-width="180" prop="createTime" />
        <ElTable.TableColumn fixed="right" label="操作" width="110">
          <template #default="{ row }">
            <ElButton link type="primary" @click="openDetail(row.id)">查看详情</ElButton>
          </template>
        </ElTable.TableColumn>
      </ElTable>
    </ElCard>
  </Page>
</template>
