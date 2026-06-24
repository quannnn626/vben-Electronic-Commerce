import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:cart',
      title: $t('page.cart.cartManage'),
    },
    name: 'Cart',
    path: '/cart',
    component: () => import('#/views/home/cart.vue'),
  },
];

export default routes;
