package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author quannnn
* @description 针对表【mall_file(附件表)】的数据库操作Service
* @createDate 2026-04-22 13:51:56
*/
public interface MallFileService extends IService<MallFile> {
    List<MallFile> uploadFiles(MultipartFile[] files);
}
