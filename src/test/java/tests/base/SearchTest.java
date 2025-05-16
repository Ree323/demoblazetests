package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.List;

public class SearchTest {
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
        if (testName.equals("testExactBookTitleSearch")) {
            testName = "TC_PAT_SEARCH_001: Verify search with exact book title";
        } else if (testName.equals("testPartialBookTitleSearch")) {
            testName = "TC_PAT_SEARCH_002: Verify search with partial book title";
        } else if (testName.equals("testAuthorNameSearch")) {
            testName = "TC_PAT_SEARCH_003: Verify search with author name";
        } else if (testName.equals("testCategorySearch")) {
            testName = "TC_PAT_SEARCH_004: Verify search with book category";
        } else if (testName.equals("testMixedCaseSearch")) {
            testName = "TC_PAT_SEARCH_005: Verify search with mixed case input";
        } else if (testName.equals("testNonExistentBookSearch")) {
            testName = "TC_PAT_SEARCH_006: Verify search with non-existent book title";
        } else if (testName.equals("testEmptySearch")) {
            testName = "TC_PAT_SEARCH_007: Verify empty search behavior";
        } else if (testName.equals("testSpecialCharactersSearch")) {
            testName = "TC_PAT_SEARCH_008: Verify search with special characters";
        } else if (testName.equals("testLongSearchTerm")) {
            testName = "TC_PAT_SEARCH_009: Verify search behavior with extremely long search term";
        } else if (testName.equals("testNumericSearch")) {
            testName = "TC_PAT_SEARCH_010: Verify search with numeric values only";
        }

        ExtentReportManager.createTest(testName, "Testing search functionality of Practice Automation Testing website");

        // Add options to avoid CDP warning and improve test stability
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        // Add additional options to handle common issues
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Set implicit wait to handle slow page loads
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        // Set driver for screenshots
        ExtentReportManager.setDriver(driver);

        // Create a wait object for better element handling
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Log navigation to the website
        ExtentReportManager.logStep("Navigating to Practice Automation Testing website");
        driver.get("https://practice.automationtesting.in/");
        ExtentReportManager.captureScreenshot("Home Page");

        // Wait for page to fully load
        try {
            Thread.sleep(3000);
            ExtentReportManager.logInfo("Waited 3 seconds for page to load");

            // Navigate to shop page first, as the home page may not have search functionality
            ExtentReportManager.logStep("Navigating to Shop page to access search functionality");
            WebElement shopMenu = driver.findElement(By.linkText("Shop"));
            shopMenu.click();
            Thread.sleep(3000);
            ExtentReportManager.captureScreenshot("Shop Page");

            // Handle any pop-ups that might appear
            try {
                List<WebElement> closeButtons = driver.findElements(By.cssSelector(".close-button, .close, .dismiss"));
                if (!closeButtons.isEmpty()) {
                    for (WebElement closeButton : closeButtons) {
                        if (closeButton.isDisplayed()) {
                            closeButton.click();
                            ExtentReportManager.logInfo("Closed a popup");
                            Thread.sleep(1000);
                        }
                    }
                }
            } catch (Exception e) {
                ExtentReportManager.logInfo("No popups found or could not close: " + e.getMessage());
            }

        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Page load wait was interrupted: " + e.getMessage());
        } catch (Exception e) {
            ExtentReportManager.logWarning("Setup encountered an issue: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Setup Issue");
        }
    }

    // Helper method to find and use the search box
    private void performSearch(String searchQuery) throws InterruptedException {
        // Locate the search box (try different possible selectors)
        WebElement searchBox = null;

        try {
            // First method: Try finding search box directly if visible
            searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='search'], .search-field, #s")));
            ExtentReportManager.logInfo("Found search box directly");
        } catch (Exception e1) {
            // Second method: Try clicking search icon first
            try {
                ExtentReportManager.logInfo("Trying to find and click search icon first");
                WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".search-icon, .noo-search, .fa-search, [data-icon='search']")));
                searchIcon.click();
                Thread.sleep(2000);
                ExtentReportManager.captureScreenshot("After Clicking Search Icon");

                // Now try to find the search box again
                searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='search'], .search-field, #s")));
            } catch (Exception e2) {
                // Third method: Try using search that's in the header/sidebar
                ExtentReportManager.logInfo("Trying to find search in sidebar/header area");
                searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".search input, .widget_search input, #searchform input")));
            }
        }

        // Verify search box was found
        Assertions.assertNotNull(searchBox, "Search box should be found on the page");
        ExtentReportManager.logPass("Search box found");
        ExtentReportManager.captureScreenshot("Search Box Found");

        // Enter search query in search box
        ExtentReportManager.logStep("Entering search query: '" + searchQuery + "'");
        searchBox.clear();
        searchBox.sendKeys(searchQuery);
        Thread.sleep(1000);
        ExtentReportManager.captureScreenshot("Search Term Entered");

        // Submit the search - try different methods
        boolean searchSubmitted = false;

        try {
            // Method 1: Press Enter key
            searchBox.sendKeys(Keys.ENTER);
            searchSubmitted = true;
            ExtentReportManager.logInfo("Search submitted using Enter key");
        } catch (Exception e) {
            try {
                // Method 2: Find and click search button if available
                ExtentReportManager.logInfo("Trying to find and click search button");
                WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit'], input[type='submit'], .search-submit")));
                searchButton.click();
                searchSubmitted = true;
                ExtentReportManager.logInfo("Search submitted by clicking search button");
            } catch (Exception e2) {
                ExtentReportManager.logWarning("Could not find a way to submit search: " + e2.getMessage());
            }
        }

        Assertions.assertTrue(searchSubmitted, "Search should be submitted successfully");
        Thread.sleep(3000);
        ExtentReportManager.captureScreenshot("Search Results");
    }

    // Helper method to verify search results
    private void verifySearchResults(String searchQuery, boolean expectResults) {
        // Check if page title or URL indicates search results
        boolean onSearchResultsPage = driver.getTitle().toLowerCase().contains("search") ||
                driver.getCurrentUrl().toLowerCase().contains("search") ||
                driver.getCurrentUrl().toLowerCase().contains("s=");
        ExtentReportManager.logInfo("On search results page: " + onSearchResultsPage);

        // Check if results contain the search term
        boolean resultsContainSearchTerm = driver.getPageSource().toLowerCase()
                .contains(searchQuery.toLowerCase());
        ExtentReportManager.logInfo("Results contain search term: " + resultsContainSearchTerm);

        // Check if results contain products
        boolean hasResults = !driver.findElements(By.cssSelector(".product, .products li, article")).isEmpty();
        ExtentReportManager.logInfo("Has product results: " + hasResults);

        // Check for "no products found" message
        boolean noResultsMessage = driver.getPageSource().toLowerCase().contains("no products") ||
                driver.getPageSource().toLowerCase().contains("no results");
        ExtentReportManager.logInfo("Has 'no results' message: " + noResultsMessage);

        if (expectResults) {
            // For expected results, we should have either:
            // 1. Results containing the search term, or
            // 2. We're on a search page with product results
            Assertions.assertTrue(resultsContainSearchTerm || (onSearchResultsPage && hasResults),
                    "Search results should display relevant products");

            if (hasResults) {
                int resultCount = driver.findElements(By.cssSelector(".product, .products li, article")).size();
                ExtentReportManager.logPass("Search returned " + resultCount + " products");
            } else if (onSearchResultsPage) {
                ExtentReportManager.logPass("On search results page");
            } else if (resultsContainSearchTerm) {
                ExtentReportManager.logPass("Results contain the search term");
            }
        } else {
            // For searches that shouldn't return results:
            // Either we should have a "no results" message, or no products found
            Assertions.assertTrue(noResultsMessage || (onSearchResultsPage && !hasResults),
                    "Search should indicate no matching products");

            if (noResultsMessage) {
                ExtentReportManager.logPass("Correctly shows 'no products found' message");
            } else if (onSearchResultsPage && !hasResults) {
                ExtentReportManager.logPass("On search results page with no products, as expected");
            }
        }
    }

    // TC_PAT_SEARCH_001: Verify search with exact book title
    @Test
    public void testExactBookTitleSearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with exact book title");

        try {
            String searchQuery = "Selenium Ruby";
            performSearch(searchQuery);
            verifySearchResults(searchQuery, true);
            ExtentReportManager.logPass("Exact book title search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test exact book title search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test exact book title search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_002: Verify search with partial book title
    @Test
    public void testPartialBookTitleSearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with partial book title");

        try {
            String searchQuery = "Thinking";
            performSearch(searchQuery);
            verifySearchResults(searchQuery, true);
            ExtentReportManager.logPass("Partial book title search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test partial book title search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test partial book title search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_003: Verify search with author name
    @Test
    public void testAuthorNameSearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with author name");

        try {
            String searchQuery = "Amir";
            performSearch(searchQuery);
            verifySearchResults(searchQuery, true);
            ExtentReportManager.logPass("Author name search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test author name search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test author name search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_004: Verify search with book category
    @Test
    public void testCategorySearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with book category");

        try {
            String searchQuery = "JavaScript";
            performSearch(searchQuery);
            verifySearchResults(searchQuery, true);
            ExtentReportManager.logPass("Category search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test category search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test category search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_005: Verify search with mixed case input
    @Test
    public void testMixedCaseSearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with mixed case input");

        try {
            String searchQuery = "HtMl AnD cSs";
            performSearch(searchQuery);
            // For mixed case, check for lowercase equivalent
            verifySearchResults("html", true);
            ExtentReportManager.logPass("Mixed case search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test mixed case search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test mixed case search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_006: Verify search with non-existent book title
    @Test
    public void testNonExistentBookSearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with non-existent book title");

        try {
            String searchQuery = "Nonexistent Book Title XYZ123";
            performSearch(searchQuery);
            verifySearchResults(searchQuery, false);
            ExtentReportManager.logPass("Non-existent book search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test non-existent book search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test non-existent book search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_007: Verify empty search behavior
    @Test
    public void testEmptySearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing empty search behavior");

        try {
            // Locate the search box (try different possible selectors)
            WebElement searchBox = null;

            try {
                // First method: Try finding search box directly if visible
                searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='search'], .search-field, #s")));
                ExtentReportManager.logInfo("Found search box directly");
            } catch (Exception e1) {
                // Second method: Try clicking search icon first
                try {
                    ExtentReportManager.logInfo("Trying to find and click search icon first");
                    WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector(".search-icon, .noo-search, .fa-search, [data-icon='search']")));
                    searchIcon.click();
                    Thread.sleep(2000);
                    ExtentReportManager.captureScreenshot("After Clicking Search Icon");

                    // Now try to find the search box again
                    searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("input[type='search'], .search-field, #s")));
                } catch (Exception e2) {
                    // Third method: Try using search that's in the header/sidebar
                    ExtentReportManager.logInfo("Trying to find search in sidebar/header area");
                    searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".search input, .widget_search input, #searchform input")));
                }
            }

            // Verify search box was found
            Assertions.assertNotNull(searchBox, "Search box should be found on the page");
            ExtentReportManager.logPass("Search box found");
            ExtentReportManager.captureScreenshot("Search Box Found");

            // Clear the search box (but don't enter anything)
            ExtentReportManager.logStep("Leaving search box empty");
            searchBox.clear();
            Thread.sleep(1000);
            ExtentReportManager.captureScreenshot("Empty Search Box");

            String initialUrl = driver.getCurrentUrl();
            ExtentReportManager.logInfo("Initial URL before empty search: " + initialUrl);

            // Submit the empty search - try different methods
            boolean searchSubmitted = false;

            try {
                // Method 1: Press Enter key
                searchBox.sendKeys(Keys.ENTER);
                searchSubmitted = true;
                ExtentReportManager.logInfo("Empty search submitted using Enter key");
            } catch (Exception e) {
                try {
                    // Method 2: Find and click search button if available
                    ExtentReportManager.logInfo("Trying to find and click search button for empty search");
                    WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("button[type='submit'], input[type='submit'], .search-submit")));
                    searchButton.click();
                    searchSubmitted = true;
                    ExtentReportManager.logInfo("Empty search submitted by clicking search button");
                } catch (Exception e2) {
                    ExtentReportManager.logWarning("Could not find a way to submit empty search: " + e2.getMessage());
                }
            }

            Thread.sleep(3000);
            ExtentReportManager.captureScreenshot("After Empty Search Submission");

            // There are multiple possible behaviors for empty search:
            // 1. Site prevents search (search box reappears/nothing happens)
            // 2. Site displays error message
            // 3. Site shows all products/shop page

            String currentUrl = driver.getCurrentUrl();
            ExtentReportManager.logInfo("URL after empty search: " + currentUrl);

            // Check if URL changed
            boolean urlChanged = !currentUrl.equals(initialUrl);
            ExtentReportManager.logInfo("URL changed after empty search: " + urlChanged);

            // Check if search box is still visible/accessible
            boolean searchBoxStillVisible = !driver.findElements(
                    By.cssSelector("input[type='search'], .search-field, #s")).isEmpty();
            ExtentReportManager.logInfo("Search box still visible after empty search: " + searchBoxStillVisible);

            // Check for error message
            boolean errorMessageDisplayed = driver.getPageSource().toLowerCase().contains("please enter a search") ||
                    driver.getPageSource().toLowerCase().contains("search term") ||
                    driver.getPageSource().toLowerCase().contains("try again");
            ExtentReportManager.logInfo("Error message displayed: " + errorMessageDisplayed);

            // Check if all products shown
            boolean productsShown = !driver.findElements(By.cssSelector(".product, .products li, article")).isEmpty();
            ExtentReportManager.logInfo("Products shown after empty search: " + productsShown);

            // All of these are valid behaviors for empty search
            Assertions.assertTrue(searchBoxStillVisible || errorMessageDisplayed || productsShown,
                    "Site should handle empty search appropriately");

            if (searchBoxStillVisible && !urlChanged) {
                ExtentReportManager.logPass("Empty search handled by keeping search box visible/active");
            } else if (errorMessageDisplayed) {
                ExtentReportManager.logPass("Empty search handled by displaying an error message");
            } else if (productsShown) {
                ExtentReportManager.logPass("Empty search shows all products (or redirects to shop page)");
            } else {
                ExtentReportManager.logPass("Empty search handled in an alternative but acceptable way");
            }

            ExtentReportManager.logPass("Empty search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test empty search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test empty search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_008: Verify search with special characters
    @Test
    public void testSpecialCharactersSearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with special characters");

        try {
            String searchQuery = "!@#$%^&*()";
            performSearch(searchQuery);

            // Special characters should generally not return results,
            // but we're flexible in how the site handles this
            Thread.sleep(2000);
            ExtentReportManager.captureScreenshot("Special Characters Search Results");

            // Check if search completed without errors - we're not testing for specific results,
            // just that the system handles special characters gracefully
            boolean noServerErrors = !driver.getPageSource().contains("Internal Server Error") &&
                    !driver.getPageSource().contains("database error");
            Assertions.assertTrue(noServerErrors, "No server errors should be displayed");
            ExtentReportManager.logPass("Special character search handled without server errors");

            // We'll accept either:
            // 1. No results found message
            boolean noResultsMessage = driver.getPageSource().toLowerCase().contains("no products") ||
                    driver.getPageSource().toLowerCase().contains("no results");

            // 2. Search page showing (possibly empty) results
            boolean onSearchPage = driver.getTitle().toLowerCase().contains("search") ||
                    driver.getCurrentUrl().toLowerCase().contains("search") ||
                    driver.getCurrentUrl().toLowerCase().contains("s=");

            // 3. Returned to shop page or home page
            boolean onValidPage = driver.getCurrentUrl().contains("shop") ||
                    driver.getTitle().toLowerCase().contains("shop") ||
                    driver.getCurrentUrl().endsWith("/");

            Assertions.assertTrue(noResultsMessage || onSearchPage || onValidPage,
                    "Site should handle special character search appropriately");

            if (noResultsMessage) {
                ExtentReportManager.logPass("Special character search shows 'no products found' message");
            } else if (onSearchPage) {
                ExtentReportManager.logPass("Special character search shows search results page");
            } else if (onValidPage) {
                ExtentReportManager.logPass("Special character search returns to a valid page");
            }

            ExtentReportManager.logPass("Special characters search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test special characters search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test special characters search: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_009: Verify search behavior with extremely long search term
    @Test
    public void testLongSearchTerm() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with extremely long search term");

        try {
            // Generate a very long search term (200+ characters)
            StringBuilder longTermBuilder = new StringBuilder();
            String baseText = "ThisIsAnExtremelyLongSearchTerm";
            // Repeat the base text to create a long string
            for (int i = 0; i < 10; i++) {
                longTermBuilder.append(baseText);
            }
            String searchQuery = longTermBuilder.toString();

            // Locate the search box (try different possible selectors)
            WebElement searchBox = null;

            try {
                // First method: Try finding search box directly if visible
                searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='search'], .search-field, #s")));
                ExtentReportManager.logInfo("Found search box directly");
            } catch (Exception e1) {
                // Second method: Try clicking search icon first
                try {
                    ExtentReportManager.logInfo("Trying to find and click search icon first");
                    WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector(".search-icon, .noo-search, .fa-search, [data-icon='search']")));
                    searchIcon.click();
                    Thread.sleep(2000);
                    ExtentReportManager.captureScreenshot("After Clicking Search Icon");

                    // Now try to find the search box again
                    searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("input[type='search'], .search-field, #s")));
                } catch (Exception e2) {
                    // Third method: Try using search that's in the header/sidebar
                    ExtentReportManager.logInfo("Trying to find search in sidebar/header area");
                    searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".search input, .widget_search input, #searchform input")));
                }
            }

            // Verify search box was found
            Assertions.assertNotNull(searchBox, "Search box should be found on the page");
            ExtentReportManager.logPass("Search box found");
            ExtentReportManager.captureScreenshot("Search Box Found");

            // Try to enter the full long search term
            ExtentReportManager.logStep("Entering extremely long search term (" + searchQuery.length() + " characters)");
            searchBox.clear();

            try {
                searchBox.sendKeys(searchQuery);
                Thread.sleep(1000);
            } catch (Exception e) {
                // If full search term fails, try a shorter one
                ExtentReportManager.logWarning("Could not enter full long term, trying shorter version: " + e.getMessage());
                searchBox.clear();
                searchBox.sendKeys(searchQuery.substring(0, 100));
                Thread.sleep(1000);
            }

            ExtentReportManager.captureScreenshot("Long Search Term Entered");

            // Log what was actually entered
            String actualValue = searchBox.getAttribute("value");
            ExtentReportManager.logInfo("Actual value entered in search box: '" + actualValue + "' (" + actualValue.length() + " characters)");

            // Submit the search
            boolean searchSubmitted = false;

            try {
                // Method 1: Press Enter key
                searchBox.sendKeys(Keys.ENTER);
                searchSubmitted = true;
                ExtentReportManager.logInfo("Long search submitted using Enter key");
            } catch (Exception e) {
                try {
                    // Method 2: Find and click search button if available
                    ExtentReportManager.logInfo("Trying to find and click search button");
                    WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("button[type='submit'], input[type='submit'], .search-submit")));
                    searchButton.click();
                    searchSubmitted = true;
                    ExtentReportManager.logInfo("Long search submitted by clicking search button");
                } catch (Exception e2) {
                    ExtentReportManager.logWarning("Could not find a way to submit long search: " + e2.getMessage());
                }
            }

            Thread.sleep(3000);
            ExtentReportManager.captureScreenshot("After Long Search Submission");

            // Check if search completed without errors
            boolean noServerErrors = !driver.getPageSource().contains("Internal Server Error") &&
                    !driver.getPageSource().contains("database error");
            Assertions.assertTrue(noServerErrors, "No server errors should be displayed");
            ExtentReportManager.logPass("Long search term handled without server errors");

            // Check if we're on a valid page
            boolean onValidPage = driver.getTitle().toLowerCase().contains("search") ||
                    driver.getCurrentUrl().toLowerCase().contains("search") ||
                    driver.getCurrentUrl().toLowerCase().contains("s=") ||
                    driver.getCurrentUrl().contains("shop");

            Assertions.assertTrue(onValidPage, "Should remain on a valid page after long search term");
            ExtentReportManager.logPass("Remained on valid page after long search term");

            ExtentReportManager.logPass("Long search term test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test long search term: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test long search term: " + e.getMessage());
        }
    }

    // TC_PAT_SEARCH_010: Verify search with numeric values only
    @Test
    public void testNumericSearch() throws InterruptedException {
        ExtentReportManager.logStep("Testing search with numeric values only");

        try {
            String searchQuery = "12345";
            performSearch(searchQuery);

            // Numeric searches could have valid results or no results - we're testing
            // that the system handles this appropriately
            Thread.sleep(2000);
            ExtentReportManager.captureScreenshot("Numeric Search Results");

            // Check if search completed without errors
            boolean searchCompleted = driver.getTitle().toLowerCase().contains("search") ||
                    driver.getCurrentUrl().toLowerCase().contains("search") ||
                    driver.getCurrentUrl().toLowerCase().contains("s=") ||
                    driver.getCurrentUrl().contains("shop");
            Assertions.assertTrue(searchCompleted, "Search should complete without errors");
            ExtentReportManager.logPass("Numeric search completed without errors");

            // We're flexible about results - could find products with numbers, or not
            boolean noResultsMessageExists = driver.getPageSource().toLowerCase().contains("no products") ||
                    driver.getPageSource().toLowerCase().contains("no results");

            boolean productsFound = !driver.findElements(By.cssSelector(".product, .products li, article")).isEmpty();

            // Either outcome is acceptable
            if (noResultsMessageExists) {
                ExtentReportManager.logPass("Numeric search shows 'no products found' message");
            } else if (productsFound) {
                int resultCount = driver.findElements(By.cssSelector(".product, .products li, article")).size();
                ExtentReportManager.logPass("Numeric search returned " + resultCount + " products");
            } else {
                ExtentReportManager.logWarning("Neither 'no products' message nor products found, but search completed");
            }

            // Verify no server errors are shown
            boolean noServerErrors = !driver.getPageSource().contains("Internal Server Error") &&
                    !driver.getPageSource().contains("database error");
            Assertions.assertTrue(noServerErrors, "No server errors should be displayed");
            ExtentReportManager.logPass("No server errors displayed for numeric search");

            ExtentReportManager.logPass("Numeric search test passed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to test numeric search: " + e.getMessage());
            ExtentReportManager.captureScreenshot("Error State");
            Assertions.fail("Failed to test numeric search: " + e.getMessage());
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