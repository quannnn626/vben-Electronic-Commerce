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
  ElSelect,
  ElOption,
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
  id: number;
  name: string;
  price: number;
  skus?: ProductSku[];
  status: number;
  stock: number;
}

interface ProductSku {
  fileId?: number;
  fileName?: string;
  filePath?: string;
  fileType?: string;
  id?: number;
  price: number;
  skuCode: string;
  specName: string;
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
  files: [] as UploadUserFile[],
  name: '',
  skus: [] as ProductSku[],
  status: true,
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
  formModel.description = '';
  formModel.status = true;
  formModel.skus = [createEmptySku()];
  formModel.categoryIds = [];
  formModel.files = [];
}

function createEmptySku(): ProductSku {
  return {
    price: 0,
    skuCode: '',
    specName: '',
    status: 1,
    stock: 0,
  };
}

const uploadedFileOptions = computed(() => {
  const map = new Map<number, ProductFile>();
  for (const file of formModel.files) {
    const maybeId = Number(file.uid);
    if (!Number.isNaN(maybeId) && maybeId > 0) {
      map.set(maybeId, {
        fileName: file.name,
        filePath: file.url || '',
        fileType: '',
        id: maybeId,
      });
    }
    if (file.response && typeof file.response === 'object' && 'id' in file.response) {
      const id = Number((file.response as any).id);
      if (!Number.isNaN(id)) {
        map.set(id, {
          fileName: (file.response as any).fileName ?? file.name,
          filePath: (file.response as any).filePath ?? file.url ?? '',
          fileType: (file.response as any).fileType ?? '',
          id,
        });
      }
    }
  }
  return Array.from(map.values());
});

function validateForm() {
  if (!formModel.name.trim()) {
    ElMessage.warning('请输入商品名称');
    return false;
  }
  if (formModel.categoryIds.length === 0) {
    ElMessage.warning('请至少选择一个分类');
    return false;
  }
  if (!formModel.skus.length) {
    ElMessage.warning('请至少添加一个SKU规格');
    return false;
  }
  const hasInvalidSku = formModel.skus.some(
    (sku) =>
      !sku.specName.trim()
      || sku.price <= 0
      || sku.stock < 0
      || !sku.fileId,
  );
  if (hasInvalidSku) {
    ElMessage.warning('请检查SKU规格：规格名不能为空、附件必选、价格需大于0、库存不能小于0');
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
  formModel.description = row.description ?? '';
  formModel.status = row.status === 1;
  formModel.skus = (row.skus ?? []).map((sku) => ({
    fileId: sku.fileId,
    fileName: sku.fileName,
    filePath: sku.filePath,
    fileType: sku.fileType,
    id: sku.id,
    price: sku.price,
    skuCode: sku.skuCode ?? '',
    specName: sku.specName ?? '',
    status: sku.status ?? 1,
    stock: sku.stock ?? 0,
  }));
  if (formModel.skus.length === 0) {
    formModel.skus = [createEmptySku()];
  }
  formModel.categoryIds = [...(row.categoryIds ?? [])];
  const uniqueFileMap = new Map<number, ProductFile>();
  for (const sku of row.skus ?? []) {
    if (!sku.fileId || !sku.filePath) continue;
    if (!uniqueFileMap.has(sku.fileId)) {
      uniqueFileMap.set(sku.fileId, {
        fileName: sku.fileName ?? `file-${sku.fileId}`,
        filePath: sku.filePath,
        fileType: sku.fileType ?? '',
        id: sku.fileId,
      });
    }
  }
  formModel.files = Array.from(uniqueFileMap.values()).map((item) => ({
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
    id: currentEditId.value,
    name: formModel.name.trim(),
    skus: formModel.skus.map((item) => ({
      fileId: item.fileId,
      id: item.id,
      price: item.price,
      skuCode: item.skuCode.trim(),
      specName: item.specName.trim(),
      status: item.status,
      stock: item.stock,
    })),
    status: formModel.status ? 1 : 0,
  };
}

function addSkuRow() {
  formModel.skus.push(createEmptySku());
}

function removeSkuRow(index: number) {
  if (formModel.skus.length <= 1) {
    ElMessage.warning('至少保留一个SKU规格');
    return;
  }
  formModel.skus.splice(index, 1);
}

async function submitProduct() {
  if (submitLoading.value) {
    return;
  }
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
    onSuccess?.(saved as any);
  } catch (e) {
    onError?.(e as any);
  }
}

function handleUploadSuccess(response: ProductFile, file: UploadUserFile) {
  file.uid = response.id;
  file.url = response.filePath;
}

function handleUploadRemove(file: UploadUserFile) {
  let removedId = Number(file.uid);
  if (Number.isNaN(removedId) && file.response && typeof file.response === 'object' && 'id' in file.response) {
    removedId = Number((file.response as any).id);
  }
  if (Number.isNaN(removedId)) return;
  formModel.skus = formModel.skus.map((sku) => (sku.fileId === removedId ? { ...sku, fileId: undefined } : sku));
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
        <ElTable.TableColumn label="最低价" min-width="100" prop="price" />
        <ElTable.TableColumn label="总库存" min-width="90" prop="stock" />
        <ElTable.TableColumn label="SKU数" min-width="90">
          <template #default="{ row }">
            {{ row.skus?.length ?? 0 }}
          </template>
        </ElTable.TableColumn>
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
            {{ row.skus?.filter((item: ProductSku) => item.fileId).length ?? 0 }}
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
        <ElFormItem label="商品状态">
          <ElSwitch v-model="formModel.status" />
        </ElFormItem>
        <ElFormItem label="SKU规格" required>
          <div class="w-full">
            <ElTable :data="formModel.skus" border size="small">
              <ElTable.TableColumn label="规格名" min-width="180">
                <template #default="{ row }">
                  <ElInput v-model="row.specName" placeholder="如：42码 / XL / 黑色" />
                </template>
              </ElTable.TableColumn>
              <ElTable.TableColumn label="SKU编码" min-width="150">
                <template #default="{ row }">
                  <ElInput v-model="row.skuCode" placeholder="可选" />
                </template>
              </ElTable.TableColumn>
              <ElTable.TableColumn label="价格" min-width="120">
                <template #default="{ row }">
                  <ElInputNumber v-model="row.price" :min="0" :precision="2" :step="1" style="width: 100%" />
                </template>
              </ElTable.TableColumn>
              <ElTable.TableColumn label="库存" min-width="110">
                <template #default="{ row }">
                  <ElInputNumber v-model="row.stock" :min="0" :step="1" style="width: 100%" />
                </template>
              </ElTable.TableColumn>
              <ElTable.TableColumn label="附件" min-width="180">
                <template #default="{ row }">
                  <ElSelect v-model="row.fileId" clearable placeholder="选择附件">
                    <ElOption
                      v-for="file in uploadedFileOptions"
                      :key="file.id"
                      :label="file.fileName"
                      :value="file.id"
                    />
                  </ElSelect>
                </template>
              </ElTable.TableColumn>
              <ElTable.TableColumn label="启用" width="90" align="center">
                <template #default="{ row }">
                  <ElSwitch v-model="row.status" :active-value="1" :inactive-value="0" />
                </template>
              </ElTable.TableColumn>
              <ElTable.TableColumn label="操作" width="80" align="center">
                <template #default="{ $index }">
                  <ElButton link type="danger" @click="removeSkuRow($index)">删除</ElButton>
                </template>
              </ElTable.TableColumn>
            </ElTable>
            <ElButton class="mt-2" plain type="primary" @click="addSkuRow">新增规格</ElButton>
          </div>
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
              <div class="text-gray-500">先上传附件，再在SKU行里给每个规格选择一个附件</div>
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
