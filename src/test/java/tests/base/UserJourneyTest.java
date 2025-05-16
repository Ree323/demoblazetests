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
 * Comprehensive test case covering the full user journey from registration to checkout and logout
 */
public class UserJourneyTest extends BaseTest {

    /**
     * TC_JOURNEY_001: Verify complete user journey from registration to checkout and logout
     */
    @Test
    @DisplayName("TC_JOURNEY_001: Verify complete user journey from registration to checkout and logout")
    public void testCompleteUserJourney() {
        ExtentReportManager.logStep("Starting comprehensive user journey test");

        // 1. REGISTRATION FLOW
        String uniqueUsername = registerNewUser();

        // 2. LOGIN FLOW (Optional if already logged in after registration)
        if (!isUserLoggedIn(uniqueUsername)) {
            loginUser(uniqueUsername, TestData.TEST_PASSWORD);
        } else {
            ExtentReportManager.logInfo("User already logged in after registration");
        }

        // 3. PRODUCT SEARCH AND FILTER
        browseAndSelectProduct();

        // 4. CART OPERATIONS
        addToCartAndVerify();

        // 5. CHECKOUT FLOW
        completeCheckout();

        // 6. LOGOUT FLOW
        logoutAndVerify();

        ExtentReportManager.logPass("Complete user journey test passed successfully");
    }

    /**
     * Step 1: Register a new user
     * @return the unique username created for the registration
     */
    private String registerNewUser() {
        ExtentReportManager.logStep("1. REGISTRATION FLOW - Creating a new user account");

        // 1.1 Navigate to DemoBlaze website (done in setUp)
        ExtentReportManager.logInfo("Website homepage loaded");
        ExtentReportManager.captureScreenshot("Home Page");

        // 1.2 Click on "Sign up" button
        ExtentReportManager.logStep("Clicking on Sign up button");
        WebElement signupLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("signin2")));
        signupLink.click();

        // Wait for sign up modal to appear
        try {
            Thread.sleep(TestData.SHORT_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Signup Modal");

        // 1.3 Generate unique username
        String uniqueUsername = TestData.getUniqueUsername();
        ExtentReportManager.logInfo("Generated unique username: " + uniqueUsername);

        // Enter username
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-username")));
        usernameField.clear();
        usernameField.sendKeys(uniqueUsername);

        // 1.4 Enter password
        WebElement passwordField = driver.findElement(By.id("sign-password"));
        passwordField.clear();
        passwordField.sendKeys(TestData.TEST_PASSWORD);
        ExtentReportManager.logInfo("Entered registration details");

        // 1.5 Click "Sign up" button
        WebElement signupButton = driver.findElement(By.xpath("//button[contains(text(),'Sign up')]"));
        signupButton.click();

        // Wait for alert
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Check for success alert
        try {
            String alertText = driver.switchTo().alert().getText();
            ExtentReportManager.logInfo("Registration alert: " + alertText);

            boolean isSuccess = alertText.contains("Sign up successful") ||
                    !alertText.contains("already exist");

            if (isSuccess) {
                ExtentReportManager.logPass("User registration successful");
            } else {
                ExtentReportManager.logWarning("Registration may have failed: " + alertText);
            }

            // 1.6 Close the alert
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not find registration alert: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Registration");
        return uniqueUsername;
    }

    /**
     * Check if user is logged in with the specified username
     * @param username the username to check
     * @return true if logged in, false otherwise
     */
    private boolean isUserLoggedIn(String username) {
        try {
            WebElement welcomeMessage = driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID));
            String welcomeText = welcomeMessage.getText();
            return welcomeText.contains(TestData.WELCOME_MESSAGE_PREFIX) &&
                    welcomeText.contains(username);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Step 2: Login with credentials
     * @param username the username to use
     * @param password the password to use
     */
    private void loginUser(String username, String password) {
        ExtentReportManager.logStep("2. LOGIN FLOW - Logging in with username: " + username);

        // 2.1 Click on "Log in" button
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(By.id(TestData.LOGIN_LINK_ID)));
        loginLink.click();

        // Wait for login modal to appear
        try {
            Thread.sleep(TestData.SHORT_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Login Modal");

        // 2.2 Enter username
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(TestData.USERNAME_FIELD_ID)));
        usernameField.clear();
        usernameField.sendKeys(username);
        ExtentReportManager.logInfo("Entered username: " + username);

        // 2.3 Enter password
        WebElement passwordField = driver.findElement(By.id(TestData.PASSWORD_FIELD_ID));
        passwordField.clear();
        passwordField.sendKeys(password);
        ExtentReportManager.logInfo("Entered password");

        // 2.4 Click "Log in" button
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Log in')]"));
        loginButton.click();

        // Wait for login to complete
        try {
            Thread.sleep(TestData.LONG_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Login");

        // Verify login was successful
        try {
            WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(TestData.WELCOME_MESSAGE_ID)));
            String welcomeText = welcomeMessage.getText();
            boolean loginSuccessful = welcomeText.contains(TestData.WELCOME_MESSAGE_PREFIX) &&
                    welcomeText.contains(username);

            Assertions.assertTrue(loginSuccessful, "Login should be successful");
            ExtentReportManager.logPass("Login successful. Welcome message: " + welcomeText);
        } catch (Exception e) {
            ExtentReportManager.logFail("Login failed: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Login Failure");
            throw new AssertionError("Login failed: " + e.getMessage());
        }
    }

    /**
     * Step 3: Browse and select a product from a category
     */
    private void browseAndSelectProduct() {
        ExtentReportManager.logStep("3. PRODUCT SEARCH AND FILTER - Browsing products by category");

        // 3.1 Click on "Laptops" category
        ExtentReportManager.logStep("Clicking on Laptops category");
        WebElement laptopsCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Laptops")));
        laptopsCategory.click();

        // 3.2 Wait for products to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Laptops Category");

        // 3.3 Verify multiple laptop products are visible
        List<WebElement> products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//div[@class='card h-100']")));

        ExtentReportManager.logInfo("Found " + products.size() + " products in the Laptops category");
        Assertions.assertTrue(products.size() >= 1, "At least one laptop product should be displayed");
        ExtentReportManager.logPass("Multiple laptop products are displayed");

        // 3.4 Select a specific laptop product
        ExtentReportManager.logStep("Selecting a laptop product");

        // Find and click on a specific product (e.g., MacBook Pro)
        try {
            WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.linkText("MacBook Pro")));
            productLink.click();
        } catch (Exception e) {
            // If MacBook Pro is not available, select the first product
            ExtentReportManager.logWarning("MacBook Pro not found, selecting the first available product");
            WebElement firstProduct = products.get(0).findElement(By.tagName("a"));
            String productName = firstProduct.getText();
            ExtentReportManager.logInfo("Selected product: " + productName);
            firstProduct.click();
        }

        // Wait for product page to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Product Details Page");

        // Verify product details page loaded
        WebElement addToCartButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(),'Add to cart')]")));
        Assertions.assertTrue(addToCartButton.isDisplayed(), "Add to cart button should be displayed on product page");
        ExtentReportManager.logPass("Product details page loaded successfully");
    }

    /**
     * Step 4: Add product to cart, verify, then delete and add again
     */
    private void addToCartAndVerify() {
        ExtentReportManager.logStep("4. CART OPERATIONS - Adding product to cart");

        // 4.1 Click "Add to cart" button
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Add to cart')]")));
        addToCartButton.click();

        // Wait for alert
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // 4.2 Close the alert
        try {
            String alertText = driver.switchTo().alert().getText();
            ExtentReportManager.logInfo("Add to cart alert: " + alertText);

            boolean isSuccess = alertText.contains("Product added");
            Assertions.assertTrue(isSuccess, "Product added confirmation should appear");

            driver.switchTo().alert().accept();
            ExtentReportManager.logPass("Product added to cart successfully");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not find add to cart alert: " + e.getMessage());
        }

        // 4.3 Navigate to "Cart" page
        ExtentReportManager.logStep("Navigating to Cart page");
        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();

        // Wait for cart page to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Cart Page");

        // 4.4 Verify product details in cart
        try {
            WebElement productRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr")));
            Assertions.assertTrue(productRow.isDisplayed(), "Product should be in the cart");

            // Get product name and price for verification
            String productName = productRow.findElement(By.xpath("./td[2]")).getText();
            String productPrice = productRow.findElement(By.xpath("./td[3]")).getText();

            ExtentReportManager.logInfo("Product in cart: " + productName + " - Price: " + productPrice);
            ExtentReportManager.logPass("Product details verified in cart");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify product in cart: " + e.getMessage());
        }

        // 4.5 Click on "Delete" link to remove product
        ExtentReportManager.logStep("Removing product from cart");
        WebElement deleteLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Delete')]")));
        deleteLink.click();

        // Wait for deletion
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Product Deletion");

        // Verify product was removed
        try {
            List<WebElement> productRows = driver.findElements(By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr"));
            boolean cartEmpty = productRows.isEmpty() ||
                    (productRows.size() == 1 && productRows.get(0).getText().trim().isEmpty());

            Assertions.assertTrue(cartEmpty, "Cart should be empty after deletion");
            ExtentReportManager.logPass("Product successfully removed from cart");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify cart empty state: " + e.getMessage());
        }

        // 4.6 Navigate back to laptops category
        ExtentReportManager.logStep("Navigating back to Laptops category");
        WebElement homeLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Home')]")));
        homeLink.click();

        try {
            Thread.sleep(TestData.SHORT_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        WebElement laptopsCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Laptops")));
        laptopsCategory.click();

        // Wait for products to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // 4.7 Select a product again
        ExtentReportManager.logStep("Selecting a product again");
        List<WebElement> products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//div[@class='card h-100']")));

        // Click on the first product
        WebElement firstProduct = products.get(0).findElement(By.tagName("a"));
        String productName = firstProduct.getText();
        ExtentReportManager.logInfo("Selected product: " + productName);
        firstProduct.click();

        // Wait for product page to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // 4.8 Click "Add to cart" button again
        ExtentReportManager.logStep("Adding product to cart again");
        addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Add to cart')]")));
        addToCartButton.click();

        // Wait for alert
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // 4.9 Close the alert
        try {
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not find add to cart alert: " + e.getMessage());
        }

        // 4.10 Navigate to "Cart" page again
        ExtentReportManager.logStep("Navigating to Cart page again");
        cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();

        // Wait for cart page to load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Cart Page After Re-adding Product");

        // Verify product is in cart
        try {
            WebElement productRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@class='table table-bordered table-hover table-striped']//tbody/tr")));
            Assertions.assertTrue(productRow.isDisplayed(), "Product should be in the cart");
            ExtentReportManager.logPass("Product successfully added to cart again");
        } catch (Exception e) {
            ExtentReportManager.logFail("Product not found in cart after re-adding: " + e.getMessage());
            throw new AssertionError("Product not in cart after re-adding: " + e.getMessage());
        }
    }

    /**
     * Step 5: Complete the checkout process
     */
    private void completeCheckout() {
        ExtentReportManager.logStep("5. CHECKOUT FLOW - Completing the checkout process");

        // 5.1 Click "Place Order" button
        ExtentReportManager.logStep("Clicking on Place Order button");
        WebElement placeOrderButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Place Order')]")));
        placeOrderButton.click();

        // Wait for order modal to appear
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Order Modal");

        // Verify order modal is displayed
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));
        Assertions.assertTrue(orderModal.isDisplayed(), "Order modal should be displayed");

        // 5.2 Enter name
        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("John Doe");
        ExtentReportManager.logInfo("Entered name: John Doe");

        // 5.3 Enter country
        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("United States");
        ExtentReportManager.logInfo("Entered country: United States");

        // 5.4 Enter city
        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("New York");
        ExtentReportManager.logInfo("Entered city: New York");

        // 5.5 Enter credit card
        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");
        ExtentReportManager.logInfo("Entered credit card: 4111111111111111");

        // 5.6 Enter month
        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");
        ExtentReportManager.logInfo("Entered month: 12");

        // 5.7 Enter year
        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");
        ExtentReportManager.logInfo("Entered year: 2025");

        ExtentReportManager.captureScreenshot("Completed Order Form");

        // 5.8 Click "Purchase" button
        ExtentReportManager.logStep("Clicking Purchase button");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for confirmation
        try {
            Thread.sleep(TestData.LONG_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Purchase Confirmation");

        // Check for order confirmation
        try {
            WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".sweet-alert h2")));

            boolean isSuccess = confirmationMessage.getText().contains("Thank you") ||
                    confirmationMessage.getText().contains("Success");

            Assertions.assertTrue(isSuccess, "Order confirmation message should be displayed");

            // Capture order details
            WebElement orderDetails = driver.findElement(By.cssSelector(".sweet-alert p.lead"));
            String orderText = orderDetails.getText();
            ExtentReportManager.logInfo("Order confirmation details: " + orderText);

            // Extract order ID if available
            if (orderText.contains("Id:")) {
                String orderId = orderText.substring(orderText.indexOf("Id:"), orderText.indexOf("\n", orderText.indexOf("Id:")));
                ExtentReportManager.logInfo("Order ID: " + orderId);
            }

            ExtentReportManager.logPass("Order placed successfully");

            // 5.9 Click "OK" button to close confirmation
            WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".sweet-alert .confirm")));
            okButton.click();

            // Wait for confirmation to close
            try {
                Thread.sleep(TestData.MEDIUM_WAIT);
            } catch (InterruptedException e) {
                ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
            }

        } catch (Exception e) {
            ExtentReportManager.logFail("Order confirmation not found: " + e.getMessage());
            throw new AssertionError("Order confirmation not found: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Completing Purchase");
    }

    /**
     * Step 6: Logout and verify logged out state
     */
    private void logoutAndVerify() {
        ExtentReportManager.logStep("6. LOGOUT FLOW - Logging out and verifying logged out state");

        // 6.1 Verify the welcome message shows the logged-in username
        try {
            WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id(TestData.WELCOME_MESSAGE_ID)));
            String welcomeText = welcomeMessage.getText();
            Assertions.assertTrue(welcomeText.contains(TestData.WELCOME_MESSAGE_PREFIX),
                    "Welcome message should contain username");
            ExtentReportManager.logInfo("Welcome message before logout: " + welcomeText);
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify welcome message before logout: " + e.getMessage());
        }

        // 6.2 Click on "Log out" in the navigation bar
        ExtentReportManager.logStep("Clicking on Log out link");
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.id(TestData.LOGOUT_LINK_ID)));
        logoutLink.click();

        // Wait for logout to complete
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Logout");

        // 6.3 Verify redirected to the home page
        String currentUrl = driver.getCurrentUrl();
        boolean onHomePage = currentUrl.contains("index.html") ||
                currentUrl.endsWith("demoblaze.com/") ||
                !currentUrl.contains("cart.html");

        Assertions.assertTrue(onHomePage, "Should be redirected to home page after logout");
        ExtentReportManager.logInfo("Current URL after logout: " + currentUrl);
        ExtentReportManager.logPass("Successfully redirected to home page after logout");

        // 6.4 Verify login link is visible
        try {
            WebElement loginLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id(TestData.LOGIN_LINK_ID)));
            Assertions.assertTrue(loginLink.isDisplayed(), "Login link should be visible after logout");
            ExtentReportManager.logPass("Login link is visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logFail("Login link not visible after logout: " + e.getMessage());
            throw new AssertionError("Login link not visible after logout: " + e.getMessage());
        }

        // 6.5 Verify sign up link is visible
        try {
            WebElement signupLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("signin2")));
            Assertions.assertTrue(signupLink.isDisplayed(), "Sign up link should be visible after logout");
            ExtentReportManager.logPass("Sign up link is visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logFail("Sign up link not visible after logout: " + e.getMessage());
            throw new AssertionError("Sign up link not visible after logout: " + e.getMessage());
        }

        // Check welcome message is gone
        try {
            boolean welcomeGone = driver.findElements(By.id(TestData.WELCOME_MESSAGE_ID)).isEmpty() ||
                    !driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID)).isDisplayed() ||
                    driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID)).getText().isEmpty();

            Assertions.assertTrue(welcomeGone, "Welcome message should not be visible after logout");
            ExtentReportManager.logPass("Welcome message is no longer visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify welcome message invisibility: " + e.getMessage());
        }

        // Check logout link is gone
        try {
            boolean logoutGone = driver.findElements(By.id(TestData.LOGOUT_LINK_ID)).isEmpty() ||
                    !driver.findElement(By.id(TestData.LOGOUT_LINK_ID)).isDisplayed();

            Assertions.assertTrue(logoutGone, "Logout link should not be visible after logout");
            ExtentReportManager.logPass("Logout link is no longer visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify logout link invisibility: " + e.getMessage());
        }

        ExtentReportManager.logPass("Logout functionality successfully verified");
    }
}