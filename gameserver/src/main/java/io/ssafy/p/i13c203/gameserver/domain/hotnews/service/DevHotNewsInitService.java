package io.ssafy.p.i13c203.gameserver.domain.hotnews.service;

import io.ssafy.p.i13c203.gameserver.domain.hotnews.entity.HotNews;
import io.ssafy.p.i13c203.gameserver.domain.hotnews.repository.HotNewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Profile({"local","docker"})
@Component
@RequiredArgsConstructor
public class DevHotNewsInitService implements CommandLineRunner {
  private final HotNewsRepository hotNewsRepository;

  @Override
  public void run(String... args) throws Exception {
    log.info("Initializing HotNews Service For Local or Docker Profile...");

    if (hotNewsRepository.count() == 0) {
      log.info("Inserting sample hot news data...");
      insertSampleData();
      log.info("Sample hot news data inserted successfully.");
    } else {
      log.info("Hot news data already exists. Skipping initialization.");
    }
  }

  @Transactional
  private void insertSampleData() {
    List<HotNews> sampleNews = Arrays.asList(
      createHotNews("https://www.yna.co.kr/view/AKR20250918030400075?section=international/all",
                   "美가수 데이비드 명의 차 트렁크에서 10대 소녀 시신 발견",
                   "美가수 데이비드 명의 차 트렁크에서 10대 소녀 시신 발견",
                   LocalDateTime.of(2025, 9, 18, 8, 38)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918072500051?section=society/all",
                   "부산 태종대 앞바다서 60대 남성 숨진 채 발견…실족 추정",
                   "부산 태종대 앞바다서 60대 남성 숨진 채 발견…실족 추정",
                   LocalDateTime.of(2025, 9, 18, 10, 31)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918094100055?section=society/all",
                   "1천50원 과자 절도 재판서 판사·변호사 헛웃음…\"이게 뭐라고\"",
                   "1천50원 과자 절도 재판서 판사·변호사 헛웃음…\"이게 뭐라고\"",
                   LocalDateTime.of(2025, 9, 18, 11, 58)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918050900055?section=industry/all",
                   "제대 앞둔 육군 병장, 진안군 아파트서 추락사",
                   "제대 앞둔 육군 병장, 진안군 아파트서 추락사",
                   LocalDateTime.of(2025, 9, 18, 9, 44)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918085900054?section=society/all",
                   "육류혼합기에 끼어 숨진 종업원…법원. 식당업주 집행유예",
                   "육류혼합기에 끼어 숨진 종업원…법원. 식당업주 집행유예",
                   LocalDateTime.of(2025, 9, 18, 11, 18)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918080300055?section=society/accident",
                   "70대 노인이 길 가던 초등생에 \"예쁘다\"…덕담인가 범죄인가",
                   "70대 노인이 길 가던 초등생에 \"예쁘다\"…덕담인가 범죄인가",
                   LocalDateTime.of(2025, 9, 18, 11, 27)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918032700009?section=international/all",
                   "이집트 박물관서 3천년 된 파라오 금팔찌 사라져…당국 조사",
                   "이집트 박물관서 3천년 된 파라오 금팔찌 사라져…당국 조사",
                   LocalDateTime.of(2025, 9, 18, 8, 58)),

      createHotNews("https://www.yna.co.kr/view/AKR20250917114100518?section=society/all",
                   "던진 그릇 빗나가도 폭행이라는데…신체접촉 안해도 폭행죄 성립",
                   "던진 그릇 빗나가도 폭행이라는데…신체접촉 안해도 폭행죄 성립",
                   LocalDateTime.of(2025, 9, 18, 6, 30)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918088400009?section=international/all",
                   "버버리코트에 어깨 드러낸 드레스까지…멜라니아 英 국빈방문 패션",
                   "버버리코트에 어깨 드러낸 드레스까지…멜라니아 英 국빈방문 패션",
                   LocalDateTime.of(2025, 9, 18, 12, 10)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918024200007?section=international/north-america",
                   "미국 테니스 선수, 중국 음식에 혐오감 표현했다가 사과",
                   "미국 테니스 선수, 중국 음식에 혐오감 표현했다가 사과",
                   LocalDateTime.of(2025, 9, 18, 7, 43)),

      createHotNews("https://www.yna.co.kr/view/AKR20250917118800061?section=society/all",
                   "10대 성착취물 100개 만든 텔레방 '단장'…검찰, 징역 30년 구형",
                   "10대 성착취물 100개 만든 텔레방 '단장'…검찰, 징역 30년 구형",
                   LocalDateTime.of(2025, 9, 18, 9, 3)),

      createHotNews("https://www.yna.co.kr/view/AKR20250918041700530?section=society/all",
                   "피부암 극복한 50대, 뇌사 장기기증으로 5명 살려",
                   "피부암 극복한 50대, 뇌사 장기기증으로 5명 살려",
                   LocalDateTime.of(2025, 9, 18, 9, 8)),

      createHotNews("https://www.yna.co.kr/view/AKR20250916149300505?section=society/all",
                   "\"5일째 물도 못먹고 토하는 중\"",
                   "\"5일째 물도 못먹고 토하는 중\"",
                   LocalDateTime.of(2025, 9, 18, 5, 50))
    );

    hotNewsRepository.saveAll(sampleNews);
  }

  private HotNews createHotNews(String sourceUrl, String title, String content, LocalDateTime publishedAt) {
    HotNews hotNews = new HotNews();
    hotNews.setSourceUrl(sourceUrl);
    hotNews.setTitle(title);
    hotNews.setContent(content);
    hotNews.setPublishedAt(publishedAt);
    return hotNews;
  }
}

