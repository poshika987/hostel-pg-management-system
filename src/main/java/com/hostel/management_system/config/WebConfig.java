package com.hostel.management_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Path.of("uploads", "complaints").toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/complaint-photos/**")
                .addResourceLocations(uploadPath);
    }
}
