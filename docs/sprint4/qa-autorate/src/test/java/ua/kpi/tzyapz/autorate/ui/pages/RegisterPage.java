package ua.kpi.tzyapz.autorate.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page Object сторінки реєстрації (F-20, F-30, GUI-20). */
public class RegisterPage {

    public static final By USERNAME = By.id("username");
    public static final By FIRST_NAME = By.id("firstName");
    public static final By LAST_NAME = By.id("lastName");
    public static final By PASSWORD = By.id("password");
    public static final By CONFIRM_PASSWORD = By.id("confirmPassword");
    public static final By REGISTER_BUTTON = By.cssSelector("my-register button[type='submit']");
    public static final By CANCEL_LINK = By.cssSelector("my-register a[role='button']");
    /** Повідомлення валідації — сусідній блок одразу після відповідного поля. */
    public static final By USERNAME_ERROR = By.cssSelector("#username + div.alert-danger");
    public static final By CONFIRM_PASSWORD_ERROR = By.cssSelector("#confirmPassword + div.alert-danger");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public RegisterPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /** Відкриває форму реєстрації кнопкою «Register» верхньої панелі (як у кроках тест-кейсів). */
    public RegisterPage openFromTopBar() {
        wait.until(ExpectedConditions.elementToBeClickable(TopBar.REGISTER_LINK)).click();
        wait.until(ExpectedConditions.urlContains("/register"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
        return this;
    }

    public RegisterPage type(By field, String value) {
        driver.findElement(field).sendKeys(value);
        return this;
    }

    /** Стирає один символ клавішею Backspace — так само, як користувач у тест-кейсі. */
    public RegisterPage eraseLastCharacter(By field) {
        driver.findElement(field).sendKeys(Keys.BACK_SPACE);
        return this;
    }

    public WebElement registerButton() {
        return driver.findElement(REGISTER_BUTTON);
    }

    public String visibleErrorText(By errorLocator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator)).getText().trim();
    }
}
