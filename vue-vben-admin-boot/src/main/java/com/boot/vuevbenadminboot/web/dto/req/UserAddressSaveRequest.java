package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

/**
 * 收货地址新增/编辑请求入参
 */
@Data
public class UserAddressSaveRequest {
    private Long id;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
}