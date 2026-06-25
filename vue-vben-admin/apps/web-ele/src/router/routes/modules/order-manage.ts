import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:clipboard-list',
      title: $t('page.order.orderManage'),
    },
    name: 'OrderManage',
    path: '/order/manage',
    component: () => import('#/views/home/order-manage.vue'),
  },
];

export default routes;
