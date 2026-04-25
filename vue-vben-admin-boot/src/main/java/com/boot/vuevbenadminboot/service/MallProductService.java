package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.ProductListItemDto;
import com.boot.vuevbenadminboot.web.dto.ProductSaveRequest;

import java.util.List;

/**
 * @author quannnn
 * @description 针对表【mall_product(商品表)】的数据库操作Service
 * @createDate 2026-04-19 19:20:12
 */
public interface MallProductService extends IService<MallProduct> {
    List<ProductListItemDto> listProducts();

    ProductListItemDto getProductDetail(Long id);

    Long createProduct(ProductSaveRequest req);

    boolean updateProduct(ProductSaveRequest req);

    boolean deleteProduct(ProductSaveRequest req);
}
