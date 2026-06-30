package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallResourceRel;
import com.boot.vuevbenadminboot.service.MallFileService;
import com.boot.vuevbenadminboot.service.MallResourceRelService;
import com.boot.vuevbenadminboot.mapper.MallResourceRelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author quannnn
* @description 针对表【mall_resource_rel(通用资源关联表)】的数据库操作Service实现
* @createDate 2026-06-30 14:08:48
*/
@Service
public class MallResourceRelServiceImpl extends ServiceImpl<MallResourceRelMapper, MallResourceRel>
    implements MallResourceRelService{

    private final MallFileService mallFileService;

    public MallResourceRelServiceImpl(MallFileService mallFileService) {
        this.mallFileService = mallFileService;
    }

    @Override
    public void attach(String resourceType, Long resourceId, Long fileId, String usageType, Integer sortOrder) {
        MallResourceRel rel = new MallResourceRel();
        rel.setResourceType(resourceType);
        rel.setResourceId(resourceId);
        rel.setFileId(fileId);
        rel.setUsageType(usageType != null ? usageType : "");
        rel.setSortOrder(sortOrder != null ? sortOrder : 0);
        rel.setCreateTime(new Date());
        this.save(rel);
    }

    @Override
    public void attachBatch(String resourceType, Long resourceId, List<Long> fileIds, String usageType) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }
        List<MallResourceRel> list = new ArrayList<>();
        int index = 0;
        for (Long fileId : fileIds) {
            MallResourceRel rel = new MallResourceRel();
            rel.setResourceType(resourceType);
            rel.setResourceId(resourceId);
            rel.setFileId(fileId);
            rel.setUsageType(usageType != null ? usageType : "");
            rel.setSortOrder(index++);
            rel.setCreateTime(new Date());
            list.add(rel);
        }
        this.saveBatch(list);
    }

    @Override
    public List<MallFile> getFiles(String resourceType, Long resourceId) {
        List<MallResourceRel> rels = this.list(
                new LambdaQueryWrapper<MallResourceRel>()
                        .eq(MallResourceRel::getResourceType, resourceType)
                        .eq(MallResourceRel::getResourceId, resourceId)
                        .orderByAsc(MallResourceRel::getSortOrder)
        );
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> fileIds = rels.stream().map(MallResourceRel::getFileId).toList();
        return mallFileService.listByIds(fileIds);
    }

    @Override
    public List<MallFile> getFilesByUsageType(String resourceType, Long resourceId, String usageType) {
        List<MallResourceRel> rels = this.list(
                new LambdaQueryWrapper<MallResourceRel>()
                        .eq(MallResourceRel::getResourceType, resourceType)
                        .eq(MallResourceRel::getResourceId, resourceId)
                        .eq(MallResourceRel::getUsageType, usageType)
                        .orderByAsc(MallResourceRel::getSortOrder)
        );
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> fileIds = rels.stream().map(MallResourceRel::getFileId).toList();
        return mallFileService.listByIds(fileIds);
    }

    @Override
    public MallFile getMainImage(String resourceType, Long resourceId) {
        List<MallFile> files = getFilesByUsageType(resourceType, resourceId, "main_image");
        return files.isEmpty() ? null : files.get(0);
    }

    @Override
    public Map<Long, List<MallFile>> getFileMapByResources(String resourceType, List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MallResourceRel> rels = this.list(
                new LambdaQueryWrapper<MallResourceRel>()
                        .eq(MallResourceRel::getResourceType, resourceType)
                        .in(MallResourceRel::getResourceId, resourceIds)
                        .orderByAsc(MallResourceRel::getSortOrder)
        );
        if (rels.isEmpty()) {
            return Collections.emptyMap();
        }
        // 收集所有 fileId
        List<Long> allFileIds = rels.stream().map(MallResourceRel::getFileId).distinct().toList();
        Map<Long, MallFile> fileMap = mallFileService.listByIds(allFileIds).stream()
                .collect(Collectors.toMap(MallFile::getId, f -> f, (a, b) -> a));

        // 按 resourceId 分组
        Map<Long, List<MallFile>> result = new LinkedHashMap<>();
        for (MallResourceRel rel : rels) {
            MallFile file = fileMap.get(rel.getFileId());
            if (file != null) {
                result.computeIfAbsent(rel.getResourceId(), k -> new ArrayList<>()).add(file);
            }
        }
        return result;
    }

    @Override
    public void removeByResource(String resourceType, Long resourceId) {
        this.remove(
                new LambdaQueryWrapper<MallResourceRel>()
                        .eq(MallResourceRel::getResourceType, resourceType)
                        .eq(MallResourceRel::getResourceId, resourceId)
        );
    }
}




