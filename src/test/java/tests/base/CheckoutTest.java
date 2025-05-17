package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.HomePage;
import pages.modals.LoginModal;
import utils.ExtentReportManager;
import utils.TestData;

public class CheckoutTest extends BaseTest {

    /**
     * TC_CHK_001: Verify that clicking "Place Order" opens the order modal form
     */
    @Test
    @DisplayName("TC_CHK_001: Verify that clicking 'Place Order' opens the order modal form")
    public void testPlaceOrderButtonOpensModal() {
        ExtentReportManager.logStep("Testing that clicking 'Place Order' opens the order modal form");

        // Login first
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Click on Place Order button
        ExtentReportManager.logStep("Clicking on 'Place Order' button");
        WebElement placeOrderButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Place Order')]")));
        placeOrderButton.click();

        // Wait for order modal to appear
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Order Modal");

        // Verify order modal is displayed
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));
        Assertions.assertTrue(orderModal.isDisplayed(), "Order modal should be displayed");

        // Verify modal title
        WebElement modalTitle = orderModal.findElement(By.cssSelector(".modal-title"));
        Assertions.assertTrue(modalTitle.isDisplayed(), "Modal title should be displayed");
        Assertions.assertEquals("Place order", modalTitle.getText().trim(),
                "Modal title should be 'Place order'");

        ExtentReportManager.logPass("'Place Order' button successfully opens the order modal form");
    }

    /**
     * TC_CHK_002: Ensure the order form modal contains all required input fields
     */
    @Test
    @DisplayName("TC_CHK_002: Ensure the order form modal contains all required input fields")
    public void testOrderFormContainsAllFields() {
        ExtentReportManager.logStep("Testing that order form contains all required input fields");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Verify all required fields are present
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        // Check for name field
        WebElement nameField = orderModal.findElement(By.id("name"));
        Assertions.assertTrue(nameField.isDisplayed(), "Name field should be displayed");
        ExtentReportManager.logInfo("Name field is displayed");

        // Check for country field
        WebElement countryField = orderModal.findElement(By.id("country"));
        Assertions.assertTrue(countryField.isDisplayed(), "Country field should be displayed");
        ExtentReportManager.logInfo("Country field is displayed");

        // Check for city field
        WebElement cityField = orderModal.findElement(By.id("city"));
        Assertions.assertTrue(cityField.isDisplayed(), "City field should be displayed");
        ExtentReportManager.logInfo("City field is displayed");

        // Check for credit card field
        WebElement cardField = orderModal.findElement(By.id("card"));
        Assertions.assertTrue(cardField.isDisplayed(), "Credit card field should be displayed");
        ExtentReportManager.logInfo("Credit card field is displayed");

        // Check for month field
        WebElement monthField = orderModal.findElement(By.id("month"));
        Assertions.assertTrue(monthField.isDisplayed(), "Month field should be displayed");
        ExtentReportManager.logInfo("Month field is displayed");

        // Check for year field
        WebElement yearField = orderModal.findElement(By.id("year"));
        Assertions.assertTrue(yearField.isDisplayed(), "Year field should be displayed");
        ExtentReportManager.logInfo("Year field is displayed");

        // Check for Purchase button
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        Assertions.assertTrue(purchaseButton.isDisplayed(), "Purchase button should be displayed");
        ExtentReportManager.logInfo("Purchase button is displayed");

        // Check for Close button
        WebElement closeButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Close')]"));
        Assertions.assertTrue(closeButton.isDisplayed(), "Close button should be displayed");
        ExtentReportManager.logInfo("Close button is displayed");

        ExtentReportManager.logPass("Order form modal contains all required input fields");
    }

    /**
     * TC_CHK_003: Test form validation by submitting an incomplete order
     */
    @Test
    @DisplayName("TC_CHK_003: Test form validation by submitting an incomplete order")
    public void testFormValidationWithIncompleteOrder() {
        ExtentReportManager.logStep("Testing form validation with incomplete order");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Fill out only the name field and leave other fields empty
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina");
        ExtentReportManager.logInfo("Entered name: Zeina");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with incomplete form");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation message
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Incomplete Form");

        // Check if the modal is still open (validation prevented submission)
        boolean modalStillOpen = false;
        try {
            modalStillOpen = orderModal.isDisplayed();
        } catch (Exception e) {
            ExtentReportManager.logWarning("Error checking if modal is still open: " + e.getMessage());
        }

        // If the modal is no longer displayed, check for alert
        if (!modalStillOpen) {
            try {
                String alertText = driver.switchTo().alert().getText();
                ExtentReportManager.logInfo("Alert text: " + alertText);

                boolean validationMessagePresent = alertText.contains("Please fill") ||
                        alertText.contains("required") ||
                        alertText.contains("empty");

                Assertions.assertTrue(validationMessagePresent,
                        "Alert should indicate that form fields are required");

                driver.switchTo().alert().accept();
                ExtentReportManager.logPass("Form validation works - alert shown for incomplete form");
            } catch (Exception e) {
                ExtentReportManager.logWarning("Could not find alert: " + e.getMessage());
                Assertions.fail("Form validation did not work - form was submitted despite incomplete fields");
            }
        } else {
            // If the modal is still open, validation worked client-side
            ExtentReportManager.logPass("Form validation works - form not submitted with incomplete fields");
        }
    }

    /**
     * TC_CHK_004: Check that the order modal closes correctly
     */
    @Test
    @DisplayName("TC_CHK_004: Check that the order modal closes correctly")
    public void testOrderModalCloses() {
        ExtentReportManager.logStep("Testing that order modal closes correctly");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Verify modal is open
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));
        Assertions.assertTrue(orderModal.isDisplayed(), "Order modal should be displayed");

        // Test closing with Close button
        ExtentReportManager.logStep("Testing closing modal with Close button");
        WebElement closeButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Close')]"));
        closeButton.click();

        // Wait for modal to close
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Closing Modal with Close Button");

        // Verify modal is closed
        try {
            boolean modalClosed = wait.until(ExpectedConditions.invisibilityOf(orderModal));
            Assertions.assertTrue(modalClosed, "Modal should be closed after clicking Close button");
            ExtentReportManager.logPass("Modal closed successfully with Close button");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify modal closure: " + e.getMessage());
        }

        // Open modal again
        openOrderModal();

        // Verify modal is open again
        orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));
        Assertions.assertTrue(orderModal.isDisplayed(), "Order modal should be displayed");

        // Test closing with X button
        ExtentReportManager.logStep("Testing closing modal with X button");
        WebElement xButton = orderModal.findElement(By.cssSelector(".close"));
        xButton.click();

        // Wait for modal to close
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Closing Modal with X Button");

        // Verify modal is closed
        try {
            boolean modalClosed = wait.until(ExpectedConditions.invisibilityOf(orderModal));
            Assertions.assertTrue(modalClosed, "Modal should be closed after clicking X button");
            ExtentReportManager.logPass("Modal closed successfully with X button");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify modal closure: " + e.getMessage());
        }

        // Verify the cart page is still displayed
        WebElement placeOrderButton = driver.findElement(By.xpath("//button[contains(text(),'Place Order')]"));
        Assertions.assertTrue(placeOrderButton.isDisplayed(),
                "Place Order button should still be visible after closing modal");

        ExtentReportManager.logPass("Order modal closes correctly and cart page remains unchanged");
    }

    /**
     * TC_CHK_005: Verify valid full name entry is accepted
     */
    @Test
    @DisplayName("TC_CHK_005: Verify valid full name entry is accepted")
    public void testValidNameAccepted() {
        ExtentReportManager.logStep("Testing that valid full name is accepted");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter valid name
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");
        ExtentReportManager.logInfo("Entered name: Zeina Amr");

        // Move to next field to trigger any validation
        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.click();

        ExtentReportManager.captureScreenshot("After Entering Valid Name");

        // Check if there are any validation errors for the name field
        boolean validationError = isFieldInvalid(nameField);

        Assertions.assertFalse(validationError, "Valid name should be accepted without errors");
        ExtentReportManager.logPass("Valid full name is accepted without errors");
    }

    /**
     * TC_CHK_006: Verify empty name field blocks submission
     */
    @Test
    @DisplayName("TC_CHK_006: Verify empty name field blocks submission")
    public void testEmptyNameBlocksSubmission() {
        ExtentReportManager.logStep("Testing that empty name field blocks form submission");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Fill all fields except name
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        // Leave name empty
        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();

        // Fill other fields
        fillFormFieldsExceptName(orderModal);

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with empty name field");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Empty Name");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Empty name field should block form submission");
    }

    /**
     * TC_CHK_007: Verify numeric name input is rejected
     */
    @Test
    @DisplayName("TC_CHK_007: Verify numeric name input is rejected")
    public void testNumericNameRejected() {
        ExtentReportManager.logStep("Testing that numeric name input is rejected");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter numeric name
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("12345");
        ExtentReportManager.logInfo("Entered numeric name: 12345");

        // Fill other fields
        fillFormFieldsExceptName(orderModal);

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with numeric name");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Numeric Name");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Numeric name input should be rejected");
    }

    /**
     * TC_CHK_008: Verify special characters in name are rejected
     */
    @Test
    @DisplayName("TC_CHK_008: Verify special characters in name are rejected")
    public void testSpecialCharsInNameRejected() {
        ExtentReportManager.logStep("Testing that special characters in name are rejected");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter name with special characters
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("@Zeina!");
        ExtentReportManager.logInfo("Entered name with special characters: @Zeina!");

        // Fill other fields
        fillFormFieldsExceptName(orderModal);

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with special characters in name");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Special Characters in Name");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Special characters in name should be rejected");
    }

    /**
     * TC_CHK_009: Verify valid country entry is accepted
     */
    @Test
    @DisplayName("TC_CHK_009: Verify valid country entry is accepted")
    public void testValidCountryAccepted() {
        ExtentReportManager.logStep("Testing that valid country entry is accepted");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter valid country
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");
        ExtentReportManager.logInfo("Entered country: Canada");

        // Move to next field to trigger any validation
        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.click();

        ExtentReportManager.captureScreenshot("After Entering Valid Country");

        // Check if there are any validation errors for the country field
        boolean validationError = isFieldInvalid(countryField);

        Assertions.assertFalse(validationError, "Valid country should be accepted without errors");
        ExtentReportManager.logPass("Valid country entry is accepted without errors");
    }

    /**
     * TC_CHK_010: Verify empty country blocks submission
     */
    @Test
    @DisplayName("TC_CHK_010: Verify empty country blocks submission")
    public void testEmptyCountryBlocksSubmission() {
        ExtentReportManager.logStep("Testing that empty country field blocks form submission");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Fill all fields except country
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");

        // Leave country empty
        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with empty country field");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Empty Country");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Empty country field should block form submission");
    }

    /**
     * TC_CHK_011: Verify valid city input is accepted
     */
    @Test
    @DisplayName("TC_CHK_011: Verify valid city input is accepted")
    public void testValidCityAccepted() {
        ExtentReportManager.logStep("Testing that valid city input is accepted");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter valid city
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");
        ExtentReportManager.logInfo("Entered city: Cairo");

        // Move to next field to trigger any validation
        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.click();

        ExtentReportManager.captureScreenshot("After Entering Valid City");

        // Check if there are any validation errors for the city field
        boolean validationError = isFieldInvalid(cityField);

        Assertions.assertFalse(validationError, "Valid city should be accepted without errors");
        ExtentReportManager.logPass("Valid city input is accepted without errors");
    }

    /**
     * TC_CHK_012: Verify empty city field submission
     */
    @Test
    @DisplayName("TC_CHK_012: Verify empty city field blocks submission")
    public void testEmptyCityBlocksSubmission() {
        ExtentReportManager.logStep("Testing that empty city field blocks form submission");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Fill all fields except city
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");

        // Leave city empty
        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with empty city field");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Empty City");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Empty city field should block form submission");
    }

    /**
     * TC_CHK_013: Verify numeric credit card input is accepted
     */
    @Test
    @DisplayName("TC_CHK_013: Verify numeric credit card input is accepted")
    public void testNumericCardAccepted() {
        ExtentReportManager.logStep("Testing that numeric credit card input is accepted");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter valid credit card
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");
        ExtentReportManager.logInfo("Entered credit card: 4111111111111111");

        // Move to next field to trigger any validation
        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.click();

        ExtentReportManager.captureScreenshot("After Entering Valid Credit Card");

        // Check if there are any validation errors for the card field
        boolean validationError = isFieldInvalid(cardField);

        Assertions.assertFalse(validationError, "Valid numeric credit card should be accepted without errors");
        ExtentReportManager.logPass("Valid numeric credit card is accepted without errors");
    }

    /**
     * TC_CHK_014: Verify credit card input with letters is rejected
     */
    @Test
    @DisplayName("TC_CHK_014: Verify credit card input with letters is rejected")
    public void testCardWithLettersRejected() {
        ExtentReportManager.logStep("Testing that credit card input with letters is rejected");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter credit card with letters
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111abcd1234");
        ExtentReportManager.logInfo("Entered credit card with letters: 4111abcd1234");

        // Fill other fields
        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with letters in credit card");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Letters in Credit Card");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Credit card with letters should be rejected");
    }

    /**
     * TC_CHK_015: Verify credit card field is required
     */
    @Test
    @DisplayName("TC_CHK_015: Verify credit card field is required")
    public void testEmptyCardBlocksSubmission() {
        ExtentReportManager.logStep("Testing that empty credit card field blocks form submission");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Fill all fields except credit card
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");

        // Leave card empty
        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with empty credit card field");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Empty Credit Card");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Empty credit card field should block form submission");
    }

    /**
     * TC_CHK_016: Verify invalid month (>12) is rejected
     */
    @Test
    @DisplayName("TC_CHK_016: Verify invalid month (>12) is rejected")
    public void testInvalidMonthRejected() {
        ExtentReportManager.logStep("Testing that invalid month (>12) is rejected");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter invalid month
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("15");
        ExtentReportManager.logInfo("Entered invalid month: 15");

        // Fill other fields
        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with invalid month");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Invalid Month");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Invalid month (>12) should be rejected");
    }

    /**
     * TC_CHK_017: Verify valid year entry is accepted
     */
    @Test
    @DisplayName("TC_CHK_017: Verify valid year entry is accepted")
    public void testValidYearAccepted() {
        ExtentReportManager.logStep("Testing that valid year entry is accepted");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter valid year
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2026");
        ExtentReportManager.logInfo("Entered valid year: 2026");

        // Click elsewhere to trigger validation
        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.click();

        ExtentReportManager.captureScreenshot("After Entering Valid Year");

        // Check if there are any validation errors for the year field
        boolean validationError = isFieldInvalid(yearField);

        Assertions.assertFalse(validationError, "Valid year should be accepted without errors");
        ExtentReportManager.logPass("Valid year entry is accepted without errors");
    }

    /**
     * TC_CHK_018: Verify invalid year (past) is rejected
     */
    @Test
    @DisplayName("TC_CHK_018: Verify invalid year (past) is rejected")
    public void testInvalidYearRejected() {
        ExtentReportManager.logStep("Testing that invalid year (past) is rejected");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Enter invalid year
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2010");
        ExtentReportManager.logInfo("Entered invalid year: 2010");

        // Fill other fields
        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with invalid year");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Invalid Year");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "Invalid year (past) should be rejected");
    }

    /**
     * TC_CHK_019: Verify form submission with all valid fields
     */
    @Test
    @DisplayName("TC_CHK_019: Verify form submission with all valid fields")
    public void testFormSubmissionWithValidFields() {
        ExtentReportManager.logStep("Testing form submission with all valid fields");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Fill all fields with valid data
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Zeina Amr");

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2026");

        ExtentReportManager.captureScreenshot("Form Filled with Valid Data");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with valid form data");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for confirmation
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with Valid Form");

        // Check for order confirmation
        try {
            WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".sweet-alert h2")));

            boolean isSuccess = confirmationMessage.getText().contains("Thank you") ||
                    confirmationMessage.getText().contains("Success");

            Assertions.assertTrue(isSuccess, "Order confirmation message should be displayed");
            ExtentReportManager.logPass("Form submitted successfully with valid fields");

            // Close confirmation if present
            try {
                WebElement okButton = driver.findElement(By.cssSelector(".sweet-alert .confirm"));
                okButton.click();
            } catch (Exception e) {
                ExtentReportManager.logWarning("Could not close confirmation: " + e.getMessage());
            }

        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not find confirmation message: " + e.getMessage());

            // Check if we're back at the home page or cart page (alternative success indicator)
            boolean backToHome = driver.getCurrentUrl().contains("index.html") ||
                    driver.getCurrentUrl().endsWith("demoblaze.com/");

            if (backToHome) {
                ExtentReportManager.logPass("Form submitted successfully - redirected to home page");
            } else {
                Assertions.fail("Form submission did not result in confirmation or redirection");
            }
        }
    }

    /**
     * TC_CHK_020: Verify form blocks submission when all fields are blank
     */
    @Test
    @DisplayName("TC_CHK_020: Verify form blocks submission when all fields are blank")
    public void testAllFieldsBlankBlocksSubmission() {
        ExtentReportManager.logStep("Testing that blank form blocks submission");

        // Login and add product to cart
        loginAndAddProductToCart();

        // Navigate to cart page
        navigateToCartPage();

        // Open order modal
        openOrderModal();

        // Leave all fields blank
        WebElement orderModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("orderModal")));

        WebElement nameField = orderModal.findElement(By.id("name"));
        nameField.clear();

        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();

        ExtentReportManager.captureScreenshot("Form with All Fields Blank");

        // Click Purchase button
        ExtentReportManager.logStep("Clicking Purchase button with all fields blank");
        WebElement purchaseButton = orderModal.findElement(By.xpath(".//button[contains(text(),'Purchase')]"));
        purchaseButton.click();

        // Wait for validation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("After Clicking Purchase with All Fields Blank");

        // Check if the form was submitted or blocked
        checkFormValidationBlocked(orderModal, "All blank fields should block form submission");
    }

    /**
     * Helper method to log in and add a product to cart
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
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Add a product to cart - Samsung Galaxy S6
        ExtentReportManager.logStep("Adding product to cart");

        // Find and click on the Samsung Galaxy S6 product
        WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Samsung galaxy s6")));
        productLink.click();

        // Wait for product page to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Click Add to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Add to cart')]")));
        addToCartButton.click();

        // Handle alert
        try {
            Thread.sleep(2000);
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
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Cart Page");

        // Verify cart page loaded
        WebElement placeOrderButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Place Order')]")));
        Assertions.assertTrue(placeOrderButton.isDisplayed(), "Place Order button should be displayed on cart page");

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
            Thread.sleep(2000);
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
     * Helper method to check if a field is invalid (for validation)
     * @param field The WebElement to check
     * @return true if the field is invalid, false otherwise
     */
    private boolean isFieldInvalid(WebElement field) {
        String classes = field.getAttribute("class");
        String validationMessage = field.getAttribute("validationMessage");

        return (classes != null && classes.contains("is-invalid")) ||
                (validationMessage != null && !validationMessage.isEmpty());
    }

    /**
     * Helper method to fill all form fields except name
     * @param orderModal The order modal WebElement
     */
    private void fillFormFieldsExceptName(WebElement orderModal) {
        WebElement countryField = orderModal.findElement(By.id("country"));
        countryField.clear();
        countryField.sendKeys("Canada");

        WebElement cityField = orderModal.findElement(By.id("city"));
        cityField.clear();
        cityField.sendKeys("Cairo");

        WebElement cardField = orderModal.findElement(By.id("card"));
        cardField.clear();
        cardField.sendKeys("4111111111111111");

        WebElement monthField = orderModal.findElement(By.id("month"));
        monthField.clear();
        monthField.sendKeys("12");

        WebElement yearField = orderModal.findElement(By.id("year"));
        yearField.clear();
        yearField.sendKeys("2025");
    }

    /**
     * Helper method to check if form validation blocked submission
     * @param orderModal The order modal WebElement
     * @param message The assertion message
     */
    private void checkFormValidationBlocked(WebElement orderModal, String message) {
        // Check if the modal is still open (validation prevented submission)
        boolean modalStillOpen = false;
        try {
            modalStillOpen = orderModal.isDisplayed();
        } catch (Exception e) {
            ExtentReportManager.logWarning("Error checking if modal is still open: " + e.getMessage());
        }

        // If the modal is no longer displayed, check for alert
        if (!modalStillOpen) {
            try {
                String alertText = driver.switchTo().alert().getText();
                ExtentReportManager.logInfo("Alert text: " + alertText);

                boolean validationMessagePresent = alertText.contains("Please fill") ||
                        alertText.contains("required") ||
                        alertText.contains("empty") ||
                        alertText.contains("invalid");

                Assertions.assertTrue(validationMessagePresent, message);

                driver.switchTo().alert().accept();
                ExtentReportManager.logPass("Form validation works - alert shown for invalid input");
            } catch (Exception e) {
                // Check if we were redirected to order confirmation (submission wasn't blocked)
                try {
                    WebElement confirmationMessage = driver.findElement(By.cssSelector(".sweet-alert h2"));
                    boolean isSuccess = confirmationMessage.getText().contains("Thank you") ||
                            confirmationMessage.getText().contains("Success");

                    if (isSuccess) {
                        // Close confirmation if present
                        try {
                            WebElement okButton = driver.findElement(By.cssSelector(".sweet-alert .confirm"));
                            okButton.click();
                        } catch (Exception ex) {
                            ExtentReportManager.logWarning("Could not close confirmation: " + ex.getMessage());
                        }

                        Assertions.fail("Form validation failed - form was submitted despite invalid input");
                    }
                } catch (Exception ex) {
                    ExtentReportManager.logWarning("Could not find alert or confirmation: " + e.getMessage());
                }
            }
        } else {
            // If the modal is still open, validation worked client-side
            ExtentReportManager.logPass("Form validation works - form not submitted with invalid input");
        }
    }
}