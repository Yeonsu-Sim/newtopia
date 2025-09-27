package io.ssafy.p.i13c203.gameserver.config;

import io.ssafy.p.i13c203.gameserver.config.converter.ApiEnumConverterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiEnumConverterFactory apiEnumConverterFactory;
    private final ThreadPoolTaskExecutor mvcAsyncTaskExecutor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(apiEnumConverterFactory);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(mvcAsyncTaskExecutor);
        configurer.setDefaultTimeout(30_000); // 30초 타임아웃
    }
}
