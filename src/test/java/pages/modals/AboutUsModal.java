package pages.modals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AboutUsModal {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By modalLocator = By.id("videoModal");
    private final By titleLocator = By.id("videoModalLabel");
    private final By closeButtonLocator = By.cssSelector("#videoModal button[aria-label='Close']");
    private final By videoLocator = By.cssSelector("#videoModal video");

    // Constructor with WebDriver parameter
    public AboutUsModal(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void waitForVisibility() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalLocator));
    }

    public String getTitle() {
        return driver.findElement(titleLocator).getText();
    }

    public void close() {
        driver.findElement(closeButtonLocator).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalLocator));
    }

    public WebElement getVideoElement() {
        return driver.findElement(videoLocator);
    }
}