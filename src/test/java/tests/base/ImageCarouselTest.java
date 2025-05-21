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

    @BeforeEach
    public void setUpTest(TestInfo testInfo) {
        ExtentReportManager.createTest(testInfo.getDisplayName(), "Image Carousel Test");
        ExtentReportManager.setDriver(driver);
    }

    @Nested
    @DisplayName("Carousel Presence and Basic Functionality Tests")
    class CarouselBasicTests {

        @Test
        @DisplayName("TC_CAR_001: Verify image carousel exists on home page")
        public void verifyCarouselPresence() {
            try {
                ExtentReportManager.logStep("Navigating to home page");
                driver.get(TestData.BASE_URL);
                ExtentReportManager.captureScreenshot("HomePageLoaded");

                ExtentReportManager.logStep("Verifying carousel presence");
                WebElement carousel = waitForElement(By.id(TestData.CAROUSEL_CONTAINER_ID));
                assertTrue(carousel.isDisplayed(), "Carousel container should be visible");
                ExtentReportManager.logPass("Carousel container is visible");

                List<WebElement> slides = driver.findElements(
                        By.cssSelector(TestData.CAROUSEL_SLIDES_CSS));
                assertFalse(slides.isEmpty(), "Carousel should have at least one slide");
                ExtentReportManager.logPass("Found " + slides.size() + " slides in carousel");

                WebElement activeSlide = waitForElement(
                        By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
                assertTrue(activeSlide.isDisplayed(), "Active slide should be visible");
                ExtentReportManager.logPass("Active slide is visible");

                ExtentReportManager.captureScreenshot("CarouselVerified");
            } catch (Exception e) {
                ExtentReportManager.logFail("Test failed: " + e.getMessage());
                ExtentReportManager.captureScreenshot("ErrorInCarouselPresenceTest");
                throw e;
            }
        }

        @Test
        @DisplayName("TC_CAR_002: Verify carousel navigation arrows work")
        public void verifyNavigationArrows() {
            try {
                ExtentReportManager.logStep("Navigating to home page");
                driver.get(TestData.BASE_URL);
                ExtentReportManager.captureScreenshot("InitialState");

                ExtentReportManager.logStep("Getting initial active image");
                WebElement initialActiveSlide = waitForElement(
                        By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
                String initialImageSrc = getImageSource(initialActiveSlide);
                ExtentReportManager.logInfo("Initial image source: " + initialImageSrc);

                ExtentReportManager.logStep("Testing next button");
                testNavigationButton(TestData.CAROUSEL_NEXT_BUTTON_CSS, "next", initialImageSrc);
                ExtentReportManager.logPass("Next button works correctly");
                ExtentReportManager.captureScreenshot("AfterNextButton");

                ExtentReportManager.logStep("Testing previous button");
                WebElement currentActiveSlide = waitForElement(
                        By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
                String currentImageSrc = getImageSource(currentActiveSlide);
                testNavigationButton(TestData.CAROUSEL_PREV_BUTTON_CSS, "previous", currentImageSrc);
                ExtentReportManager.logPass("Previous button works correctly");
                ExtentReportManager.captureScreenshot("AfterPrevButton");
            } catch (Exception e) {
                ExtentReportManager.logFail("Test failed: " + e.getMessage());
                ExtentReportManager.captureScreenshot("ErrorInNavigationTest");
                throw e;
            }
        }
    }

    @Nested
    @DisplayName("Carousel Advanced Functionality Tests")
    class CarouselAdvancedTests {

        @Test
        @DisplayName("TC_CAR_003: Verify carousel indicators navigate to specific slides")
        public void verifyIndicatorNavigation() {
            try {
                ExtentReportManager.logStep("Navigating to home page");
                driver.get(TestData.BASE_URL);
                ExtentReportManager.captureScreenshot("BeforeIndicatorTest");

                ExtentReportManager.logStep("Getting carousel indicators and slides");
                List<WebElement> indicators = waitForElements(
                        By.cssSelector(TestData.CAROUSEL_INDICATORS_CSS));
                List<WebElement> slides = waitForElements(
                        By.cssSelector(TestData.CAROUSEL_SLIDES_CSS));

                assertEquals(indicators.size(), slides.size(),
                        "Number of indicators should match number of slides");
                ExtentReportManager.logPass("Found " + indicators.size() + " indicators matching slides");

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
                    ExtentReportManager.logPass("Indicator " + (i + 1) + " works correctly");
                }

                ExtentReportManager.captureScreenshot("AfterIndicatorTest");
            } catch (Exception e) {
                ExtentReportManager.logFail("Test failed: " + e.getMessage());
                ExtentReportManager.captureScreenshot("ErrorInIndicatorTest");
                throw e;
            }
        }

        @Test
        @DisplayName("TC_CAR_004: Verify carousel automatically rotates images")
        public void verifyAutoRotation() {
            try {
                ExtentReportManager.logStep("Navigating to home page");
                driver.get(TestData.BASE_URL);
                ExtentReportManager.captureScreenshot("BeforeRotationTest");

                ExtentReportManager.logStep("Getting carousel slides");
                List<WebElement> slides = waitForElements(
                        By.cssSelector(TestData.CAROUSEL_SLIDES_CSS));
                assertTrue(slides.size() > 1, "Carousel should have multiple slides for rotation");
                ExtentReportManager.logPass("Found " + slides.size() + " slides");

                WebElement initialActiveSlide = waitForElement(
                        By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
                String initialImageSrc = getImageSource(initialActiveSlide);
                ExtentReportManager.logInfo("Initial image source: " + initialImageSrc);

                List<String> seenImageSources = new ArrayList<>();
                seenImageSources.add(initialImageSrc);

                ExtentReportManager.logStep("Observing carousel auto-rotation for " +
                        (OBSERVATION_PERIOD_MS/1000) + " seconds");
                waitForImageChange(initialImageSrc);

                WebElement currentActiveSlide = waitForElement(
                        By.cssSelector(TestData.CAROUSEL_ACTIVE_SLIDE_CSS));
                String currentImageSrc = getImageSource(currentActiveSlide);
                ExtentReportManager.logInfo("Current image source: " + currentImageSrc);

                assertNotEquals(initialImageSrc, currentImageSrc,
                        "Carousel should have automatically rotated to a different image");
                ExtentReportManager.logPass("Auto-rotation verified");

                ExtentReportManager.captureScreenshot("AfterRotationTest");
            } catch (Exception e) {
                ExtentReportManager.logFail("Test failed: " + e.getMessage());
                ExtentReportManager.captureScreenshot("ErrorInRotationTest");
                throw e;
            }
        }
    }

    // ===== HELPER METHODS (unchanged from original) =====
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
            Thread.sleep(1000); // Original timing preserved
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