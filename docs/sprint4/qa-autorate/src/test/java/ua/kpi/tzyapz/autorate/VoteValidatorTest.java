package ua.kpi.tzyapz.autorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести класу VoteValidator (SRS 1.1, розділ 5).
 * BL-30, BL-40 — автор Фощій Дмитро (СМ).
 * У тестах одного правила решта параметрів коректна, щоб тест мав одну причину впасти:
 * для BL-30/BL-40 коментар «Гарна модель».
 */
@Tag("unit")
@DisplayName("VoteValidator — перевірка голосу (BL-30, BL-40, BL-50, BL-80)")
class VoteValidatorTest {

    private static final String DUPLICATE = "повторний голос";
    private static final String RATING_OUT_OF_RANGE = "оцінка поза межами 1..5";
    private static final String COMMENT = "Гарна модель";

    private VoteValidator validator;

    @BeforeEach
    void setUp() {
        validator = new VoteValidator();
    }

    // ============ BL-30, BL-40: повторний голос і межі оцінки — автор Фощій Дмитро ============

    @Test
    @DisplayName("BL-30: коректний перший голос приймається — перелік порушень порожній")
    void validFirstVoteIsAccepted() {
        // act
        List<String> errors = validator.validate("u1", "m1", 4, COMMENT, List.of());
        // assert
        assertAll("голос прийнято",
                () -> assertTrue(errors.isEmpty(), "Порушення: " + errors),
                () -> assertTrue(validator.isValid("u1", "m1", 4, COMMENT, List.of())));
    }

    @Test
    @DisplayName("BL-30: та сама пара (userId, modelId) → «повторний голос»")
    void sameUserSameModelIsRejectedAsDuplicate() {
        List<Vote> existing = List.of(new Vote("u1", "m1", 5, 2, "уже голосував"));

        List<String> errors = validator.validate("u1", "m1", 4, COMMENT, existing);

        assertAll("повторний голос відхилено",
                () -> assertTrue(errors.contains(DUPLICATE), "Порушення: " + errors),
                () -> assertFalse(validator.isValid("u1", "m1", 4, COMMENT, existing)));
    }

    // BUG-016 (GitHub Issue: вписати номер після створення): v1 порівнює лише userId, без modelId.
    @Test
    @DisplayName("BL-30: той самий користувач, інша модель → голос приймається")
    void sameUserOtherModelIsAccepted() {
        List<Vote> existing = List.of(new Vote("u1", "m2", 5, 2, "голос за іншу модель"));

        List<String> errors = validator.validate("u1", "m1", 4, COMMENT, existing);

        assertFalse(errors.contains(DUPLICATE),
                "Голос u1 за m2 не забороняє голос u1 за m1; порушення: " + errors);
    }

    @Test
    @DisplayName("BL-30: інший користувач, та сама модель → голос приймається")
    void otherUserSameModelIsAccepted() {
        List<Vote> existing = List.of(new Vote("u2", "m1", 5, 2, "голос іншого користувача"));

        List<String> errors = validator.validate("u1", "m1", 4, COMMENT, existing);

        assertFalse(errors.contains(DUPLICATE), "Порушення: " + errors);
    }

    // BUG-017 (GitHub Issue: вписати номер після створення): v1 відхиляє оцінку 5 (верхня межа).
    @ParameterizedTest(name = "BL-40: оцінка {0} у межах 1..5 приймається")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void ratingWithinRangeIsAccepted(int rating) {
        List<String> errors = validator.validate("u1", "m1", rating, COMMENT, List.of());

        assertFalse(errors.contains(RATING_OUT_OF_RANGE),
                "Оцінка " + rating + " допустима за BL-40; порушення: " + errors);
    }

    @ParameterizedTest(name = "BL-40: оцінка {0} поза 1..5 → «оцінка поза межами 1..5»")
    @ValueSource(ints = {0, 6, -1, 100})
    void ratingOutsideRangeIsRejected(int rating) {
        List<String> errors = validator.validate("u1", "m1", rating, COMMENT, List.of());

        assertTrue(errors.contains(RATING_OUT_OF_RANGE), "Порушення: " + errors);
    }

    @Test
    @DisplayName("BL-30 + BL-40: кілька порушень повертаються одним переліком")
    void severalViolationsAreReportedTogether() {
        List<Vote> existing = List.of(new Vote("u1", "m1", 5, 2, "уже голосував"));

        List<String> errors = validator.validate("u1", "m1", 0, COMMENT, existing);

        assertAll("обидва порушення",
                () -> assertTrue(errors.contains(DUPLICATE), "Порушення: " + errors),
                () -> assertTrue(errors.contains(RATING_OUT_OF_RANGE), "Порушення: " + errors),
                () -> assertEquals(2, errors.size(), "Порушення: " + errors));
    }

    @Test
    @DisplayName("BL-30 / розд. 5: null замість колекції existingVotes → IllegalArgumentException")
    void nullExistingVotesThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate("u1", "m1", 4, COMMENT, null));
    }
}
