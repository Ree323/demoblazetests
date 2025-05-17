package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import tests.base.BaseTest;
import utils.ExtentReportManager;
import utils.TestData;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DemoBlaze Carousel Test Suite")
public class ImageCarouselTest extends BaseTest {
    private static final int CAROUSEL_ROTATION_INTERVAL_MS = 5000;
    private static final int OBSERVATION_PERIOD_MS = CAROUSEL_ROTATION_INTERVAL_MS * 3;

    @Nested
    @DisplayName("Carousel Presence and Basic Functionality Tests")
    class CarouselBasicTests {

        @Test
        @DisplayName("TC_CAR_001: Verify image carousel exists on home page")
        public void verifyCarouselPresence() {
            ExtentReportManager.logStep("Navigating to home page");
            driver.get(TestData.BASE_URL);
            ExtentReportManager.captureScreenshot("Home Page Loaded");

            ExtentReportManager.logStep("Verifying carousel presence");
            WebElement carousel = waitForElement(By.id(TestData.CAROUSEL_CONTAINER_ID));
            assertTrue(carousel.isDisplayed(), "Carousel container should be visible");

            List<WebElement> slides = driver.findElements(
                    By.cssSelector(TestData.CAROUSEL_SLIDES_CSS));
            assertFalse(slides.isEmpty(), "Carousel should have at least one slide");

            WebElement activeSlide = waitForElement(
                    By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
            assertTrue(activeSlide.isDisplayed(), "Active slide should be visible");

            ExtentReportManager.logPass("Carousel presence verified with " + slides.size() + " slides");
        }

        @Test
        @DisplayName("TC_CAR_002: Verify carousel navigation arrows work")
        public void verifyNavigationArrows() {
            ExtentReportManager.logStep("Navigating to home page");
            driver.get(TestData.BASE_URL);

            ExtentReportManager.logStep("Getting initial active image");
            WebElement initialActiveSlide = waitForElement(
                    By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
            String initialImageSrc = getImageSource(initialActiveSlide);

            ExtentReportManager.logStep("Testing next button");
            testNavigationButton(TestData.CAROUSEL_NEXT_BUTTON_CSS, "next", initialImageSrc);

            ExtentReportManager.logStep("Testing previous button");
            WebElement currentActiveSlide = waitForElement(
                    By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
            String currentImageSrc = getImageSource(currentActiveSlide);
            testNavigationButton(TestData.CAROUSEL_PREV_BUTTON_CSS, "previous", currentImageSrc);

            ExtentReportManager.logPass("Both navigation arrows working correctly");
            ExtentReportManager.captureScreenshot("After Navigation Tests");
        }
    }

    @Nested
    @DisplayName("Carousel Advanced Functionality Tests")
    class CarouselAdvancedTests {

        @Test
        @DisplayName("TC_CAR_003: Verify carousel indicators navigate to specific slides")
        public void verifyIndicatorNavigation() {
            ExtentReportManager.logStep("Navigating to home page");
            driver.get(TestData.BASE_URL);

            ExtentReportManager.logStep("Getting carousel indicators and slides");
            List<WebElement> indicators = waitForElements(
                    By.cssSelector(TestData.CAROUSEL_INDICATORS_CSS));
            List<WebElement> slides = waitForElements(
                    By.cssSelector(TestData.CAROUSEL_SLIDES_CSS));

            assertEquals(indicators.size(), slides.size(),
                    "Number of indicators should match number of slides");

            WebElement initialActiveSlide = waitForElement(
                    By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
            String initialImageSrc = getImageSource(initialActiveSlide);

            for (int i = 0; i < indicators.size(); i++) {
                ExtentReportManager.logStep("Testing indicator " + (i + 1));
                WebElement indicator = indicators.get(i);
                indicator.click();
                waitForCarouselTransition();

                WebElement activeSlide = waitForElement(
                        By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
                String activeImageSrc = getImageSource(activeSlide);

                WebElement expectedSlide = slides.get(i);
                String expectedImageSrc = getImageSource(expectedSlide);

                assertEquals(expectedImageSrc, activeImageSrc,
                        "Indicator " + (i + 1) + " should navigate to slide " + (i + 1));
            }

            ExtentReportManager.logPass("All indicators navigation verified");
        }

        @Test
        @DisplayName("TC_CAR_004: Verify carousel automatically rotates images")
        public void verifyAutoRotation() {
            ExtentReportManager.logStep("Navigating to home page");
            driver.get(TestData.BASE_URL);

            ExtentReportManager.logStep("Getting carousel slides");
            List<WebElement> slides = waitForElements(
                    By.cssSelector(TestData.CAROUSEL_SLIDES_CSS));
            assertTrue(slides.size() > 1, "Carousel should have multiple slides for rotation");

            WebElement initialActiveSlide = waitForElement(
                    By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
            String initialImageSrc = getImageSource(initialActiveSlide);

            List<String> seenImageSources = new ArrayList<>();
            seenImageSources.add(initialImageSrc);

            ExtentReportManager.logStep("Observing carousel auto-rotation for " +
                    (OBSERVATION_PERIOD_MS/1000) + " seconds");

            waitForImageChange(initialImageSrc);

            WebElement currentActiveSlide = waitForElement(
                    By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
            String currentImageSrc = getImageSource(currentActiveSlide);

            assertNotEquals(initialImageSrc, currentImageSrc,
                    "Carousel should have automatically rotated to a different image");

            ExtentReportManager.logPass("Carousel auto-rotation verified");
            ExtentReportManager.captureScreenshot("After Auto-Rotation Test");
        }
    }

    // ===== HELPER METHODS =====
    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private List<WebElement> waitForElements(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    private void testNavigationButton(String buttonSelector, String direction, String currentImageSrc) {
        WebElement navButton = waitForElement(By.cssSelector(buttonSelector));
        navButton.click();
        waitForCarouselTransition();

        WebElement newActiveSlide = waitForElement(
                By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
        String newImageSrc = getImageSource(newActiveSlide);

        assertNotEquals(currentImageSrc, newImageSrc,
                "Image should change after clicking " + direction + " button");
    }

    private void waitForCarouselTransition() {
        try {
            Thread.sleep(1000); // Brief pause for transition animation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitForImageChange(String originalSrc) {
        new WebDriverWait(driver, Duration.ofMillis(OBSERVATION_PERIOD_MS))
                .until(driver -> {
                    String currentSrc = getImageSource(
                            driver.findElement(By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS)));
                    return !currentSrc.equals(originalSrc);
                });
    }

    private String getImageSource(WebElement element) {
        return element.findElement(By.tagName("img")).getDomProperty("src");
    }
}