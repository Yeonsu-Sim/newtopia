package io.ssafy.p.i13c203.news_crawler.client;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class YnaWebConfig {

  private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

  @Bean
  WebClient ynaNewsClient() {
    var provider = ConnectionProvider.builder("yna-pool")
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
            .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .defaultHeader("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
            .defaultHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .build();
  }
}