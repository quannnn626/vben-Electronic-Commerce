package com.boot.vuevbenadminboot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 物流商品关联表
 * @TableName mall_order_delivery_item
 */
@TableName(value ="mall_order_delivery_item")
@Data
public class MallOrderDeliveryItem {
    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 物流单ID
     */
    private Long deliveryId;

    /**
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 发货数量
     */
    private Integer quantity;

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
        MallOrderDeliveryItem other = (MallOrderDeliveryItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDeliveryId() == null ? other.getDeliveryId() == null : this.getDeliveryId().equals(other.getDeliveryId()))
            && (this.getOrderItemId() == null ? other.getOrderItemId() == null : this.getOrderItemId().equals(other.getOrderItemId()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDeliveryId() == null) ? 0 : getDeliveryId().hashCode());
        result = prime * result + ((getOrderItemId() == null) ? 0 : getOrderItemId().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deliveryId=").append(deliveryId);
        sb.append(", orderItemId=").append(orderItemId);
        sb.append(", quantity=").append(quantity);
        sb.append("]");
        return sb.toString();
    }
}