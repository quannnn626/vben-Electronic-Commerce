package com.boot.vuevbenadminboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebStaticConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Path.of("").toAbsolutePath().resolve("upload").toUri().toString();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}
