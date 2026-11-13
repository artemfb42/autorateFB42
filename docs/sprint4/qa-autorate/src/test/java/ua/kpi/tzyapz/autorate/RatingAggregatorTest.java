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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести класу RatingAggregator (SRS 1.1, розділ 5).
 * BL-10 average — автор Собко Артем (КТ); BL-20 topN — автор Бурмай Юлія (ТА).
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

    // BUG-014 (GitHub Issue: 15): v1 відкидає другий знак замість HALF_UP.
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

    // ===================== BL-20: topN — автор Бурмай Юлія =====================

    private static List<String> fullNames(List<CarModel> models) {
        return models.stream().map(CarModel::fullName).collect(Collectors.toList());
    }

    @Test
    @DisplayName("BL-20: моделі впорядковано за рейтингом за спаданням")
    void ordersByRatingDescending() {
        // arrange
        List<CarModel> models = List.of(
                new CarModel("Ferrari", "F40", 4.2, 10),
                new CarModel("Lamborghini", "Diablo", 4.9, 12),
                new CarModel("Ford", "Mustang", 3.5, 15));
        // act
        List<CarModel> top = aggregator.topN(models, 3);
        // assert
        assertIterableEquals(List.of("Lamborghini Diablo", "Ferrari F40", "Ford Mustang"), fullNames(top));
    }

    @Test
    @DisplayName("BL-20: рівні рейтинги — за повною назвою (марка + модель) за алфавітом без урахування регістру")
    void tiesAreOrderedByFullNameIgnoringCase() {
        // «audi R8» з малої літери: з урахуванням регістру вона опинилася б після «BMW M3»
        List<CarModel> models = List.of(
                new CarModel("Ferrari", "F40", 4.5, 10),
                new CarModel("BMW", "M3", 4.5, 10),
                new CarModel("audi", "R8", 4.5, 10),
                new CarModel("BMW", "M1", 4.5, 10));

        List<CarModel> top = aggregator.topN(models, 4);

        assertIterableEquals(List.of("audi R8", "BMW M1", "BMW M3", "Ferrari F40"), fullNames(top));
    }

    // BUG-015 (GitHub Issue: №19): v1 вимагає > 3 голосів замість ≥ 3.
    @ParameterizedTest(name = "BL-20: модель із {0} голосами потрапляє у список: {1}")
    @CsvSource({
            "0, false",
            "2, false",
            "3, true",
            "4, true"
    })
    void modelNeedsAtLeastThreeVotes(int votes, boolean expectedInList) {
        List<CarModel> models = List.of(new CarModel("Ferrari", "F40", 4.2, votes));

        List<CarModel> top = aggregator.topN(models, 5);

        assertEquals(expectedInList, !top.isEmpty(),
                "Поріг BL-20: модель потрапляє у список лише з ≥3 голосами; голосів: " + votes);
    }

    @Test
    @DisplayName("BL-20: повертається не більше ніж n моделей — найкращі за рейтингом")
    void returnsAtMostNModels() {
        List<CarModel> models = List.of(
                new CarModel("Ferrari", "F40", 4.2, 10),
                new CarModel("Lamborghini", "Diablo", 4.9, 12),
                new CarModel("Ford", "Mustang", 3.5, 15),
                new CarModel("Audi", "R8", 4.6, 20));

        List<CarModel> top = aggregator.topN(models, 2);

        assertIterableEquals(List.of("Lamborghini Diablo", "Audi R8"), fullNames(top));
    }

    @Test
    @DisplayName("BL-20: n більше за кількість моделей — повертаються всі моделі, що пройшли поріг")
    void nGreaterThanCatalogReturnsAllEligible() {
        List<CarModel> models = List.of(
                new CarModel("Ferrari", "F40", 4.2, 10),
                new CarModel("Ford", "Mustang", 3.5, 1));

        List<CarModel> top = aggregator.topN(models, 100);

        assertIterableEquals(List.of("Ferrari F40"), fullNames(top));
    }

    @ParameterizedTest(name = "BL-20: n = {0} (≤ 0) → порожній список")
    @ValueSource(ints = {0, -1, -100})
    void nonPositiveNGivesEmptyList(int n) {
        List<CarModel> models = List.of(new CarModel("Ferrari", "F40", 4.2, 10));
        assertTrue(aggregator.topN(models, n).isEmpty());
    }

    @Test
    @DisplayName("BL-20: n = 1 (межа) → рівно одна найкраща модель")
    void nEqualToOneReturnsSingleBestModel() {
        List<CarModel> models = List.of(
                new CarModel("Ferrari", "F40", 4.2, 10),
                new CarModel("Lamborghini", "Diablo", 4.9, 12));

        List<CarModel> top = aggregator.topN(models, 1);

        assertIterableEquals(List.of("Lamborghini Diablo"), fullNames(top));
    }

    @Test
    @DisplayName("BL-20 / розд. 5: null замість колекції моделей → IllegalArgumentException")
    void nullModelsThrow() {
        assertThrows(IllegalArgumentException.class, () -> aggregator.topN(null, 3));
    }
}
