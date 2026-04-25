<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElImage,
  ElInputNumber,
  ElMessage,
  ElRate,
  ElScrollbar,
  ElSpace,
  ElTabPane,
  ElTabs,
  ElTag,
} from 'element-plus';

import { requestClient } from '#/api/request';

interface ProductItem {
  categoryIds: number[];
  createTime: string;
  description: string;
  id: number;
  name: string;
  price: number;
  skus?: ProductSku[];
  status: number;
  stock: number;
}

interface ProductSku {
  fileId?: number;
  fileName?: string;
  filePath?: string;
  fileType?: string;
  id: number;
  price: number;
  specName: string;
  stock: number;
}

interface MediaItem {
  filePath: string;
  fileType: string;
  id: number;
}

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const detail = ref<null | ProductItem>(null);
const activeMediaId = ref<null | number>(null);
const activeDetailTab = ref('reviews');
const buyCount = ref(1);
const selectedSkuId = ref<null | number>(null);
const mockReviewList = [
  { id: 1, user: '小明', score: 5, content: '包装很完整，发货速度快，实物和图片一致。', time: '2026-04-20' },
  { id: 2, user: 'Lina', score: 4, content: '视频附件展示清晰，功能符合预期，性价比不错。', time: '2026-04-21' },
  { id: 3, user: '阿哲', score: 5, content: '客服响应及时，商品细节做得很好，推荐购买。', time: '2026-04-22' },
];

const imageExtList = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.bmp'];
const videoExtList = ['.mp4', '.webm', '.ogg', '.mov', '.m4v'];

function normalizeFileUrl(rawPath?: string) {
  if (!rawPath) return '';
  if (/^https?:\/\//.test(rawPath)) return rawPath;
  if (rawPath.startsWith('/api/')) return rawPath;
  if (rawPath.startsWith('/upload/')) return `/api${rawPath}`;
  if (rawPath.startsWith('upload/')) return `/api/${rawPath}`;
  if (rawPath.startsWith('/')) return `/api${rawPath}`;
  return `/api/${rawPath}`;
}

function detectMediaKind(file: MediaItem) {
  const type = (file.fileType || '').toLowerCase();
  const path = (file.filePath || '').toLowerCase();
  if (type.startsWith('image/') || imageExtList.some((ext) => path.endsWith(ext))) {
    return 'image';
  }
  if (type.startsWith('video/') || videoExtList.some((ext) => path.endsWith(ext))) {
    return 'video';
  }
  return 'other';
}

const mediaFiles = computed(() =>
  (detail.value?.skus ?? [])
    .filter((sku): sku is ProductSku & { fileId: number; filePath: string } => !!sku.fileId && !!sku.filePath)
    .map((sku) => ({
      id: sku.fileId,
      mediaKind: detectMediaKind({
        filePath: sku.filePath,
        fileType: sku.fileType ?? '',
        id: sku.fileId,
      }),
      previewUrl: normalizeFileUrl(sku.filePath),
    }))
    .filter((file) => file.mediaKind !== 'other'),
);

const activeMedia = computed(() => {
  if (mediaFiles.value.length === 0) return null;
  if (activeMediaId.value == null) return mediaFiles.value[0] ?? null;
  return mediaFiles.value.find((item) => item.id === activeMediaId.value) ?? mediaFiles.value[0] ?? null;
});
const skuOptions = computed(() => detail.value?.skus ?? []);
const selectedSku = computed(
  () => skuOptions.value.find((item) => item.id === selectedSkuId.value) ?? skuOptions.value[0] ?? null,
);
const displayPrice = computed(() => selectedSku.value?.price ?? detail.value?.price ?? 0);

watch(selectedSku, (sku) => {
  if (!sku?.fileId) return;
  if (mediaFiles.value.some((item) => item.id === sku.fileId)) {
    activeMediaId.value = sku.fileId;
  }
});

async function loadDetail() {
  const id = Number(route.params.id);
  if (Number.isNaN(id) || id <= 0) {
    ElMessage.error('商品ID无效');
    return;
  }
  loading.value = true;
  try {
    const data = await requestClient.get<ProductItem>('/mall/product/detail', {
      params: { id },
    });
    detail.value = data;
    activeMediaId.value = mediaFiles.value[0]?.id ?? null;
    selectedSkuId.value = (data.skus ?? [])[0]?.id ?? null;
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.back();
}

onMounted(async () => {
  await loadDetail();
});
</script>

<template>
  <Page description="查看商品详细信息" title="商品详情">
    <ElCard shadow="never">
      <ElSpace wrap>
        <ElButton @click="goBack">返回上一级</ElButton>
        <ElTag type="info">商品ID：{{ detail?.id ?? '-' }}</ElTag>
      </ElSpace>
    </ElCard>

    <ElCard class="mt-4" shadow="never" v-loading="loading">
      <div v-if="detail" class="detail-layout">
        <section class="media-panel">
          <div class="media-stage">
            <ElImage
              v-if="activeMedia?.mediaKind === 'image'"
              :src="activeMedia.previewUrl"
              fit="contain"
              class="stage-image"
            />
            <video
              v-else-if="activeMedia?.mediaKind === 'video'"
              :src="activeMedia.previewUrl"
              controls
              class="stage-video"
            />
            <div v-else class="stage-empty">暂无可预览媒体</div>
          </div>
          <ElScrollbar v-if="mediaFiles.length > 0" class="thumb-scroll">
            <div class="thumb-list">
              <button
                v-for="file in mediaFiles"
                :key="file.id"
                class="thumb-item"
                :class="{ active: activeMedia?.id === file.id }"
                type="button"
                @click="activeMediaId = file.id"
              >
                <ElImage
                  v-if="file.mediaKind === 'image'"
                  :src="file.previewUrl"
                  fit="cover"
                  class="thumb-image"
                />
                <video
                  v-else
                  :src="file.previewUrl"
                  class="thumb-video"
                  muted
                  playsinline
                  preload="metadata"
                />
              </button>
            </div>
          </ElScrollbar>
        </section>

        <section class="info-panel">
          <h1 class="product-title">{{ detail.name }}</h1>
          <div class="price-line">
            <span class="price-label">活动价</span>
            <span class="price-value">￥{{ displayPrice }}</span>
          </div>
          <div class="meta-grid">
            <div class="meta-item">
              <span class="meta-key">库存</span>
              <span class="meta-value">{{ detail.stock }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-key">状态</span>
              <span class="meta-value">
                <ElTag :type="detail.status === 1 ? 'success' : 'info'">
                  {{ detail.status === 1 ? '上架' : '下架' }}
                </ElTag>
              </span>
            </div>
            <div class="meta-item">
              <span class="meta-key">分类</span>
              <span class="meta-value">{{ detail.categoryIds?.join(' / ') || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-key">创建时间</span>
              <span class="meta-value">{{ detail.createTime || '-' }}</span>
            </div>
          </div>
          <div class="desc-block">
            <div class="desc-title">商品卖点</div>
            <div class="desc-content">{{ detail.description || '暂无描述' }}</div>
          </div>

          <div class="choose-block">
            <div class="choose-title">选择商品规格</div>
            <div class="choose-options">
              <button
                v-for="sku in skuOptions"
                :key="sku.id"
                type="button"
                class="choose-item"
                :class="{ active: selectedSkuId === sku.id }"
                @click="selectedSkuId = sku.id"
              >
                {{ sku.specName || `SKU-${sku.id}` }}
              </button>
            </div>
            <div class="buy-line">
              <span class="buy-label">购买数量</span>
              <ElInputNumber v-model="buyCount" :max="selectedSku?.stock || detail.stock || 1" :min="1" />
            </div>
            <ElSpace wrap>
              <ElButton type="danger">立即购买</ElButton>
              <ElButton type="primary" plain>加入购物车</ElButton>
            </ElSpace>
          </div>
        </section>
      </div>
    </ElCard>

    <div class="bottom-layout mt-4" v-if="detail">
      <section class="bottom-left">
        <ElCard shadow="never">
          <ElTabs v-model="activeDetailTab">
            <ElTabPane label="用户评价" name="reviews">
              <ElScrollbar class="tab-scroll">
                <div class="review-list">
                  <div v-for="review in mockReviewList" :key="review.id" class="review-item">
                    <div class="review-head">
                      <span class="review-user">{{ review.user }}</span>
                      <ElRate :model-value="review.score" disabled />
                    </div>
                    <div class="review-content">{{ review.content }}</div>
                    <div class="review-time">{{ review.time }}</div>
                  </div>
                </div>
              </ElScrollbar>
            </ElTabPane>

            <ElTabPane label="商品详情" name="detail">
              <ElScrollbar class="tab-scroll">
                <div class="detail-rich-block">
                  <h3>商品参数</h3>
                  <p>商品ID：{{ detail.id }}</p>
                  <p>库存：{{ detail.stock }}</p>
                  <p>状态：{{ detail.status === 1 ? '上架' : '下架' }}</p>
                  <p>分类：{{ detail.categoryIds?.join(' / ') || '-' }}</p>
                  <p>创建时间：{{ detail.createTime || '-' }}</p>
                  <h3>商品说明</h3>
                  <p>{{ detail.description || '暂无说明' }}</p>
                </div>
              </ElScrollbar>
            </ElTabPane>

            <ElTabPane label="图文详情" name="rich">
              <ElScrollbar class="tab-scroll">
                <div class="rich-media-list">
                  <template v-for="file in mediaFiles" :key="`rich-${file.id}`">
                    <ElImage
                      v-if="file.mediaKind === 'image'"
                      :src="file.previewUrl"
                      fit="contain"
                      class="rich-image"
                    />
                    <video
                      v-else
                      :src="file.previewUrl"
                      controls
                      class="rich-video"
                    />
                  </template>
                  <div v-if="mediaFiles.length === 0" class="stage-empty">暂无图文内容</div>
                </div>
              </ElScrollbar>
            </ElTabPane>
          </ElTabs>
        </ElCard>
      </section>

      <aside class="bottom-right">
        <ElCard shadow="never" class="sticky-card">
          <h3 class="side-title">商品选择</h3>
          <div class="side-row">
            <span>已选规格：</span>
            <ElTag type="primary">{{ selectedSku?.specName || '-' }}</ElTag>
          </div>
          <div class="side-row">
            <span>购买数量：</span>
            <span>{{ buyCount }}</span>
          </div>
          <div class="side-row">
            <span>预计金额：</span>
            <span class="side-price">￥{{ (displayPrice * buyCount).toFixed(2) }}</span>
          </div>
        </ElCard>
      </aside>
    </div>
  </Page>
</template>

<style scoped>
.detail-layout {
  display: grid;
  gap: 24px;
  grid-template-columns: minmax(360px, 45%) minmax(420px, 1fr);
}

.media-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.media-stage {
  align-items: center;
  background: var(--el-fill-color-lighter);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  display: flex;
  height: 440px;
  justify-content: center;
  overflow: hidden;
}

.stage-image,
.stage-video {
  height: 100%;
  width: 100%;
}

.stage-video {
  object-fit: contain;
}

.stage-empty {
  color: var(--el-text-color-secondary);
}

.thumb-scroll {
  width: 100%;
}

.thumb-list {
  display: flex;
  gap: 10px;
  padding-bottom: 4px;
}

.thumb-item {
  background: #fff;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  height: 72px;
  overflow: hidden;
  padding: 0;
  width: 72px;
}

.thumb-item.active {
  border-color: var(--el-color-primary);
}

.thumb-image {
  height: 100%;
  width: 100%;
}

.thumb-video {
  background: #000;
  display: block;
  height: 100%;
  object-fit: cover;
  pointer-events: none;
  width: 100%;
}

.product-title {
  color: var(--el-text-color-primary);
  font-size: 24px;
  font-weight: 600;
  line-height: 1.4;
  margin: 0 0 18px;
}

.price-line {
  align-items: center;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  padding: 14px 16px;
}

.price-label {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.price-value {
  color: var(--el-color-danger);
  font-size: 30px;
  font-weight: 700;
}

.meta-grid {
  border-bottom: 1px solid var(--el-border-color-light);
  border-top: 1px solid var(--el-border-color-light);
  display: grid;
  grid-template-columns: 1fr 1fr;
  row-gap: 14px;
  padding: 16px 0;
}

.meta-item {
  align-items: center;
  display: flex;
  min-height: 32px;
}

.meta-key {
  color: var(--el-text-color-secondary);
  width: 88px;
}

.meta-value {
  color: var(--el-text-color-primary);
  flex: 1;
  word-break: break-all;
}

.desc-block {
  margin-top: 16px;
}

.desc-title {
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
}

.desc-content {
  color: var(--el-text-color-regular);
  line-height: 1.8;
  white-space: pre-wrap;
}

.choose-block {
  border-top: 1px solid var(--el-border-color-light);
  margin-top: 18px;
  padding-top: 16px;
}

.choose-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 10px;
}

.choose-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.choose-item {
  background: #fff;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  padding: 6px 12px;
}

.choose-item.active {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.buy-line {
  align-items: center;
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.buy-label {
  color: var(--el-text-color-secondary);
  min-width: 72px;
}

.bottom-layout {
  display: grid;
  gap: 16px;
  grid-template-columns: minmax(0, 1fr) 300px;
}

.tab-scroll {
  max-height: 520px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-item {
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  padding: 12px;
}

.review-head {
  align-items: center;
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.review-user {
  font-weight: 600;
}

.review-content {
  line-height: 1.7;
}

.review-time {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  margin-top: 6px;
}

.detail-rich-block h3 {
  margin: 8px 0;
}

.detail-rich-block p {
  line-height: 1.8;
  margin: 0 0 6px;
}

.rich-media-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rich-image,
.rich-video {
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  width: 100%;
}

.rich-image {
  min-height: 260px;
}

.side-title {
  font-size: 16px;
  margin: 0 0 12px;
}

.side-row {
  align-items: center;
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.side-price {
  color: var(--el-color-danger);
  font-size: 20px;
  font-weight: 700;
}

.sticky-card {
  position: sticky;
  top: 12px;
}

@media (max-width: 1080px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }

  .media-stage {
    height: 320px;
  }

  .bottom-layout {
    grid-template-columns: 1fr;
  }

  .tab-scroll {
    max-height: none;
  }

  .sticky-card {
    position: static;
  }
}
</style>
