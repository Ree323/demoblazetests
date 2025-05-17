package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;
import utils.TestData;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for verifying About Us modal functionality
 */
public class AboutUsTest extends BaseTest {

    // Locators
    private final By aboutUsLink = By.xpath("//a[contains(text(),'About us')]");
    private final By videoModal = By.id("videoModal");
    private final By modalTitle = By.id("videoModalLabel");
    private final By videoElement = By.cssSelector("#videoModal video");
    private final By closeButton = By.cssSelector("#videoModal button[aria-label='Close']");
    private final By xButton = By.cssSelector("#videoModal .close");

    private void openAboutUsModal() {
        ExtentReportManager.logStep("Clicking 'About us' link");
        try {
            WebElement aboutUs = wait.until(ExpectedConditions.elementToBeClickable(aboutUsLink));
            aboutUs.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(videoModal));
            ExtentReportManager.logInfo("About Us modal opened successfully");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to open About Us modal: " + e.getMessage());
            throw e;
        }
    }
    /**
     * TC_ABT_001: Verify About Us modal opens and displays correct content
     */
    @Test
    @DisplayName("TC_ABT_001: Verify About Us modal content")
    public void testAboutUsModalContent() {
        ExtentReportManager.logStep("1. Open About Us modal");
        openAboutUsModal();

        ExtentReportManager.logStep("2. Verify modal content");
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(videoModal));

        assertAll("Modal content verification",
                () -> assertEquals("About us", modal.findElement(modalTitle).getText(),
                        "Modal title should match"),
                () -> assertTrue(modal.findElement(videoElement).isDisplayed(),
                        "Video element should be visible"),
                () -> assertTrue(modal.findElement(closeButton).isDisplayed(),
                        "Close button should be visible"),
                () -> assertTrue(modal.findElement(xButton).isDisplayed(),
                        "X button should be visible")
        );

        ExtentReportManager.logPass("All modal content verified successfully");
        ExtentReportManager.captureScreenshot("About Us Modal Content");
    }

    /**
     * TC_ABT_002: Verify modal closes using X button
     */
    @Test
    @DisplayName("TC_ABT_002: Verify modal closes with X button")
    public void testModalClosesWithXButton() {
        ExtentReportManager.logStep("1. Open About Us modal");
        openAboutUsModal();

        ExtentReportManager.logStep("2. Click X button");
        driver.findElement(xButton).click();

        ExtentReportManager.logStep("3. Verify modal closes");
        assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(videoModal)),
                "Modal should close after clicking X button");

        ExtentReportManager.logPass("Modal closed successfully with X button");
        ExtentReportManager.captureScreenshot("After X Button Click");
    }

    /**
     * TC_ABT_003: Verify modal closes using Close button
     */
    @Test
    @DisplayName("TC_ABT_003: Verify modal closes with Close button")
    public void testModalClosesWithCloseButton() {
        ExtentReportManager.logStep("1. Open About Us modal");
        openAboutUsModal();

        ExtentReportManager.logStep("2. Click Close button");
        driver.findElement(closeButton).click();

        ExtentReportManager.logStep("3. Verify modal closes");
        assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(videoModal)),
                "Modal should close after clicking Close button");

        ExtentReportManager.logPass("Modal closed successfully with Close button");
        ExtentReportManager.captureScreenshot("After Close Button Click");
    }

    /**
     * TC_ABT_004: Verify video element behavior (known issue)
     */
    @Test
    @DisplayName("TC_ABT_004: Verify video playback (known issue)")
    public void testVideoPlayback() {
        ExtentReportManager.logStep("1. Open About Us modal");
        openAboutUsModal();

        ExtentReportManager.logStep("2. Get video element");
        WebElement video = driver.findElement(videoElement);

        ExtentReportManager.logStep("3. Attempt playback (expected to fail)");
        ((JavascriptExecutor)driver).executeScript("arguments[0].play();", video);

        // Change to expect Selenium's TimeoutException instead
        assertThrows(org.openqa.selenium.TimeoutException.class, () -> {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(driver -> {
                        Object result = ((JavascriptExecutor)driver)
                                .executeScript("return !arguments[0].paused", video);
                        return result != null && (Boolean)result;
                    });
        }, "Video should fail to play (known issue)");

        ExtentReportManager.logPass("Verified known video playback issue");
        ExtentReportManager.captureScreenshot("Video Playback Attempt");
    }
}