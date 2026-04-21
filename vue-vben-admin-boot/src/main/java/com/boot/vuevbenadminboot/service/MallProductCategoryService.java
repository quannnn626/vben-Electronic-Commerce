package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallProductCategory;
import com.boot.vuevbenadminboot.web.dto.CategorySaveRequest;
import com.boot.vuevbenadminboot.web.dto.CategoryTreeNode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author quannnn
 * @description 针对表【mall_product_category(商品类目表)】的数据库操作Service
 * @createDate 2026-04-21 17:48:47
 */
public interface MallProductCategoryService extends IService<MallProductCategory> {
    List<CategoryTreeNode> getCategoryTree();

    Long createCategory(CategorySaveRequest req);

    boolean updateCategory(CategorySaveRequest req);

    boolean deleteCategory(CategorySaveRequest req);
}
