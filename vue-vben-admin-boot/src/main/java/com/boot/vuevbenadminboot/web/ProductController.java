package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.service.MallProductService;
import com.boot.vuevbenadminboot.web.dto.ProductSaveRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mall/product")
public class ProductController {
    private final MallProductService productService;

    public ProductController(MallProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        return ApiResponse.of(0, productService.listProducts(), "success");
    }

    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam Long id) {
        try {
            return ApiResponse.of(0, productService.getProductDetail(id), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody ProductSaveRequest req) {
        try {
            return ApiResponse.of(0, productService.createProduct(req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody ProductSaveRequest req) {
        try {
            return ApiResponse.of(0, productService.updateProduct(req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestBody ProductSaveRequest req) {
        try {
            return ApiResponse.of(0, productService.deleteProduct(req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
