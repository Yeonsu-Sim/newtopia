package io.ssafy.p.i13c203.news_crawler.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Configuration
@RequiredArgsConstructor
public class ReactiveKafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public SenderOptions<String, Object> kafkaSenderOptions() {
        return SenderOptions.<String, Object>create(kafkaProperties.buildProducerProperties()).maxInFlight(256);
    }

    @Bean
    public KafkaSender<String, Object> reactiveKafkaSender(SenderOptions<String, Object> senderOptions) {
        return KafkaSender.create(senderOptions);
    }
}