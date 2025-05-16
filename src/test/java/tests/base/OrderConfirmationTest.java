package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.HomePage;
import pages.modals.LoginModal;
import utils.ExtentReportManager;
import utils.TestData;

import java.util.List;

/**
 * Tests for the order confirmation functionality
 */
public class OrderConfirmationTest extends BaseTest {

    /**
     * TC_OCF_001: Submit a complete order and verify functionality
     */
    @Test
    @DisplayName("TC_OCF_001: Submit a complete order and verify functionality")
    public void testCompleteOrderSubmission() {
        ExtentReportManager.logStep("Testing complete order submission functionality");

        // Prepare the cart with a product and open the order form
        loginAndAddProductToCart();
        navigateToCartPage();
        openOrderModal();

        // Fill all fields with valid data
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        ExtentReportManager.logStep("Entering valid order information");

        // Fill name field
        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("John Doe");
        ExtentReportManager.logInfo("Entered name: John Doe");

        // Fill country field
        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("United States");
        ExtentReportManager.logInfo("Entered country: United States");

        // Fill city field
        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("New York");
        ExtentReportManager.logInfo("Entered city: New York");

        // Fill credit card field
        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4242424242424242");
        ExtentReportManager.logInfo("Entered credit card: 4242424242424242");

        // Fill month field
        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");
        ExtentReportManager.logInfo("Entered month: 12");

        // Fill year field
        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");
        ExtentReportManager.logInfo("Entered year: 2025");

        ExtentReportManager.captureScreenshot("Order Form Filled");

        // Click Purchase button
        ExtentReportManager.logStep("Submitting the order");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for confirmation to appear
        try {
            Thread.sleep(TestData.LONG_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Purchase Submission");

        // Verify confirmation is displayed
        try {
            WebElement confirmationBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".sweet-alert")));

            boolean isConfirmationVisible = confirmationBox.isDisplayed();
            Assertions.assertTrue(isConfirmationVisible, "Order confirmation should be displayed");

            // Check for success message in confirmation
            WebElement confirmationTitle = confirmationBox.findElement(By.cssSelector("h2"));
            boolean hasSuccessMessage = confirmationTitle.getText().contains("Thank you") ||
                    confirmationTitle.getText().contains("Success");

            Assertions.assertTrue(hasSuccessMessage,
                    "Confirmation should contain 'Thank you' or 'Success' message");

            ExtentReportManager.logPass("Order confirmation is displayed successfully");

        } catch (Exception e) {
            ExtentReportManager.logFail("Order confirmation not found: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Confirmation Failure");
            throw new AssertionError("Order confirmation not found: " + e.getMessage());
        }
    }

    /**
     * TC_OCF_002: Check if confirmation message shows after order submission
     */
    @Test
    @DisplayName("TC_OCF_002: Check if confirmation message shows after order submission")
    public void testConfirmationMessageDetails() {
        ExtentReportManager.logStep("Testing confirmation message details after order submission");

        // Prepare the cart with a product and submit an order
        loginAndAddProductToCart();
        navigateToCartPage();
        submitValidOrder();

        // Verify confirmation message contents
        try {
            WebElement confirmationBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".sweet-alert")));

            // Verify confirmation title
            WebElement confirmationTitle = confirmationBox.findElement(By.cssSelector("h2"));
            String titleText = confirmationTitle.getText();
            ExtentReportManager.logInfo("Confirmation title: " + titleText);
            Assertions.assertTrue(titleText.contains("Thank you") || titleText.contains("Success"),
                    "Confirmation title should contain 'Thank you' or 'Success'");

            // Verify confirmation details - should contain order ID and amount
            WebElement confirmationDetails = confirmationBox.findElement(By.cssSelector("p.lead"));
            String detailsText = confirmationDetails.getText();
            ExtentReportManager.logInfo("Confirmation details: " + detailsText);

            boolean containsOrderId = detailsText.contains("Id:") || detailsText.contains("ID:");
            boolean containsAmount = detailsText.contains("Amount:") || detailsText.toLowerCase().contains("total:");

            Assertions.assertTrue(containsOrderId, "Confirmation should contain order ID");
            Assertions.assertTrue(containsAmount, "Confirmation should contain order amount");

            // Extract and log the order ID if present
            if (containsOrderId) {
                String orderIdSection = detailsText.substring(detailsText.indexOf("Id:"));
                String orderId = orderIdSection.substring(0, orderIdSection.indexOf("\n") > 0 ?
                        orderIdSection.indexOf("\n") : orderIdSection.length());
                ExtentReportManager.logInfo("Order ID: " + orderId.trim());
            }

            // Extract and log the amount if present
            if (containsAmount) {
                int amountIndex = detailsText.indexOf("Amount:") > 0 ?
                        detailsText.indexOf("Amount:") : detailsText.toLowerCase().indexOf("total:");
                String amountSection = detailsText.substring(amountIndex);
                String amount = amountSection.substring(0, amountSection.indexOf("\n") > 0 ?
                        amountSection.indexOf("\n") : amountSection.length());
                ExtentReportManager.logInfo("Order amount: " + amount.trim());
            }

            ExtentReportManager.logPass("Confirmation message contains order ID and amount as expected");

        } catch (Exception e) {
            ExtentReportManager.logFail("Could not verify confirmation details: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Confirmation Details Failure");
            throw new AssertionError("Could not verify confirmation details: " + e.getMessage());
        }
    }

    /**
     * TC_OCF_003: Verify "OK" button on confirmation modal closes it
     */
    @Test
    @DisplayName("TC_OCF_003: Verify 'OK' button on confirmation modal closes it")
    public void testConfirmationOkButtonFunctionality() {
        ExtentReportManager.logStep("Testing 'OK' button functionality on confirmation modal");

        // Prepare the cart with a product and submit an order
        loginAndAddProductToCart();
        navigateToCartPage();
        submitValidOrder();

        // Verify confirmation is displayed first
        WebElement confirmationBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".sweet-alert")));
        Assertions.assertTrue(confirmationBox.isDisplayed(), "Confirmation modal should be displayed");

        // Take a screenshot of the confirmation
        ExtentReportManager.captureScreenshot("Confirmation Modal Before OK");

        // Click the OK button
        ExtentReportManager.logStep("Clicking 'OK' button on confirmation modal");
        WebElement okButton = confirmationBox.findElement(By.cssSelector(".confirm"));
        okButton.click();

        // Wait for confirmation to close
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking OK Button");

        // Verify confirmation is closed
        try {
            boolean confirmationClosed = wait.until(ExpectedConditions.invisibilityOf(confirmationBox));
            Assertions.assertTrue(confirmationClosed, "Confirmation modal should be closed after clicking OK");
            ExtentReportManager.logPass("Confirmation modal closed successfully after clicking OK");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify confirmation closure: " + e.getMessage());

            // Alternative check - try to find the confirmation element
            boolean confirmationStillVisible = driver.findElements(By.cssSelector(".sweet-alert")).size() > 0 &&
                    driver.findElement(By.cssSelector(".sweet-alert")).isDisplayed();

            Assertions.assertFalse(confirmationStillVisible,
                    "Confirmation modal should not be visible after clicking OK");
        }

        // Verify user is on empty cart page
        try {
            // Check current URL to verify we're on cart page
            boolean onCartPage = driver.getCurrentUrl().contains("cart.html");

            if (onCartPage) {
                // Check if cart is empty
                List<WebElement> productRows = driver.findElements(
                        By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr"));

                boolean cartEmpty = productRows.isEmpty() ||
                        (productRows.size() == 1 && productRows.get(0).getText().trim().isEmpty());

                Assertions.assertTrue(cartEmpty, "Cart should be empty after completing order");
                ExtentReportManager.logPass("User sees empty cart after closing confirmation");
            } else {
                // If we're not on cart page, we're likely on home page which is also acceptable
                boolean onHomePage = driver.getCurrentUrl().contains("index.html") ||
                        driver.getCurrentUrl().endsWith("demoblaze.com/");

                Assertions.assertTrue(onHomePage,
                        "User should be on home page or cart page after closing confirmation");
                ExtentReportManager.logPass("User redirected to home page after closing confirmation");
            }
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify cart state: " + e.getMessage());

            // Alternative check - just verify we're not seeing the confirmation anymore
            boolean confirmationGone = driver.findElements(By.cssSelector(".sweet-alert")).isEmpty() ||
                    !driver.findElement(By.cssSelector(".sweet-alert")).isDisplayed();

            Assertions.assertTrue(confirmationGone,
                    "Confirmation should not be visible after clicking OK");
            ExtentReportManager.logPass("Confirmation is no longer visible after clicking OK");
        }
    }

    /**
     * Helper method to login and add a product to cart
     */
    private void loginAndAddProductToCart() {
        ExtentReportManager.logStep("Logging in and adding product to cart");

        // Login
        HomePage homePage = new HomePage(driver);
        LoginModal loginModal = homePage.clickLoginLink();

        loginModal.enterUsername(TestData.TEST_USERNAME);
        loginModal.enterPassword(TestData.TEST_PASSWORD);
        ExtentReportManager.captureScreenshot("Login Form Filled");

        loginModal.clickLoginButton();

        // Wait for login to complete
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Verify login was successful
        WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(TestData.WELCOME_MESSAGE_ID)));
        Assertions.assertTrue(welcomeMessage.isDisplayed(), "Welcome message should be displayed after login");

        // Add a product to cart
        ExtentReportManager.logStep("Adding product to cart");

        // Find and click on the Samsung Galaxy S6 product (or first available product)
        WebElement productLink;
        try {
            productLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.linkText("Samsung galaxy s6")));
        } catch (Exception e) {
            // If specific product not found, select the first product
            List<WebElement> products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//div[@class='card h-100']")));

            productLink = products.get(0).findElement(By.tagName("a"));
        }

        productLink.click();

        // Wait for product page to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Click Add to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Add to cart')]")));
        addToCartButton.click();

        // Handle alert
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        try {
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            ExtentReportManager.logWarning("No alert found after adding to cart: " + e.getMessage());
        }

        ExtentReportManager.logPass("Product added to cart successfully");
    }

    /**
     * Helper method to navigate to cart page
     */
    private void navigateToCartPage() {
        ExtentReportManager.logStep("Navigating to cart page");

        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();

        // Wait for cart page to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Cart Page");

        // Verify cart page loaded
        WebElement placeOrderButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Place Order')]")));
        Assertions.assertTrue(placeOrderButton.isDisplayed(),
                "Place Order button should be displayed on cart page");

        ExtentReportManager.logPass("Successfully navigated to cart page");
    }

    /**
     * Helper method to open order modal
     */
    private void openOrderModal() {
        ExtentReportManager.logStep("Opening order modal");

        WebElement placeOrderButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Place Order')]")));
        placeOrderButton.click();

        // Wait for modal to appear
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Order Modal");

        // Verify modal is displayed
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));
        Assertions.assertTrue(orderModal.isDisplayed(), "Order modal should be displayed");

        ExtentReportManager.logPass("Order modal opened successfully");
    }

    /**
     * Helper method to submit a valid order
     */
    private void submitValidOrder() {
        ExtentReportManager.logStep("Submitting a valid order");

        // Open order modal
        openOrderModal();

        // Fill all fields
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        // Fill name field
        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("John Doe");

        // Fill country field
        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("United States");

        // Fill city field
        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("New York");

        // Fill credit card field
        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4242424242424242");

        // Fill month field
        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        // Fill year field
        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");

        // Click Purchase button
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for confirmation to appear
        try {
            Thread.sleep(TestData.LONG_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Verify confirmation is displayed
        WebElement confirmationBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".sweet-alert")));
        Assertions.assertTrue(confirmationBox.isDisplayed(), "Order confirmation should be displayed");

        ExtentReportManager.captureScreenshot("Order Confirmation");
        ExtentReportManager.logPass("Valid order submitted successfully");
    }
}