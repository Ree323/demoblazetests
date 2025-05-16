package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.HomePage;
import pages.modals.LoginModal;
import tests.base.BaseTest;
import utils.ExtentReportManager;
import utils.TestData;

import java.util.Arrays;
import java.util.List;

public class LoginTest extends BaseTest {

    // Use the specific username and password for all tests
    private final String VALID_USERNAME = TestData.TEST_USERNAME; // "Rama27"
    private final String VALID_PASSWORD = TestData.TEST_PASSWORD; // "rama123"

    // For case sensitivity test - using the same credentials
    private final String CASE_SENSITIVE_USERNAME = TestData.TEST_USERNAME; // "Rama27"
    private final String CASE_SENSITIVE_PASSWORD = TestData.TEST_PASSWORD; // "rama123"

    // For session persistence test - using the same credentials
    private final String PERSIST_USERNAME = TestData.TEST_USERNAME; // "Rama27"
    private final String PERSIST_PASSWORD = TestData.TEST_PASSWORD; // "rama123"

    /**
     * TC_LOG_001: Verify login form displays correctly
     */
    @Test
    @DisplayName("TC_LOG_001: Verify login form displays correctly")
    public void testLoginFormDisplays() {
        ExtentReportManager.logStep("Testing login form from multiple pages");

        // Test from home page
        ExtentReportManager.logStep("Testing from home page");
        HomePage homePage = new HomePage(driver);
        LoginModal loginModal = homePage.clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal on Home Page");

        // Verify modal elements
        verifyLoginModalElements(loginModal);

        // Close the modal
        loginModal.clickClose();

        // Test from cart page
        ExtentReportManager.logStep("Testing from cart page");
        driver.navigate().to("https://www.demoblaze.com/cart.html");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("Cart Page");

        loginModal = new HomePage(driver).clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal on Cart Page");

        // Verify modal elements
        verifyLoginModalElements(loginModal);

        // Close the modal
        loginModal.clickClose();

        // Test from product page
        ExtentReportManager.logStep("Testing from product page");
        driver.navigate().to("https://www.demoblaze.com/prod.html?idp_=1");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("Product Page");

        loginModal = new HomePage(driver).clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal on Product Page");

        // Verify modal elements
        verifyLoginModalElements(loginModal);

        ExtentReportManager.logPass("Login form displays correctly on all pages");
    }

    /**
     * Helper method to verify login modal elements
     */
    private void verifyLoginModalElements(LoginModal loginModal) {
        // Verify modal title
        String modalTitle = loginModal.getModalTitle();
        Assertions.assertEquals("Log in", modalTitle, "Modal title should be 'Log in'");
        ExtentReportManager.logPass("Login modal title verified: " + modalTitle);

        // Verify username field
        Assertions.assertTrue(loginModal.isUsernameFieldDisplayed(), "Username field should be displayed");
        ExtentReportManager.logPass("Username field is displayed");

        // Verify password field
        Assertions.assertTrue(loginModal.isPasswordFieldDisplayed(), "Password field should be displayed");
        ExtentReportManager.logPass("Password field is displayed");

        // Verify login button
        Assertions.assertTrue(loginModal.isLoginButtonDisplayed(), "Login button should be displayed");
        ExtentReportManager.logPass("Login button is displayed");

        // Verify close button
        Assertions.assertTrue(loginModal.isCloseButtonDisplayed(), "Close button should be displayed");
        ExtentReportManager.logPass("Close button is displayed");

        // Verify X icon
        Assertions.assertTrue(loginModal.isXIconDisplayed(), "X close icon should be displayed");
        ExtentReportManager.logPass("X close icon is displayed");
    }

    /**
     * TC_LOG_002: Verify successful login with valid credentials
     */
    @Test
    @DisplayName("TC_LOG_002: Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        ExtentReportManager.logStep("Testing successful login with valid credentials");

        ExtentReportManager.logInfo("Using username: " + VALID_USERNAME + " and password: " + VALID_PASSWORD);

        HomePage homePage = new HomePage(driver);
        LoginModal loginModal = homePage.clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal");

        // Enter credentials and login
        ExtentReportManager.logStep("Entering valid credentials");
        loginModal.enterUsername(VALID_USERNAME);
        loginModal.enterPassword(VALID_PASSWORD);
        ExtentReportManager.captureScreenshot("Login Form Filled");

        ExtentReportManager.logStep("Clicking Login button");
        loginModal.clickLoginButton();

        // Wait for login to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("After Login");

        // Verify login was successful by checking for welcome message
        try {
            WebElement welcomeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("nameofuser")));
            String welcomeMessage = welcomeElement.getText();

            Assertions.assertTrue(welcomeMessage.contains("Welcome"), "Welcome message should be displayed");
            Assertions.assertTrue(welcomeMessage.contains(VALID_USERNAME), "Welcome message should contain the username");

            ExtentReportManager.logPass("Login successful. Welcome message displayed: " + welcomeMessage);
        } catch (Exception e) {
            ExtentReportManager.logFail("Welcome message not found after login: " + e.getMessage());
            Assertions.fail("Login failed, welcome message not displayed");
        }
    }

    /**
     * TC_LOG_003: Verify failed login with invalid credentials
     */
    @Test
    @DisplayName("TC_LOG_003: Verify failed login with invalid credentials")
    public void testFailedLogin() {
        ExtentReportManager.logStep("Testing failed login with invalid credentials");

        // Test non-existent username
        ExtentReportManager.logStep("Testing with non-existent username");
        HomePage homePage = new HomePage(driver);
        LoginModal loginModal = homePage.clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal");

        String nonExistentUser = "invaliduser_" + System.currentTimeMillis();
        loginModal.enterUsername(nonExistentUser);
        loginModal.enterPassword("anypassword");
        ExtentReportManager.captureScreenshot("Login Form with Invalid Username");

        loginModal.clickLoginButton();

        // Check for the expected error message
        String alertText = loginModal.getAlertText();
        ExtentReportManager.logInfo("Alert message: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("does not exist"),
                "Alert should indicate that user does not exist");
        ExtentReportManager.logPass("System correctly displayed error for non-existent user");

        loginModal.acceptAlert();

        // Close and reopen modal for next test
        loginModal.clickClose();
        driver.navigate().refresh();

        // Test wrong password for existing user
        ExtentReportManager.logStep("Testing with valid username but wrong password");
        loginModal = homePage.clickLoginLink();

        loginModal.enterUsername(VALID_USERNAME);
        loginModal.enterPassword("wrongpassword");
        ExtentReportManager.captureScreenshot("Login Form with Wrong Password");

        loginModal.clickLoginButton();

        // Check for the expected error message
        alertText = loginModal.getAlertText();
        ExtentReportManager.logInfo("Alert message: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("Wrong password"),
                "Alert should indicate wrong password");
        ExtentReportManager.logPass("System correctly displayed error for wrong password");

        loginModal.acceptAlert();

        ExtentReportManager.logPass("Invalid credentials test completed successfully");
    }

    /**
     * TC_LOG_004: Verify empty field validation during login
     */
    @Test
    @DisplayName("TC_LOG_004: Verify empty field validation during login")
    public void testEmptyFieldValidation() {
        ExtentReportManager.logStep("Testing empty field validation during login");

        HomePage homePage = new HomePage(driver);

        // Test empty username
        ExtentReportManager.logStep("Testing with empty username");
        LoginModal loginModal = homePage.clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal");

        loginModal.enterUsername("");
        loginModal.enterPassword(VALID_PASSWORD);
        ExtentReportManager.captureScreenshot("Login Form with Empty Username");

        loginModal.clickLoginButton();

        // Check for the expected error message
        String alertText = loginModal.getAlertText();
        ExtentReportManager.logInfo("Alert message: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("fill out"),
                "Alert should require filling out fields");
        ExtentReportManager.logPass("Empty username validation works correctly");

        loginModal.acceptAlert();

        // Close and reopen modal for next test
        loginModal.clickClose();
        driver.navigate().refresh();

        // Test empty password
        ExtentReportManager.logStep("Testing with empty password");
        loginModal = homePage.clickLoginLink();

        loginModal.enterUsername(VALID_USERNAME);
        loginModal.enterPassword("");
        ExtentReportManager.captureScreenshot("Login Form with Empty Password");

        loginModal.clickLoginButton();

        // Check for the expected error message
        alertText = loginModal.getAlertText();
        ExtentReportManager.logInfo("Alert message: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("fill out"),
                "Alert should require filling out fields");
        ExtentReportManager.logPass("Empty password validation works correctly");

        loginModal.acceptAlert();

        // Close and reopen modal for next test
        loginModal.clickClose();
        driver.navigate().refresh();

        // Test both fields empty
        ExtentReportManager.logStep("Testing with both fields empty");
        loginModal = homePage.clickLoginLink();

        loginModal.enterUsername("");
        loginModal.enterPassword("");
        ExtentReportManager.captureScreenshot("Login Form with Both Fields Empty");

        loginModal.clickLoginButton();

        // Check for the expected error message
        alertText = loginModal.getAlertText();
        ExtentReportManager.logInfo("Alert message: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("fill out"),
                "Alert should require filling out fields");
        ExtentReportManager.logPass("Both fields empty validation works correctly");

        loginModal.acceptAlert();

        ExtentReportManager.logPass("Empty field validation test completed successfully");
    }

    /**
     * TC_LOG_005: Verify close modal functionality for login form
     */
    @Test
    @DisplayName("TC_LOG_005: Verify close modal functionality for login form")
    public void testCloseModalFunctionality() {
        ExtentReportManager.logStep("Testing close functionality of login modal");

        HomePage homePage = new HomePage(driver);

        // Test Close button
        ExtentReportManager.logStep("Testing Close button");
        LoginModal loginModal = homePage.clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal");

        loginModal.clickClose();
        ExtentReportManager.captureScreenshot("After clicking Close button");

        try {
            Thread.sleep(1000); // Wait for the modal to close
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        boolean modalClosed = !loginModal.isDisplayed();
        Assertions.assertTrue(modalClosed, "Modal should be closed after clicking Close button");
        ExtentReportManager.logPass("Modal closed successfully using Close button");

        // Test X icon
        ExtentReportManager.logStep("Testing X icon");
        loginModal = homePage.clickLoginLink();
        ExtentReportManager.captureScreenshot("Login Modal Again");

        loginModal.clickXIcon();
        ExtentReportManager.captureScreenshot("After clicking X icon");

        try {
            Thread.sleep(1000); // Wait for the modal to close
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        modalClosed = !loginModal.isDisplayed();
        Assertions.assertTrue(modalClosed, "Modal should be closed after clicking X icon");
        ExtentReportManager.logPass("Modal closed successfully using X icon");

        ExtentReportManager.logPass("Close modal functionality test completed successfully");
    }

    /**
     * TC_LOG_006: Verify session persistence after login
     */
    @Test
    @DisplayName("TC_LOG_006: Verify session persistence after login")
    public void testSessionPersistence() {
        ExtentReportManager.logStep("Testing session persistence after login");

        // Login first
        HomePage homePage = new HomePage(driver);
        LoginModal loginModal = homePage.clickLoginLink();

        loginModal.enterUsername(PERSIST_USERNAME);
        loginModal.enterPassword(PERSIST_PASSWORD);
        ExtentReportManager.captureScreenshot("Login Form Filled");

        loginModal.clickLoginButton();

        // Wait for login to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("After Login");

        // Verify welcome message is displayed
        WebElement welcomeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameofuser")));
        String welcomeMessage = welcomeElement.getText();
        ExtentReportManager.logInfo("Welcome message: " + welcomeMessage);

        // Navigate to Cart page
        ExtentReportManager.logStep("Navigating to Cart page");
        driver.navigate().to("https://www.demoblaze.com/cart.html");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("Cart Page");

        // Verify user is still logged in
        welcomeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameofuser")));
        String welcomeMessageCart = welcomeElement.getText();

        Assertions.assertEquals(welcomeMessage, welcomeMessageCart, "Welcome message should be consistent across pages");
        ExtentReportManager.logPass("User is still logged in on Cart page");

        // Navigate back to Home page
        ExtentReportManager.logStep("Navigating back to Home page");
        driver.navigate().to("https://www.demoblaze.com/index.html");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("Home Page Again");

        // Verify user is still logged in
        welcomeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameofuser")));
        String welcomeMessageHome = welcomeElement.getText();

        Assertions.assertEquals(welcomeMessage, welcomeMessageHome, "Welcome message should be consistent across pages");
        ExtentReportManager.logPass("User is still logged in on Home page");

        // Refresh the browser
        ExtentReportManager.logStep("Refreshing the browser");
        driver.navigate().refresh();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("After Refresh");

        // Verify user is still logged in
        welcomeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameofuser")));
        String welcomeMessageRefresh = welcomeElement.getText();

        Assertions.assertEquals(welcomeMessage, welcomeMessageRefresh, "Welcome message should persist after refresh");
        ExtentReportManager.logPass("User is still logged in after browser refresh");

        ExtentReportManager.logPass("Session persistence test completed successfully");
    }

    /**
     * TC_LOG_007: Verify case sensitivity of login credentials
     */
    @Test
    @DisplayName("TC_LOG_007: Verify case sensitivity of login credentials")
    public void testCaseSensitivity() {
        ExtentReportManager.logStep("Testing case sensitivity of login credentials");

        HomePage homePage = new HomePage(driver);

        // Test username case sensitivity
        ExtentReportManager.logStep("Testing username case sensitivity");
        LoginModal loginModal = homePage.clickLoginLink();

        String lowercaseUsername = CASE_SENSITIVE_USERNAME.toLowerCase();
        loginModal.enterUsername(lowercaseUsername);
        loginModal.enterPassword(CASE_SENSITIVE_PASSWORD);
        ExtentReportManager.captureScreenshot("Login Form with Lowercase Username");

        loginModal.clickLoginButton();

        // Check for the expected error message
        String alertText = loginModal.getAlertText();
        ExtentReportManager.logInfo("Alert message: " + alertText);

        boolean usernameIsCaseSensitive = alertText != null &&
                (alertText.contains("does not exist") || alertText.contains("Wrong"));

        if (usernameIsCaseSensitive) {
            ExtentReportManager.logPass("Username is case-sensitive");
        } else {
            ExtentReportManager.logInfo("Username might not be case-sensitive");
        }

        loginModal.acceptAlert();

        // Close and reopen modal for next test
        loginModal.clickClose();
        driver.navigate().refresh();

        // Test password case sensitivity
        ExtentReportManager.logStep("Testing password case sensitivity");
        loginModal = homePage.clickLoginLink();

        String lowercasePassword = CASE_SENSITIVE_PASSWORD.toLowerCase();
        loginModal.enterUsername(CASE_SENSITIVE_USERNAME);
        loginModal.enterPassword(lowercasePassword);
        ExtentReportManager.captureScreenshot("Login Form with Lowercase Password");

        loginModal.clickLoginButton();

        // Check for the expected error message
        alertText = loginModal.getAlertText();
        ExtentReportManager.logInfo("Alert message: " + alertText);

        boolean passwordIsCaseSensitive = alertText != null && alertText.contains("Wrong password");

        if (passwordIsCaseSensitive) {
            ExtentReportManager.logPass("Password is case-sensitive");
        } else {
            ExtentReportManager.logInfo("Password might not be case-sensitive");
        }

        loginModal.acceptAlert();

        // Close and reopen modal for next test
        loginModal.clickClose();
        driver.navigate().refresh();

        // Test correct case for both fields
        ExtentReportManager.logStep("Testing with correct case for both fields");
        loginModal = homePage.clickLoginLink();

        loginModal.enterUsername(CASE_SENSITIVE_USERNAME);
        loginModal.enterPassword(CASE_SENSITIVE_PASSWORD);
        ExtentReportManager.captureScreenshot("Login Form with Correct Case");

        loginModal.clickLoginButton();

        // Wait for login to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted");
        }
        ExtentReportManager.captureScreenshot("After Correct Case Login");

        // Verify login result - may succeed or fail depending on if this account exists
        try {
            WebElement welcomeElement = driver.findElement(By.id("nameofuser"));
            if (welcomeElement.isDisplayed()) {
                String welcomeMessage = welcomeElement.getText();
                ExtentReportManager.logPass("Login successful with correct case. Welcome message: " + welcomeMessage);
            }
        } catch (Exception e) {
            // Check if there was an alert
            try {
                alertText = loginModal.getAlertText();
                ExtentReportManager.logInfo("Alert with correct case credentials: " + alertText);
                loginModal.acceptAlert();
            } catch (Exception ex) {
                ExtentReportManager.logInfo("No welcome message or alert found");
            }
        }

        ExtentReportManager.logPass("Case sensitivity test completed");
    }

    /**
     * TC_LOG_008: Verify account lockout after multiple failed attempts
     */
    @Test
    @DisplayName("TC_LOG_008: Verify account lockout after multiple failed attempts")
    public void testAccountLockout() {
        ExtentReportManager.logStep("Testing account lockout after multiple failed attempts");

        // Test data - use the valid username with wrong passwords
        String[] wrongPasswords = {"wrongpass1", "wrongpass2", "wrongpass3", "wrongpass4", "wrongpass5", "wrongpass6"};

        HomePage homePage = new HomePage(driver);

        // Try multiple incorrect login attempts
        for (int i = 0; i < wrongPasswords.length; i++) {
            ExtentReportManager.logStep("Failed login attempt #" + (i+1));

            LoginModal loginModal = homePage.clickLoginLink();

            loginModal.enterUsername(VALID_USERNAME);
            loginModal.enterPassword(wrongPasswords[i]);
            ExtentReportManager.captureScreenshot("Login Attempt #" + (i+1));

            loginModal.clickLoginButton();

            // Check for alert message
            String alertText = loginModal.getAlertText();
            ExtentReportManager.logInfo("Alert message: " + alertText);

            // Accept the alert
            loginModal.acceptAlert();

            // Look for any lockout messages or CAPTCHA
            try {
                boolean captchaExists = driver.findElements(By.cssSelector(".captcha")).size() > 0;
                if (captchaExists) {
                    ExtentReportManager.logInfo("CAPTCHA detected after " + (i+1) + " failed attempts");
                    break;
                }

                boolean lockoutMessageExists = driver.getPageSource().contains("locked") ||
                        driver.getPageSource().contains("too many attempts");
                if (lockoutMessageExists) {
                    ExtentReportManager.logInfo("Account lockout message detected after " + (i+1) + " failed attempts");
                    break;
                }
            } catch (Exception e) {
                // Continue with test
            }

            // Check if modal is still open
            try {
                boolean modalStillOpen = loginModal.isDisplayed();
                if (!modalStillOpen) {
                    // Try to close it if somehow still open
                    loginModal.clickClose();
                }
            } catch (Exception e) {
                // Modal might have closed already
            }

            // Refresh page for next attempt
            driver.navigate().refresh();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ExtentReportManager.logWarning("Wait interrupted");
            }
        }

        // Time how long it takes to get a response after multiple failures
        long startTime = System.currentTimeMillis();

        // Final attempt after multiple failures
        ExtentReportManager.logStep("Attempting final login after multiple failures");
        LoginModal loginModal = homePage.clickLoginLink();

        loginModal.enterUsername(VALID_USERNAME);
        loginModal.enterPassword("anotherWrongPassword");
        ExtentReportManager.captureScreenshot("Final Login Attempt");

        loginModal.clickLoginButton();

        // Check for lockout message or other security measures
        String alertText = loginModal.getAlertText();
        long endTime = System.currentTimeMillis();

        ExtentReportManager.logInfo("Final alert message: " + alertText);
        ExtentReportManager.logInfo("Response time after multiple failures: " + (endTime - startTime) + "ms");

        loginModal.acceptAlert();

        // Note about security practices
        ExtentReportManager.logInfo("This test checks for account lockout which may not be implemented");
        ExtentReportManager.logInfo("Modern web security practices suggest implementing measures like:");
        ExtentReportManager.logInfo("- Account lockout after multiple failed attempts");
        ExtentReportManager.logInfo("- Progressive delays between login attempts");
        ExtentReportManager.logInfo("- CAPTCHA requirements after suspicious activity");

        ExtentReportManager.logPass("Account lockout test completed - check logs for security measures");
    }
}