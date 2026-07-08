<script lang="ts" setup>
import type { UploadRequestOptions, UploadUserFile } from 'element-plus';

import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElOption,
  ElSelect,
  ElUpload,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface ProductFile {
  fileName: string;
  filePath: string;
  fileType: string;
  id: number;
}

const route = useRoute();
const router = useRouter();
const submitting = ref(false);
const tempFileIds = ref<number[]>([]);
const uploadedFiles = ref<{ id: number; name: string; url: string }[]>([]);

const orderId = Number(route.query.orderId ?? 0);
const orderItemId = Number(route.query.orderItemId ?? 0);

const form = reactive({
  description: '',
  quantity: 1,
  reason: '',
  type: null as number | null,
});

const refundTypes = [
  { label: '仅退款', value: 0 },
  { label: '退货退款', value: 1 },
  { label: '换货', value: 2 },
];

const reasonOptions = [
  '质量问题',
  '商品与描述不符',
  '发错货',
  '商品破损',
  '尺码/规格不合适',
  '不想要了',
  '其他',
];

const fileUploadApi = '/mall/file/upload';
const fileDeleteApi = '/mall/file/delete';
const afterSaleApi = '/mall/afterSale/create';

function normalizeFileUrl(rawPath?: string) {
  if (!rawPath) return '';
  if (/^https?:\/\//.test(rawPath)) return rawPath;
  if (rawPath.startsWith('/api/')) return rawPath;
  if (rawPath.startsWith('/upload/')) return `/api${rawPath}`;
  if (rawPath.startsWith('/')) return `/api${rawPath}`;
  return `/api/${rawPath}`;
}

function validateForm() {
  if (form.type === null) {
    ElMessage.warning('请选择售后类型');
    return false;
  }
  if (!form.reason.trim()) {
    ElMessage.warning('请选择售后原因');
    return false;
  }
  return true;
}

async function uploadFile(option: UploadRequestOptions) {
  const { file, onError, onSuccess } = option;
  const formData = new FormData();
  formData.append('files', file);
  try {
    const data = await requestClient.post<ProductFile[]>(fileUploadApi, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    const saved = Array.isArray(data) ? data[0] : null;
    if (!saved) throw new Error('上传失败');
    onSuccess?.(saved as any);
  } catch (e) {
    onError?.(e as any);
  }
}

function handleUploadSuccess(response: ProductFile, file: UploadUserFile) {
  file.uid = response.id;
  file.url = response.filePath;
  tempFileIds.value.push(response.id);
}

function handleUploadRemove(file: UploadUserFile) {
  const id = Number(file.uid);
  if (Number.isNaN(id)) return;
  tempFileIds.value = tempFileIds.value.filter((fid) => fid !== id);
}

function goBack() {
  router.back();
}

async function submitForm() {
  if (!validateForm()) return;
  submitting.value = true;
  try {
    await requestClient.post(afterSaleApi, {
      orderId,
      orderItemId,
      quantity: form.quantity,
      type: form.type,
      reason: form.reason.trim(),
      description: form.description.trim(),
      fileIds: tempFileIds.value,
    });
    ElMessage.success('售后申请已提交');
    router.back();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '提交失败');
  } finally {
    submitting.value = false;
  }
}

onMounted(() => {
  if (!orderId || !orderItemId) {
    ElMessage.error('缺少订单信息');
    router.back();
  }
});
</script>

<template>
  <Page description="提交售后申请" title="申请售后">
    <ElCard shadow="never">
      <ElButton @click="goBack">返回</ElButton>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElForm label-width="100px">
        <ElFormItem label="订单编号">
          <span class="text-gray-600">#{{ orderId }}</span>
        </ElFormItem>

        <ElFormItem label="售后类型" required>
          <ElSelect v-model="form.type" placeholder="请选择售后类型">
            <ElOption
              v-for="item in refundTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
          <span v-if="form.type === 2" class="text-gray-500 text-sm">
            换货无需退款，审核通过后将为您补发商品
          </span>
          <span v-else-if="form.type !== null" class="text-gray-500 text-sm">
            退款金额将按订单实付金额自动计算
          </span>
        </ElFormItem>

        <ElFormItem label="售后数量" required>
          <ElInputNumber v-model="form.quantity" :min="1" :step="1" />
        </ElFormItem>

        <ElFormItem label="售后原因" required>
          <ElSelect v-model="form.reason" placeholder="请选择售后原因">
            <ElOption
              v-for="r in reasonOptions"
              :key="r"
              :label="r"
              :value="r"
            />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="问题描述">
          <ElInput
            v-model="form.description"
            :rows="4"
            maxlength="500"
            placeholder="请描述您遇到的问题（选填）"
            show-word-limit
            type="textarea"
          />
        </ElFormItem>

        <ElFormItem label="凭证上传">
          <ElUpload
            :http-request="uploadFile"
            :on-remove="handleUploadRemove"
            :on-success="handleUploadSuccess"
            :file-list="uploadedFiles"
            multiple
          >
            <ElButton type="primary">上传凭证</ElButton>
            <template #tip>
              <div class="text-gray-500">可上传订单截图、商品照片等凭证（选填）</div>
            </template>
          </ElUpload>
        </ElFormItem>

        <ElFormItem>
          <ElButton :loading="submitting" type="danger" @click="submitForm">
            提交申请
          </ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </Page>
</template>
