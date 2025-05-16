package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.modals.SignUpModal;
import pages.modals.LoginModal;

import java.time.Duration;

public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By signupNavLinkLocator = By.id("signin2");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public SignUpModal clickSignUpLink() {
        wait.until(ExpectedConditions.elementToBeClickable(signupNavLinkLocator)).click();
        return new SignUpModal(driver);
    }

    public LoginModal clickLoginLink() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login2"))).click();
        return new LoginModal(driver);
    }
}

