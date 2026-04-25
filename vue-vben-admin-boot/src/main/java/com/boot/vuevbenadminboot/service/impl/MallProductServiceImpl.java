package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallProduct;
import com.boot.vuevbenadminboot.domain.MallProductCategoryRel;
import com.boot.vuevbenadminboot.domain.MallProductCategory;
import com.boot.vuevbenadminboot.domain.MallSku;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.boot.vuevbenadminboot.mapper.MallProductMapper;
import com.boot.vuevbenadminboot.mapper.MallProductCategoryMapper;
import com.boot.vuevbenadminboot.service.MallFileService;
import com.boot.vuevbenadminboot.service.MallProductCategoryRelService;
import com.boot.vuevbenadminboot.service.MallProductService;
import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.web.dto.ProductListItemDto;
import com.boot.vuevbenadminboot.web.dto.ProductSaveRequest;
import com.boot.vuevbenadminboot.web.dto.ProductSkuDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
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
    private final MallProductCategoryMapper productCategoryMapper;
    private final MallFileService mallFileService;
    private final MallSkuService mallSkuService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MallProductServiceImpl(
            MallProductCategoryRelService productCategoryRelService,
            MallProductCategoryMapper productCategoryMapper,
            MallFileService mallFileService,
            MallSkuService mallSkuService) {
        this.productCategoryRelService = productCategoryRelService;
        this.productCategoryMapper = productCategoryMapper;
        this.mallFileService = mallFileService;
        this.mallSkuService = mallSkuService;
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
        Map<Long, List<String>> categoryNameMap = buildCategoryNameMap(categoryMap);
        Map<Long, List<ProductSkuDto>> skuMap = buildSkuMap(productIds);

        List<ProductListItemDto> result = new ArrayList<>();
        for (MallProduct product : products) {
            ProductListItemDto dto = new ProductListItemDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            List<ProductSkuDto> skuList = skuMap.getOrDefault(product.getId(), List.of());
            dto.setSkus(skuList);
            dto.setPrice(calculateMinSkuPrice(skuList));
            dto.setStock(sumSkuStock(skuList));
            dto.setDescription(product.getDescription());
            dto.setStatus(product.getStatus());
            dto.setCreateTime(product.getCreateTime());
            List<Long> categoryIds = categoryMap.getOrDefault(product.getId(), List.of());
            dto.setCategoryIds(categoryIds);
            dto.setCategoryNames(categoryNameMap.getOrDefault(product.getId(), List.of()));
            result.add(dto);
        }
        return result;
    }

    @Override
    public ProductListItemDto getProductDetail(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        MallProduct product = this.getById(id);
        if (product == null || Objects.equals(product.getDeleted(), 1)) {
            throw new IllegalArgumentException("商品不存在");
        }
        List<Long> productIds = List.of(id);
        Map<Long, List<Long>> categoryMap = buildCategoryMap(productIds);
        Map<Long, List<String>> categoryNameMap = buildCategoryNameMap(categoryMap);
        Map<Long, List<ProductSkuDto>> skuMap = buildSkuMap(productIds);

        ProductListItemDto dto = new ProductListItemDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        List<ProductSkuDto> skuList = skuMap.getOrDefault(product.getId(), List.of());
        dto.setSkus(skuList);
        dto.setPrice(calculateMinSkuPrice(skuList));
        dto.setStock(sumSkuStock(skuList));
        dto.setDescription(product.getDescription());
        dto.setStatus(product.getStatus());
        dto.setCreateTime(product.getCreateTime());
        List<Long> categoryIds = categoryMap.getOrDefault(product.getId(), List.of());
        dto.setCategoryIds(categoryIds);
        dto.setCategoryNames(categoryNameMap.getOrDefault(product.getId(), List.of()));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductSaveRequest req) {
        validateRequest(req, false);
        MallProduct product = new MallProduct();
        product.setName(req.getName().trim());
        product.setDescription(req.getDescription());
        product.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        product.setDeleted(0);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        this.save(product);
        saveProductSkus(product.getId(), req.getSkus());
        saveProductRelations(product.getId(), req);
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProduct(ProductSaveRequest req) {
        validateRequest(req, true);
        MallProduct old = this.getById(req.getId());
        if (old == null || Objects.equals(old.getDeleted(), 1)) {
            throw new IllegalArgumentException("商品不存在");
        }
        old.setName(req.getName().trim());
        old.setDescription(req.getDescription());
        old.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        old.setUpdateTime(new Date());
        boolean updated = this.updateById(old);
        saveProductSkus(req.getId(), req.getSkus());
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
        mallSkuService.remove(
                new LambdaQueryWrapper<MallSku>()
                        .eq(MallSku::getProductId, req.getId())
        );
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
        if (req.getSkus() == null || req.getSkus().isEmpty()) {
            throw new IllegalArgumentException("请至少维护一个SKU规格");
        }
        for (ProductSkuDto sku : req.getSkus()) {
            if (sku == null) {
                throw new IllegalArgumentException("SKU信息不能为空");
            }
            if (sku.getSpecName() == null || sku.getSpecName().isBlank()) {
                throw new IllegalArgumentException("SKU规格名称不能为空");
            }
            if (sku.getPrice() == null || sku.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("SKU价格必须大于0");
            }
            if (sku.getStock() == null || sku.getStock() < 0) {
                throw new IllegalArgumentException("SKU库存不能小于0");
            }
            if (sku.getFileId() == null) {
                throw new IllegalArgumentException("每个SKU都必须选择一个附件");
            }
        }
    }

    private void saveProductSkus(Long productId, List<ProductSkuDto> skus) {
        mallSkuService.remove(
                new LambdaQueryWrapper<MallSku>()
                        .eq(MallSku::getProductId, productId)
        );
        if (skus == null || skus.isEmpty()) {
            return;
        }
        Date now = new Date();
        List<MallSku> entities = new ArrayList<>();
        Map<Long, MallFile> fileMap = buildFileMapByIds(
                skus.stream().map(ProductSkuDto::getFileId).filter(Objects::nonNull).collect(Collectors.toSet())
        );
        for (ProductSkuDto sku : skus) {
            if (!fileMap.containsKey(sku.getFileId())) {
                throw new IllegalArgumentException("SKU附件不存在: " + sku.getFileId());
            }
            MallSku entity = new MallSku();
            entity.setProductId(productId);
            entity.setSkuCode(sku.getSkuCode());
            entity.setSpecData(buildSpecData(sku));
            entity.setPrice(sku.getPrice());
            entity.setStock(sku.getStock());
            entity.setLockedStock(0);
            entity.setStatus(sku.getStatus() == null ? 1 : sku.getStatus());
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            entities.add(entity);
        }
        mallSkuService.saveBatch(entities);
    }

    private void clearProductRelations(Long productId) {
        productCategoryRelService.remove(
                new LambdaQueryWrapper<MallProductCategoryRel>()
                        .eq(MallProductCategoryRel::getProductId, productId)
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

    private Map<Long, List<String>> buildCategoryNameMap(Map<Long, List<Long>> categoryMap) {
        if (categoryMap == null || categoryMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> allCategoryIds = categoryMap.values().stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (allCategoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, String> idNameMap = productCategoryMapper.selectBatchIds(allCategoryIds).stream()
                .filter(item -> !Objects.equals(item.getDeleted(), 1))
                .collect(Collectors.toMap(MallProductCategory::getId, MallProductCategory::getName, (a, b) -> a));

        Map<Long, List<String>> result = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : categoryMap.entrySet()) {
            List<String> names = entry.getValue().stream()
                    .map(idNameMap::get)
                    .filter(name -> name != null && !name.isBlank())
                    .toList();
            result.put(entry.getKey(), names);
        }
        return result;
    }

    private Map<Long, List<ProductSkuDto>> buildSkuMap(List<Long> productIds) {
        List<MallSku> skuList = mallSkuService.list(
                new LambdaQueryWrapper<MallSku>()
                        .in(MallSku::getProductId, productIds)
                        .orderByAsc(MallSku::getId)
        );
        if (skuList.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> fileIds = new LinkedHashSet<>();
        Map<Long, Map<String, Object>> parsedSpecMap = new HashMap<>();
        for (MallSku sku : skuList) {
            Map<String, Object> specMap = parseSpecData(sku.getSpecData());
            parsedSpecMap.put(sku.getId(), specMap);
            Long fileId = toLong(specMap.get("fileId"));
            if (fileId != null) {
                fileIds.add(fileId);
            }
        }
        Map<Long, MallFile> fileMap = buildFileMapByIds(fileIds);

        Map<Long, List<ProductSkuDto>> map = new HashMap<>();
        for (MallSku sku : skuList) {
            Map<String, Object> specMap = parsedSpecMap.getOrDefault(sku.getId(), Map.of());
            Long fileId = toLong(specMap.get("fileId"));
            MallFile file = fileId == null ? null : fileMap.get(fileId);
            ProductSkuDto dto = new ProductSkuDto();
            dto.setId(sku.getId());
            dto.setSkuCode(sku.getSkuCode());
            dto.setPrice(sku.getPrice());
            dto.setStock(sku.getStock() == null ? 0 : sku.getStock());
            dto.setLockedStock(sku.getLockedStock() == null ? 0 : sku.getLockedStock());
            dto.setStatus(sku.getStatus() == null ? 1 : sku.getStatus());
            dto.setSpecName(Objects.toString(specMap.getOrDefault("name", ""), ""));
            dto.setFileId(fileId);
            if (file != null) {
                dto.setFileName(file.getFileName());
                dto.setFilePath(file.getFilePath());
                dto.setFileType(file.getFileType());
            }
            map.computeIfAbsent(sku.getProductId(), k -> new ArrayList<>()).add(dto);
        }
        return map;
    }

    private Map<Long, MallFile> buildFileMapByIds(Set<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return mallFileService.listByIds(fileIds).stream()
                .collect(Collectors.toMap(MallFile::getId, item -> item, (a, b) -> a));
    }

    private String buildSpecData(ProductSkuDto sku) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", sku.getSpecName().trim());
        map.put("fileId", sku.getFileId());
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalArgumentException("SKU规格数据序列化失败");
        }
    }

    private Map<String, Object> parseSpecData(Object specData) {
        if (specData == null) {
            return Map.of();
        }
        if (specData instanceof Map<?, ?> raw) {
            Map<String, Object> converted = new LinkedHashMap<>();
            raw.forEach((k, v) -> converted.put(String.valueOf(k), v));
            return converted;
        }
        try {
            return objectMapper.readValue(String.valueOf(specData), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int sumSkuStock(List<ProductSkuDto> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            return 0;
        }
        return skuList.stream().map(ProductSkuDto::getStock).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }

    private BigDecimal calculateMinSkuPrice(List<ProductSkuDto> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return skuList.stream()
                .map(ProductSkuDto::getPrice)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

}




