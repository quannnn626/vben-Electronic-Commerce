package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallProductCategory;
import com.boot.vuevbenadminboot.mapper.MallProductCategoryMapper;
import com.boot.vuevbenadminboot.service.MallProductCategoryService;
import com.boot.vuevbenadminboot.web.dto.CategorySaveRequest;
import com.boot.vuevbenadminboot.web.dto.CategoryTreeNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author quannnn
* @description 针对表【mall_product_category(商品类目表)】的数据库操作Service实现
* @createDate 2026-04-21 17:48:47
*/
@Service
public class MallProductCategoryServiceImpl extends ServiceImpl<MallProductCategoryMapper, MallProductCategory>
    implements MallProductCategoryService{

    @Override
    public List<CategoryTreeNode> getCategoryTree() {
        List<MallProductCategory> all = this.list(
                new LambdaQueryWrapper<MallProductCategory>()
                        .eq(MallProductCategory::getDeleted, 0)
                        .orderByAsc(MallProductCategory::getLevel)
                        .orderByAsc(MallProductCategory::getSort)
                        .orderByAsc(MallProductCategory::getId)
        );
        return buildTree(all);
    }

    @Override
    public Long createCategory(CategorySaveRequest req) {
        String error = validateRequest(req, false);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        MallProductCategory entity = new MallProductCategory();
        entity.setName(req.getName().trim());
        entity.setParentId(req.getLevel() == 1 ? 0L : req.getParentId());
        entity.setLevel(req.getLevel());
        entity.setSort(req.getSort());
        entity.setStatus(req.getStatus() ? 1 : 0);
        entity.setIcon(req.getCode() == null ? null : req.getCode().trim());
        entity.setDeleted(0);
        this.save(entity);
        return entity.getId();
    }

    @Override
    public boolean updateCategory(CategorySaveRequest req) {
        String error = validateRequest(req, true);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        MallProductCategory old = this.getById(req.getId());
        if (old == null || Objects.equals(old.getDeleted(), 1)) {
            throw new IllegalArgumentException("类目不存在");
        }
        old.setName(req.getName().trim());
        old.setSort(req.getSort());
        old.setStatus(req.getStatus() ? 1 : 0);
        old.setIcon(req.getCode() == null ? null : req.getCode().trim());
        return this.updateById(old);
    }

    @Override
    public boolean deleteCategory(CategorySaveRequest req) {
        if (req == null || req.getId() == null) {
            throw new IllegalArgumentException("类目ID不能为空");
        }
        MallProductCategory root = this.getById(req.getId());
        if (root == null || Objects.equals(root.getDeleted(), 1)) {
            throw new IllegalArgumentException("类目不存在");
        }
        List<MallProductCategory> all = this.list(
                new LambdaQueryWrapper<MallProductCategory>().eq(MallProductCategory::getDeleted, 0)
        );
        Set<Long> toDeleteIds = collectSubtreeIds(req.getId(), all);
        if (toDeleteIds.isEmpty()) {
            toDeleteIds = Set.of(req.getId());
        }
        return this.removeByIds(toDeleteIds);
    }

    private String validateRequest(CategorySaveRequest req, boolean isUpdate) {
        if (req == null) {
            return "请求参数不能为空";
        }
        if (isUpdate && req.getId() == null) {
            return "类目ID不能为空";
        }
        if (req.getName() == null || req.getName().isBlank()) {
            return "类目名称不能为空";
        }
        if (req.getLevel() == null || req.getLevel() < 1 || req.getLevel() > 3) {
            return "类目层级必须在1到3之间";
        }
        if (!isUpdate && req.getLevel() > 1 && req.getParentId() == null) {
            return "二级/三级类目必须传父类目";
        }
        if (req.getSort() == null) {
            req.setSort(0);
        }
        if (req.getStatus() == null) {
            req.setStatus(true);
        }
        return null;
    }

    private Set<Long> collectSubtreeIds(Long rootId, List<MallProductCategory> all) {
        Map<Long, List<MallProductCategory>> grouped = all.stream()
                .collect(Collectors.groupingBy(item -> item.getParentId() == null ? 0L : item.getParentId()));
        Set<Long> ids = new java.util.HashSet<>();
        dfsCollect(rootId, grouped, ids);
        return ids;
    }

    private void dfsCollect(Long id, Map<Long, List<MallProductCategory>> grouped, Set<Long> ids) {
        ids.add(id);
        List<MallProductCategory> children = grouped.getOrDefault(id, List.of());
        for (MallProductCategory child : children) {
            dfsCollect(child.getId(), grouped, ids);
        }
    }

    private List<CategoryTreeNode> buildTree(List<MallProductCategory> all) {
        Map<Long, CategoryTreeNode> nodeMap = new HashMap<>();
        for (MallProductCategory item : all) {
            nodeMap.put(item.getId(), toNode(item));
        }
        List<CategoryTreeNode> roots = new ArrayList<>();
        for (MallProductCategory item : all) {
            Long parentId = item.getParentId() == null ? 0L : item.getParentId();
            CategoryTreeNode node = nodeMap.get(item.getId());
            if (parentId == 0L) {
                roots.add(node);
            } else {
                CategoryTreeNode parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    roots.add(node);
                }
            }
        }
        return roots;
    }

    private CategoryTreeNode toNode(MallProductCategory item) {
        CategoryTreeNode node = new CategoryTreeNode();
        node.setId(item.getId());
        node.setName(item.getName());
        node.setParentId(item.getParentId() == null || item.getParentId() == 0 ? null : item.getParentId());
        node.setLevel(item.getLevel() == null ? 1 : item.getLevel());
        node.setSort(item.getSort() == null ? 0 : item.getSort());
        node.setStatus(Objects.equals(item.getStatus(), 1));
        node.setCode(item.getIcon() == null ? String.valueOf(item.getId()) : item.getIcon());
        node.setRemark("");
        node.setCreateTime(item.getCreateTime());
        node.setChildren(new ArrayList<>());
        return node;
    }
}




