package com.boot.vuevbenadminboot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName mall_order_delivery
 */
@TableName(value ="mall_order_delivery")
@Data
public class MallOrderDelivery {
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 物流状态
     */
    private Integer deliveryStatus;

    /**
     * 发货备注
     */
    private String deliveryRemark;

    /**
     * 发货人
     */
    private Long deliveryUser;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer deleted;

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
        MallOrderDelivery other = (MallOrderDelivery) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getLogisticsCompany() == null ? other.getLogisticsCompany() == null : this.getLogisticsCompany().equals(other.getLogisticsCompany()))
            && (this.getTrackingNo() == null ? other.getTrackingNo() == null : this.getTrackingNo().equals(other.getTrackingNo()))
            && (this.getDeliveryStatus() == null ? other.getDeliveryStatus() == null : this.getDeliveryStatus().equals(other.getDeliveryStatus()))
            && (this.getDeliveryRemark() == null ? other.getDeliveryRemark() == null : this.getDeliveryRemark().equals(other.getDeliveryRemark()))
            && (this.getDeliveryUser() == null ? other.getDeliveryUser() == null : this.getDeliveryUser().equals(other.getDeliveryUser()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getLogisticsCompany() == null) ? 0 : getLogisticsCompany().hashCode());
        result = prime * result + ((getTrackingNo() == null) ? 0 : getTrackingNo().hashCode());
        result = prime * result + ((getDeliveryStatus() == null) ? 0 : getDeliveryStatus().hashCode());
        result = prime * result + ((getDeliveryRemark() == null) ? 0 : getDeliveryRemark().hashCode());
        result = prime * result + ((getDeliveryUser() == null) ? 0 : getDeliveryUser().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", logisticsCompany=").append(logisticsCompany);
        sb.append(", trackingNo=").append(trackingNo);
        sb.append(", deliveryStatus=").append(deliveryStatus);
        sb.append(", deliveryRemark=").append(deliveryRemark);
        sb.append(", deliveryUser=").append(deliveryUser);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", deleted=").append(deleted);
        sb.append("]");
        return sb.toString();
    }
}