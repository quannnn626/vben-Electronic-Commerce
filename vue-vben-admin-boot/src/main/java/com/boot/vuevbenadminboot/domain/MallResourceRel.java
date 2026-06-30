package com.boot.vuevbenadminboot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 通用资源关联表
 * @TableName mall_resource_rel
 */
@TableName(value ="mall_resource_rel")
@Data
public class MallResourceRel {
    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源类型：product商品 / sku规格 / avatar头像 / after_sale售后 / chat_msg聊天
     */
    private String resourceType;

    /**
     * 资源记录ID
     */
    private Long resourceId;

    /**
     * 附件ID
     */
    private Long fileId;

    /**
     * 用途标识：main_image主图 / detail_image详情图 / video视频 / receipt凭证
     */
    private String usageType;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        MallResourceRel other = (MallResourceRel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getResourceType() == null ? other.getResourceType() == null : this.getResourceType().equals(other.getResourceType()))
            && (this.getResourceId() == null ? other.getResourceId() == null : this.getResourceId().equals(other.getResourceId()))
            && (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()))
            && (this.getUsageType() == null ? other.getUsageType() == null : this.getUsageType().equals(other.getUsageType()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getResourceType() == null) ? 0 : getResourceType().hashCode());
        result = prime * result + ((getResourceId() == null) ? 0 : getResourceId().hashCode());
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        result = prime * result + ((getUsageType() == null) ? 0 : getUsageType().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", resourceType=").append(resourceType);
        sb.append(", resourceId=").append(resourceId);
        sb.append(", fileId=").append(fileId);
        sb.append(", usageType=").append(usageType);
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}