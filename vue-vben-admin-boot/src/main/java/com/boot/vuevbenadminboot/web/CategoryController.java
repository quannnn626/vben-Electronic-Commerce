package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.service.MallProductCategoryService;
import com.boot.vuevbenadminboot.web.dto.CategorySaveRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mall/product-category")
public class CategoryController {

    private final MallProductCategoryService categoryService;

    public CategoryController(MallProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public Map<String, Object> tree() {
        return ApiResponse.of(0, categoryService.getCategoryTree(), "success");
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody CategorySaveRequest req) {
        try {
            return ApiResponse.of(0, categoryService.createCategory(req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody CategorySaveRequest req) {
        try {
            return ApiResponse.of(0, categoryService.updateCategory(req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestBody CategorySaveRequest req) {
        try {
            return ApiResponse.of(0, categoryService.deleteCategory(req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
