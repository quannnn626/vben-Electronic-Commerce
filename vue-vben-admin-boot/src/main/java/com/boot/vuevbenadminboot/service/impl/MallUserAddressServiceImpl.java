package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallUserAddress;
import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.mapper.SysUserMapper;
import com.boot.vuevbenadminboot.service.MallUserAddressService;
import com.boot.vuevbenadminboot.mapper.MallUserAddressMapper;
import com.boot.vuevbenadminboot.web.dto.UserAddressSaveRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author quannnn
* @description 针对表【mall_user_address(用户地址表（版本化）)】的数据库操作Service实现
* @createDate 2026-04-23 13:48:03
*/
@Service
public class MallUserAddressServiceImpl extends ServiceImpl<MallUserAddressMapper, MallUserAddress>
    implements MallUserAddressService{
    private final MallUserAddressMapper addressMapper;
    private final SysUserMapper sysUserMapper;

    public MallUserAddressServiceImpl(
            MallUserAddressMapper addressMapper,
            SysUserMapper sysUserMapper
    ) {
        this.addressMapper = addressMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public List<MallUserAddress> listByUsername(String username) {
        Long userId = requireUserId(username);
        return addressMapper.selectList(new LambdaQueryWrapper<MallUserAddress>()
                .eq(MallUserAddress::getUserId, userId)
                .eq(MallUserAddress::getDeleted, 0)
                .orderByDesc(MallUserAddress::getIsDefault)
                .orderByDesc(MallUserAddress::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAddress(String username, UserAddressSaveRequest request) {
        validateRequest(request, false);
        Long userId = requireUserId(username);

        MallUserAddress entity = new MallUserAddress();
        entity.setUserId(userId);
        entity.setReceiverName(request.getReceiverName().trim());
        entity.setReceiverPhone(request.getReceiverPhone().trim());
        entity.setProvince(trimOrNull(request.getProvince()));
        entity.setCity(trimOrNull(request.getCity()));
        entity.setDistrict(trimOrNull(request.getDistrict()));
        entity.setDetailAddress(request.getDetailAddress().trim());
        entity.setIsDefault(normalizeDefaultFlag(request.getIsDefault()));
        entity.setDeleted(0);
        Date now = new Date(System.currentTimeMillis());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        if (entity.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }

        int rows = addressMapper.insert(entity);
        if (rows != 1 || entity.getId() == null) {
            throw new IllegalArgumentException("新增地址失败");
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddress(String username, UserAddressSaveRequest request) {
        validateRequest(request, true);
        Long userId = requireUserId(username);
        MallUserAddress existing = requireAddress(userId, request.getId());

        existing.setReceiverName(request.getReceiverName().trim());
        existing.setReceiverPhone(request.getReceiverPhone().trim());
        existing.setProvince(trimOrNull(request.getProvince()));
        existing.setCity(trimOrNull(request.getCity()));
        existing.setDistrict(trimOrNull(request.getDistrict()));
        existing.setDetailAddress(request.getDetailAddress().trim());
        existing.setIsDefault(normalizeDefaultFlag(request.getIsDefault()));
        existing.setUpdateTime(new Date(System.currentTimeMillis()));

        if (existing.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }
        return addressMapper.updateById(existing) == 1;
    }

    @Override
    public boolean deleteAddress(String username, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("地址ID不能为空");
        }
        Long userId = requireUserId(username);
        MallUserAddress existing = requireAddress(userId, id);
        existing.setDeleted(1);
        existing.setUpdateTime(new Date(System.currentTimeMillis()));
        return addressMapper.updateById(existing) == 1;
    }

    private Long requireUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("未登录");
        }
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user.getId();
    }

    private MallUserAddress requireAddress(Long userId, Long addressId) {
        MallUserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<MallUserAddress>()
                .eq(MallUserAddress::getId, addressId)
                .eq(MallUserAddress::getUserId, userId)
                .eq(MallUserAddress::getDeleted, 0)
                .last("limit 1"));
        if (address == null) {
            throw new IllegalArgumentException("地址不存在");
        }
        return address;
    }

    private void validateRequest(UserAddressSaveRequest request, boolean checkId) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (checkId && request.getId() == null) {
            throw new IllegalArgumentException("地址ID不能为空");
        }
        if (request.getReceiverName() == null || request.getReceiverName().trim().isEmpty()) {
            throw new IllegalArgumentException("收货人不能为空");
        }
        if (request.getReceiverPhone() == null || request.getReceiverPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (request.getDetailAddress() == null || request.getDetailAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("详细地址不能为空");
        }
    }

    private Integer normalizeDefaultFlag(Integer isDefault) {
        return (isDefault != null && isDefault == 1) ? 1 : 0;
    }

    private String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void clearDefaultAddress(Long userId) {
        addressMapper.update(null, new LambdaUpdateWrapper<MallUserAddress>()
                .eq(MallUserAddress::getUserId, userId)
                .eq(MallUserAddress::getDeleted, 0)
                .set(MallUserAddress::getIsDefault, 0));
    }
}




