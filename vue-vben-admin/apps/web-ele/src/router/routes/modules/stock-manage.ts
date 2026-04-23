import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:archive-arrow-up',
      title: $t('page.stock.stockManage'),
    },
    name: 'Stock',
    path: '/stock',
    component: () => import('#/views/home/stock-manage.vue'),
  },
];

export default routes;
