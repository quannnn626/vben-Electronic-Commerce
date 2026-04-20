<template>
  <div class="p-4">
    <h1>home page</h1>
    <el-button type="primary" :loading="loading" @click="handleTestToken">
      测试 Token（请求 /user/info）
    </el-button>
    <pre v-if="lastResult" class="mt-4 rounded bg-gray-100 p-3 text-sm">{{
      lastResult
    }}</pre>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

import { ElMessage } from 'element-plus';

import { getUserInfoApi } from '#/api/core/user';

const loading = ref(false);
const lastResult = ref('');

async function handleTestToken() {
  loading.value = true;
  lastResult.value = '';
  try {
    const data = await getUserInfoApi();
    lastResult.value = JSON.stringify(data, null, 2);
    ElMessage.success('Token 有效，已拿到用户信息');
  } catch {
    // 401 等错误已在 request 拦截器里提示
  } finally {
    loading.value = false;
  }
}
</script>
