package pages.modals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginModal {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By modalLocator = By.id("logInModal");
    private final By modalTitleLocator = By.id("logInModalLabel");
    private final By usernameFieldLocator = By.id("loginusername");
    private final By passwordFieldLocator = By.id("loginpassword");
    private final By loginButtonLocator = By.xpath("//button[contains(text(),'Log in')]");
    private final By closeButtonLocator = By.xpath("//div[@id='logInModal']//button[contains(text(),'Close')]");
    private final By xIconLocator = By.xpath("//div[@id='logInModal']//button[@class='close']");

    public LoginModal(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitForModalToBeVisible();
    }

    private void waitForModalToBeVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalLocator));
    }

    public boolean isDisplayed() {
        try {
            return driver.findElement(modalLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitleLocator)).getText();
    }

    public boolean isUsernameFieldDisplayed() {
        try {
            return driver.findElement(usernameFieldLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordFieldDisplayed() {
        try {
            return driver.findElement(passwordFieldLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginButtonDisplayed() {
        try {
            return driver.findElement(loginButtonLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCloseButtonDisplayed() {
        try {
            return driver.findElement(closeButtonLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isXIconDisplayed() {
        try {
            return driver.findElement(xIconLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterUsername(String username) {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameFieldLocator));
        usernameField.clear();
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordFieldLocator));
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButtonLocator)).click();
    }

    public void clickClose() {
        wait.until(ExpectedConditions.elementToBeClickable(closeButtonLocator)).click();
    }

    public void clickXIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(xIconLocator)).click();
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
}