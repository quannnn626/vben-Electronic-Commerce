package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

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
