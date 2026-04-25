import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:eye-outline',
      title: $t('page.product.productBrowse'),
    },
    name: 'ProductBrowse',
    path: '/product/browse',
    component: () => import('#/views/home/product-browse.vue'),
  },
  {
    meta: {
      hideInMenu: true,
      title: '商品详情',
    },
    name: 'ProductDetail',
    path: '/product/browse/:id',
    component: () => import('#/views/home/product-detail.vue'),
  },
];

export default routes;
