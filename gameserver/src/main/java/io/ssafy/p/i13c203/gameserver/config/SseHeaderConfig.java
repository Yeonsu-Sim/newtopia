package io.ssafy.p.i13c203.gameserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.WebFilter;

@Configuration
public class SseHeaderConfig {
    @Bean
    public WebFilter sseNoBufferingHeaders() {
        return (exchange, chain) -> {
            var resp = exchange.getResponse().getHeaders();
            resp.add(HttpHeaders.CACHE_CONTROL, "no-cache");
            resp.add("X-Accel-Buffering", "no");
            resp.add(HttpHeaders.CONNECTION, "keep-alive");  // http 1.0 이라면 필요
            return chain.filter(exchange);
        };
    }
}
