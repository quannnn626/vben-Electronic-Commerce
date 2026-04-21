package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author quannnn
* @description 针对表【sys_user(系统用户表)】的数据库操作Service
* @createDate 2026-04-19 19:20:12
*/
public interface SysUserService extends IService<SysUser> {
    SysUser selectByUsername(String username);

    boolean insert(SysUser user);
}
