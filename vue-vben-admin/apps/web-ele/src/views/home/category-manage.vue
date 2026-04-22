<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElMessageBox,
  ElOption,
  ElSelect,
  ElSpace,
  ElSwitch,
  ElTable,
  ElTag,
} from 'element-plus';
import { requestClient } from '#/api/request';

type StatusFilter = 'all' | 'disabled' | 'enabled';

interface CategoryNode {
  children?: CategoryNode[];
  code: string;
  createTime: string;
  id: number;
  level: 1 | 2 | 3;
  name: string;
  parentId: null | number;
  remark: string;
  sort: number;
  status: boolean;
}

interface ParentOption {
  id: number;
  label: string;
}
type CategoryAction = 'create' | 'create-child' | 'delete' | 'edit';

const tableLoading = ref(false);
const keyword = ref('');
const statusFilter = ref<StatusFilter>('all');

const dialogVisible = ref(false);
const submitLoading = ref(false);
const isEdit = ref(false);
const currentEditId = ref<null | number>(null);
const lockLevelByNode = ref(false);
const currentAction = ref<'create' | 'create-child' | 'edit'>('create');
const categoryApiBase = '/mall/product-category';
const treeApiPath = `${categoryApiBase}/tree`;

const treeData = ref<CategoryNode[]>([]);

const formModel = reactive({
  code: '',
  level: 1 as 1 | 2 | 3,
  name: '',
  parentId: null as null | number,
  remark: '',
  sort: 1,
  status: true,
});

function walkNodes(list: CategoryNode[], cb: (node: CategoryNode) => void) {
  for (const node of list) {
    cb(node);
    if (node.children?.length) {
      walkNodes(node.children, cb);
    }
  }
}

function findNodeAndParentById(
  list: CategoryNode[],
  id: number,
  parent: CategoryNode | null = null,
): { node: CategoryNode | null; parent: CategoryNode | null } {
  for (const node of list) {
    if (node.id === id) {
      return { node, parent };
    }
    if (node.children?.length) {
      const found = findNodeAndParentById(node.children, id, node);
      if (found.node) {
        return found;
      }
    }
  }
  return { node: null, parent: null };
}

function cloneAndFilterTree(nodes: CategoryNode[], key: string, status: StatusFilter): CategoryNode[] {
  const result: CategoryNode[] = [];
  for (const node of nodes) {
    const children = node.children?.length ? cloneAndFilterTree(node.children, key, status) : [];
    const matchKeyword =
      key.length === 0 ||
      node.name.toLowerCase().includes(key) ||
      node.code.toLowerCase().includes(key);
    const matchStatus =
      status === 'all' ||
      (status === 'enabled' && node.status) ||
      (status === 'disabled' && !node.status);
    if (matchKeyword && matchStatus) {
      result.push({ ...node, children });
    } else if (children.length > 0) {
      result.push({ ...node, children });
    }
  }
  return result;
}

function normalizeTree(nodes: CategoryNode[], level: 1 | 2 | 3 = 1): CategoryNode[] {
  return nodes.map((node) => {
    const nextLevel = (Math.min(level + 1, 3) as 2 | 3);
    const children = node.children?.length ? normalizeTree(node.children, nextLevel) : [];
    return {
      ...node,
      children,
      level,
      parentId: node.parentId ?? null,
    };
  });
}

async function loadCategoryTree() {
  tableLoading.value = true;
  try {
    const data = await requestClient.get<CategoryNode[]>(treeApiPath);
    treeData.value = normalizeTree(Array.isArray(data) ? data : []);
  } catch {
    treeData.value = [];
    ElMessage.error('加载类目树失败，请检查接口地址或后端服务');
  } finally {
    tableLoading.value = false;
  }
}

const tableData = computed(() => {
  const key = keyword.value.trim().toLowerCase();
  return cloneAndFilterTree(treeData.value, key, statusFilter.value);
});

const parentOptions = computed<ParentOption[]>(() => {
  if (formModel.level === 1) {
    return [];
  }
  const targetLevel = (formModel.level - 1) as 1 | 2;
  const options: ParentOption[] = [];
  walkNodes(treeData.value, (node) => {
    if (node.level === targetLevel) {
      options.push({
        id: node.id,
        label: `${node.name}（L${node.level}）`,
      });
    }
  });
  return options;
});

function resetForm() {
  formModel.name = '';
  formModel.code = '';
  formModel.level = 1;
  formModel.parentId = null;
  formModel.sort = 1;
  formModel.status = true;
  formModel.remark = '';
}

function handleSearch() {
  tableLoading.value = true;
  setTimeout(() => {
    tableLoading.value = false;
  }, 200);
}

function handleReset() {
  keyword.value = '';
  statusFilter.value = 'all';
}

function openDialog(action: CategoryAction, row?: CategoryNode) {
  if (action === 'delete') {
    return;
  }
  resetForm();
  currentEditId.value = null;
  lockLevelByNode.value = false;

  if (action === 'create') {
    isEdit.value = false;
    currentAction.value = 'create';
    dialogVisible.value = true;
    return;
  }
  if (!row) {
    return;
  }
  if (action === 'create-child') {
    if (row.level >= 3) {
      ElMessage.warning('三级类目不能继续新增下级');
      return;
    }
    isEdit.value = false;
    currentAction.value = 'create-child';
    lockLevelByNode.value = true;
    formModel.level = (row.level + 1) as 2 | 3;
    formModel.parentId = row.id;
    dialogVisible.value = true;
    return;
  }
  isEdit.value = true;
  currentAction.value = 'edit';
  lockLevelByNode.value = true;
  currentEditId.value = row.id;
  formModel.name = row.name;
  formModel.code = row.code;
  formModel.level = row.level;
  formModel.parentId = row.parentId;
  formModel.sort = row.sort;
  formModel.status = row.status;
  formModel.remark = row.remark;
  dialogVisible.value = true;
}

function handleLevelChange() {
  if (formModel.level === 1) {
    formModel.parentId = null;
    return;
  }
  const valid = parentOptions.value.some((item) => item.id === formModel.parentId);
  if (!valid) {
    formModel.parentId = null;
  }
}

function validateForm(): boolean {
  if (!formModel.name.trim()) {
    ElMessage.warning('请输入类目名称');
    return false;
  }
  if (!formModel.code.trim()) {
    ElMessage.warning('请输入类目标识');
    return false;
  }
  if (formModel.level > 1 && formModel.parentId === null) {
    ElMessage.warning('二级和三级类目必须选择父类目');
    return false;
  }
  if (formModel.level === 3 && !isEdit.value && formModel.parentId !== null) {
    const found = findNodeAndParentById(treeData.value, formModel.parentId);
    if (found.node?.level !== 2) {
      ElMessage.warning('三级类目只能挂在二级类目下');
      return false;
    }
  }
  return true;
}

function buildCategoryPayload() {
  return {
    code: formModel.code.trim(),
    id: currentEditId.value,
    level: formModel.level,
    name: formModel.name.trim(),
    parentId: formModel.level === 1 ? null : formModel.parentId,
    remark: formModel.remark.trim(),
    sort: formModel.sort,
    status: formModel.status,
  };
}

async function submitCategory() {
  if (!validateForm()) {
    return;
  }
  const payload = buildCategoryPayload();
  submitLoading.value = true;
  try {
    if (isEdit.value && currentEditId.value !== null) {
      await requestClient.put(categoryApiBase, {
        ...payload,
        id: currentEditId.value,
      });
      ElMessage.success('类目修改成功');
    } else {
      await requestClient.post(categoryApiBase, {
        code: payload.code,
        level: payload.level,
        name: payload.name,
        parentId: payload.parentId,
        remark: payload.remark,
        sort: payload.sort,
        status: payload.status,
      });
      ElMessage.success('类目新增成功');
    }
    dialogVisible.value = false;
    await loadCategoryTree();
  } finally {
    submitLoading.value = false;
  }
}

async function removeCategory(row: CategoryNode, action: CategoryAction = 'delete') {
  if (action !== 'delete') {
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确认删除类目「${row.name}」吗？该节点下的子类目也会被删除。`,
      '删除确认',
      { confirmButtonText: '确认删除', type: 'warning' },
    );
    await requestClient.delete(categoryApiBase, { data: { id: row.id } });
    ElMessage.success('删除成功');
    await loadCategoryTree();
  } catch {
    // 用户取消删除 或 请求失败（错误提示由拦截器处理）
  }
}

onMounted(() => {
  loadCategoryTree();
});
</script>

<template>
  <Page description="树形类目结构管理（最多三级）" title="商品类目管理">
    <ElCard shadow="never">
      <ElForm inline>
        <ElFormItem label="关键字">
          <ElInput
            v-model="keyword"
            clearable
            placeholder="请输入类目名称/类目标识"
            style="width: 220px"
          />
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSelect v-model="statusFilter" style="width: 140px">
            <ElOption label="全部" value="all" />
            <ElOption label="启用" value="enabled" />
            <ElOption label="禁用" value="disabled" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem>
          <ElSpace>
            <ElButton type="primary" @click="handleSearch">查询</ElButton>
            <ElButton @click="handleReset">重置</ElButton>
            <ElButton type="success" @click="openDialog('create')">新增类目</ElButton>
          </ElSpace>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="mt-4" shadow="never">
      <ElTable
        :data="tableData"
        :tree-props="{ children: 'children' }"
        border
        row-key="id"
        v-loading="tableLoading"
      >
        <ElTable.TableColumn label="类目名称" min-width="200" prop="name" />
        <ElTable.TableColumn label="层级" min-width="90">
          <template #default="{ row }">
            <ElTag>{{ `L${row.level}` }}</ElTag>
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="类目标识" min-width="140" prop="code" />
        <ElTable.TableColumn label="排序" min-width="90" prop="sort" />
        <ElTable.TableColumn label="状态" min-width="100">
          <template #default="{ row }">
            <ElTag :type="row.status ? 'success' : 'info'">
              {{ row.status ? '启用' : '禁用' }}
            </ElTag>
          </template>
        </ElTable.TableColumn>
        <ElTable.TableColumn label="备注" min-width="220" prop="remark" show-overflow-tooltip />
        <ElTable.TableColumn label="创建时间" min-width="180" prop="createTime" />
        <ElTable.TableColumn fixed="right" label="操作" width="230">
          <template #default="{ row }">
            <ElSpace>
              <ElButton
                :disabled="row.level >= 3"
                link
                type="success"
                @click="openDialog('create-child', row)"
              >
                新增下级
              </ElButton>
              <ElButton link type="primary" @click="openDialog('edit', row)">编辑</ElButton>
              <ElButton link type="danger" @click="removeCategory(row)">删除</ElButton>
            </ElSpace>
          </template>
        </ElTable.TableColumn>
      </ElTable>
    </ElCard>

    <ElDialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品类目' : '新增商品类目'"
      destroy-on-close
      width="560px"
    >
      <ElForm label-width="100px">
        <ElFormItem label="类目层级" required>
          <ElSelect
            v-model="formModel.level"
            :disabled="isEdit || lockLevelByNode"
            style="width: 180px"
            @change="handleLevelChange"
          >
            <ElOption :value="1" label="一级类目" />
            <ElOption :value="2" label="二级类目" />
            <ElOption :value="3" label="三级类目" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem v-if="formModel.level > 1" label="父类目" required>
          <ElSelect v-model="formModel.parentId" placeholder="请选择父类目" style="width: 320px">
            <ElOption
              v-for="item in parentOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="类目名称" required>
          <ElInput v-model="formModel.name" maxlength="32" placeholder="请输入类目名称" />
        </ElFormItem>
        <ElFormItem label="类目标识" required>
          <ElInput
            v-model="formModel.code"
            maxlength="32"
            placeholder="请输入唯一标识，如：ELECTRONICS"
          />
        </ElFormItem>
        <ElFormItem label="排序">
          <ElInputNumber v-model="formModel.sort" :min="1" :step="1" />
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSwitch v-model="formModel.status" />
        </ElFormItem>
        <ElFormItem label="备注">
          <ElInput
            v-model="formModel.remark"
            :rows="3"
            maxlength="120"
            placeholder="请输入备注信息"
            show-word-limit
            type="textarea"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElSpace>
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton :loading="submitLoading" type="primary" @click="submitCategory">
            {{ isEdit ? '保存' : '创建' }}
          </ElButton>
        </ElSpace>
      </template>
    </ElDialog>
  </Page>
</template>
