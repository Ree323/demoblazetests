package pages.modals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SignUpModal {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By modalLocator = By.id("signInModal");
    private final By modalTitleLocator = By.id("signInModalLabel");
    private final By usernameFieldLocator = By.id("sign-username");
    private final By passwordFieldLocator = By.id("sign-password");
    private final By signupButtonLocator = By.xpath("//button[contains(text(),'Sign up')]");
    private final By closeButtonLocator = By.xpath("//div[@id='signInModal']//button[contains(text(),'Close')]");
    private final By xIconLocator = By.xpath("//div[@id='signInModal']//button[@class='close']");

    public SignUpModal(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalLocator));
    }

    public String getModalTitle() {
        return driver.findElement(modalTitleLocator).getText();
    }

    public boolean isUsernameFieldDisplayed() {
        return driver.findElement(usernameFieldLocator).isDisplayed();
    }

    public boolean isPasswordFieldDisplayed() {
        return driver.findElement(passwordFieldLocator).isDisplayed();
    }

    public boolean isSignUpButtonDisplayed() {
        return driver.findElement(signupButtonLocator).isDisplayed();
    }

    public boolean isCloseButtonDisplayed() {
        return driver.findElement(closeButtonLocator).isDisplayed();
    }

    public boolean isXIconDisplayed() {
        return driver.findElement(xIconLocator).isDisplayed();
    }

    public void enterUsername(String username) {
        WebElement usernameField = driver.findElement(usernameFieldLocator);
        usernameField.clear();
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement passwordField = driver.findElement(passwordFieldLocator);
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickSignUp() {
        driver.findElement(signupButtonLocator).click();
    }

    public void clickClose() {
        driver.findElement(closeButtonLocator).click();
    }

    public void clickXIcon() {
        driver.findElement(xIconLocator).click();
    }

    public String getAlertText() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return driver.switchTo().alert().getText();
        } catch (Exception e) {
            return null;
        }
    }

    public void acceptAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // Alert may have been dismissed already
        }
    }

    public boolean isDisplayed() {
        try {
            return driver.findElement(modalLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}