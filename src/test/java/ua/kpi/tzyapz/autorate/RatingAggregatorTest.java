package ua.kpi.tzyapz.autorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести класу RatingAggregator (SRS 1.1, розділ 5).
 * BL-10 average — автор Собко Артем (КТ).
 * Очікувані значення обчислено вручну за текстом вимог.
 */
@Tag("unit")
@DisplayName("RatingAggregator — рейтинг моделі (BL-10) і Top-N (BL-20)")
class RatingAggregatorTest {

    private static final double DELTA = 1e-9;

    private RatingAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new RatingAggregator();
    }

    // ===================== BL-10: average — автор Собко Артем =====================

    /** Перетворює рядок "оцінка:вага;оцінка:вага" на список голосів за одну модель. */
    private static List<Vote> votes(String spec) {
        List<Vote> result = new ArrayList<>();
        int i = 1;
        for (String pair : spec.split(";")) {
            String[] parts = pair.trim().split(":");
            result.add(new Vote("u" + i++, "m1", Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), "ok"));
        }
        return result;
    }

    @Test
    @DisplayName("BL-10: вага голосу — репутація: (5×3 + 4×1 + 2×2) / 6 = 3,83 → 3,8")
    void weightedAverageUsesReputationAsWeight() {
        // arrange
        List<Vote> votes = votes("5:3; 4:1; 2:2");
        // act
        double rating = aggregator.average(votes);
        // assert
        assertEquals(3.8, rating, DELTA);
    }

    @Test
    @DisplayName("BL-10: порожній список голосів → 0,0")
    void emptyVotesGiveZero() {
        assertEquals(0.0, aggregator.average(List.of()), DELTA);
    }

    @ParameterizedTest(name = "BL-10: голоси [{0}] → {1} (межі діапазону оцінок і ваг)")
    @CsvSource({
            "1:1,           1.0",
            "5:3,           5.0",
            "1:3; 1:1,      1.0",
            "5:1; 5:2; 5:3, 5.0",
            "4:2,           4.0"
    })
    void boundaryRatingsAndReputationsAreAccepted(String spec, double expected) {
        assertEquals(expected, aggregator.average(votes(spec)), DELTA);
    }

    @ParameterizedTest(name = "BL-10: голоси [{0}] → {1} (третя цифра < 5, округлення вниз)")
    @CsvSource({
            "5:1; 4:1; 4:1,      4.3",
            "4:1; 4:1; 4:1; 3:1; 3:1, 3.6",
            "2:2; 3:1,           2.3"
    })
    void roundsDownWhenNextDigitBelowFive(String spec, double expected) {
        // 13/3 = 4,333… → 4,3;  18/5 = 3,6 (точно);  (2×2 + 3×1)/3 = 7/3 = 2,333… → 2,3
        assertEquals(expected, aggregator.average(votes(spec)), DELTA);
    }

    // BUG-014 (GitHub Issue: вписати номер після створення): v1 відкидає другий знак замість HALF_UP.
    @ParameterizedTest(name = "BL-10: голоси [{0}] → {1} (HALF_UP: друга цифра ≥ 5 округлюється вгору)")
    @CsvSource({
            "5:1; 5:1; 4:1,      4.7",
            "4:1; 3:1; 3:1; 3:1, 3.3",
            "4:1; 4:1; 4:1; 3:1, 3.8",
            "5:3; 4:1,           4.8"
    })
    void roundsHalfUpToOneDecimal(String spec, double expected) {
        // 14/3 = 4,666… → 4,7;  13/4 = 3,25 → 3,3;  15/4 = 3,75 → 3,8;  19/4 = 4,75 → 4,8
        assertEquals(expected, aggregator.average(votes(spec)), DELTA);
    }

    @ParameterizedTest(name = "BL-10: оцінка {0} поза 1..5 → IllegalArgumentException")
    @ValueSource(ints = {0, 6, -1})
    void ratingOutOfRangeThrows(int rating) {
        List<Vote> votes = List.of(new Vote("u1", "m1", rating, 2, "ok"));
        assertThrows(IllegalArgumentException.class, () -> aggregator.average(votes));
    }

    @ParameterizedTest(name = "BL-10: репутація {0} поза 1..3 → IllegalArgumentException")
    @ValueSource(ints = {0, 4, -1})
    void reputationOutOfRangeThrows(int reputation) {
        List<Vote> votes = List.of(new Vote("u1", "m1", 3, reputation, "ok"));
        assertThrows(IllegalArgumentException.class, () -> aggregator.average(votes));
    }

    @Test
    @DisplayName("BL-10 / розд. 5: null замість колекції голосів → IllegalArgumentException")
    void nullVotesThrow() {
        assertThrows(IllegalArgumentException.class, () -> aggregator.average(null));
    }
}
