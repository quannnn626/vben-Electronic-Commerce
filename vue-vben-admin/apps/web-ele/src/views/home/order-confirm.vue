<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDivider,
  ElDialog,
  ElEmpty,
  ElIcon,
  ElInput,
  ElMessage,
  ElRadio,
  ElRadioGroup,
  ElScrollbar,
  ElSpace,
} from 'element-plus';

import { requestClient } from '#/api/request';

/* ---------- 类型定义 ---------- */

interface AddressItem {
  id: number;
  receiverName: string;
  receiverPhone: string;
  province?: string;
  city?: string;
  district?: string;
  detailAddress: string;
  isDefault: number;
}

interface OrderGoodsItem {
  productId: number;
  productName: string;
  productImage?: string;
  productImageType?: string;
  skuId?: number;
  skuSpecName?: string;
  price: number;
  quantity: number;
  totalPrice: number;
}

/* ---------- 路由 ---------- */

const route = useRoute();
const router = useRouter();

/* ---------- 加载状态 ---------- */

const loading = ref(false);
const submitLoading = ref(false);

/* ---------- 收货地址 ---------- */

const addressList = ref<AddressItem[]>([]);
const selectedAddressId = ref<null | number>(null);
const addressDialogVisible = ref(false);
const tempAddressId = ref<null | number>(null);

const selectedAddress = computed(
  () =>
    addressList.value.find((a) => a.id === selectedAddressId.value) ??
    addressList.value[0] ??
    null,
);

const fullAddress = computed(() => {
  const addr = selectedAddress.value;
  if (!addr) return '';
  return [addr.province, addr.city, addr.district, addr.detailAddress]
    .filter(Boolean)
    .join('');
});

async function loadAddresses() {
  try {
    const res = await requestClient.get<AddressItem[]>('/mall/address/list');
    addressList.value = Array.isArray(res) ? res : [];
    const defaultAddr = addressList.value.find((a) => a.isDefault === 1);
    selectedAddressId.value = defaultAddr?.id ?? addressList.value[0]?.id ?? null;
  } catch {
    addressList.value = [];
  }
}

function openAddressDialog() {
  tempAddressId.value = selectedAddressId.value;
  addressDialogVisible.value = true;
}

function confirmAddressSelect() {
  if (tempAddressId.value !== null) {
    selectedAddressId.value = tempAddressId.value;
  }
  addressDialogVisible.value = false;
}

/* ---------- 商品清单 ---------- */

const goodsList = ref<OrderGoodsItem[]>([]);

interface QueryGoods {
  productId: number;
  productName: string;
  productImage?: string;
  productImageType?: string;
  skuId?: number;
  skuSpecName?: string;
  price: number;
  quantity: number;
}

function initGoodsFromQuery() {
  const raw = route.query.goods;
  if (!raw) return;
  try {
    const list: QueryGoods[] = JSON.parse(decodeURIComponent(raw as string));
    goodsList.value = list.map((item) => ({
      productId: Number(item.productId),
      productName: item.productName,
      productImage: item.productImage,
      productImageType: item.productImageType,
      skuId: item.skuId ? Number(item.skuId) : undefined,
      skuSpecName: item.skuSpecName,
      price: Number(item.price),
      quantity: Number(item.quantity),
      totalPrice: Number(item.price) * Number(item.quantity),
    }));
  } catch {
    ElMessage.error('商品信息解析失败');
  }
}

/* ---------- 支付方式 ---------- */

const paymentMethod = ref('alipay');
const paymentOptions = [
  { label: '支付宝', value: 'alipay' },
  { label: '微信支付', value: 'wechat' },
];

/* ---------- 金额汇总 ---------- */

const totalAmount = computed(() =>
  goodsList.value.reduce((sum, g) => sum + g.totalPrice, 0),
);

const itemCount = computed(() =>
  goodsList.value.reduce((sum, g) => sum + g.quantity, 0),
);

/* ---------- 备注 ---------- */

const remark = ref('');

/* ---------- 提交订单 ---------- */

async function submitOrder() {
  if (goodsList.value.length === 0) {
    ElMessage.warning('没有可提交的商品');
    return;
  }
  if (!selectedAddress.value) {
    ElMessage.warning('请先添加收货地址');
    return;
  }
  submitLoading.value = true;
  try {
    const payload = {
      addressId: selectedAddress.value.id,
      items: goodsList.value.map((g) => ({
        productId: g.productId,
        skuId: g.skuId ?? null,
        productName: g.productName,
        productImage: g.productImage ?? null,
        skuSpecName: g.skuSpecName ?? null,
        price: g.price,
        quantity: g.quantity,
        totalPrice: g.totalPrice,
      })),
      paymentMethod: paymentMethod.value,
      remark: remark.value || null,
    };
    const res = await requestClient.post<{ orderNo: string; id: number }>('/mall/order/create', payload);
    ElMessage.success('订单创建成功');
    if (res?.orderNo) {
      router.replace({
        path: `/order/confirm/${res.id}`,
        query: { success: '1', orderNo: res.orderNo },
      });
    } else {
      router.back();
    }
  } catch (e: any) {
    ElMessage.error(e?.message ?? '下单失败，请重试');
  } finally {
    submitLoading.value = false;
  }
}

/* ---------- 返回 ---------- */

function goBack() {
  router.back();
}

/* ---------- 跳转地址管理 ---------- */

function goAddressManage() {
  router.push('/profile');
}

/* ---------- 初始化 ---------- */

onMounted(async () => {
  loading.value = true;
  try {
    await Promise.all([loadAddresses(), initGoodsFromQuery()]);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <Page description="确认订单信息并提交" title="确认订单">
    <!-- 成功提示 -->
    <ElCard v-if="route.query.success === '1'" shadow="never" class="success-card">
      <div class="success-banner">
        <ElIcon :size="48" color="#67C23A">✓</ElIcon>
        <div>
          <h2 class="success-title">下单成功</h2>
          <p class="success-sub">
            订单编号：{{ route.query.orderNo ?? '-' }}，请尽快完成支付
          </p>
        </div>
      </div>
    </ElCard>

    <!-- 左右布局 -->
    <div class="confirm-layout">
      <!-- 左侧主内容 -->
      <div class="confirm-main">
        <!-- 收货地址 -->
        <ElCard shadow="never">
          <template #header>
            <div class="section-header">
              <span class="section-title">
                <span class="step-num">1</span> 收货地址
              </span>
              <ElButton link type="primary" @click="goAddressManage">管理收货地址</ElButton>
            </div>
          </template>
          <!-- 有地址：显示一个选中的地址卡片 -->
          <div v-if="selectedAddress" class="current-address" @click="openAddressDialog">
            <div class="current-address-main">
              <div class="current-address-top">
                <span class="current-name">{{ selectedAddress.receiverName }}</span>
                <span class="current-phone">{{ selectedAddress.receiverPhone }}</span>
                <span v-if="selectedAddress.isDefault === 1" class="address-default-tag">默认</span>
              </div>
              <div class="current-address-text">{{ fullAddress }}</div>
            </div>
            <ElButton type="primary" plain size="small" class="change-btn">更换</ElButton>
          </div>
          <!-- 无地址 -->
          <div v-else class="no-address-box" @click="goAddressManage">
            <ElEmpty description="暂无收货地址，请先添加">
              <ElButton type="primary">去添加</ElButton>
            </ElEmpty>
          </div>
        </ElCard>

        <!-- 地址选择弹窗 -->
        <ElDialog
          v-model="addressDialogVisible"
          title="选择收货地址"
          width="560px"
          destroy-on-close
          append-to-body
        >
          <ElScrollbar max-height="400px">
            <ElRadioGroup v-model="tempAddressId" class="address-radio-group">
              <div
                v-for="addr in addressList"
                :key="addr.id"
                class="address-card"
                :class="{ active: tempAddressId === addr.id }"
                @click="tempAddressId = addr.id"
              >
                <ElRadio :value="addr.id" class="address-radio" />
                <div class="address-info">
                  <div class="address-top">
                    <span class="address-name">{{ addr.receiverName }}</span>
                    <span class="address-phone">{{ addr.receiverPhone }}</span>
                    <span v-if="addr.isDefault === 1" class="address-default-tag">默认</span>
                  </div>
                  <div class="address-text">
                    {{ [addr.province, addr.city, addr.district, addr.detailAddress].filter(Boolean).join('') }}
                  </div>
                </div>
              </div>
            </ElRadioGroup>
          </ElScrollbar>
          <template #footer>
            <ElButton @click="addressDialogVisible = false">取消</ElButton>
            <ElButton type="primary" @click="confirmAddressSelect">确定</ElButton>
          </template>
        </ElDialog>

        <!-- 商品清单 -->
        <ElCard class="mt-4" shadow="never">
          <template #header>
            <span class="section-title">
              <span class="step-num">2</span> 商品清单
            </span>
          </template>
          <div v-if="goodsList.length > 0" class="goods-list">
            <div v-for="(goods, idx) in goodsList" :key="idx" class="goods-item">
              <div v-if="goods.productImage" class="goods-image-box">
                <img
                  v-if="goods.productImageType !== 'video'"
                  :src="goods.productImage"
                  alt=""
                  class="goods-img"
                />
                <video
                  v-else
                  :src="goods.productImage"
                  class="goods-video-cover"
                  muted
                  playsinline
                  preload="metadata"
                />
              </div>
              <div v-else class="goods-image-box no-img">
                <span>{{ goods.productName?.charAt(0) ?? '?' }}</span>
              </div>
              <div class="goods-info">
                <div class="goods-name">{{ goods.productName }}</div>
                <div v-if="goods.skuSpecName" class="goods-spec">{{ goods.skuSpecName }}</div>
              </div>
              <div class="goods-price-col">
                <div class="goods-unit-price">¥{{ goods.price.toFixed(2) }}</div>
                <div class="goods-qty">×{{ goods.quantity }}</div>
              </div>
              <div class="goods-subtotal">¥{{ goods.totalPrice.toFixed(2) }}</div>
            </div>
          </div>
          <ElEmpty v-else description="没有商品信息" />
        </ElCard>

        <!-- 支付方式 -->
        <ElCard class="mt-4" shadow="never">
          <template #header>
            <span class="section-title">
              <span class="step-num">3</span> 支付方式
            </span>
          </template>
          <div class="payment-options">
            <div
              v-for="opt in paymentOptions"
              :key="opt.value"
              class="payment-item"
              :class="{ active: paymentMethod === opt.value }"
              @click="paymentMethod = opt.value"
            >
              <ElRadio :value="opt.value" v-model="paymentMethod" class="payment-radio" />
              <span class="payment-label">{{ opt.label }}</span>
            </div>
          </div>
        </ElCard>
      </div>

      <!-- 右侧摘要栏 -->
      <aside class="confirm-sidebar">
        <ElCard shadow="never" class="summary-card">
          <h3 class="summary-title">订单摘要</h3>

          <div class="summary-row">
            <span class="summary-label">商品件数</span>
            <span class="summary-value">{{ itemCount }} 件</span>
          </div>

          <div class="summary-row">
            <span class="summary-label">商品金额</span>
            <span class="summary-value">¥{{ totalAmount.toFixed(2) }}</span>
          </div>

          <div class="summary-row">
            <span class="summary-label">运费</span>
            <span class="summary-value free-shipping">免运费</span>
          </div>

          <ElDivider />

          <div class="summary-row summary-total-row">
            <span class="summary-label">应付总额</span>
            <span class="summary-total-price">¥{{ totalAmount.toFixed(2) }}</span>
          </div>

          <ElDivider />

          <div class="summary-remark">
            <div class="remark-label">订单备注</div>
            <ElInput
              v-model="remark"
              :rows="2"
              maxlength="200"
              placeholder="选填，建议与商家沟通的内容"
              show-word-limit
              type="textarea"
            />
          </div>

          <div class="summary-actions">
            <ElButton class="action-btn" @click="goBack">返回</ElButton>
            <ElButton
              :disabled="route.query.success === '1'"
              :loading="submitLoading"
              class="action-btn action-submit"
              type="danger"
              @click="submitOrder"
            >
              提交订单
            </ElButton>
          </div>
        </ElCard>
      </aside>
    </div>
  </Page>
</template>

<style scoped>
.success-card {
  margin-bottom: 20px;
}

.success-banner {
  align-items: center;
  display: flex;
  gap: 16px;
}

.success-title {
  color: var(--el-color-success);
  font-size: 20px;
  margin: 0 0 4px;
}

.success-sub {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin: 0;
}

/* 左右布局 */
.confirm-layout {
  display: grid;
  gap: 20px;
  grid-template-columns: minmax(0, 1fr) 340px;
  align-items: start;
}

.mt-4 {
  margin-top: 16px;
}

/* 步骤编号 */
.step-num {
  align-items: center;
  background: var(--el-color-primary);
  border-radius: 50%;
  color: #fff;
  display: inline-flex;
  font-size: 13px;
  font-weight: 600;
  height: 22px;
  justify-content: center;
  margin-right: 8px;
  width: 22px;
}

/* 区域头部 */
.section-header {
  align-items: center;
  display: flex;
  justify-content: space-between;
}

.section-title {
  align-items: center;
  display: flex;
  font-size: 16px;
  font-weight: 600;
}

/* 当前选中的地址卡片 */
.current-address {
  align-items: center;
  border: 1px solid var(--el-border-color-light);
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  gap: 16px;
  padding: 16px 20px;
  transition: all 0.2s;
}

.current-address:hover {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.current-address-main {
  flex: 1;
  min-width: 0;
}

.current-address-top {
  align-items: center;
  display: flex;
  gap: 12px;
  margin-bottom: 6px;
}

.current-name {
  font-size: 16px;
  font-weight: 600;
}

.current-phone {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.current-address-text {
  color: var(--el-text-color-regular);
  font-size: 14px;
  line-height: 1.6;
}

.change-btn {
  flex-shrink: 0;
}

.no-address-box {
  cursor: pointer;
}

/* 地址列表（弹窗内） */
.address-radio-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.address-card {
  align-items: flex-start;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  transition: all 0.2s;
}

.address-card:hover {
  border-color: var(--el-border-color-hover);
}

.address-card.active {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.address-radio {
  margin-top: 2px;
}

.address-info {
  flex: 1;
  min-width: 0;
}

.address-top {
  align-items: center;
  display: flex;
  gap: 12px;
  margin-bottom: 6px;
}

.address-name {
  font-size: 15px;
  font-weight: 600;
}

.address-phone {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.address-default-tag {
  background: var(--el-color-primary-light-8);
  border-radius: 4px;
  color: var(--el-color-primary);
  font-size: 12px;
  padding: 1px 6px;
}

.address-text {
  color: var(--el-text-color-regular);
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
}

/* 商品列表 */
.goods-list {
  display: flex;
  flex-direction: column;
}

.goods-item {
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-lighter);
  display: flex;
  gap: 16px;
  padding: 16px 0;
}

.goods-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.goods-item:first-child {
  padding-top: 0;
}

.goods-image-box {
  flex-shrink: 0;
  align-items: center;
  background: var(--el-fill-color-lighter);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  display: flex;
  height: 80px;
  justify-content: center;
  overflow: hidden;
  width: 80px;
}

.goods-image-box.no-img {
  color: var(--el-text-color-secondary);
  font-size: 28px;
  font-weight: 600;
}

.goods-img {
  height: 100%;
  object-fit: cover;
  width: 100%;
}

.goods-video-cover {
  display: block;
  height: 100%;
  object-fit: cover;
  pointer-events: none;
  width: 100%;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-spec {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.goods-price-col {
  flex-shrink: 0;
  text-align: right;
  min-width: 80px;
}

.goods-unit-price {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.goods-qty {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  margin-top: 2px;
}

.goods-subtotal {
  color: var(--el-text-color-primary);
  flex-shrink: 0;
  font-size: 16px;
  font-weight: 600;
  min-width: 90px;
  text-align: right;
}

/* 支付方式 */
.payment-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.payment-item {
  align-items: center;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  gap: 8px;
  padding: 12px 20px;
  transition: all 0.2s;
}

.payment-item:hover {
  border-color: var(--el-border-color-hover);
}

.payment-item.active {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.payment-label {
  font-size: 15px;
  font-weight: 500;
}

/* 右侧摘要 */
.summary-card {
  position: sticky;
  top: 12px;
}

.summary-title {
  font-size: 17px;
  font-weight: 600;
  margin: 0 0 16px;
}

.summary-row {
  align-items: center;
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
}

.summary-label {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.summary-value {
  font-size: 14px;
  font-weight: 500;
}

.free-shipping {
  color: var(--el-color-success);
}

.summary-total-row {
  padding: 8px 0;
}

.summary-total-price {
  color: var(--el-color-danger);
  font-size: 22px;
  font-weight: 700;
}

.summary-remark {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.remark-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-regular);
}

.summary-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.action-btn {
  flex: 1;
}

.action-submit {
  flex: 2;
}

/* 响应式 */
@media (max-width: 960px) {
  .confirm-layout {
    grid-template-columns: 1fr;
  }

  .summary-card {
    position: static;
  }
}
</style>
