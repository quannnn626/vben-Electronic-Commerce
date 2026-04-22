import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:package-variant-closed',
      title: $t('page.product.productManage'),
    },
    name: 'Product',
    path: '/product',
    component: () => import('#/views/home/product-manage.vue'),
  },
];

export default routes;
