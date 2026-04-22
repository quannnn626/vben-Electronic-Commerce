package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.mapper.MallFileMapper;
import com.boot.vuevbenadminboot.service.MallFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
* @author quannnn
* @description 针对表【mall_file(附件表)】的数据库操作Service实现
* @createDate 2026-04-22 13:51:56
*/
@Service
public class MallFileServiceImpl extends ServiceImpl<MallFileMapper, MallFile>
    implements MallFileService{

    @Override
    public List<MallFile> uploadFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("请至少上传一个附件");
        }
        Path uploadDir = Path.of("").toAbsolutePath().resolve("upload");
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("创建上传目录失败", e);
        }

        List<MallFile> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String originalName = file.getOriginalFilename();
            String safeName = (originalName == null || originalName.isBlank()) ? "unknown" : originalName;
            String ext = "";
            int dotIndex = safeName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < safeName.length() - 1) {
                ext = safeName.substring(dotIndex).toLowerCase(Locale.ROOT);
            }
            String diskName = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                    + "_" + UUID.randomUUID().toString().replace("-", "")
                    + ext;
            Path target = uploadDir.resolve(diskName);
            try {
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalStateException("保存附件失败: " + safeName, e);
            }

            MallFile mallFile = new MallFile();
            mallFile.setFileName(safeName);
            mallFile.setFilePath("/upload/" + diskName);
            mallFile.setFileType(file.getContentType());
            mallFile.setCreateTime(new Date());
            mallFile.setUpdateTime(new Date());
            mallFile.setStatus(0);
            this.save(mallFile);
            saved.add(mallFile);
        }
        return saved;
    }
}




