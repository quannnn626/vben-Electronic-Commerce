package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.mapper.SysUserMapper;
import com.boot.vuevbenadminboot.web.dto.UserSaveRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author quannnn
 * @description 针对表【sys_user(系统用户表)】的数据库操作Service实现
 * @createDate 2026-04-19 19:20:12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final SysUserMapper sysUserMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public SysUser selectByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public boolean insert(SysUser user) {
        Date now = new Date(System.currentTimeMillis());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setRole("user");
        user.setStatus(1);
        user.setDeleted(0);
        user.setHomePath("/analytics");
        int insert = sysUserMapper.insert(user);
        if (insert > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUser(UserSaveRequest request, String username) {
        SysUser sysUser = sysUserMapper.selectByUsername(username);
        if (sysUser != null) {
            sysUser.setUsername(request.getUsername());
            sysUser.setUpdateTime(new Date(System.currentTimeMillis()));
            sysUser.setRole(request.getRoles());
            sysUser.setNickname(request.getRealName());
            int i = sysUserMapper.updateById(sysUser);
            return i == 1;
        }
        return false;
    }
}




