package io.ssafy.p.i13c203.news_crawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("News Crawling API")
            .description("WebFlux 기반 네이버 뉴스 크롤링 API")
            .version("v1.0.0"));
  }
}