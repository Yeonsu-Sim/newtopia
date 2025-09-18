package io.ssafy.p.i13c203.gameserver.domain.ranking.service;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ChoiceWeights;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameRepository;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Gender;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Role;
import io.ssafy.p.i13c203.gameserver.domain.member.repository.MemberRepository;
import io.ssafy.p.i13c203.gameserver.domain.ranking.dto.RankingDto;
import io.ssafy.p.i13c203.gameserver.domain.ranking.entity.Ranking;
import io.ssafy.p.i13c203.gameserver.domain.ranking.repository.RankingRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class RankingServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private RankingService rankingService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RankingRepository rankingRepository;

    private Member testMember1;
    private Member testMember2;
    private Game finishedGame1;
    private Game finishedGame2;
    private Game finishedGame3;

    @BeforeEach
    void setUp() {
        cleanUp();
        createTestData();
    }

    private void cleanUp() {
        rankingRepository.deleteAll();
        gameRepository.deleteAll();
        memberRepository.deleteAll();
    }

    private void createTestData() {
        testMember1 = createMember("user1@test.com", "User1", "password123");
        testMember2 = createMember("user2@test.com", "User2", "password456");

        finishedGame1 = createFinishedGame(testMember1.getId(), "Korea", 10, Instant.now().minusSeconds(100));
        finishedGame2 = createFinishedGame(testMember1.getId(), "Japan", 15, Instant.now().minusSeconds(200));
        finishedGame3 = createFinishedGame(testMember2.getId(), "China", 20, Instant.now().minusSeconds(50));
    }

    private Member createMember(String email, String nickname, String password) {
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .age(25)
                .gender(Gender.MALE)
                .role(Role.MEMBER)
                .build();
        return memberRepository.save(member);
    }

    private Game createFinishedGame(Long memberId, String countryName, int turn, Instant endedAt) {
        CardDoc currentCard = new CardDoc(
                UUID.randomUUID(),
                1L,
                CardType.ORIGIN,
                "Test Card Title",
                "Test Card Content",
                null,
                null,
                new HashMap<>(),
                null
        );

        Map<String, ChoiceDoc> currentChoices = new HashMap<>();
        // 문영호 생성자 에러가 나서 잠깐 처리해놓음
//        currentChoices.put("CHOICE_A", new ChoiceDoc("CHOICE_A", "Choice A", null));
//        currentChoices.put("CHOICE_B", new ChoiceDoc("CHOICE_B", "Choice B", null));

        Game game = Game.builder()
                .memberId(memberId)
                .endingCode("GOOD_END")
                .countryName(countryName)
                .countryStats(CountryStats.builder()
                        .economy(75)
                        .defense(80)
                        .publicSentiment(70)
                        .environment(65)
                        .build())
                .choiceWeights(ChoiceWeights.builder()
                        .macroeconomy(0.5)
                        .fiscalPolicy(0.3)
                        .build())
                .turn(turn)
                .version(1L)
                .choosedCode("CHOICE_01")
                .currentCard(currentCard)
                .currentChoices(currentChoices)
                .active(false)
                .endedAt(endedAt)
                .build();
        return gameRepository.save(game);
    }

    @Test
    @DisplayName("게임 ID로 랭킹 등록 - 정상 케이스")
    void registerRanking_WithGameId_ShouldCreateRanking() {
        Long gameId = finishedGame1.getGameId();

        rankingService.registerRanking(gameId);

        List<Ranking> rankings = rankingRepository.findAll();
        assertThat(rankings).hasSize(1);

        Ranking ranking = rankings.get(0);
        assertThat(ranking.getGame().getGameId()).isEqualTo(gameId);

        long expectedScore = finishedGame1.getTurn() * 1_000_000_000L - finishedGame1.getEndedAt().getEpochSecond();
        assertThat(ranking.getScore()).isEqualTo(expectedScore);
    }

    @Test
    @DisplayName("존재하지 않는 게임 ID로 랭킹 등록 - 예외 발생")
    void registerRanking_WithInvalidGameId_ShouldThrowNotFoundException() {
        Long invalidGameId = 999999L;

        assertThatThrownBy(() -> rankingService.registerRanking(invalidGameId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Game 객체로 랭킹 등록 - 정상 케이스")
    void registerRanking_WithGameObject_ShouldCreateRanking() {
        rankingService.registerRanking(finishedGame2);

        List<Ranking> rankings = rankingRepository.findAll();
        assertThat(rankings).hasSize(1);

        Ranking ranking = rankings.get(0);
        assertThat(ranking.getGame().getGameId()).isEqualTo(finishedGame2.getGameId());

        long expectedScore = finishedGame2.getTurn() * 1_000_000_000L - finishedGame2.getEndedAt().getEpochSecond();
        assertThat(ranking.getScore()).isEqualTo(expectedScore);
    }

    @Test
    @DisplayName("점수 계산 로직 검증")
    void registerRanking_ShouldCalculateScoreCorrectly() {
        rankingService.registerRanking(finishedGame1);
        rankingService.registerRanking(finishedGame2);
        rankingService.registerRanking(finishedGame3);

        List<Ranking> rankings = rankingRepository.findAll();
        assertThat(rankings).hasSize(3);

        long score1 = findRankingByGameId(rankings, finishedGame1.getGameId()).getScore();
        long score2 = findRankingByGameId(rankings, finishedGame2.getGameId()).getScore();
        long score3 = findRankingByGameId(rankings, finishedGame3.getGameId()).getScore();

        assertThat(score3).isGreaterThan(score2);
        assertThat(score2).isGreaterThan(score1);
    }

    @Test
    @DisplayName("게임 ID로 랭킹 조회 - 정상 케이스")
    void getRankingByGameId_ShouldReturnCorrectRankingDto() {
        rankingService.registerRanking(finishedGame1);
        rankingService.registerRanking(finishedGame2);
        rankingService.registerRanking(finishedGame3);

        RankingDto ranking = rankingService.getRankingByGameId(finishedGame1.getGameId());

        assertThat(ranking).isNotNull();
        assertThat(ranking.gameId()).isEqualTo(finishedGame1.getGameId());
        assertThat(ranking.countryName()).isEqualTo(finishedGame1.getCountryName());
        assertThat(ranking.turn()).isEqualTo(finishedGame1.getTurn());
        assertThat(ranking.order()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 게임 ID로 랭킹 조회 - 예외 발생")
    void getRankingByGameId_WithInvalidGameId_ShouldThrowNotFoundException() {
        Long invalidGameId = 999999L;

        assertThatThrownBy(() -> rankingService.getRankingByGameId(invalidGameId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("사용자 ID로 랭킹 조회 - 여러 게임 기록")
    void getRankingByUserId_ShouldReturnAllMemberRankings() {
        rankingService.registerRanking(finishedGame1);
        rankingService.registerRanking(finishedGame2);
        rankingService.registerRanking(finishedGame3);

        List<RankingDto> userRankings = rankingService.getRankingByMemberId(testMember1.getId());

        assertThat(userRankings).hasSize(2);
        assertThat(userRankings)
                .extracting(RankingDto::gameId)
                .containsExactlyInAnyOrder(finishedGame1.getGameId(), finishedGame2.getGameId());
    }

    @Test
    @DisplayName("사용자 ID로 랭킹 조회 - 게임 기록 없음")
    void getRankingByMemberId_WithNoGames_ShouldReturnEmptyList() {
        Member memberWithNoGames = createMember("nogames@test.com", "NoGames", "password");

        List<RankingDto> userRankings = rankingService.getRankingByMemberId(memberWithNoGames.getId());

        assertThat(userRankings).isEmpty();
    }

    @Test
    @DisplayName("상위 N개 랭킹 조회 - 정상 케이스")
    void getRankingByTopN_ShouldReturnTopRankingsInOrder() {
        rankingService.registerRanking(finishedGame1);
        rankingService.registerRanking(finishedGame2);
        rankingService.registerRanking(finishedGame3);

        List<RankingDto> topRankings = rankingService.getRankingByTopN(2);

        assertThat(topRankings).hasSize(2);
        assertThat(topRankings.get(0).order()).isEqualTo(1L);
        assertThat(topRankings.get(1).order()).isEqualTo(2L);

        assertThat(topRankings.get(0).gameId()).isEqualTo(finishedGame3.getGameId());
        assertThat(topRankings.get(1).gameId()).isEqualTo(finishedGame2.getGameId());
    }

    @Test
    @DisplayName("상위 N개 랭킹 조회 - 전체 랭킹보다 큰 N")
    void getRankingByTopN_WithLargerN_ShouldReturnAllRankings() {
        rankingService.registerRanking(finishedGame1);
        rankingService.registerRanking(finishedGame2);

        List<RankingDto> topRankings = rankingService.getRankingByTopN(10);

        assertThat(topRankings).hasSize(2);
    }

    @Test
    @DisplayName("랭킹 순서 정확성 검증")
    void verifyRankingOrder() {
        rankingService.registerRanking(finishedGame1);
        rankingService.registerRanking(finishedGame2);
        rankingService.registerRanking(finishedGame3);

        List<RankingDto> allRankings = rankingService.getRankingByTopN(10);

        assertThat(allRankings).hasSize(3);

        for (int i = 0; i < allRankings.size() - 1; i++) {
            assertThat(allRankings.get(i).order()).isLessThan(allRankings.get(i + 1).order());
        }
    }

    @Test
    @DisplayName("동일한 게임에 대한 중복 랭킹 등록 방지")
    void registerRanking_DuplicateGame_ShouldHandleGracefully() {
        rankingService.registerRanking(finishedGame1);

        assertThatThrownBy(() -> rankingService.registerRanking(finishedGame1))
                .hasMessageContaining("could not execute statement");
    }

    @Test
    @DisplayName("전역 순위 정확 값 검증")
    void getRankingByGameId_ShouldReturnExpectedRanks_Deterministic() {
        Instant T0 = Instant.parse("2025-01-01T00:00:00Z");

        Game g1 = gameRepository.save(createFinishedGame(testMember1.getId(), "Korea", 10, T0.minusSeconds(100)));
        Game g2 = gameRepository.save(createFinishedGame(testMember1.getId(), "Japan", 30, T0.minusSeconds(200)));
        Game g3 = gameRepository.save(createFinishedGame(testMember2.getId(), "China", 20, T0.minusSeconds(50)));

        rankingService.registerRanking(g1.getGameId());
        rankingService.registerRanking(g2.getGameId());
        rankingService.registerRanking(g3.getGameId());

        assertThat(rankingService.getRankingByGameId(g3.getGameId()).order()).isEqualTo(2L);
        assertThat(rankingService.getRankingByGameId(g2.getGameId()).order()).isEqualTo(1L);
        assertThat(rankingService.getRankingByGameId(g1.getGameId()).order()).isEqualTo(3L);
    }

    private Ranking findRankingByGameId(List<Ranking> rankings, Long gameId) {
        return rankings.stream()
                .filter(r -> r.getGame().getGameId().equals(gameId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ranking not found for gameId: " + gameId));
    }
}