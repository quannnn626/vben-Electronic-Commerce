<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElSpace,
  ElSwitch,
  ElTable,
  ElTag,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface AddressItem {
  id: number;
  receiverName: string;
  receiverPhone: string;
  province?: string;
  city?: string;
  district?: string;
  detailAddress: string;
  isDefault: number;
}

const tableLoading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentEditId = ref<null | number>(null);
const addressList = ref<AddressItem[]>([]);

const addressApiBase = '/mall/address';

const formModel = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  postalCode: '',
  isDefault: false,
});

// 加载地址列表
async function loadAddresses() {
  tableLoading.value = true;
  try {
    const res = await requestClient.get<AddressItem[]>(`${addressApiBase}/list`);
    addressList.value = Array.isArray(res) ? res : [];
  } finally {
    tableLoading.value = false;
  }
}

// 打开新增弹窗
function openCreateDialog() {
  isEdit.value = false;
  currentEditId.value = null;
  resetForm();
  dialogVisible.value = true;
}

// 打开编辑弹窗
function openEditDialog(row: AddressItem) {
  isEdit.value = true;
  currentEditId.value = row.id;
  formModel.receiverName = row.receiverName ?? '';
  formModel.receiverPhone = row.receiverPhone ?? '';
  formModel.province = row.province ?? '';
  formModel.city = row.city ?? '';
  formModel.district = row.district ?? '';
  formModel.detailAddress = row.detailAddress ?? '';
  formModel.postalCode = '';
  formModel.isDefault = row.isDefault === 1;
  dialogVisible.value = true;
}

// 重置表单
function resetForm() {
  formModel.receiverName = '';
  formModel.receiverPhone = '';
  formModel.province = '';
  formModel.city = '';
  formModel.district = '';
  formModel.detailAddress = '';
  formModel.postalCode = '';
  formModel.isDefault = false;
}

// 校验表单
function validateForm() {
  if (!formModel.receiverName.trim()) {
    ElMessage.warning('请输入收货人');
    return false;
  }
  if (!formModel.receiverPhone.trim()) {
    ElMessage.warning('请输入联系电话');
    return false;
  }
  if (!formModel.detailAddress.trim()) {
    ElMessage.warning('请输入详细地址');
    return false;
  }
  return true;
}

// 提交新增或编辑
async function submitAddress() {
  if (submitLoading.value) return;
  if (!validateForm()) return;
  submitLoading.value = true;
  try {
    const payload = {
      id: currentEditId.value,
      receiverName: formModel.receiverName.trim(),
      receiverPhone: formModel.receiverPhone.trim(),
      province: formModel.province.trim() || undefined,
      city: formModel.city.trim() || undefined,
      district: formModel.district.trim() || undefined,
      detailAddress: formModel.detailAddress.trim(),
      isDefault: formModel.isDefault ? 1 : 0,
    };
    if (isEdit.value) {
      await requestClient.put(addressApiBase, payload);
      ElMessage.success('地址修改成功');
    } else {
      await requestClient.post(addressApiBase, payload);
      ElMessage.success('地址新增成功');
    }
    dialogVisible.value = false;
    await loadAddresses();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '保存失败');
  } finally {
    submitLoading.value = false;
  }
}

// 删除地址
async function deleteAddress(row: AddressItem) {
  try {
    await ElMessageBox.confirm(
      `确认删除收货人「${row.receiverName}」的地址吗？`,
      '删除确认',
      { confirmButtonText: '确认删除', type: 'warning' },
    );
    await requestClient.delete(`${addressApiBase}?id=${row.id}`);
    ElMessage.success('删除成功');
    await loadAddresses();
  } catch {
    // 用户取消
  }
}

// 设为默认地址
async function setDefault(row: AddressItem) {
  if (row.isDefault === 1) return;
  try {
    await requestClient.put(addressApiBase, {
      id: row.id,
      receiverName: row.receiverName,
      receiverPhone: row.receiverPhone,
      province: row.province,
      city: row.city,
      district: row.district,
      detailAddress: row.detailAddress,
      isDefault: 1,
    });
    ElMessage.success('已设为默认地址');
    await loadAddresses();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '设置失败');
    await loadAddresses();
  }
}

onMounted(() => {
  loadAddresses();
});
</script>

<template>
  <Page title="地址管理">
    <ElCard shadow="never">
      <ElForm inline>
        <ElFormItem>
          <ElButton type="primary" @click="openCreateDialog">新增地址</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElTable v-loading="tableLoading" :data="addressList" border row-key="id">
        <ElTable.TableColumn label="收货人" min-width="100" prop="receiverName" />
        <ElTable.TableColumn label="联系电话" min-width="130" prop="receiverPhone" />
        <ElTable.TableColumn label="收货地址" min-width="260">
          <template #default="{ row }">
            {{ [row.province, row.city, row.district].filter(Boolean).join('') }}{{ row.detailAddress }}
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="默认" width="80" align="center">
          <template #default="{ row }">
            <ElTag v-if="row.isDefault === 1" type="success">默认</ElTag>
            <span v-else class="text-gray-400 text-sm">-</span>
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="操作" width="200" align="center">
          <template #default="{ row }">
            <ElSpace>
              <ElButton link type="primary" @click="openEditDialog(row)">编辑</ElButton>
              <ElButton link type="primary" @click="setDefault(row)">
                {{ row.isDefault === 1 ? '已默认' : '设为默认' }}
              </ElButton>
              <ElButton link type="danger" @click="deleteAddress(row)">删除</ElButton>
            </ElSpace>
          </template>
        </ElTable.TableColumn>
      </ElTable>
    </ElCard>

    <ElDialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '新增地址'"
      destroy-on-close
      width="600px"
    >
      <ElForm label-width="90px">
        <ElFormItem label="收货人" required>
          <ElInput v-model="formModel.receiverName" maxlength="32" placeholder="请输入收货人" />
        </ElFormItem>
        <ElFormItem label="联系电话" required>
          <ElInput v-model="formModel.receiverPhone" maxlength="20" placeholder="请输入联系电话" />
        </ElFormItem>
        <ElFormItem label="省份">
          <ElInput v-model="formModel.province" maxlength="32" placeholder="请输入省份" />
        </ElFormItem>
        <ElFormItem label="城市">
          <ElInput v-model="formModel.city" maxlength="32" placeholder="请输入城市" />
        </ElFormItem>
        <ElFormItem label="区/县">
          <ElInput v-model="formModel.district" maxlength="32" placeholder="请输入区/县" />
        </ElFormItem>
        <ElFormItem label="详细地址" required>
          <ElInput
            v-model="formModel.detailAddress"
            :rows="2"
            maxlength="128"
            placeholder="请输入详细地址"
            type="textarea"
          />
        </ElFormItem>
        <ElFormItem label="默认地址">
          <ElSwitch v-model="formModel.isDefault" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElSpace>
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton :loading="submitLoading" type="primary" @click="submitAddress">
            {{ isEdit ? '保存' : '新增' }}
          </ElButton>
        </ElSpace>
      </template>
    </ElDialog>
  </Page>
</template>
