package io.ssafy.p.i13c203.gameserver.domain.gameresult.reader;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.GameHistory;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameHistoryReaderImpl implements GameHistoryReader {

    private final GameHistoryRepository historyRepo;

    @Override
    public List<String> readTop10Turns(long gameId) {
        List<GameHistory> all = historyRepo.findByGameIdOrderByTurnAsc(gameId)
                .stream()
                .filter(h -> h.getTurn() != 0) // 0턴 제외
                .toList();

        if (all.isEmpty()) return List.of();

        // 마지막 턴
        GameHistory lastTurn = all.get(all.size() - 1);

        // 델타 기준 Top 10
        List<GameHistory> top10 = all.stream()
                .sorted((a, b) -> Integer.compare(
                        calcDeltaMagnitude(b), calcDeltaMagnitude(a)))
                .limit(10)
                .collect(Collectors.toList());

        // 마지막 턴을 반드시 포함시키기
        if (!top10.contains(lastTurn)) {
            top10.add(lastTurn);
        }

        // 중복 제거 + 턴 번호 오름차순 정렬
        return top10.stream()
                .distinct()
                .sorted(Comparator.comparingInt(GameHistory::getTurn))
                .map(this::formatEntry)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> readTop10TurnsNotEvent(long gameId) {
        List<GameHistory> all = historyRepo.findByGameIdOrderByTurnAsc(gameId)
                .stream()
                .filter(h -> h.getTurn() != 0) // 0턴 제외
                .filter(h -> h.getEntry().card().type() != CardType.EVENT)
                .toList();

        if (all.isEmpty()) return List.of();

        // 마지막 턴
        GameHistory lastTurn = all.get(all.size() - 1);

        // 델타 기준 Top 10
        List<GameHistory> top10 = all.stream()
                .sorted((a, b) -> Integer.compare(
                        calcDeltaMagnitude(b), calcDeltaMagnitude(a)))
                .limit(10)
                .collect(Collectors.toList());

        // 마지막 턴을 반드시 포함시키기
        if (!top10.contains(lastTurn)) {
            top10.add(lastTurn);
        }

        // 중복 제거 + 턴 번호 오름차순 정렬
        return top10.stream()
                .distinct()
                .sorted(Comparator.comparingInt(GameHistory::getTurn))
                .map(this::formatEntry)
                .collect(Collectors.toList());
    }


    /***** UTILS *****/

    private String formatEntry(GameHistory h) {
        // entry 자체가 없을 수 있으니 방어
        var e = h.getEntry();
        if (e == null) {
            return h.getTurn() + "턴: 로그 없음";
        }

        // 선택 라벨
        String choice = safe(e.choosedLabel(), "선택 없음");

        // 카드 정보 (title / content)
        String cardTitle   = (e.card() != null && e.card().title()   != null) ? e.card().title()   : "제목 없음";
        String cardContent = (e.card() != null && e.card().content() != null) ? e.card().content() : "내용 없음";

        // delta (없으면 0으로)
        int eco = 0, def = 0, env = 0, pop = 0;
        if (e.applied() != null && e.applied().delta() != null) {
            var d = e.applied().delta();
            eco = nz(d.economy());
            def = nz(d.defense());
            env = nz(d.environment());
            pop = nz(d.publicSentiment());
        }
        String result = String.format("경제 %+d, 국방 %+d, 환경 %+d, 민심 %+d", eco, def, env, pop);

        return String.format(
                "%d턴: 카드[%s] - %s | 선택: [%s] | 결과: %s",
                h.getTurn(), cardTitle, cardContent, choice, result
        );
    }

    private int calcDeltaMagnitude(GameHistory h) {
        if (h.getEntry() == null || h.getEntry().applied() == null
                || h.getEntry().applied().delta() == null) {
            return 0;
        }
        var d = h.getEntry().applied().delta();
        return Math.abs(d.economy())
                + Math.abs(d.defense())
                + Math.abs(d.environment())
                + Math.abs(d.publicSentiment());
    }

    private static String safe(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }

    private static int nz(Integer v) { // primitive여도 오토박싱되니 사용 가능
        return v == null ? 0 : v;
    }

}
