package ua.kpi.tzyapz.autorate.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object верхньої панелі (GUI-10): форма входу, привітання, Profile, Logout.
 * Локатори зібрано в одному місці: зміна розмітки виправляється тільки тут.
 */
public class TopBar {

    public static final By LOGIN_INPUT = By.name("login");
    public static final By PASSWORD_INPUT = By.name("password");
    public static final By LOGIN_BUTTON = By.cssSelector("my-login button[type='submit']");
    public static final By REGISTER_LINK = By.cssSelector("my-login a[href='/register']");
    public static final By ERROR_LABEL = By.cssSelector("my-login .label-warning");
    public static final By GREETING = By.cssSelector("my-login span.nav-link");
    public static final By PROFILE_LINK = By.cssSelector("my-login a[href='/profile']");
    public static final By LOGOUT_LINK = By.linkText("Logout");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public TopBar(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /** Вводить логін і пароль у верхній панелі та натискає «Login». */
    public TopBar submitLogin(String login, String password) {
        WebElement loginInput = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_INPUT));
        loginInput.clear();
        loginInput.sendKeys(login);
        WebElement passwordInput = driver.findElement(PASSWORD_INPUT);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
        return this;
    }

    /** Чекає на привітання після успішного входу і повертає його текст («Hi, Test»). */
    public String greetingText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(GREETING)).getText().trim();
    }

    /** Чекає на повідомлення про невдалий вхід і повертає його текст. */
    public String errorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_LABEL)).getText().trim();
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_LINK)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_INPUT));
    }
}
