<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElCheckbox,
  ElEmpty,
  ElInputNumber,
  ElMessage,
  ElPopconfirm,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';
import { requestClient } from '#/api/request';

interface CartItem {
  cartId: number;
  productId: number;
  productName: string;
  productImage: string;
  skuId: number;
  skuName: string;
  specText: string;
  salePrice: number;
  stock: number;
  quantity: number;
  subtotalAmount: number;
}

const router = useRouter();
const loading = ref(false);
const items = ref<CartItem[]>([]);
const selectedIds = ref<Set<number>>(new Set());

function normalizeImage(url?: string) {
  if (!url) return '';
  if (/^https?:\/\//.test(url)) return url;
  if (url.startsWith('/')) return `/api${url}`;
  return `/api/${url}`;
}

const isAllSelected = computed(() =>
  items.value.length > 0 && items.value.every((i) => selectedIds.value.has(i.cartId)),
);

const selectedItems = computed(() =>
  items.value.filter((i) => selectedIds.value.has(i.cartId)),
);

const totalAmount = computed(() =>
  selectedItems.value.reduce((sum, i) => sum + i.subtotalAmount, 0),
);

const totalCount = computed(() =>
  selectedItems.value.reduce((sum, i) => sum + i.quantity, 0),
);

function toggleSelectAll() {
  if (isAllSelected.value) {
    selectedIds.value = new Set();
  } else {
    selectedIds.value = new Set(items.value.map((i) => i.cartId));
  }
}

function toggleSelect(id: number) {
  const next = new Set(selectedIds.value);
  if (next.has(id)) {
    next.delete(id);
  } else {
    next.add(id);
  }
  selectedIds.value = next;
}

async function loadCart() {
  loading.value = true;
  try {
    items.value = await requestClient.get<CartItem[]>('/mall/cart/list');
  } catch (e: any) {
    ElMessage.error(e?.message ?? '加载购物车失败');
  } finally {
    loading.value = false;
  }
}

async function handleUpdateQuantity(row: CartItem, newQty: number | undefined) {
  if (!newQty || newQty < 1) {
    ElMessage.warning('数量至少为1');
    loadCart();
    return;
  }
  try {
    await requestClient.post('/mall/cart/update', {
      skuId: row.skuId,
      quantity: newQty,
    });
    loadCart();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '更新数量失败');
    loadCart();
  }
}

async function handleDelete(row: CartItem) {
  try {
    await requestClient.post('/mall/cart/delete', { cartId: row.cartId });
    selectedIds.value.delete(row.cartId);
    ElMessage.success('已移除');
    loadCart();
  } catch (e: any) {
    ElMessage.error(e?.message ?? '删除失败');
  }
}

function handleCheckout() {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品');
    return;
  }
  const goods = selectedItems.value.map((i) => ({
    productId: i.productId,
    productName: i.productName,
    productImage: i.productImage,
    productImageType: '',
    skuId: i.skuId,
    skuSpecName: i.skuName,
    price: i.salePrice,
    quantity: i.quantity,
  }));
  router.push({
    path: '/order/confirm',
    query: { goods: JSON.stringify(goods) },
  });
}

onMounted(() => {
  loadCart();
});
</script>

<template>
  <Page description="管理购物车商品并结算" title="购物车">
    <div v-loading="loading" class="cart-wrapper">
      <template v-if="items.length > 0">
        <ElCard shadow="never" class="cart-table-card">
          <ElTable :data="items" row-key="cartId" class="cart-table">
            <ElTableColumn width="50">
              <template #default="{ row }">
                <ElCheckbox
                  :model-value="selectedIds.has(row.cartId)"
                  @change="toggleSelect(row.cartId)"
                />
              </template>
            </ElTableColumn>

            <ElTableColumn label="商品" min-width="320">
              <template #default="{ row }">
                <div class="goods-cell">
                  <div class="goods-image-box">
                    <img
                      v-if="row.productImage"
                      :src="normalizeImage(row.productImage)"
                      class="goods-img"
                      alt=""
                    />
                    <span v-else class="goods-img-placeholder">图</span>
                  </div>
                  <div class="goods-info">
                    <div class="goods-name">{{ row.productName }}</div>
                    <div v-if="row.skuName" class="goods-sku">
                      <ElTag size="small" type="info">{{ row.skuName }}</ElTag>
                    </div>
                  </div>
                </div>
              </template>
            </ElTableColumn>

            <ElTableColumn label="单价" width="120" align="center">
              <template #default="{ row }">
                <span class="price-cell">¥{{ row.salePrice.toFixed(2) }}</span>
              </template>
            </ElTableColumn>

            <ElTableColumn label="数量" width="140" align="center">
              <template #default="{ row }">
                <ElInputNumber
                  :model-value="row.quantity"
                  :min="1"
                  :max="row.stock"
                  size="small"
                  @change="(v: number | undefined) => handleUpdateQuantity(row, v)"
                />
              </template>
            </ElTableColumn>

            <ElTableColumn label="小计" width="130" align="center">
              <template #default="{ row }">
                <span class="subtotal-cell">¥{{ row.subtotalAmount.toFixed(2) }}</span>
              </template>
            </ElTableColumn>

            <ElTableColumn label="操作" width="80" align="center">
              <template #default="{ row }">
                <ElPopconfirm
                  title="确定要移除该商品吗？"
                  @confirm="handleDelete(row)"
                >
                  <template #reference>
                    <ElButton size="small" type="danger" text>删除</ElButton>
                  </template>
                </ElPopconfirm>
              </template>
            </ElTableColumn>
          </ElTable>
        </ElCard>

        <div class="cart-footer">
          <ElCard shadow="never">
            <div class="footer-inner">
              <div class="footer-left">
                <ElCheckbox
                  :model-value="isAllSelected"
                  @change="toggleSelectAll"
                >
                  全选
                </ElCheckbox>
              </div>
              <div class="footer-right">
                <span class="footer-count">
                  已选 <strong>{{ selectedItems.length }}</strong> 种商品，共
                  <strong>{{ totalCount }}</strong> 件
                </span>
                <span class="footer-total-label">合计：</span>
                <span class="footer-total-price">¥{{ totalAmount.toFixed(2) }}</span>
                <ElButton
                  type="danger"
                  size="large"
                  :disabled="selectedItems.length === 0"
                  class="checkout-btn"
                  @click="handleCheckout"
                >
                  去结算
                </ElButton>
              </div>
            </div>
          </ElCard>
        </div>
      </template>

      <ElEmpty v-else description="购物车为空，快去逛逛吧" />
    </div>
  </Page>
</template>

<style scoped>
.cart-wrapper {
  max-width: 1100px;
}

.cart-table-card {
  margin-bottom: 16px;
}

.goods-cell {
  align-items: center;
  display: flex;
  gap: 12px;
}

.goods-image-box {
  align-items: center;
  background: var(--el-fill-color-lighter);
  border-radius: 6px;
  display: flex;
  flex-shrink: 0;
  height: 64px;
  justify-content: center;
  overflow: hidden;
  width: 64px;
}

.goods-img {
  height: 100%;
  object-fit: cover;
  width: 100%;
}

.goods-img-placeholder {
  color: var(--el-text-color-placeholder);
  font-size: 12px;
}

.goods-info {
  min-width: 0;
}

.goods-name {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-sku {
  margin-top: 4px;
}

.price-cell {
  color: var(--el-text-color-primary);
  font-weight: 500;
}

.subtotal-cell {
  color: var(--el-color-danger);
  font-weight: 600;
}

.cart-footer {
  position: sticky;
  bottom: 0;
  z-index: 10;
}

.footer-inner {
  align-items: center;
  display: flex;
  justify-content: space-between;
}

.footer-right {
  align-items: center;
  display: flex;
  gap: 8px;
}

.footer-count {
  color: var(--el-text-color-regular);
  font-size: 13px;
  margin-right: 12px;
}

.footer-total-label {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.footer-total-price {
  color: var(--el-color-danger);
  font-size: 22px;
  font-weight: 700;
  margin-right: 8px;
}

.checkout-btn {
  min-width: 120px;
}
</style>
