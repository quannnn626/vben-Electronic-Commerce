<script setup lang="ts">
import type { BasicOption, Recordable } from '@vben/types';

import type { VbenFormSchema } from '#/adapter/form';

import { computed, onMounted, ref } from 'vue';

import { ElMessage } from 'element-plus';
import { useUserStore } from '@vben/stores';

import { useAuthStore } from '#/store';

import { ProfileBaseSetting } from '@vben/common-ui';

import { getUserInfoApi, updateUserInfoApi } from '#/api';

const profileBaseSettingRef = ref();
const authStore = useAuthStore();
const userStore = useUserStore();

const MOCK_ROLES_OPTIONS: BasicOption[] = [
  {
    label: '管理员',
    value: 'super',
  },
  {
    label: '用户',
    value: 'user',
  },
  {
    label: '测试',
    value: 'test',
  },
];

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      fieldName: 'realName',
      component: 'Input',
      label: '姓名',
    },
    {
      fieldName: 'username',
      component: 'Input',
      label: '用户名',
    },
    {
      fieldName: 'roles',
      component: 'Select',
      componentProps: {
        mode: 'tags',
        options: MOCK_ROLES_OPTIONS,
      },
      label: '角色',
    },
    {
      fieldName: 'introduction',
      component: 'Textarea',
      label: '个人简介',
    },
  ];
});

onMounted(async () => {
  const data = await getUserInfoApi();
  profileBaseSettingRef.value.getFormApi().setValues(data);
});

async function handleProfileSubmit(values: Recordable<any>) {
  const userId = userStore.userInfo?.userId ?? values.userId;
  await updateUserInfoApi({
    ...values,
    userId,
  });
  await authStore.fetchUserInfo();
  ElMessage.success('保存成功');
}
</script>
<template>
  <ProfileBaseSetting
    ref="profileBaseSettingRef"
    :form-schema="formSchema"
    @submit="handleProfileSubmit"
  />
</template>
