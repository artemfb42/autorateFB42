package ua.kpi.tzyapz.autorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести класу VoteValidator (SRS 1.1, розділ 5).
 * BL-30, BL-40 — автор Фощій Дмитро (СМ); BL-50, BL-80 — автор Бурмай Юлія (ТА).
 * У тестах одного правила решта параметрів коректна, щоб тест мав одну причину впасти:
 * для BL-30/BL-40 коментар «Гарна модель»; для BL-50/BL-80 оцінка 3 і порожній existingVotes.
 */
@Tag("unit")
@DisplayName("VoteValidator — перевірка голосу (BL-30, BL-40, BL-50, BL-80)")
class VoteValidatorTest {

    private static final String DUPLICATE = "повторний голос";
    private static final String RATING_OUT_OF_RANGE = "оцінка поза межами 1..5";
    private static final String COMMENT = "Гарна модель";
    private static final String TOO_LONG = "коментар задовгий";
    private static final String STOP_WORD = "недопустиме слово";

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

    // BUG-016 (GitHub Issue: 22): v1 порівнює лише userId, без modelId.
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

    // BUG-017 (GitHub Issue: 21): v1 відхиляє оцінку 5 (верхня межа).
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

    // ============ BL-50, BL-80: довжина коментаря і стоп-список — автор Бурмай Юлія ============

    private List<String> validateComment(String comment) {
        return validator.validate("u1", "m1", 3, comment, List.of());
    }

    @ParameterizedTest(name = "BL-50: коментар із {0} символів приймається")
    @ValueSource(ints = {1, 199, 200})
    void commentUpToTwoHundredCharactersIsAccepted(int length) {
        // arrange
        String comment = "a".repeat(length);
        // act
        List<String> errors = validateComment(comment);
        // assert
        assertTrue(errors.isEmpty(), "Порушення: " + errors);
    }

    @ParameterizedTest(name = "BL-50: коментар із {0} символів → «коментар задовгий»")
    @ValueSource(ints = {201, 202, 1000})
    void commentLongerThanTwoHundredCharactersIsRejected(int length) {
        String comment = "a".repeat(length);

        List<String> errors = validateComment(comment);

        assertTrue(errors.contains(TOO_LONG), "Порушення: " + errors);
    }

    @Test
    @DisplayName("BL-50: 200 кириличних символів приймається (довжина рахується в символах, не в байтах)")
    void twoHundredCyrillicCharactersAreAccepted() {
        String comment = "я".repeat(200);

        List<String> errors = validateComment(comment);

        assertFalse(errors.contains(TOO_LONG), "Порушення: " + errors);
    }

    @Test
    @Disabled("RD-09 очікує рішення замовника: SRS 1.0 і модуль v1 дозволяють порожній коментар, "
            + "SRS 1.1 (BL-50) вимагає повідомлення «коментар обов'язковий». Увімкнути після погодження RD-09.")
    @DisplayName("BL-50 (SRS 1.1, RD-09): порожній коментар, null і лише пробіли → «коментар обов'язковий»")
    void emptyCommentIsRejectedAsRequired() {
        assertAll("порожній коментар",
                () -> assertTrue(validateComment("").contains("коментар обов'язковий")),
                () -> assertTrue(validateComment(null).contains("коментар обов'язковий")),
                () -> assertTrue(validateComment("   ").contains("коментар обов'язковий")));
    }

    @ParameterizedTest(name = "BL-80: «{0}» містить окреме стоп-слово → «недопустиме слово»")
    @ValueSource(strings = {
            "this is spam",
            "SCAM!",
            "Best Casino ever",
            "це казино, не рейтинг",
            "КАЗИНО",
            "anti-spam",
            "spam"
    })
    void commentWithStopWordIsRejected(String comment) {
        List<String> errors = validateComment(comment);

        assertTrue(errors.contains(STOP_WORD), "Порушення: " + errors);
    }

    @ParameterizedTest(name = "BL-80: «{0}» — стоп-слово лише як частина іншого слова → приймається")
    @ValueSource(strings = {
            "spammer",
            "scammers everywhere",
            "casinos",
            "казинове місто",
            "Чудова машина, швидка і надійна"
    })
    void stopWordInsideAnotherWordIsAccepted(String comment) {
        List<String> errors = validateComment(comment);

        assertFalse(errors.contains(STOP_WORD), "Порушення: " + errors);
    }

    @Test
    @DisplayName("BL-80: власний стоп-список через конструктор замінює список за замовчуванням")
    void customStopListReplacesDefault() {
        VoteValidator custom = new VoteValidator(List.of("Lada"));

        assertAll("власний стоп-список",
                () -> assertTrue(custom.validate("u1", "m1", 3, "стара lada", List.of()).contains(STOP_WORD),
                        "слово з власного списку має відхилятися без урахування регістру"),
                () -> assertFalse(custom.validate("u1", "m1", 3, "this is spam", List.of()).contains(STOP_WORD),
                        "слово зі списку за замовчуванням більше не заборонене"));
    }

    @Test
    @DisplayName("BL-80 / розд. 5: null замість колекції стоп-слів → IllegalArgumentException")
    void nullStopListThrows() {
        assertThrows(IllegalArgumentException.class, () -> new VoteValidator(null));
    }
}