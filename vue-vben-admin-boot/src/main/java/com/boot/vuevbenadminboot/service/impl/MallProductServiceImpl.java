package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallProduct;
import com.boot.vuevbenadminboot.domain.MallProductCategoryRel;
import com.boot.vuevbenadminboot.domain.MallProductFileRel;
import com.boot.vuevbenadminboot.domain.MallProductStock;
import com.boot.vuevbenadminboot.mapper.MallProductMapper;
import com.boot.vuevbenadminboot.service.MallFileService;
import com.boot.vuevbenadminboot.service.MallProductCategoryRelService;
import com.boot.vuevbenadminboot.service.MallProductFileRelService;
import com.boot.vuevbenadminboot.service.MallProductService;
import com.boot.vuevbenadminboot.service.MallProductStockService;
import com.boot.vuevbenadminboot.service.dto.ProductFileDto;
import com.boot.vuevbenadminboot.service.dto.ProductListItemDto;
import com.boot.vuevbenadminboot.service.dto.ProductSaveRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author quannnn
* @description 针对表【mall_product(商品表)】的数据库操作Service实现
* @createDate 2026-04-19 19:20:12
*/
@Service
public class MallProductServiceImpl extends ServiceImpl<MallProductMapper, MallProduct>
    implements MallProductService{

    private final MallProductCategoryRelService productCategoryRelService;
    private final MallProductFileRelService productFileRelService;
    private final MallFileService mallFileService;
    private final MallProductStockService productStockService;

    public MallProductServiceImpl(
            MallProductCategoryRelService productCategoryRelService,
            MallProductFileRelService productFileRelService,
            MallFileService mallFileService,
            MallProductStockService productStockService) {
        this.productCategoryRelService = productCategoryRelService;
        this.productFileRelService = productFileRelService;
        this.mallFileService = mallFileService;
        this.productStockService = productStockService;
    }

    @Override
    public List<ProductListItemDto> listProducts() {
        List<MallProduct> products = this.list(
                new LambdaQueryWrapper<MallProduct>()
                        .eq(MallProduct::getDeleted, 0)
                        .orderByDesc(MallProduct::getId)
        );
        if (products.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = products.stream().map(MallProduct::getId).toList();
        Map<Long, List<Long>> categoryMap = buildCategoryMap(productIds);
        Map<Long, List<ProductFileDto>> fileMap = buildFileMap(productIds);
        Map<Long, MallProductStock> stockMap = productStockService.getStockMapByProductIds(productIds);

        List<ProductListItemDto> result = new ArrayList<>();
        for (MallProduct product : products) {
            ProductListItemDto dto = new ProductListItemDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            MallProductStock stock = stockMap.get(product.getId());
            dto.setStock(stock == null || stock.getStock() == null ? 0 : stock.getStock());
            dto.setDescription(product.getDescription());
            dto.setStatus(product.getStatus());
            dto.setCreateTime(product.getCreateTime());
            dto.setCategoryIds(categoryMap.getOrDefault(product.getId(), List.of()));
            dto.setFiles(fileMap.getOrDefault(product.getId(), List.of()));
            result.add(dto);
        }
        return result;
    }

    @Override
    public Long createProduct(ProductSaveRequest req) {
        validateRequest(req, false);
        MallProduct product = new MallProduct();
        product.setName(req.getName().trim());
        product.setPrice(req.getPrice());
        product.setDescription(req.getDescription());
        product.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        product.setDeleted(0);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        this.save(product);
        productStockService.saveProductStock(product.getId(), req.getStock());
        saveProductRelations(product.getId(), req);
        return product.getId();
    }

    @Override
    public boolean updateProduct(ProductSaveRequest req) {
        validateRequest(req, true);
        MallProduct old = this.getById(req.getId());
        if (old == null || Objects.equals(old.getDeleted(), 1)) {
            throw new IllegalArgumentException("商品不存在");
        }
        old.setName(req.getName().trim());
        old.setPrice(req.getPrice());
        old.setDescription(req.getDescription());
        old.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        old.setUpdateTime(new Date());
        boolean updated = this.updateById(old);
        productStockService.saveProductStock(req.getId(), req.getStock());
        clearProductRelations(req.getId());
        saveProductRelations(req.getId(), req);
        return updated;
    }

    @Override
    public boolean deleteProduct(ProductSaveRequest req) {
        if (req == null || req.getId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        MallProduct old = this.getById(req.getId());
        if (old == null || Objects.equals(old.getDeleted(), 1)) {
            throw new IllegalArgumentException("商品不存在");
        }
        productStockService.deleteByProductId(req.getId());
        clearProductRelations(req.getId());
        return this.removeById(req.getId());
    }

    private void validateRequest(ProductSaveRequest req, boolean isUpdate) {
        if (req == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (isUpdate && req.getId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (req.getPrice() == null) {
            throw new IllegalArgumentException("商品价格不能为空");
        }
        if (req.getStock() == null) {
            throw new IllegalArgumentException("库存不能为空");
        }
    }

    private void clearProductRelations(Long productId) {
        productCategoryRelService.remove(
                new LambdaQueryWrapper<MallProductCategoryRel>()
                        .eq(MallProductCategoryRel::getProductId, productId)
        );
        productFileRelService.remove(
                new LambdaQueryWrapper<MallProductFileRel>()
                        .eq(MallProductFileRel::getProductId, productId)
        );
    }

    private void saveProductRelations(Long productId, ProductSaveRequest req) {
        List<Long> categoryIds = req.getCategoryIds() == null ? List.of() : req.getCategoryIds();
        for (Long categoryId : new LinkedHashSet<>(categoryIds)) {
            if (categoryId == null) {
                continue;
            }
            MallProductCategoryRel rel = new MallProductCategoryRel();
            rel.setId(com.baomidou.mybatisplus.core.toolkit.IdWorker.getId());
            rel.setProductId(productId);
            rel.setCategoryId(categoryId);
            productCategoryRelService.save(rel);
        }

        List<Long> fileIds = req.getFileIds() == null ? List.of() : req.getFileIds();
        for (Long fileId : new LinkedHashSet<>(fileIds)) {
            if (fileId == null) {
                continue;
            }
            MallProductFileRel rel = new MallProductFileRel();
            rel.setId(com.baomidou.mybatisplus.core.toolkit.IdWorker.getId());
            rel.setProductId(productId);
            rel.setFileId(fileId);
            productFileRelService.save(rel);
        }
    }

    private Map<Long, List<Long>> buildCategoryMap(List<Long> productIds) {
        List<MallProductCategoryRel> rels = productCategoryRelService.list(
                new LambdaQueryWrapper<MallProductCategoryRel>()
                        .in(MallProductCategoryRel::getProductId, productIds)
        );
        return rels.stream().collect(
                Collectors.groupingBy(
                        MallProductCategoryRel::getProductId,
                        Collectors.mapping(MallProductCategoryRel::getCategoryId, Collectors.toList())
                )
        );
    }

    private Map<Long, List<ProductFileDto>> buildFileMap(List<Long> productIds) {
        List<MallProductFileRel> rels = productFileRelService.list(
                new LambdaQueryWrapper<MallProductFileRel>()
                        .in(MallProductFileRel::getProductId, productIds)
        );
        if (rels.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> fileIds = rels.stream().map(MallProductFileRel::getFileId).collect(Collectors.toSet());
        Map<Long, MallFile> fileMap = mallFileService.listByIds(fileIds).stream()
                .collect(Collectors.toMap(MallFile::getId, item -> item, (a, b) -> a));

        Map<Long, List<ProductFileDto>> result = new HashMap<>();
        for (MallProductFileRel rel : rels) {
            MallFile file = fileMap.get(rel.getFileId());
            if (file == null) {
                continue;
            }
            ProductFileDto dto = new ProductFileDto();
            dto.setId(file.getId());
            dto.setFileName(file.getFileName());
            dto.setFilePath(file.getFilePath());
            dto.setFileType(file.getFileType());
            result.computeIfAbsent(rel.getProductId(), k -> new ArrayList<>()).add(dto);
        }
        return result;
    }

}




