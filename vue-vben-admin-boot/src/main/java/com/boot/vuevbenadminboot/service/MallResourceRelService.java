package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallResourceRel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author quannnn
* @description 针对表【mall_resource_rel(通用资源关联表)】的数据库操作Service
* @createDate 2026-06-30 14:08:48
*/
public interface MallResourceRelService extends IService<MallResourceRel> {

    /**
     * 关联单个附件到资源
     *
     * @param resourceType 资源类型（如 sku / avatar / after_sale）
     * @param resourceId   资源ID
     * @param fileId       附件ID
     * @param usageType    用途标识（如 main_image / detail_image / receipt）
     * @param sortOrder    排序序号
     */
    void attach(String resourceType, Long resourceId, Long fileId, String usageType, Integer sortOrder);

    /**
     * 批量关联附件到资源
     *
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @param fileIds      附件ID列表
     * @param usageType    用途标识
     */
    void attachBatch(String resourceType, Long resourceId, List<Long> fileIds, String usageType);

    /**
     * 获取某资源的所有附件
     */
    List<MallFile> getFiles(String resourceType, Long resourceId);

    /**
     * 按用途获取某资源的附件
     */
    List<MallFile> getFilesByUsageType(String resourceType, Long resourceId, String usageType);

    /**
     * 查询某资源的主图（第一张 main_image）
     */
    MallFile getMainImage(String resourceType, Long resourceId);

    /**
     * 批量查询多个资源的附件，返回 resourceId → 附件列表
     */
    Map<Long, List<MallFile>> getFileMapByResources(String resourceType, List<Long> resourceIds);

    /**
     * 解绑某资源的所有附件
     */
    void removeByResource(String resourceType, Long resourceId);
}
