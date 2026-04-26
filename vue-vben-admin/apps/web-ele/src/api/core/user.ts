import type { Recordable, UserInfo } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 获取用户信息
 */
export async function getUserInfoApi() {
  return requestClient.get<UserInfo>('/user/info');
}

/**
 * 更新个人资料
 */
export async function updateUserInfoApi(data: Recordable<any>) {
  return requestClient.put<unknown>('/user/update', data);
}
