package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.HomePage;
import pages.modals.SignUpModal;
import tests.base.BaseTest;
import utils.ExtentReportManager;

import java.util.UUID;

public class RegistrationTest extends BaseTest {

    /**
     * Helper method to generate a random username
     */
    private String generateRandomUsername() {
        return "testuser_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * TC_REG_011: Verify sign up form displays correctly
     */
    @Test
    @DisplayName("TC_REG_011: Verify sign up form displays correctly")
    public void testSignUpFormDisplays() {
        ExtentReportManager.logStep("Opening sign up modal");

        HomePage homePage = new HomePage(driver);
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        // Verify modal title
        String modalTitle = signUpModal.getModalTitle();
        Assertions.assertEquals("Sign up", modalTitle, "Modal title should be 'Sign up'");
        ExtentReportManager.logPass("Sign up modal title verified");

        // Verify username field
        Assertions.assertTrue(signUpModal.isUsernameFieldDisplayed(), "Username field should be displayed");
        ExtentReportManager.logPass("Username field is displayed");

        // Verify password field
        Assertions.assertTrue(signUpModal.isPasswordFieldDisplayed(), "Password field should be displayed");
        ExtentReportManager.logPass("Password field is displayed");

        // Verify sign up button
        Assertions.assertTrue(signUpModal.isSignUpButtonDisplayed(), "Sign up button should be displayed");
        ExtentReportManager.logPass("Sign up button is displayed");

        // Verify close button
        Assertions.assertTrue(signUpModal.isCloseButtonDisplayed(), "Close button should be displayed");
        ExtentReportManager.logPass("Close button is displayed");

        // Verify X icon
        Assertions.assertTrue(signUpModal.isXIconDisplayed(), "X close icon should be displayed");
        ExtentReportManager.logPass("X close icon is displayed");

        ExtentReportManager.logPass("Sign up form displays correctly with all required elements");
    }

    /**
     * TC_REG_002: Verify username validation for uniqueness
     * Note: This test assumes "testuser123" already exists in the system
     */
    @Test
    @DisplayName("TC_REG_002: Verify username validation for uniqueness")
    public void testUsernameUniqueness() {
        ExtentReportManager.logStep("Testing username uniqueness validation");

        HomePage homePage = new HomePage(driver);
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        String existingUsername = "testuser123"; // Replace with a username that exists in the system
        String password = "password123";

        ExtentReportManager.logStep("Entering existing username: " + existingUsername);
        signUpModal.enterUsername(existingUsername);
        signUpModal.enterPassword(password);
        ExtentReportManager.captureScreenshot("Form filled with existing username");

        ExtentReportManager.logStep("Clicking Sign up button");
        signUpModal.clickSignUp();

        ExtentReportManager.logStep("Checking for alert message");
        String alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("already exist"),
                "Alert should indicate that user already exists");
        ExtentReportManager.logPass("System correctly validated username uniqueness");

        // Accept the alert
        signUpModal.acceptAlert();
        ExtentReportManager.captureScreenshot("After Alert");
    }

    /**
     * TC_REG_003: Verify password requirements acceptance
     */
    @Test
    @DisplayName("TC_REG_003: Verify password requirements acceptance")
    public void testPasswordRequirementsAcceptance() {
        ExtentReportManager.logStep("Testing password requirements acceptance");

        HomePage homePage = new HomePage(driver);

        // Username to use for all tests
        String username = "Rama1123";

        // Test scenario A: Simple password
        ExtentReportManager.logStep("Scenario A: Testing simple password 'password'");
        SignUpModal signUpModal = homePage.clickSignUpLink();
        signUpModal.enterUsername(username);
        signUpModal.enterPassword("password");
        ExtentReportManager.captureScreenshot("Scenario A: Simple password");
        signUpModal.clickSignUp();

        String alertText = signUpModal.getAlertText();

        if (alertText != null && alertText.contains("successful")) {
            ExtentReportManager.logPass("System accepted simple password");
        } else if (alertText != null && alertText.contains("already exist")) {
            ExtentReportManager.logInfo("Username already exists, but the test is for password validation");
        } else if (alertText != null) {
            ExtentReportManager.logInfo("Alert message: " + alertText);
        }

        signUpModal.acceptAlert();

        // Refresh page and reopen modal for next test
        driver.navigate().refresh();

        // Test scenario B: Numeric password
        ExtentReportManager.logStep("Scenario B: Testing numeric password '12345'");
        signUpModal = homePage.clickSignUpLink();
        signUpModal.enterUsername(username);
        signUpModal.enterPassword("12345");
        ExtentReportManager.captureScreenshot("Scenario B: Numeric password");
        signUpModal.clickSignUp();

        alertText = signUpModal.getAlertText();

        if (alertText != null && alertText.contains("successful")) {
            ExtentReportManager.logPass("System accepted numeric password");
        } else if (alertText != null && alertText.contains("already exist")) {
            ExtentReportManager.logInfo("Username already exists, but the test is for password validation");
        } else if (alertText != null) {
            ExtentReportManager.logInfo("Alert message: " + alertText);
        }

        signUpModal.acceptAlert();

        // Refresh page and reopen modal for next test
        driver.navigate().refresh();

        // Test scenario C: Password with special characters
        ExtentReportManager.logStep("Scenario C: Testing password with special characters 'p@ssw0rd'");
        signUpModal = homePage.clickSignUpLink();
        signUpModal.enterUsername(username);
        signUpModal.enterPassword("p@ssw0rd");
        ExtentReportManager.captureScreenshot("Scenario C: Password with special characters");
        signUpModal.clickSignUp();

        alertText = signUpModal.getAlertText();

        if (alertText != null && alertText.contains("successful")) {
            ExtentReportManager.logPass("System accepted password with special characters");
        } else if (alertText != null && alertText.contains("already exist")) {
            ExtentReportManager.logInfo("Username already exists, but the test is for password validation");
        } else if (alertText != null) {
            ExtentReportManager.logInfo("Alert message: " + alertText);
        }

        signUpModal.acceptAlert();

        // Refresh page and reopen modal for next test
        driver.navigate().refresh();

        // Test scenario D: Complex password
        ExtentReportManager.logStep("Scenario D: Testing complex password 'P@$$w0rd!123'");
        signUpModal = homePage.clickSignUpLink();
        signUpModal.enterUsername(username);
        signUpModal.enterPassword("P@$$w0rd!123");
        ExtentReportManager.captureScreenshot("Scenario D: Complex password");
        signUpModal.clickSignUp();

        alertText = signUpModal.getAlertText();

        if (alertText != null && alertText.contains("successful")) {
            ExtentReportManager.logPass("System accepted complex password");
        } else if (alertText != null && alertText.contains("already exist")) {
            ExtentReportManager.logInfo("Username already exists, but the test is for password validation");
        } else if (alertText != null) {
            ExtentReportManager.logInfo("Alert message: " + alertText);
        }

        signUpModal.acceptAlert();

        ExtentReportManager.logPass("Password requirements acceptance test completed");
    }

    /**
     * TC_REG_004: Verify successful user registration
     */
    @Test
    @DisplayName("TC_REG_004: Verify successful user registration")
    public void testSuccessfulRegistration() {
        ExtentReportManager.logStep("Testing successful user registration");

        // Generate random username to avoid conflicts
        String username = generateRandomUsername();
        String password = "password456";

        ExtentReportManager.logInfo("Using username: " + username);

        HomePage homePage = new HomePage(driver);
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        // Fill form and submit
        ExtentReportManager.logStep("Filling registration form");
        signUpModal.enterUsername(username);
        signUpModal.enterPassword(password);
        ExtentReportManager.captureScreenshot("Form filled");

        ExtentReportManager.logStep("Clicking Sign up button");
        signUpModal.clickSignUp();

        // Check for success message
        ExtentReportManager.logStep("Checking for success alert");
        String alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("successful"),
                "Alert should indicate successful registration");
        ExtentReportManager.logPass("User registration completed successfully");

        // Accept the alert
        signUpModal.acceptAlert();
        ExtentReportManager.captureScreenshot("After Registration");
    }

    /**
     * TC_REG_005: Verify registration validation for duplicate username
     */
    @Test
    @DisplayName("TC_REG_005: Verify registration validation for duplicate username")
    public void testDuplicateUsername() {
        ExtentReportManager.logStep("Testing duplicate username validation");

        String existingUsername = "existinguser789"; // Replace with a username that exists in the system
        String password = "newpassword789";

        HomePage homePage = new HomePage(driver);
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        // Fill form and submit
        ExtentReportManager.logStep("Filling registration form with existing username");
        signUpModal.enterUsername(existingUsername);
        signUpModal.enterPassword(password);
        ExtentReportManager.captureScreenshot("Form filled with existing username");

        ExtentReportManager.logStep("Clicking Sign up button");
        signUpModal.clickSignUp();

        // Check for error message
        ExtentReportManager.logStep("Checking for error alert");
        String alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("already exist"),
                "Alert should indicate that user already exists");
        ExtentReportManager.logPass("System correctly validated duplicate username");

        // Accept the alert
        signUpModal.acceptAlert();
        ExtentReportManager.captureScreenshot("After Alert");
    }

    /**
     * TC_REG_006: Verify empty field validation during registration
     */
    @Test
    @DisplayName("TC_REG_006: Verify empty field validation during registration")
    public void testEmptyFieldValidation() {
        ExtentReportManager.logStep("Testing empty field validation");

        HomePage homePage = new HomePage(driver);

        // Test empty username
        ExtentReportManager.logStep("Testing empty username");
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        signUpModal.enterUsername("");
        signUpModal.enterPassword("password123");
        ExtentReportManager.captureScreenshot("Form with empty username");

        signUpModal.clickSignUp();

        String alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("fill out"),
                "Alert should require filling out fields");
        ExtentReportManager.logPass("Empty username validation works correctly");

        signUpModal.acceptAlert();

        // Close and reopen modal
        signUpModal.clickClose();
        driver.navigate().refresh();

        // Test empty password
        ExtentReportManager.logStep("Testing empty password");
        signUpModal = homePage.clickSignUpLink();

        signUpModal.enterUsername("testuser123");
        signUpModal.enterPassword("");
        ExtentReportManager.captureScreenshot("Form with empty password");

        signUpModal.clickSignUp();

        alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("fill out"),
                "Alert should require filling out fields");
        ExtentReportManager.logPass("Empty password validation works correctly");

        signUpModal.acceptAlert();

        // Close and reopen modal
        signUpModal.clickClose();
        driver.navigate().refresh();

        // Test both fields empty
        ExtentReportManager.logStep("Testing both fields empty");
        signUpModal = homePage.clickSignUpLink();

        signUpModal.enterUsername("");
        signUpModal.enterPassword("");
        ExtentReportManager.captureScreenshot("Form with both fields empty");

        signUpModal.clickSignUp();

        alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        Assertions.assertTrue(alertText != null && alertText.contains("fill out"),
                "Alert should require filling out fields");
        ExtentReportManager.logPass("Both fields empty validation works correctly");

        signUpModal.acceptAlert();

        ExtentReportManager.logPass("Empty field validation test completed successfully");
    }

    /**
     * TC_REG_007: Verify close modal functionality for sign up form
     */
    @Test
    @DisplayName("TC_REG_007: Verify close modal functionality for sign up form")
    public void testCloseModalFunctionality() {
        ExtentReportManager.logStep("Testing close functionality of sign up modal");

        HomePage homePage = new HomePage(driver);

        // Test Close button
        ExtentReportManager.logStep("Testing Close button");
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        signUpModal.clickClose();
        ExtentReportManager.captureScreenshot("After clicking Close button");

        try {
            Thread.sleep(1000); // Wait for the modal to close
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean modalClosed = !signUpModal.isDisplayed();
        Assertions.assertTrue(modalClosed, "Modal should be closed after clicking Close button");
        ExtentReportManager.logPass("Modal closed successfully using Close button");

        // Test X icon
        ExtentReportManager.logStep("Testing X icon");
        signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal Again");

        signUpModal.clickXIcon();
        ExtentReportManager.captureScreenshot("After clicking X icon");

        try {
            Thread.sleep(1000); // Wait for the modal to close
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        modalClosed = !signUpModal.isDisplayed();
        Assertions.assertTrue(modalClosed, "Modal should be closed after clicking X icon");
        ExtentReportManager.logPass("Modal closed successfully using X icon");

        ExtentReportManager.logPass("Close modal functionality test completed successfully");
    }

    /**
     * TC_REG_008: Verify special characters handling in username field
     */
    @Test
    @DisplayName("TC_REG_008: Verify special characters handling in username field")
    public void testSpecialCharactersInUsername() {
        ExtentReportManager.logStep("Testing special characters handling in username field");

        String username = "Test@#$%";
        String password = "password123";

        HomePage homePage = new HomePage(driver);
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        // Fill form and submit
        ExtentReportManager.logStep("Filling registration form with special characters in username");
        signUpModal.enterUsername(username);
        signUpModal.enterPassword(password);
        ExtentReportManager.captureScreenshot("Form filled with special characters username");

        ExtentReportManager.logStep("Clicking Sign up button");
        signUpModal.clickSignUp();

        // Check alert response
        String alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        if (alertText != null && alertText.contains("successful")) {
            ExtentReportManager.logPass("System accepted username with special characters");
        } else if (alertText != null && alertText.contains("invalid")) {
            ExtentReportManager.logPass("System rejected username with special characters with appropriate error message");
        } else if (alertText != null && alertText.contains("already exist")) {
            ExtentReportManager.logInfo("Username already exists, test inconclusive");
        } else {
            ExtentReportManager.logInfo("System response was: " + alertText);
        }

        signUpModal.acceptAlert();
        ExtentReportManager.captureScreenshot("After Alert");

        ExtentReportManager.logPass("Special characters handling test completed");
    }

    /**
     * TC_REG_009: Verify maximum length validation for username
     */
    @Test
    @DisplayName("TC_REG_009: Verify maximum length validation for username")
    public void testMaximumLengthUsername() {
        ExtentReportManager.logStep("Testing maximum length validation for username");

        // Create a 101 character username
        StringBuilder usernameBuilder = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            usernameBuilder.append("a");
        }

        String username = usernameBuilder.toString();
        String password = "password123";

        ExtentReportManager.logInfo("Username length: " + username.length() + " characters");

        HomePage homePage = new HomePage(driver);
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        // Fill form and submit
        ExtentReportManager.logStep("Filling registration form with extremely long username");
        signUpModal.enterUsername(username);
        signUpModal.enterPassword(password);
        ExtentReportManager.captureScreenshot("Form filled with long username");

        ExtentReportManager.logStep("Clicking Sign up button");
        signUpModal.clickSignUp();

        // Check alert response
        String alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("Alert text: " + alertText);

        if (alertText != null && alertText.contains("successful")) {
            ExtentReportManager.logInfo("System accepted extremely long username");

            // Get the actual length that was accepted
            // This would require additional verification steps like login and checking the profile
        } else if (alertText != null && alertText.contains("maximum length") || alertText.contains("too long")) {
            ExtentReportManager.logPass("System rejected extremely long username with appropriate error message");
        } else {
            ExtentReportManager.logInfo("System response was: " + alertText);
        }

        signUpModal.acceptAlert();
        ExtentReportManager.captureScreenshot("After Alert");

        ExtentReportManager.logPass("Maximum length validation test completed");
    }

    /**
     * TC_REG_010: Verify XSS vulnerability protection in registration
     */
    @Test
    @DisplayName("TC_REG_010: Verify XSS vulnerability protection in registration")
    public void testXSSProtection() {
        ExtentReportManager.logStep("Testing XSS vulnerability protection in registration");

        // Test XSS in username field
        ExtentReportManager.logStep("Testing XSS in username field");
        String xssUsername = "<script>alert('XSS')</script>";
        String normalPassword = "password123";

        HomePage homePage = new HomePage(driver);
        SignUpModal signUpModal = homePage.clickSignUpLink();
        ExtentReportManager.captureScreenshot("Sign Up Modal");

        // Fill form and submit
        ExtentReportManager.logStep("Filling registration form with XSS script in username");
        signUpModal.enterUsername(xssUsername);
        signUpModal.enterPassword(normalPassword);
        ExtentReportManager.captureScreenshot("Form filled with XSS in username");

        // Check if JavaScript alert appears (should not happen if properly sanitized)
        boolean alertDetected = false;
        try {
            // Wait a moment to see if alert appears
            Thread.sleep(2000);
            // If an alert is present, it means XSS worked (vulnerability)
            wait.until(ExpectedConditions.alertIsPresent());
            alertDetected = true;
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // No alert means XSS was prevented (good)
            alertDetected = false;
        }

        Assertions.assertFalse(alertDetected, "XSS script should not execute in username field");
        ExtentReportManager.logPass("XSS script did not execute in username field");

        // Now try to submit the form
        signUpModal.clickSignUp();

        // Check system response
        String alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("System response: " + alertText);

        signUpModal.acceptAlert();

        // Close and refresh for next test
        signUpModal.clickClose();
        driver.navigate().refresh();

        // Test XSS in password field
        ExtentReportManager.logStep("Testing XSS in password field");
        String normalUsername = "testuser";
        String xssPassword = "<script>alert('XSS')</script>";

        signUpModal = homePage.clickSignUpLink();

        // Fill form and submit
        ExtentReportManager.logStep("Filling registration form with XSS script in password");
        signUpModal.enterUsername(normalUsername);
        signUpModal.enterPassword(xssPassword);
        ExtentReportManager.captureScreenshot("Form filled with XSS in password");

        // Check if JavaScript alert appears (should not happen if properly sanitized)
        alertDetected = false;
        try {
            // Wait a moment to see if alert appears
            Thread.sleep(2000);
            // If an alert is present, it means XSS worked (vulnerability)
            wait.until(ExpectedConditions.alertIsPresent());
            alertDetected = true;
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // No alert means XSS was prevented (good)
            alertDetected = false;
        }

        Assertions.assertFalse(alertDetected, "XSS script should not execute in password field");
        ExtentReportManager.logPass("XSS script did not execute in password field");

        // Now try to submit the form
        signUpModal.clickSignUp();

        // Check system response
        alertText = signUpModal.getAlertText();
        ExtentReportManager.logInfo("System response: " + alertText);

        signUpModal.acceptAlert();

        ExtentReportManager.logPass("XSS vulnerability protection test completed");
    }
}