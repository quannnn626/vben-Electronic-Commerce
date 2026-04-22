package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.service.MallFileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mall/file")
public class FileController {
    private final MallFileService mallFileService;

    public FileController(MallFileService mallFileService) {
        this.mallFileService = mallFileService;
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestPart("files") MultipartFile[] files) {
        try {
            List<MallFile> saved = mallFileService.uploadFiles(files);
            return ApiResponse.of(0, saved, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
