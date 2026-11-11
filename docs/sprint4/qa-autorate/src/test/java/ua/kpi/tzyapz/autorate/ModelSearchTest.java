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
 * Юніт-тести правил BL-60 (пошук за префіксом) і BL-70 (сортування за рейтингом), SRS 1.1.
 * Автор: Деркач Юрій (ІТ, спринт 4).
 */
@Tag("unit")
@DisplayName("ModelSearch — пошук за префіксом і сортування (BL-60, BL-70)")
class ModelSearchTest {

    private ModelSearch search;
    private List<CarModel> catalog;

    @BeforeEach
    void setUp() {
        search = new ModelSearch();
        catalog = List.of(
                new CarModel("Lamborghini", "Diablo", 4.6, 12),
                new CarModel("Ferrari", "F40", 4.2, 8),
                new CarModel("Lamborghini", "Aventador", 4.8, 20),
                new CarModel("Ford", "Mustang", 3.9, 15));
    }

    private static List<String> fullNames(List<CarModel> models) {
        return models.stream().map(CarModel::fullName).collect(Collectors.toList());
    }

    @ParameterizedTest(name = "BL-60: search(\"{0}\") → {1}")
    @CsvSource({
            "lam,   Lamborghini Diablo|Lamborghini Aventador",
            "LAMBO, Lamborghini Diablo|Lamborghini Aventador",
            "f,     Ferrari F40|Ford Mustang",
            "must,  Ford Mustang",
            "AVENT, Lamborghini Aventador",
            "f4,    Ferrari F40"
    })
    void findsByMakeOrModelPrefixIgnoringCaseInInputOrder(String query, String expected) {
        // act
        List<CarModel> found = search.search(query, catalog);
        // assert: порядок результатів збігається з порядком у вхідному списку
        assertIterableEquals(List.of(expected.split("\\|")), fullNames(found));
    }

    @ParameterizedTest(name = "BL-60: «{0}» є всередині назви, але не префіксом → порожній результат")
    @ValueSource(strings = {"iablo", "orghini", "40", "stang", "zzz"})
    void substringThatIsNotPrefixFindsNothing(String query) {
        assertTrue(search.search(query, catalog).isEmpty());
    }

    // BUG-018 (GitHub Issue: вписати номер після створення): v1 повертає весь каталог для порожнього запиту.
    @ParameterizedTest(name = "BL-60: порожній або пробільний запит «{0}» → порожній результат")
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void blankQueryGivesEmptyResult(String query) {
        List<CarModel> found = search.search(query, catalog);

        assertTrue(found.isEmpty(), "Очікувався порожній результат, отримано: " + fullNames(found));
    }

    @Test
    @DisplayName("BL-60 / розд. 5: null замість колекції моделей → IllegalArgumentException")
    void searchWithNullModelsThrows() {
        assertThrows(IllegalArgumentException.class, () -> search.search("lam", null));
    }

    // BUG-019 (GitHub Issue: вписати номер після створення): v1 сортує за зростанням рейтингу.
    @Test
    @DisplayName("BL-70: sortByRating — за рейтингом за спаданням")
    void sortsByRatingDescending() {
        List<CarModel> sorted = search.sortByRating(catalog);

        assertIterableEquals(
                List.of("Lamborghini Aventador", "Lamborghini Diablo", "Ferrari F40", "Ford Mustang"),
                fullNames(sorted));
    }

    @Test
    @DisplayName("BL-70: рівні рейтинги — за повною назвою за алфавітом без урахування регістру")
    void equalRatingsAreOrderedByFullNameIgnoringCase() {
        List<CarModel> sameRating = List.of(
                new CarModel("Ford", "Mustang", 4.0, 5),
                new CarModel("BMW", "M3", 4.0, 5),
                new CarModel("audi", "R8", 4.0, 5));

        List<CarModel> sorted = search.sortByRating(sameRating);

        assertIterableEquals(List.of("audi R8", "BMW M3", "Ford Mustang"), fullNames(sorted));
    }

    @Test
    @DisplayName("BL-70: повертається новий список, вхідний список не змінюється")
    void sortReturnsNewListAndKeepsInputUntouched() {
        // arrange
        List<CarModel> input = new ArrayList<>(catalog);
        List<CarModel> snapshot = new ArrayList<>(input);
        // act
        List<CarModel> sorted = search.sortByRating(input);
        // assert
        assertAll("вхідний список не змінено",
                () -> assertNotSame(input, sorted),
                () -> assertIterableEquals(snapshot, input),
                () -> assertEquals(input.size(), sorted.size()));
    }

    @Test
    @DisplayName("BL-70 / розд. 5: null замість колекції моделей → IllegalArgumentException")
    void sortWithNullModelsThrows() {
        assertThrows(IllegalArgumentException.class, () -> search.sortByRating(null));
    }
}
