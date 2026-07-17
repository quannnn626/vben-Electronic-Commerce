import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:file-document-edit',
      title: '售后管理',
    },
    name: 'AfterSaleManage',
    path: '/after-sale/manage',
    component: () => import('#/views/home/after-sale-manage.vue'),
  },
];

export default routes;
