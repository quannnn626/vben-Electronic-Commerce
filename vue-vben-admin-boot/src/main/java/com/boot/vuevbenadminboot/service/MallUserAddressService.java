package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallUserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.UserAddressSaveRequest;

import java.util.List;

/**
* @author quannnn
* @description 针对表【mall_user_address(用户地址表（版本化）)】的数据库操作Service
* @createDate 2026-04-23 13:48:03
*/
public interface MallUserAddressService extends IService<MallUserAddress> {
    List<MallUserAddress> listByUsername(String username);

    Long createAddress(String username, UserAddressSaveRequest request);

    boolean updateAddress(String username, UserAddressSaveRequest request);

    boolean deleteAddress(String username, Long id);

}
