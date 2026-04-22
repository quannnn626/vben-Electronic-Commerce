<script lang="ts" setup>
import type {
  UploadRequestOptions,
  UploadUserFile,
} from 'element-plus';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { useAccessStore } from '@vben/stores';

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
  ElSwitch,
  ElTable,
  ElTag,
  ElTreeSelect,
  ElUpload,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface CategoryNode {
  children?: CategoryNode[];
  id: number;
  name: string;
}

interface ProductFile {
  fileName: string;
  filePath: string;
  fileType: string;
  id: number;
}

interface ProductItem {
  categoryIds: number[];
  createTime: string;
  description: string;
  files: ProductFile[];
  id: number;
  name: string;
  price: number;
  status: number;
  stock: number;
}

const keyword = ref('');
const tableLoading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentEditId = ref<null | number>(null);
const products = ref<ProductItem[]>([]);
const categoryTree = ref<CategoryNode[]>([]);

const productApiBase = '/mall/product';
const categoryTreeApi = '/mall/product-category/tree';
const fileUploadApi = '/mall/file/upload';
const maxFileSizeBytes = 100 * 1024 * 1024;

const formModel = reactive({
  categoryIds: [] as number[],
  description: '',
  fileIds: [] as number[],
  files: [] as UploadUserFile[],
  name: '',
  price: 0,
  status: true,
  stock: 0,
});

const categoryTreeOptions = computed(() => {
  const mapNode = (node: CategoryNode): Record<string, any> => ({
    children: node.children?.map(mapNode) ?? [],
    label: node.name,
    value: node.id,
  });
  return categoryTree.value.map(mapNode);
});

const tableData = computed(() => {
  const key = keyword.value.trim().toLowerCase();
  if (!key) {
    return products.value;
  }
  return products.value.filter((item) => item.name.toLowerCase().includes(key));
});

function resetForm() {
  formModel.name = '';
  formModel.price = 0;
  formModel.stock = 0;
  formModel.description = '';
  formModel.status = true;
  formModel.categoryIds = [];
  formModel.fileIds = [];
  formModel.files = [];
}

function validateForm() {
  if (!formModel.name.trim()) {
    ElMessage.warning('请输入商品名称');
    return false;
  }
  if (formModel.price <= 0) {
    ElMessage.warning('商品价格必须大于0');
    return false;
  }
  if (formModel.stock < 0) {
    ElMessage.warning('库存不能小于0');
    return false;
  }
  if (formModel.categoryIds.length === 0) {
    ElMessage.warning('请至少选择一个分类');
    return false;
  }
  return true;
}

async function loadCategoryTree() {
  const data = await requestClient.get<CategoryNode[]>(categoryTreeApi);
  categoryTree.value = Array.isArray(data) ? data : [];
}

async function loadProducts() {
  tableLoading.value = true;
  try {
    const data = await requestClient.get<ProductItem[]>(`${productApiBase}/list`);
    products.value = Array.isArray(data) ? data : [];
  } finally {
    tableLoading.value = false;
  }
}

function openCreateDialog() {
  isEdit.value = false;
  currentEditId.value = null;
  resetForm();
  dialogVisible.value = true;
}

function openEditDialog(row: ProductItem) {
  isEdit.value = true;
  currentEditId.value = row.id;
  formModel.name = row.name;
  formModel.price = row.price;
  formModel.stock = row.stock;
  formModel.description = row.description ?? '';
  formModel.status = row.status === 1;
  formModel.categoryIds = [...(row.categoryIds ?? [])];
  formModel.fileIds = (row.files ?? []).map((item) => item.id);
  formModel.files = (row.files ?? []).map((item) => ({
    name: item.fileName,
    status: 'success',
    uid: item.id,
    url: item.filePath,
  }));
  dialogVisible.value = true;
}

function buildPayload() {
  return {
    categoryIds: formModel.categoryIds,
    description: formModel.description.trim(),
    fileIds: formModel.fileIds,
    id: currentEditId.value,
    name: formModel.name.trim(),
    price: formModel.price,
    status: formModel.status ? 1 : 0,
    stock: formModel.stock,
  };
}

async function submitProduct() {
  if (!validateForm()) {
    return;
  }
  submitLoading.value = true;
  try {
    const payload = buildPayload();
    if (isEdit.value && currentEditId.value !== null) {
      await requestClient.put(productApiBase, payload);
      ElMessage.success('商品修改成功');
    } else {
      await requestClient.post(productApiBase, payload);
      ElMessage.success('商品新增成功');
    }
    dialogVisible.value = false;
    await loadProducts();
  } finally {
    submitLoading.value = false;
  }
}

async function removeProduct(row: ProductItem) {
  try {
    await ElMessageBox.confirm(`确认删除商品「${row.name}」吗？`, '删除确认', {
      confirmButtonText: '确认删除',
      type: 'warning',
    });
    await requestClient.delete(productApiBase, {
      data: { id: row.id },
    });
    ElMessage.success('删除成功');
    await loadProducts();
  } catch {
    // 用户取消删除或请求失败
  }
}

function beforeFileUpload(file: File) {
  const isImage = file.type.startsWith('image/');
  const isVideo = file.type.startsWith('video/');
  if (!isImage && !isVideo) {
    ElMessage.warning('商品附件仅支持图片或视频');
    return false;
  }
  if (file.size > maxFileSizeBytes) {
    ElMessage.warning('单个附件大小不能超过 100MB');
    return false;
  }
  return true;
}

async function uploadFile(option: UploadRequestOptions) {
  const { file, onError, onSuccess } = option;
  const formData = new FormData();
  formData.append('files', file);
  try {
    const data = await requestClient.post<ProductFile[]>(
      fileUploadApi,
      formData,
      {
        headers: { 'Content-Type': 'multipart/form-data' },
      },
    );
    const saved = Array.isArray(data) ? data[0] : null;
    if (!saved) {
      throw new Error('上传失败');
    }
    formModel.fileIds.push(saved.id);
    onSuccess?.(saved as any);
  } catch (e) {
    onError?.(e as any);
  }
}

function handleUploadSuccess(response: ProductFile, file: UploadUserFile) {
  file.url = response.filePath;
}

function handleUploadRemove(file: UploadUserFile) {
  const fileId = Number(file.uid);
  if (!Number.isNaN(fileId)) {
    formModel.fileIds = formModel.fileIds.filter((id) => id !== fileId);
    return;
  }
  if (file.response && typeof file.response === 'object' && 'id' in file.response) {
    const uploadedId = Number((file.response as any).id);
    if (!Number.isNaN(uploadedId)) {
      formModel.fileIds = formModel.fileIds.filter((id) => id !== uploadedId);
    }
  }
}

function getAuthHeaders() {
  const accessStore = useAccessStore();
  const token = accessStore.accessToken ? `Bearer ${accessStore.accessToken}` : '';
  return { Authorization: token };
}

onMounted(async () => {
  await Promise.all([loadCategoryTree(), loadProducts()]);
});
</script>

<template>
  <Page description="商品新增、编辑、删除，支持分类多选与附件上传" title="商品管理">
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
            <ElButton type="primary" @click="loadProducts">查询</ElButton>
            <ElButton @click="keyword = ''">重置</ElButton>
            <ElButton type="success" @click="openCreateDialog">新增商品</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElTable :data="tableData" border row-key="id" v-loading="tableLoading">
        <ElTable.TableColumn label="商品名称" min-width="180" prop="name" />
        <ElTable.TableColumn label="价格" min-width="100" prop="price" />
        <ElTable.TableColumn label="库存" min-width="90" prop="stock" />
        <ElTable.TableColumn label="状态" min-width="100">
          <template #default="{ row }">
            <ElTag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </ElTag>
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="分类数量" min-width="100">
          <template #default="{ row }">
            {{ row.categoryIds?.length ?? 0 }}
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="附件数量" min-width="100">
          <template #default="{ row }">
            {{ row.files?.length ?? 0 }}
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="创建时间" min-width="180" prop="createTime" />
        <ElTable.TableColumn fixed="right" label="操作" width="150">
          <template #default="{ row }">
            <ElSpace>
              <ElButton link type="primary" @click="openEditDialog(row)">编辑</ElButton>
              <ElButton link type="danger" @click="removeProduct(row)">删除</ElButton>
            </ElSpace>
          </template>
        </ElTable.TableColumn>
      </ElTable>
    </ElCard>

    <ElDialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '新增商品'"
      destroy-on-close
      width="700px"
    >
      <ElForm label-width="90px">
        <ElFormItem label="商品名称" required>
          <ElInput v-model="formModel.name" maxlength="64" placeholder="请输入商品名称" />
        </ElFormItem>
        <ElFormItem label="商品价格" required>
          <ElInputNumber
            v-model="formModel.price"
            :min="0"
            :precision="2"
            :step="1"
            style="width: 220px"
          />
        </ElFormItem>
        <ElFormItem label="库存" required>
          <ElInputNumber v-model="formModel.stock" :min="0" :step="1" style="width: 220px" />
        </ElFormItem>
        <ElFormItem label="商品状态">
          <ElSwitch v-model="formModel.status" />
        </ElFormItem>
        <ElFormItem label="商品分类" required>
          <ElTreeSelect
            v-model="formModel.categoryIds"
            :data="categoryTreeOptions"
            multiple
            show-checkbox
            placeholder="请选择分类（可多选）"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="附件上传">
          <ElUpload
            v-model:file-list="formModel.files"
            :before-upload="beforeFileUpload"
            :headers="getAuthHeaders()"
            :http-request="uploadFile"
            :on-remove="handleUploadRemove"
            :on-success="handleUploadSuccess"
            multiple
          >
            <ElButton type="primary">选择附件</ElButton>
            <template #tip>
              <div class="text-gray-500">仅支持图片或视频，可上传多个附件</div>
            </template>
          </ElUpload>
        </ElFormItem>
        <ElFormItem label="商品描述">
          <ElInput
            v-model="formModel.description"
            :rows="4"
            maxlength="500"
            placeholder="请输入商品描述"
            show-word-limit
            type="textarea"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElSpace>
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton :loading="submitLoading" type="primary" @click="submitProduct">
            {{ isEdit ? '保存' : '创建' }}
          </ElButton>
        </ElSpace>
      </template>
    </ElDialog>
  </Page>
</template>
