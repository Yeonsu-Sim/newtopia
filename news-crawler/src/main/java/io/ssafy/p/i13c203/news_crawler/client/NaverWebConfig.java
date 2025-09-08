package io.ssafy.p.i13c203.news_crawler.client;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class NaverWebConfig {

  @Value("${client.naver.id}")
  private String clientId;
  @Value("${client.naver.secret}")
  private String clientSecret;
  @Value("${client.naver.base-url}")
  private String baseUrl;

  private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();


  @Bean
  public WebClient naverNewsApiClient() {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("X-Naver-Client-Id", clientId)
        .defaultHeader("X-Naver-Client-Secret", clientSecret)
        .build();
  }

  @Bean
  WebClient naverNewsClient() {
    var provider = ConnectionProvider.builder("naver-pool")
            .maxConnections(AVAILABLE_PROCESSORS * 4)
            .pendingAcquireMaxCount(2048)
            .maxIdleTime(Duration.ofSeconds(30))
            .evictInBackground(Duration.ofSeconds(30))
            .build();

    var http = HttpClient.create(provider)
            .responseTimeout(Duration.ofSeconds(60))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .keepAlive(true);

    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(http))
            .defaultHeader("User-Agent", "Mozilla/5.0")
            .defaultHeader("Accept-Language", "ko-KR,ko;q=0.9")
            .build();
  }
  
}
