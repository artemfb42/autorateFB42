package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.kpi.tzyapz.autorate.ui.pages.TopBar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести входу і виходу: TC-017, TC-018, TC-019 спринта 3 (вимоги F-40, F-50, GUI-30, U-20, S-10).
 * Обліковий запис: fb42_reg1 (аркуш Accounts журналу спринта 3) або -Dautorate.login / -Dautorate.password.
 * Запуск: mvn test -DexcludedGroups=none -Dtest=LoginUiTest
 */
@Tag("ui")
@DisplayName("АвтоРейт (Buggy Cars Rating): вхід і вихід")
class LoginUiTest extends UiTestBase {

    private static final By VOTE_NEEDS_LOGIN_TEXT = By.cssSelector("my-model p.card-text");
    private static final By COMMENT_FIELD = By.id("comment");
    private static final By POPULAR_MODEL_LINK = By.cssSelector("my-home a[href^='/model/']");

    private TopBar topBar;

    @BeforeEach
    void openHomePage() {
        open("/");
        topBar = new TopBar(driver, wait);
        visible(TopBar.LOGIN_INPUT);
    }

    @Test
    @DisplayName("TC-018: невідомий логін — повідомлення про помилку текстом на сторінці, сеанс не створено")
    void unknownLoginShowsErrorAndDoesNotCreateSession() {
        topBar.submitLogin("nouser_zz9", "Abcdef12");

        String error = topBar.errorText();

        // Точний текст повідомлення F-40 очікує погодження (RD-05), тому перевіряємо незмінну частину.
        assertAll("невдалий вхід",
                () -> assertTrue(error.startsWith("Invalid"), "Текст повідомлення: " + error),
                () -> assertTrue(isAbsent(TopBar.LOGOUT_LINK), "кнопки Logout не має бути"),
                () -> assertTrue(isAbsent(TopBar.PROFILE_LINK), "посилання Profile не має бути"),
                () -> assertTrue(driver.findElement(TopBar.LOGIN_INPUT).isDisplayed(), "форма входу лишається"));
    }

    @Test
    @DisplayName("TC-017: після успішного входу повідомлення про попередню невдалу спробу відсутнє")
    void errorMessageDisappearsAfterSuccessfulLogin() {
        // передумова: невдала спроба з неправильним паролем показала повідомлення
        topBar.submitLogin(LOGIN, "Wrong1234@");
        assertTrue(topBar.errorText().startsWith("Invalid"));

        topBar.submitLogin(LOGIN, PASSWORD);
        String greeting = topBar.greetingText();

        assertAll("успішний вхід",
                () -> assertEquals("Hi, " + FIRST_NAME, greeting),
                () -> assertTrue(driver.findElement(TopBar.PROFILE_LINK).isDisplayed(), "посилання Profile"),
                () -> assertTrue(driver.findElement(TopBar.LOGOUT_LINK).isDisplayed(), "кнопка Logout"),
                () -> assertTrue(isAbsent(TopBar.ERROR_LABEL), "повідомлення про помилку має зникнути"));
    }

    @Test
    @DisplayName("TC-019: Logout завершує сеанс — панель відвідувача, форма голосування недоступна")
    void logoutEndsSessionAndHidesVoteForm() {
        // передумова: користувач увійшов і відкрив профіль
        topBar.submitLogin(LOGIN, PASSWORD);
        clickable(TopBar.PROFILE_LINK).click();
        wait.until(ExpectedConditions.urlContains("/profile"));

        topBar.logout();

        assertAll("панель відвідувача",
                () -> assertTrue(driver.findElement(TopBar.LOGIN_INPUT).isDisplayed(), "поле Login"),
                () -> assertTrue(driver.findElement(TopBar.PASSWORD_INPUT).isDisplayed(), "поле пароля"),
                () -> assertTrue(driver.findElement(TopBar.REGISTER_LINK).isDisplayed(), "кнопка Register"),
                () -> assertTrue(isAbsent(TopBar.LOGOUT_LINK), "кнопки Logout не має бути"),
                () -> assertTrue(isAbsent(TopBar.PROFILE_LINK), "посилання Profile не має бути"));

        // сторінка моделі після виходу: замість форми голосування — текст для відвідувача
        open("/");
        clickable(POPULAR_MODEL_LINK).click();
        String voteBlockText = text(visible(VOTE_NEEDS_LOGIN_TEXT));

        assertAll("голосування недоступне",
                () -> assertEquals("You need to be logged in to vote.", voteBlockText),
                () -> assertTrue(isAbsent(COMMENT_FIELD), "поля Comment не має бути"));
    }
}
