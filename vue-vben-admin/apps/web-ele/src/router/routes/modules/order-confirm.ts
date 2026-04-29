import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      affixTab: false,
      hideInMenu: true,
      tabClosable: true,
      title: '确认订单',
    },
    name: 'OrderConfirm',
    path: '/order/confirm',
    component: () => import('#/views/home/order-confirm.vue'),
  },
  {
    meta: {
      affixTab: false,
      hideInMenu: true,
      tabClosable: true,
      title: '订单详情',
    },
    name: 'OrderConfirmResult',
    path: '/order/confirm/:id',
    component: () => import('#/views/home/order-confirm.vue'),
  },
];

export default routes;
