package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ExtentReportManager;
import utils.TestData;

/**
 * Tests for the contact form functionality
 */
public class ContactTest extends BaseTest {

    /**
     * TC_CNT_001: Verify contact modal opens and contains all required fields
     */
    @Test
    @DisplayName("TC_CNT_001: Verify contact modal opens and contains all required fields")
    public void testContactModalContainsAllFields() {
        ExtentReportManager.logStep("Testing that Contact modal opens and contains all required fields");

        // Open the contact modal
        openContactModal();

        // Get the contact modal element
        WebElement contactModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("exampleModal")));

        // Verify email field exists and is visible
        ExtentReportManager.logStep("Checking for Email field");
        WebElement emailField = contactModal.findElement(By.id("recipient-email"));
        Assertions.assertTrue(emailField.isDisplayed(), "Email field should be displayed");
        ExtentReportManager.logPass("Email field is present and visible");

        // Verify name field exists and is visible
        ExtentReportManager.logStep("Checking for Name field");
        WebElement nameField = contactModal.findElement(By.id("recipient-name"));
        Assertions.assertTrue(nameField.isDisplayed(), "Name field should be displayed");
        ExtentReportManager.logPass("Name field is present and visible");

        // Verify message field exists and is visible
        ExtentReportManager.logStep("Checking for Message field");
        WebElement messageField = contactModal.findElement(By.id("message-text"));
        Assertions.assertTrue(messageField.isDisplayed(), "Message field should be displayed");
        ExtentReportManager.logPass("Message field is present and visible");

        // Verify send message button exists and is visible
        ExtentReportManager.logStep("Checking for Send message button");
        WebElement sendButton = contactModal.findElement(By.xpath(".//button[contains(text(),'Send message')]"));
        Assertions.assertTrue(sendButton.isDisplayed(), "Send message button should be displayed");
        ExtentReportManager.logPass("Send message button is present and visible");

        // Verify close button exists and is visible
        ExtentReportManager.logStep("Checking for Close button");
        WebElement closeButton = contactModal.findElement(By.xpath(".//button[contains(text(),'Close')]"));
        Assertions.assertTrue(closeButton.isDisplayed(), "Close button should be displayed");
        ExtentReportManager.logPass("Close button is present and visible");

        // Verify X button exists and is visible
        ExtentReportManager.logStep("Checking for X button");
        WebElement xButton = contactModal.findElement(By.cssSelector(".close"));
        Assertions.assertTrue(xButton.isDisplayed(), "X button should be displayed");
        ExtentReportManager.logPass("X button is present and visible");

        // Verify modal title
        ExtentReportManager.logStep("Checking modal title");
        WebElement modalTitle = contactModal.findElement(By.cssSelector(".modal-title"));
        Assertions.assertTrue(modalTitle.isDisplayed(), "Modal title should be displayed");
        Assertions.assertEquals("New message", modalTitle.getText().trim(),
                "Modal title should be 'New message'");
        ExtentReportManager.logPass("Modal title is correct: " + modalTitle.getText().trim());

        ExtentReportManager.logPass("Contact modal contains all required fields");
    }

    /**
     * TC_CNT_002: Ensure contact form submits when all fields are filled
     */
    @Test
    @DisplayName("TC_CNT_002: Ensure contact form submits when all fields are filled")
    public void testContactFormSubmitsWithValidData() {
        ExtentReportManager.logStep("Testing that contact form submits when all fields are filled with valid data");

        // Open the contact modal
        openContactModal();

        // Get the contact modal element
        WebElement contactModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("exampleModal")));

        // Fill email field
        ExtentReportManager.logStep("Filling email field");
        WebElement emailField = contactModal.findElement(By.id("recipient-email"));
        emailField.clear();
        emailField.sendKeys("test@example.com");
        ExtentReportManager.logInfo("Entered email: test@example.com");

        // Fill name field
        ExtentReportManager.logStep("Filling name field");
        WebElement nameField = contactModal.findElement(By.id("recipient-name"));
        nameField.clear();
        nameField.sendKeys("John Doe");
        ExtentReportManager.logInfo("Entered name: John Doe");

        // Fill message field
        ExtentReportManager.logStep("Filling message field");
        WebElement messageField = contactModal.findElement(By.id("message-text"));
        messageField.clear();
        messageField.sendKeys("This is a test message for the contact form submission.");
        ExtentReportManager.logInfo("Entered message: This is a test message for the contact form submission.");

        ExtentReportManager.captureScreenshot("Contact Form Filled");

        // Click Send message button
        ExtentReportManager.logStep("Clicking Send message button");
        WebElement sendButton = contactModal.findElement(By.xpath(".//button[contains(text(),'Send message')]"));
        sendButton.click();

        // Wait for alert or confirmation
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Form Submission");

        // Check for success alert or confirmation
        try {
            // Try to find an alert first
            String alertText = driver.switchTo().alert().getText();
            ExtentReportManager.logInfo("Alert message after submission: " + alertText);

            boolean isSuccess = !alertText.contains("error") &&
                    !alertText.contains("invalid") &&
                    !alertText.contains("failed");

            Assertions.assertTrue(isSuccess, "Alert should indicate successful submission");

            // Accept the alert
            driver.switchTo().alert().accept();

            ExtentReportManager.logPass("Contact form submitted successfully with alert confirmation: " + alertText);
        } catch (Exception e) {
            ExtentReportManager.logInfo("No alert found after submission, checking if modal closed: " + e.getMessage());

            // If no alert, check if modal closed (which can also indicate success)
            try {
                boolean modalClosed = wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.id("exampleModal")));

                Assertions.assertTrue(modalClosed, "Modal should close after successful submission");
                ExtentReportManager.logPass("Contact form submitted successfully (modal closed after submission)");
            } catch (Exception ex) {
                ExtentReportManager.logFail("Could not verify form submission success: " + ex.getMessage());
                throw new AssertionError("Could not verify form submission success: " + ex.getMessage());
            }
        }

        // After submission and alert handling, verify we're back on home page
        String currentUrl = driver.getCurrentUrl();
        boolean onHomePage = currentUrl.contains("index.html") ||
                currentUrl.endsWith("demoblaze.com/");

        Assertions.assertTrue(onHomePage, "Should be on home page after submission");
        ExtentReportManager.logPass("User is on home page after contact form submission");
    }

    /**
     * TC_CNT_003: Test validation by submitting empty contact form
     */
    @Test
    @DisplayName("TC_CNT_003: Test validation by submitting empty contact form")
    public void testEmptyContactFormValidation() {
        ExtentReportManager.logStep("Testing validation by submitting empty contact form");

        // Open the contact modal
        openContactModal();

        // Get the contact modal element
        WebElement contactModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("exampleModal")));

        // Ensure all fields are empty
        ExtentReportManager.logStep("Ensuring all fields are empty");

        WebElement emailField = contactModal.findElement(By.id("recipient-email"));
        emailField.clear();

        WebElement nameField = contactModal.findElement(By.id("recipient-name"));
        nameField.clear();

        WebElement messageField = contactModal.findElement(By.id("message-text"));
        messageField.clear();

        ExtentReportManager.captureScreenshot("Empty Contact Form");

        // Click Send message button
        ExtentReportManager.logStep("Clicking Send message button with empty fields");
        WebElement sendButton = contactModal.findElement(By.xpath(".//button[contains(text(),'Send message')]"));
        sendButton.click();

        // Wait for validation or alert
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Empty Form Submission");

        // Check if the modal is still open (validation prevented submission)
        boolean modalStillOpen = false;
        try {
            modalStillOpen = contactModal.isDisplayed();
        } catch (Exception e) {
            ExtentReportManager.logWarning("Error checking if modal is still open: " + e.getMessage());
        }

        // Check validation behavior, which could be:
        // 1. Modal still open (client-side validation)
        // 2. Alert with error (server-side validation)
        // 3. Browser's HTML5 validation

        if (modalStillOpen) {
            // Check for HTML5 validation messages on fields
            boolean validationVisible = false;

            try {
                // Check email field validation
                String emailValidation = emailField.getAttribute("validationMessage");
                if (emailValidation != null && !emailValidation.isEmpty()) {
                    ExtentReportManager.logInfo("Email field validation message: " + emailValidation);
                    validationVisible = true;
                }

                // If email didn't have validation, check name field
                if (!validationVisible) {
                    String nameValidation = nameField.getAttribute("validationMessage");
                    if (nameValidation != null && !nameValidation.isEmpty()) {
                        ExtentReportManager.logInfo("Name field validation message: " + nameValidation);
                        validationVisible = true;
                    }
                }

                // If still no validation visible, check message field
                if (!validationVisible) {
                    String messageValidation = messageField.getAttribute("validationMessage");
                    if (messageValidation != null && !messageValidation.isEmpty()) {
                        ExtentReportManager.logInfo("Message field validation message: " + messageValidation);
                        validationVisible = true;
                    }
                }

                if (validationVisible) {
                    ExtentReportManager.logPass("Form validation works - HTML5 validation messages shown");
                } else {
                    // If no HTML5 validation but modal still open, form submission was still blocked
                    ExtentReportManager.logPass("Form validation works - form not submitted with empty fields");
                }

            } catch (Exception e) {
                ExtentReportManager.logWarning("Could not check HTML5 validation: " + e.getMessage());
                // Still pass because modal is open, indicating submission was blocked
                ExtentReportManager.logPass("Form validation works - modal still open after submission attempt");
            }

        } else {
            // If modal is no longer displayed, check for alert with error message
            try {
                String alertText = driver.switchTo().alert().getText();
                ExtentReportManager.logInfo("Alert text: " + alertText);

                boolean validationMessagePresent = alertText.contains("fill") ||
                        alertText.contains("required") ||
                        alertText.contains("empty") ||
                        alertText.contains("invalid");

                Assertions.assertTrue(validationMessagePresent,
                        "Alert should indicate that form fields are required");

                driver.switchTo().alert().accept();
                ExtentReportManager.logPass("Form validation works - alert shown for empty form");
            } catch (Exception e) {
                ExtentReportManager.logWarning("Could not find alert: " + e.getMessage());
                Assertions.fail("Form validation did not work - empty form was submitted without validation");
            }
        }

        // Final assertion to ensure the form was not successfully submitted
        boolean formSubmittedAnyway = !driver.findElements(By.id("exampleModal")).isEmpty() &&
                !driver.findElement(By.id("exampleModal")).isDisplayed();

        Assertions.assertFalse(formSubmittedAnyway,
                "Empty form should not be successfully submitted");

        ExtentReportManager.logPass("Empty contact form validation successful");
    }

    /**
     * TC_CNT_004: Verify modal can be closed using Close or X
     */
    @Test
    @DisplayName("TC_CNT_004: Verify modal can be closed using Close or X")
    public void testContactModalCloses() {
        ExtentReportManager.logStep("Testing that contact modal can be closed using Close or X button");

        // Test closing with Close button
        ExtentReportManager.logStep("Testing closing with Close button");

        // Open the contact modal
        openContactModal();

        // Get the contact modal element
        WebElement contactModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("exampleModal")));

        // Verify modal is open
        Assertions.assertTrue(contactModal.isDisplayed(), "Contact modal should be displayed");

        // Click Close button
        WebElement closeButton = contactModal.findElement(By.xpath(".//button[contains(text(),'Close')]"));
        closeButton.click();

        // Wait for modal to close
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Closing Modal with Close Button");

        // Verify modal is closed
        try {
            boolean modalClosed = wait.until(ExpectedConditions.invisibilityOf(contactModal));
            Assertions.assertTrue(modalClosed, "Modal should be closed after clicking Close button");
            ExtentReportManager.logPass("Modal closed successfully with Close button");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify modal closure with Close button: " + e.getMessage());

            // Alternative check - see if the modal is still in the DOM but not visible
            boolean modalInvisible = !driver.findElements(By.id("exampleModal")).isEmpty() &&
                    !driver.findElement(By.id("exampleModal")).isDisplayed();

            Assertions.assertTrue(modalInvisible, "Modal should not be visible after clicking Close button");
            ExtentReportManager.logPass("Modal is not visible after clicking Close button");
        }

        // Test closing with X button
        ExtentReportManager.logStep("Testing closing with X button");

        // Open the contact modal again
        openContactModal();

        // Get the contact modal element again
        contactModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("exampleModal")));

        // Verify modal is open
        Assertions.assertTrue(contactModal.isDisplayed(), "Contact modal should be displayed");

        // Click X button
        WebElement xButton = contactModal.findElement(By.cssSelector(".close"));
        xButton.click();

        // Wait for modal to close
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Closing Modal with X Button");

        // Verify modal is closed
        try {
            boolean modalClosed = wait.until(ExpectedConditions.invisibilityOf(contactModal));
            Assertions.assertTrue(modalClosed, "Modal should be closed after clicking X button");
            ExtentReportManager.logPass("Modal closed successfully with X button");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify modal closure with X button: " + e.getMessage());

            // Alternative check - see if the modal is still in the DOM but not visible
            boolean modalInvisible = !driver.findElements(By.id("exampleModal")).isEmpty() &&
                    !driver.findElement(By.id("exampleModal")).isDisplayed();

            Assertions.assertTrue(modalInvisible, "Modal should not be visible after clicking X button");
            ExtentReportManager.logPass("Modal is not visible after clicking X button");
        }

        // Verify we're back on the home page
        String currentUrl = driver.getCurrentUrl();
        boolean onHomePage = currentUrl.contains("index.html") ||
                currentUrl.endsWith("demoblaze.com/");

        Assertions.assertTrue(onHomePage, "Should be on home page after closing modal");
        ExtentReportManager.logPass("User is on home page after closing contact modal");
    }

    /**
     * Helper method to open the contact modal
     */
    private void openContactModal() {
        ExtentReportManager.logStep("Opening contact modal");

        // Click on Contact link in navigation
        WebElement contactLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Contact')]")));
        contactLink.click();

        // Wait for modal to appear
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Contact Modal");

        // Verify modal is displayed
        WebElement contactModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("exampleModal")));
        Assertions.assertTrue(contactModal.isDisplayed(), "Contact modal should be displayed");

        ExtentReportManager.logInfo("Contact modal opened successfully");
    }
}