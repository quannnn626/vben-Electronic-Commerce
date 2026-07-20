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
  {
    meta: {
      hideInMenu: true,
      title: '批量审核',
    },
    name: 'AfterSaleBatchAudit',
    path: '/after-sale/batch-audit',
    component: () => import('#/views/home/after-sale-batch-audit.vue'),
  },
];

export default routes;
