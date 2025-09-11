package io.ssafy.p.i13c203.gameserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Value("${app.storage.local.base-dir}")
    private String baseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + (baseDir.endsWith("/") ? baseDir : baseDir + "/");

        registry.addResourceHandler("/files/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
