package ua.kpi.tzyapz.autorate.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Спільний життєвий цикл UI-тестів: кожен тест отримує власний браузер і закриває його.
 *
 * Параметри запуску (необов'язкові):
 *   -Dautorate.baseUrl=https://buggy.justtestit.org   адреса demo-застосунку
 *   -Dheadless=false                                  показати вікно браузера (для демо)
 *   -Dautorate.login=... -Dautorate.password=...      тестовий обліковий запис (за замовчуванням —
 *                                                     fb42_reg1 з docs/sprint3/execution-log.xlsx, аркуш Accounts)
 */
abstract class UiTestBase {

    protected static final String BASE_URL =
            System.getProperty("autorate.baseUrl", "https://buggy.justtestit.org").replaceAll("/+$", "");
    protected static final String LOGIN = System.getProperty("autorate.login", "fb42_reg1");
    protected static final String PASSWORD = System.getProperty("autorate.password", "Abcdef12@");
    /** First Name облікового запису fb42_reg1 — показується у привітанні «Hi, Test». */
    protected static final String FIRST_NAME = System.getProperty("autorate.firstName", "Test");

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (!"false".equalsIgnoreCase(System.getProperty("headless"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Відкриває сторінку demo за відносним шляхом ("/" — головна). */
    protected void open(String path) {
        driver.get(BASE_URL + path);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean isAbsent(By locator) {
        return driver.findElements(locator).isEmpty();
    }

    /** Текст елемента без нерозривних пробілів і крайніх пробілів. */
    protected static String text(WebElement element) {
        return element.getText().replace(' ', ' ').trim();
    }
}
