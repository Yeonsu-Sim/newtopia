package io.ssafy.p.i13c203.news_crawler.service.producer;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsContentProducerService {

  private final KafkaSender<String, Object> kafkaSender;

  @Value("${kafka.topics.news-content}")
  private String newsContentTopic;

  public Mono<Long> produceNewsContent(List<ParsedNewsContent> newsContents) {
    log.info("Starting to produce {} news contents to Kafka", newsContents.size());

    return Flux.fromIterable(newsContents)
      .map(this::createSenderRecord)
      .buffer(250)
      .concatMap(batch -> {
        log.info("Sending batch of {} records to Kafka", batch.size());
        return kafkaSender.send(Flux.fromIterable(batch))
          .doOnNext(result -> {
            if (result.exception() != null) {
              log.error("Failed to send record: {}", result.exception().getMessage());
            } else {
              log.debug("Successfully sent record with offset: {}",
                result.recordMetadata().offset());
            }
          })
          .count()
          .delayElement(Duration.ofMillis(100));
      })
      .reduce(0L, Long::sum)
      .doOnSuccess(totalSent -> log.info("Successfully sent {} records to Kafka topic: {}",
        totalSent, newsContentTopic))
      .doOnError(e -> log.error("Error producing news content to Kafka", e));
  }

  private SenderRecord<String, Object, String> createSenderRecord(ParsedNewsContent newsContent) {
    ProducerRecord<String, Object> record = new ProducerRecord<>(newsContentTopic, null, newsContent);
    return SenderRecord.create(record, null);
  }
}