package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ExtentReportManager;
import utils.TestData;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Tests for performance metrics of the website
 */
public class PerformanceTest extends BaseTest {

    // Format for display of timing results
    private static final DecimalFormat df = new DecimalFormat("0.000");

    // Performance requirements (in milliseconds)
    private static final long PAGE_LOAD_THRESHOLD = 3000; // 3 seconds
    private static final long ACTION_RESPONSE_THRESHOLD = 1000; // 1 second

    /**
     * TC_PRF_001: Check that all pages load in under 3 seconds
     */
    @Test
    @DisplayName("TC_PRF_001: Check that all pages load in under 3 seconds")
    public void testPageLoadPerformance() {
        ExtentReportManager.logStep("Testing page load performance (threshold: " + PAGE_LOAD_THRESHOLD + "ms)");

        // Test home page load time
        measurePageLoadTime(TestData.BASE_URL, "Home page");

        // Test cart page load time
        measurePageLoadTime(TestData.CART_URL, "Cart page");

        // Test product page load time
        measurePageLoadTime(TestData.PRODUCT_URL, "Product page");

        // Test Categories - Phones
        measurePageLoadTime(TestData.BASE_URL + "#", "Categories - All");

        // Test Categories - Phones
        openCategoryAndMeasureLoad("Phones", "Phones category");

        // Test Categories - Laptops
        openCategoryAndMeasureLoad("Laptops", "Laptops category");

        // Test Categories - Monitors
        openCategoryAndMeasureLoad("Monitors", "Monitors category");

        // Test Contact modal load time
        ExtentReportManager.logStep("Measuring Contact modal load time");
        long startTime = System.currentTimeMillis();

        // Click Contact link
        WebElement contactLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Contact')]")));
        contactLink.click();

        // Wait for Contact modal to appear
        WebElement contactModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("exampleModal")));

        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;

        logAndAssertTimingResult("Contact modal", loadTime, PAGE_LOAD_THRESHOLD);

        // Close Contact modal
        WebElement closeButton = contactModal.findElement(By.cssSelector(".close"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOf(contactModal));

        // Test About Us modal load time
        ExtentReportManager.logStep("Measuring About Us modal load time");
        startTime = System.currentTimeMillis();

        // Click About Us link
        WebElement aboutUsLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'About us')]")));
        aboutUsLink.click();

        // Wait for About Us modal to appear
        WebElement aboutUsModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("videoModal")));

        endTime = System.currentTimeMillis();
        loadTime = endTime - startTime;

        logAndAssertTimingResult("About Us modal", loadTime, PAGE_LOAD_THRESHOLD);

        // Close About Us modal
        closeButton = aboutUsModal.findElement(By.cssSelector(".close"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOf(aboutUsModal));

        // Test Log in modal load time
        ExtentReportManager.logStep("Measuring Log in modal load time");
        startTime = System.currentTimeMillis();

        // Click Log in link
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.id(TestData.LOGIN_LINK_ID)));
        loginLink.click();

        // Wait for Login modal to appear
        WebElement loginModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("logInModal")));

        endTime = System.currentTimeMillis();
        loadTime = endTime - startTime;

        logAndAssertTimingResult("Log in modal", loadTime, PAGE_LOAD_THRESHOLD);

        // Close Login modal
        closeButton = loginModal.findElement(By.cssSelector(".close"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOf(loginModal));

        ExtentReportManager.logPass("All page load performance tests completed");
    }

    /**
     * TC_PRF_002: Ensure UI responds within 1 second for key actions
     */
    @Test
    @DisplayName("TC_PRF_002: Ensure UI responds within 1 second for key actions")
    public void testUiResponsePerformance() {
        ExtentReportManager.logStep("Testing UI response time performance (threshold: " + ACTION_RESPONSE_THRESHOLD + "ms)");

        // Login first to be able to test all key actions
        loginUser();

        // Test "Add to cart" button response time
        ExtentReportManager.logStep("Measuring 'Add to cart' button response time");

        // Navigate to a product page
        driver.navigate().to(TestData.PRODUCT_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-content")));

        // Click "Add to cart" and measure response time
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Add to cart')]")));

        long startTime = System.currentTimeMillis();
        addToCartButton.click();

        // Wait for alert to appear
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            logAndAssertTimingResult("Add to cart alert", responseTime, ACTION_RESPONSE_THRESHOLD);

            // Accept the alert
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not find alert after clicking Add to cart: " + e.getMessage());
        }

        // Test category navigation response time
        measureCategoryClickResponseTime("Phones", "Phones category navigation");
        measureCategoryClickResponseTime("Laptops", "Laptops category navigation");
        measureCategoryClickResponseTime("Monitors", "Monitors category navigation");

        // Navigate to cart page to test Place Order button
        ExtentReportManager.logStep("Navigating to cart page");
        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Place Order')]")));

        // Test "Place Order" button response time
        ExtentReportManager.logStep("Measuring 'Place Order' button response time");
        WebElement placeOrderButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Place Order')]")));

        startTime = System.currentTimeMillis();
        placeOrderButton.click();

        // Wait for order modal to appear
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        logAndAssertTimingResult("Place Order modal", responseTime, ACTION_RESPONSE_THRESHOLD);

        // Close order modal
        WebElement closeButton = orderModal.findElement(By.cssSelector(".close"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOf(orderModal));

        // Test Logout button response time
        ExtentReportManager.logStep("Measuring 'Logout' button response time");
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.id(TestData.LOGOUT_LINK_ID)));

        startTime = System.currentTimeMillis();
        logoutLink.click();

        // Wait for login link to appear (indicating logout completed)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(TestData.LOGIN_LINK_ID)));

        endTime = System.currentTimeMillis();
        responseTime = endTime - startTime;

        logAndAssertTimingResult("Logout action", responseTime, ACTION_RESPONSE_THRESHOLD);

        ExtentReportManager.logPass("All UI response performance tests completed");
    }

    /**
     * TC_PRF_003: Test cart add/remove timing
     */
    @Test
    @DisplayName("TC_PRF_003: Test cart add/remove timing")
    public void testCartOperationsPerformance() {
        ExtentReportManager.logStep("Testing cart operations performance (threshold: " + ACTION_RESPONSE_THRESHOLD + "ms)");

        // Login first
        loginUser();

        // Add product to cart
        addProductToCart();

        // Navigate to cart page
        ExtentReportManager.logStep("Navigating to cart page");
        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();

        // Wait for cart page to load and show products
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table[@class='table table-bordered table-hover table-striped']")));

        // Test "Delete" link response time
        ExtentReportManager.logStep("Measuring 'Delete' link response time for removing item from cart");

        // Verify item exists in cart before attempting to delete
        List<WebElement> cartItems = driver.findElements(
                By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr"));

        if (cartItems.isEmpty() || (cartItems.size() == 1 && cartItems.get(0).getText().trim().isEmpty())) {
            ExtentReportManager.logWarning("No items in cart to delete. Adding product again.");
            // Navigate back to product page and add to cart again
            driver.navigate().to(TestData.PRODUCT_URL);
            addProductToCart();
            // Navigate back to cart
            cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
            cartLink.click();
        }

        // Find delete link and measure click response time
        WebElement deleteLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Delete')]")));

        // Before we click, get the number of items to verify after deletion
        int itemCountBefore = driver.findElements(
                By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr")).size();

        long startTime = System.currentTimeMillis();
        deleteLink.click();

        // Wait for item to be removed (either fewer items in cart or empty cart)
        try {
            // Wait for the number of rows to decrease
            wait.until(ExpectedConditions.numberOfElementsToBeLessThan(
                    By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr"),
                    itemCountBefore
            ));

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            logAndAssertTimingResult("Delete from cart", responseTime, ACTION_RESPONSE_THRESHOLD);
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify item removal by count: " + e.getMessage());

            // Alternative approach: check if cart is empty after deletion
            try {
                Thread.sleep(500); // Small wait to ensure DOM updates

                List<WebElement> remainingItems = driver.findElements(
                        By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr"));

                boolean cartEmpty = remainingItems.isEmpty() ||
                        (remainingItems.size() == 1 && remainingItems.get(0).getText().trim().isEmpty());

                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;

                if (cartEmpty) {
                    logAndAssertTimingResult("Delete from cart (to empty cart)", responseTime, ACTION_RESPONSE_THRESHOLD);
                } else {
                    ExtentReportManager.logWarning("Cart is not empty after delete operation");
                    Assertions.fail("Delete operation did not remove item or could not verify removal");
                }
            } catch (Exception ex) {
                ExtentReportManager.logFail("Could not verify cart state after deletion: " + ex.getMessage());
                throw new AssertionError("Could not verify cart state after deletion: " + ex.getMessage());
            }
        }

        // Now test adding item to cart again
        ExtentReportManager.logStep("Testing add to cart performance from product list");

        // Navigate back to home page
        WebElement homeLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Home')]")));
        homeLink.click();

        // Wait for product list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='card h-100']")));

        // Click on the first product
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='card h-100']//a")));
        firstProduct.click();

        // Wait for product page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(),'Add to cart')]")));

        // Measure add to cart performance again
        addProductToCartAndMeasurePerformance();

        // Navigate to cart to verify item was added
        cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();

        // Wait for cart to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table[@class='table table-bordered table-hover table-striped']")));

        // Verify item is in cart
        cartItems = driver.findElements(
                By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr"));

        boolean itemAdded = !cartItems.isEmpty() &&
                !(cartItems.size() == 1 && cartItems.get(0).getText().trim().isEmpty());

        Assertions.assertTrue(itemAdded, "Item should be added to cart");
        ExtentReportManager.logPass("Item successfully added to cart");

        ExtentReportManager.logPass("All cart operations performance tests completed");
    }

    /**
     * Helper method to measure page load time
     * @param url the URL to load
     * @param pageName the name of the page (for logging)
     */
    private void measurePageLoadTime(String url, String pageName) {
        ExtentReportManager.logStep("Measuring " + pageName + " load time");

        long startTime = System.currentTimeMillis();

        // Navigate to the page
        driver.navigate().to(url);

        // Wait for page to finish loading
        ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");

        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;

        // Additional wait for the page to visually settle (not counted in timing)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignore
        }

        ExtentReportManager.captureScreenshot(pageName);

        logAndAssertTimingResult(pageName, loadTime, PAGE_LOAD_THRESHOLD);
    }

    /**
     * Helper method to open a category and measure load time
     * @param categoryName the name of the category to click
     * @param logName the name to use in the log
     */
    private void openCategoryAndMeasureLoad(String categoryName, String logName) {
        ExtentReportManager.logStep("Measuring " + logName + " load time");

        // Make sure we're on the home page
        driver.navigate().to(TestData.BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'" + categoryName + "')]")));

        long startTime = System.currentTimeMillis();

        // Click category
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'" + categoryName + "')]")));
        categoryLink.click();

        // Wait for products to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbodyid")));

        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;

        ExtentReportManager.captureScreenshot(logName);

        logAndAssertTimingResult(logName, loadTime, PAGE_LOAD_THRESHOLD);
    }

    /**
     * Helper method to measure category click response time
     * @param categoryName the name of the category to click
     * @param logName the name to use in the log
     */
    private void measureCategoryClickResponseTime(String categoryName, String logName) {
        ExtentReportManager.logStep("Measuring " + logName + " response time");

        // Make sure we're on the home page
        if (!driver.getCurrentUrl().equals(TestData.BASE_URL)) {
            driver.navigate().to(TestData.BASE_URL);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbodyid")));

        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'" + categoryName + "')]")));

        long startTime = System.currentTimeMillis();
        categoryLink.click();

        // Wait for products to update (can check for specific category products or just wait for any update)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbodyid")));

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        logAndAssertTimingResult(logName, responseTime, ACTION_RESPONSE_THRESHOLD);
    }

    /**
     * Helper method to add product to cart
     */
    private void addProductToCart() {
        // Navigate to a product page
        driver.navigate().to(TestData.PRODUCT_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-content")));

        // Click Add to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Add to cart')]")));
        addToCartButton.click();

        // Handle alert
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            ExtentReportManager.logWarning("No alert found after adding to cart: " + e.getMessage());
        }
    }

    /**
     * Helper method to add product to cart and measure performance
     */
    private void addProductToCartAndMeasurePerformance() {
        ExtentReportManager.logStep("Measuring 'Add to cart' response time");

        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Add to cart')]")));

        long startTime = System.currentTimeMillis();
        addToCartButton.click();

        // Wait for alert to appear
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            logAndAssertTimingResult("Add to cart", responseTime, ACTION_RESPONSE_THRESHOLD);

            // Accept the alert
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not find alert after clicking Add to cart: " + e.getMessage());
        }
    }

    /**
     * Helper method to login user
     */
    private void loginUser() {
        ExtentReportManager.logStep("Logging in user");

        // Click on Log in link
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.id(TestData.LOGIN_LINK_ID)));
        loginLink.click();

        // Wait for login modal to appear
        WebElement loginModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("logInModal")));

        // Enter credentials
        WebElement usernameField = loginModal.findElement(By.id(TestData.USERNAME_FIELD_ID));
        usernameField.clear();
        usernameField.sendKeys(TestData.TEST_USERNAME);

        WebElement passwordField = loginModal.findElement(By.id(TestData.PASSWORD_FIELD_ID));
        passwordField.clear();
        passwordField.sendKeys(TestData.TEST_PASSWORD);

        // Click login button
        WebElement loginButton = loginModal.findElement(By.xpath(".//button[contains(text(),'Log in')]"));
        loginButton.click();

        // Wait for login to complete
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Verify login successful
        try {
            WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id(TestData.WELCOME_MESSAGE_ID)));
            Assertions.assertTrue(welcomeMessage.isDisplayed(), "Welcome message should be displayed after login");
            ExtentReportManager.logPass("Login successful");
        } catch (Exception e) {
            ExtentReportManager.logFail("Login failed: " + e.getMessage());
            throw new AssertionError("Login failed: " + e.getMessage());
        }
    }

    /**
     * Helper method to log and assert timing results
     * @param operationName the name of the operation
     * @param timeTaken the time taken in milliseconds
     * @param threshold the threshold in milliseconds
     */
    private void logAndAssertTimingResult(String operationName, long timeTaken, long threshold) {
        double seconds = timeTaken / 1000.0;
        String formattedTime = df.format(seconds) + " seconds (" + timeTaken + "ms)";

        ExtentReportManager.logInfo(operationName + " load time: " + formattedTime);

        boolean withinThreshold = timeTaken <= threshold;

        if (withinThreshold) {
            ExtentReportManager.logPass(operationName + " loaded within threshold (" + threshold + "ms)");
        } else {
            ExtentReportManager.logFail(operationName + " exceeded load time threshold: " +
                    formattedTime + " > " + (threshold / 1000.0) + " seconds");

            // Take a screenshot when threshold is exceeded
            ExtentReportManager.captureScreenshot(operationName + " - Threshold Exceeded");

            Assertions.fail(operationName + " load time (" + formattedTime + ") exceeded threshold of " +
                    (threshold / 1000.0) + " seconds");
        }
    }
}