import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:category',
      title: $t('page.category.categoryManage'),
    },
    name: 'Category',
    path: '/category',
    component: () => import('#/views/home/category-manage.vue'),
  },
];

export default routes;
