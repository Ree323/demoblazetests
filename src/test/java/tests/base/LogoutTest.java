package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;
import utils.TestData;

import java.time.Duration;

public class LogoutTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public static void setupReport() {
        // Initialize ExtentReports
        ExtentReportManager.init();
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        // Create test in ExtentReports
        String testName = testInfo.getDisplayName();
        if (testName.equals("testLogoutEndsSession")) {
            testName = "TC_OUT_001: Verify logout functionality ends user session";
        } else if (testName.equals("testPostLogoutNavigationOptions")) {
            testName = "TC_OUT_002: Verify post-logout state shows correct navigation options";
        } else if (testName.equals("testLogoutRedirectsToHomePage")) {
            testName = "TC_OUT_003: Verify redirect after logout goes to home page";
        }

        ExtentReportManager.createTest(testName, "Testing logout functionality of DemoBlaze website");

        // Add options to avoid CDP warning and improve test stability
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Set driver for screenshots
        ExtentReportManager.setDriver(driver);

        // Create a wait object for better element handling
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestData.IMPLICIT_WAIT));

        // Log navigation to the website
        ExtentReportManager.logStep("Navigating to DemoBlaze website");
        driver.get(TestData.BASE_URL);
        ExtentReportManager.captureScreenshot("Home Page");

        // Wait for page to fully load
        try {
            Thread.sleep(TestData.MEDIUM_WAIT);
            ExtentReportManager.logInfo("Waited 2 seconds for page to load");
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Page load wait was interrupted: " + e.getMessage());
        }
    }

    // TC_OUT_001: Verify logout functionality ends user session
    @Test
    public void testLogoutEndsSession() throws InterruptedException {
        // 1. Navigate to website (done in setUp)
        // 2. Log in with test credentials
        loginUser(TestData.TEST_USERNAME, TestData.TEST_PASSWORD);

        // 3. Verify welcome message appears
        ExtentReportManager.logStep("Verifying welcome message is displayed");
        WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(TestData.WELCOME_MESSAGE_ID)));
        String welcomeText = welcomeMessage.getText();
        ExtentReportManager.logInfo("Welcome message: " + welcomeText);
        Assertions.assertTrue(welcomeText.contains(TestData.WELCOME_MESSAGE_PREFIX + TestData.TEST_USERNAME),
                "Welcome message should contain username");
        ExtentReportManager.logPass("Welcome message verified: " + welcomeText);

        // 4. Click on "Log out" in the navigation bar
        ExtentReportManager.logStep("Clicking on Log out link");
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.id(TestData.LOGOUT_LINK_ID)));
        logoutLink.click();
        Thread.sleep(TestData.MEDIUM_WAIT); // Wait for logout to complete
        ExtentReportManager.captureScreenshot("After Logout");

        // Verify user is logged out
        ExtentReportManager.logStep("Verifying user is logged out");

        // Check that login link is visible (indicating logged out state)
        WebElement loginLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(TestData.LOGIN_LINK_ID)));
        Assertions.assertTrue(loginLink.isDisplayed(), "Login link should be visible after logout");
        ExtentReportManager.logPass("Login link is visible after logout");

        // Check that signup link is visible
        WebElement signupLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin2")));
        Assertions.assertTrue(signupLink.isDisplayed(), "Sign up link should be visible after logout");
        ExtentReportManager.logPass("Sign up link is visible after logout");

        // Check that welcome message is no longer visible
        try {
            boolean welcomeGone = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(TestData.WELCOME_MESSAGE_ID)));
            Assertions.assertTrue(welcomeGone, "Welcome message should not be visible after logout");
            ExtentReportManager.logPass("Welcome message is no longer visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify welcome message invisibility: " + e.getMessage());
            // Alternative check - if we can find it, make sure it's empty or not visible
            try {
                WebElement welcomeElem = driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID));
                String postLogoutText = welcomeElem.getText();
                boolean isEmpty = postLogoutText.isEmpty() || !welcomeElem.isDisplayed();
                Assertions.assertTrue(isEmpty, "Welcome message should be empty or not visible after logout");
                ExtentReportManager.logPass("Welcome message is empty or not visible after logout");
            } catch (NoSuchElementException nsee) {
                // This is actually good - element doesn't exist
                ExtentReportManager.logPass("Welcome message element is completely removed from DOM after logout");
            }
        }

        // Check that logout link is no longer visible
        try {
            boolean logoutGone = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(TestData.LOGOUT_LINK_ID)));
            Assertions.assertTrue(logoutGone, "Logout link should not be visible after logout");
            ExtentReportManager.logPass("Logout link is no longer visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify logout link invisibility: " + e.getMessage());
            // Alternative check
            try {
                WebElement logoutElem = driver.findElement(By.id(TestData.LOGOUT_LINK_ID));
                boolean isNotVisible = !logoutElem.isDisplayed();
                Assertions.assertTrue(isNotVisible, "Logout link should not be visible after logout");
                ExtentReportManager.logPass("Logout link is not visible after logout");
            } catch (NoSuchElementException nsee) {
                // This is actually good - element doesn't exist
                ExtentReportManager.logPass("Logout link element is completely removed from DOM after logout");
            }
        }

        ExtentReportManager.logPass("Logout functionality successfully ends user session");
    }

    // TC_OUT_002: Verify post-logout state shows correct navigation options
    @Test
    public void testPostLogoutNavigationOptions() throws InterruptedException {
        // 1. Navigate to website (done in setUp)
        // 2. Log in with test credentials
        loginUser(TestData.TEST_USERNAME, TestData.TEST_PASSWORD);

        // Capture the state of navigation bar before logout for comparison
        ExtentReportManager.logStep("Capturing navigation bar state before logout");
        ExtentReportManager.captureScreenshot("Navigation Bar Before Logout");

        boolean welcomeVisibleBeforeLogout = false;
        boolean logoutVisibleBeforeLogout = false;

        try {
            WebElement welcomeMessage = driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID));
            welcomeVisibleBeforeLogout = welcomeMessage.isDisplayed();
            ExtentReportManager.logInfo("Welcome message visible before logout: " + welcomeVisibleBeforeLogout);

            WebElement logoutLink = driver.findElement(By.id(TestData.LOGOUT_LINK_ID));
            logoutVisibleBeforeLogout = logoutLink.isDisplayed();
            ExtentReportManager.logInfo("Logout link visible before logout: " + logoutVisibleBeforeLogout);
        } catch (Exception e) {
            ExtentReportManager.logWarning("Exception when checking pre-logout state: " + e.getMessage());
        }

        // 3. Click on "Log out" in the navigation bar
        ExtentReportManager.logStep("Clicking on Log out link");
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.id(TestData.LOGOUT_LINK_ID)));
        logoutLink.click();
        Thread.sleep(TestData.MEDIUM_WAIT); // Wait for logout to complete

        // 4. Observe the navigation bar options
        ExtentReportManager.logStep("Checking navigation bar options after logout");
        ExtentReportManager.captureScreenshot("Navigation Bar After Logout");

        // Verify "Log in" option is visible
        WebElement loginLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(TestData.LOGIN_LINK_ID)));
        Assertions.assertTrue(loginLink.isDisplayed(), "Login link should be visible after logout");
        ExtentReportManager.logPass("Login link is visible after logout: " + loginLink.getText());

        // Verify "Sign up" option is visible
        WebElement signupLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin2")));
        Assertions.assertTrue(signupLink.isDisplayed(), "Sign up link should be visible after logout");
        ExtentReportManager.logPass("Sign up link is visible after logout: " + signupLink.getText());

        // Verify welcome message is no longer visible
        try {
            boolean welcomeGone = driver.findElements(By.id(TestData.WELCOME_MESSAGE_ID)).isEmpty()
                    || !driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID)).isDisplayed()
                    || driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID)).getText().isEmpty();
            Assertions.assertTrue(welcomeGone, "Welcome message should not be visible after logout");
            ExtentReportManager.logPass("Welcome message is no longer visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Error checking welcome message: " + e.getMessage());
            // Try an alternative approach - just check if the element exists at all
            boolean elementExists = !driver.findElements(By.id(TestData.WELCOME_MESSAGE_ID)).isEmpty();
            if (elementExists) {
                WebElement elem = driver.findElement(By.id(TestData.WELCOME_MESSAGE_ID));
                ExtentReportManager.logInfo("Element still exists, checking visibility and text");
                ExtentReportManager.logInfo("Element visible: " + elem.isDisplayed());
                ExtentReportManager.logInfo("Element text: " + elem.getText());
            } else {
                ExtentReportManager.logPass("Welcome message element doesn't exist in DOM after logout");
            }
        }

        // Verify logout link is no longer visible
        try {
            boolean logoutGone = driver.findElements(By.id(TestData.LOGOUT_LINK_ID)).isEmpty()
                    || !driver.findElement(By.id(TestData.LOGOUT_LINK_ID)).isDisplayed();
            Assertions.assertTrue(logoutGone, "Logout link should not be visible after logout");
            ExtentReportManager.logPass("Logout link is no longer visible after logout");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Error checking logout link: " + e.getMessage());
            // Try an alternative approach
            boolean elementExists = !driver.findElements(By.id(TestData.LOGOUT_LINK_ID)).isEmpty();
            ExtentReportManager.logInfo("Logout link element exists in DOM: " + elementExists);
            if (elementExists) {
                ExtentReportManager.logInfo("Element visible: " + driver.findElement(By.id(TestData.LOGOUT_LINK_ID)).isDisplayed());
            } else {
                ExtentReportManager.logPass("Logout link element doesn't exist in DOM after logout");
            }
        }

        // Summary check - login and signup links must be visible after logout
        boolean loginVisible = !driver.findElements(By.id(TestData.LOGIN_LINK_ID)).isEmpty() && driver.findElement(By.id(TestData.LOGIN_LINK_ID)).isDisplayed();
        boolean signupVisible = !driver.findElements(By.id("signin2")).isEmpty() && driver.findElement(By.id("signin2")).isDisplayed();
        Assertions.assertTrue(loginVisible && signupVisible, "Both login and signup links must be visible after logout");
        ExtentReportManager.logPass("Both login and signup links are visible after logout");

        ExtentReportManager.logPass("Post-logout navigation state shows correct options");
    }

    // TC_OUT_003: Verify redirect after logout goes to home page
    @Test
    public void testLogoutRedirectsToHomePage() throws InterruptedException {
        // 1. Navigate to cart page
        ExtentReportManager.logStep("Navigating to cart page");
        driver.navigate().to(TestData.CART_URL);
        Thread.sleep(TestData.MEDIUM_WAIT);
        ExtentReportManager.captureScreenshot("Cart Page");

        // Verify we're on cart page
        ExtentReportManager.logStep("Verifying current page is cart page");
        String cartUrl = driver.getCurrentUrl();
        ExtentReportManager.logInfo("Cart URL: " + cartUrl);
        Assertions.assertTrue(cartUrl.contains("cart.html"), "URL should contain cart.html");
        ExtentReportManager.logPass("Current page is cart page: " + cartUrl);

        // 2. Log in with test credentials
        loginUser(TestData.TEST_USERNAME, TestData.TEST_PASSWORD);

        // 3. Confirm user is still on cart page
        ExtentReportManager.logStep("Confirming user is still on cart page after login");
        String postLoginUrl = driver.getCurrentUrl();
        ExtentReportManager.logInfo("URL after login: " + postLoginUrl);
        Assertions.assertTrue(postLoginUrl.contains("cart.html"), "URL should still contain cart.html after login");
        ExtentReportManager.logPass("User is still on cart page after login");

        // 4. Click on "Log out" in the navigation bar
        ExtentReportManager.logStep("Clicking on Log out link");
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.id(TestData.LOGOUT_LINK_ID)));
        logoutLink.click();
        Thread.sleep(TestData.MEDIUM_WAIT); // Wait for logout and redirect to complete
        ExtentReportManager.captureScreenshot("After Logout");

        // 5. Observe current page - should be redirected to home page
        ExtentReportManager.logStep("Checking if user is redirected to home page");
        String postLogoutUrl = driver.getCurrentUrl();
        ExtentReportManager.logInfo("URL after logout: " + postLogoutUrl);

        // Check if redirected to home page (could be index.html or just the base domain)
        boolean redirectedToHome = postLogoutUrl.contains("index.html") ||
                postLogoutUrl.endsWith("demoblaze.com/") ||
                !postLogoutUrl.contains("cart.html");

        Assertions.assertTrue(redirectedToHome, "User should be redirected to home page after logout");
        ExtentReportManager.logPass("User is redirected to home page after logout: " + postLogoutUrl);

        // Verify home page elements are visible to confirm it's really the home page
        try {
            boolean catalogVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbodyid"))).isDisplayed();
            Assertions.assertTrue(catalogVisible, "Product catalog should be visible on home page");
            ExtentReportManager.logPass("Product catalog is visible, confirming home page redirect");
        } catch (Exception e) {
            ExtentReportManager.logWarning("Could not verify product catalog: " + e.getMessage());
            // Alternative check - just verify we're not on cart page anymore
            Assertions.assertFalse(postLogoutUrl.contains("cart.html"), "URL should not contain cart.html after logout");
            ExtentReportManager.logPass("User is no longer on cart page after logout");
        }

        ExtentReportManager.logPass("Logout successfully redirects user to home page");
    }

    /**
     * Helper method to log in a user
     * @param username the username to log in with
     * @param password the password to log in with
     * @throws InterruptedException if thread sleep is interrupted
     */
    private void loginUser(String username, String password) throws InterruptedException {
        ExtentReportManager.logStep("Logging in with username: " + username);

        // Click on Log in link to open modal
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(By.id(TestData.LOGIN_LINK_ID)));
        loginLink.click();
        Thread.sleep(TestData.SHORT_WAIT);
        ExtentReportManager.captureScreenshot("Login Modal");

        // Enter username
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(TestData.USERNAME_FIELD_ID)));
        usernameField.clear();
        usernameField.sendKeys(username);
        ExtentReportManager.logInfo("Entered username: " + username);

        // Enter password
        WebElement passwordField = driver.findElement(By.id(TestData.PASSWORD_FIELD_ID));
        passwordField.clear();
        passwordField.sendKeys(password);
        ExtentReportManager.logInfo("Entered password");

        // Click Log in button
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Log in')]"));
        loginButton.click();

        // Wait for login to complete and page to reload
        Thread.sleep(TestData.LONG_WAIT);
        ExtentReportManager.captureScreenshot("After Login");

        // Verify login was successful by checking for welcome message
        try {
            WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(TestData.WELCOME_MESSAGE_ID)));
            String welcomeText = welcomeMessage.getText();
            boolean loginSuccessful = welcomeText.contains(TestData.WELCOME_MESSAGE_PREFIX) && welcomeText.contains(username);

            if (loginSuccessful) {
                ExtentReportManager.logPass("Login successful. Welcome message: " + welcomeText);
            } else {
                ExtentReportManager.logFail("Login may have failed. Welcome message: " + welcomeText);
                throw new AssertionError("Login failed: Welcome message does not contain username");
            }
        } catch (Exception e) {
            ExtentReportManager.logFail("Login failed: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Login Failure");
            throw new AssertionError("Login failed: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        ExtentReportManager.logStep("Finishing test and closing browser");

        if (driver != null) {
            try {
                driver.quit();
                ExtentReportManager.logInfo("Browser closed successfully");
            } catch (Exception e) {
                ExtentReportManager.logWarning("Error while closing browser: " + e.getMessage());
            }
        }
    }

    @AfterAll
    public static void tearDownReport() {
        // Generate the report
        ExtentReportManager.flush();
    }
}