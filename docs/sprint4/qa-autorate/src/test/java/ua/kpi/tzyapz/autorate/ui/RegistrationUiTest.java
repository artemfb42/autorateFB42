package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ua.kpi.tzyapz.autorate.ui.pages.RegisterPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тести форми реєстрації: TC-001, TC-004, TC-005 спринта 3 (вимоги F-20, F-30, GUI-20).
 * Форма в тестах не надсилається — облікові записи на demo не створюються.
 * Запуск: mvn test -DexcludedGroups=none -Dtest=RegistrationUiTest
 */
@Tag("ui")
@DisplayName("АвтоРейт (Buggy Cars Rating): форма реєстрації")
class RegistrationUiTest extends UiTestBase {

    private RegisterPage page;

    @BeforeEach
    void openRegistrationForm() {
        open("/");
        page = new RegisterPage(driver, wait).openFromTopBar();
    }

    @Test
    @DisplayName("TC-001: сторінка реєстрації містить п'ять полів, «Cancel» і неактивну «Register»")
    void registrationFormHasAllFieldsAndDisabledRegister() {
        assertAll("форма реєстрації",
                () -> assertTrue(driver.findElement(RegisterPage.USERNAME).isDisplayed(), "поле Login"),
                () -> assertTrue(driver.findElement(RegisterPage.FIRST_NAME).isDisplayed(), "поле First Name"),
                () -> assertTrue(driver.findElement(RegisterPage.LAST_NAME).isDisplayed(), "поле Last Name"),
                () -> assertTrue(driver.findElement(RegisterPage.PASSWORD).isDisplayed(), "поле Password"),
                () -> assertTrue(driver.findElement(RegisterPage.CONFIRM_PASSWORD).isDisplayed(),
                        "поле Confirm Password"),
                () -> assertEquals("Register", text(page.registerButton())),
                () -> assertFalse(page.registerButton().isEnabled(), "«Register» має бути неактивною"),
                () -> assertEquals("Cancel", text(driver.findElement(RegisterPage.CANCEL_LINK))));
    }

    @Test
    @DisplayName("TC-004: порожнє обов'язкове поле Login — повідомлення і неактивна «Register»")
    void emptyRequiredFieldShowsErrorAndKeepsRegisterDisabled() {
        page.type(RegisterPage.USERNAME, "a")
                .eraseLastCharacter(RegisterPage.USERNAME);

        String error = page.visibleErrorText(RegisterPage.USERNAME_ERROR);

        assertAll("порожній Login",
                () -> assertEquals("Login is required", error),
                () -> assertEquals("", driver.findElement(RegisterPage.USERNAME).getAttribute("value")),
                () -> assertFalse(page.registerButton().isEnabled(), "«Register» має бути неактивною"));
    }

    @Test
    @DisplayName("TC-005: розбіжність Password і Confirm Password блокує реєстрацію")
    void passwordMismatchShowsErrorAndKeepsRegisterDisabled() {
        page.type(RegisterPage.USERNAME, "fb42_tc005")
                .type(RegisterPage.FIRST_NAME, "Test")
                .type(RegisterPage.LAST_NAME, "Tester")
                .type(RegisterPage.PASSWORD, "Pass1234a@")
                .type(RegisterPage.CONFIRM_PASSWORD, "Pass1235a@");

        String error = page.visibleErrorText(RegisterPage.CONFIRM_PASSWORD_ERROR);

        assertAll("паролі не збігаються",
                () -> assertEquals("Passwords do not match", error),
                () -> assertFalse(page.registerButton().isEnabled(), "«Register» має бути неактивною"),
                () -> assertTrue(driver.getCurrentUrl().contains("/register"), "користувач лишається на формі"));
    }
}
