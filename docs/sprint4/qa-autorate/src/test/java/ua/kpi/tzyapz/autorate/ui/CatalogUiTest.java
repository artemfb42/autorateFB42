package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести каталогу для відвідувача: TC-021, TC-022, TC-023, TC-027 спринта 3
 * (вимоги F-10, F-60, F-70, F-90, GUI-50). Вхід не потрібен.
 * Лічильники голосів на demo спільні й змінюються, тому тести не перевіряють конкретні числа (SRS, розд. 2.3).
 * Запуск: mvn test -DexcludedGroups=none -Dtest=CatalogUiTest
 */
@Tag("ui")
@DisplayName("АвтоРейт (Buggy Cars Rating): головна сторінка, марка, рейтинг, модель")
class CatalogUiTest extends UiTestBase {

    private static final By HOME_CARD_HEADERS = By.cssSelector("my-home h2.card-header");
    private static final By POPULAR_MAKE_LINK = By.cssSelector("my-home a[href^='/make/']");
    private static final By POPULAR_MODEL_LINK = By.cssSelector("my-home a[href^='/model/']");
    private static final By OVERALL_LINK = By.cssSelector("my-home a[href='/overall']");

    private static final By MAKE_TABLE_HEADERS = By.cssSelector("my-make table.cars thead th");
    private static final By MAKE_TABLE_ROWS = By.cssSelector("my-make table.cars tbody tr");
    private static final By OVERALL_TABLE_HEADERS = By.cssSelector("my-overall table.cars thead th");
    private static final By OVERALL_TABLE_ROWS = By.cssSelector("my-overall table.cars tbody tr");
    private static final By OVERALL_RANK_CELLS = By.cssSelector("my-overall table.cars tbody tr td:nth-child(4)");
    private static final By PAGER = By.cssSelector("my-pager");

    private static final By MODEL_SPECIFICATION_TITLE = By.xpath("//my-model//h4[normalize-space()='Specification']");
    private static final By MODEL_VOTE_TEXT = By.cssSelector("my-model p.card-text");
    private static final By COMMENT_FIELD = By.id("comment");
    private static final By VOTE_BUTTON = By.xpath("//my-model//button[normalize-space()='Vote!']");

    /** Чекає, доки таблиця домалюється (SPA), і повертає її рядки. */
    private List<WebElement> loadedRows(By rows) {
        return wait.until(d -> {
            List<WebElement> found = d.findElements(rows);
            return found.isEmpty() ? null : found;
        });
    }

    /** Заголовки стовпців без порожнього стовпця з мініатюрою. */
    private List<String> columnTitles(By headers) {
        return driver.findElements(headers).stream()
                .map(UiTestBase::text)
                .filter(title -> !title.isEmpty())
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("TC-021: головна сторінка — три блоки, клік відкриває сторінку марки, моделі, рейтингу")
    void homePageHasThreeBlocksThatOpenTheirPages() {
        open("/");
        clickable(POPULAR_MAKE_LINK);
        List<String> blocks = driver.findElements(HOME_CARD_HEADERS).stream()
                .map(UiTestBase::text).collect(Collectors.toList());
        assertIterableEquals(List.of("Popular Make", "Popular Model", "Overall Rating"), blocks);

        clickable(POPULAR_MAKE_LINK).click();
        wait.until(ExpectedConditions.urlContains("/make/"));
        boolean makeTableShown = !loadedRows(MAKE_TABLE_ROWS).isEmpty();

        open("/");
        clickable(POPULAR_MODEL_LINK).click();
        wait.until(ExpectedConditions.urlContains("/model/"));
        boolean specificationShown = visible(MODEL_SPECIFICATION_TITLE).isDisplayed();

        open("/");
        clickable(OVERALL_LINK).click();
        wait.until(ExpectedConditions.urlContains("/overall"));
        boolean overallTableShown = !loadedRows(OVERALL_TABLE_ROWS).isEmpty();

        assertAll("переходи з головної сторінки",
                () -> assertTrue(makeTableShown, "сторінка марки з таблицею моделей"),
                () -> assertTrue(specificationShown, "сторінка моделі з блоком Specification"),
                () -> assertTrue(overallTableShown, "сторінка загального рейтингу з таблицею"));
    }

    @Test
    @DisplayName("TC-022: сторінка марки — стовпці Model, Rank, Votes, Comments; ≤3 коментарі і «View more» у рядку")
    void makePageTableHasColumnsAndAtMostThreeComments() {
        open("/");
        clickable(POPULAR_MAKE_LINK).click();
        wait.until(ExpectedConditions.urlContains("/make/"));
        List<WebElement> rows = loadedRows(MAKE_TABLE_ROWS);

        assertAll("таблиця моделей марки",
                () -> assertIterableEquals(List.of("Model", "Rank", "Votes", "Comments"),
                        columnTitles(MAKE_TABLE_HEADERS)),
                () -> assertTrue(rows.stream().allMatch(
                                row -> row.findElements(By.cssSelector("p.comment")).size() <= 3),
                        "у кожному рядку не більше трьох коментарів"),
                () -> assertTrue(rows.stream().allMatch(
                                row -> !row.findElements(By.linkText("View more")).isEmpty()),
                        "у кожному рядку є посилання View more"));
    }

    @Test
    @DisplayName("TC-023: Overall Rating — стовпці, посилання на марку й модель, рядки за рангом за зростанням")
    void overallRatingTableHasColumnsLinksAndAscendingRanks() {
        open("/");
        clickable(OVERALL_LINK).click();
        wait.until(ExpectedConditions.urlContains("/overall"));
        List<WebElement> rows = loadedRows(OVERALL_TABLE_ROWS);
        List<Integer> ranks = driver.findElements(OVERALL_RANK_CELLS).stream()
                .map(cell -> Integer.parseInt(text(cell)))
                .collect(Collectors.toList());
        List<Integer> sortedRanks = ranks.stream().sorted().collect(Collectors.toList());

        assertAll("таблиця загального рейтингу",
                () -> assertIterableEquals(List.of("Make", "Model", "Rank", "Votes", "Engine", "Comments"),
                        columnTitles(OVERALL_TABLE_HEADERS)),
                () -> assertIterableEquals(sortedRanks, ranks),
                () -> assertTrue(rows.stream().allMatch(
                                row -> !row.findElements(By.cssSelector("td a[href^='/make/']")).isEmpty()),
                        "назва марки — посилання на сторінку марки"),
                () -> assertTrue(rows.stream().allMatch(
                                row -> !row.findElements(By.cssSelector("td a[href^='/model/']")).isEmpty()),
                        "назва моделі — посилання на сторінку моделі"),
                () -> assertTrue(text(driver.findElement(PAGER)).contains("page 1 of"),
                        "під таблицею є напис «page X of Y»"));
    }

    @Test
    @DisplayName("TC-027: відвідувач не бачить форми голосування на сторінці моделі")
    void visitorSeesLoginHintInsteadOfVoteForm() {
        open("/");
        clickable(POPULAR_MODEL_LINK).click();
        wait.until(ExpectedConditions.urlContains("/model/"));

        String voteBlockText = text(visible(MODEL_VOTE_TEXT));

        assertAll("блок Votes для відвідувача",
                () -> assertEquals("You need to be logged in to vote.", voteBlockText),
                () -> assertTrue(isAbsent(COMMENT_FIELD), "поля Comment не має бути"),
                () -> assertTrue(isAbsent(VOTE_BUTTON), "кнопки Vote! не має бути"));
    }
}
