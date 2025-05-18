package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ExtentReportManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryTest extends BaseTest {

    /**
     * TC_CAT_001: Verify categories display in left sidebar
     */
    @Test
    @DisplayName("TC_CAT_001: Verify categories display in left sidebar")
    public void testCategoriesDisplay() {
        ExtentReportManager.logStep("Testing categories display in left sidebar");

        // Verify categories header
        WebElement categoriesHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@id='cat' and contains(text(), 'CATEGORIES')]")));
        Assertions.assertTrue(categoriesHeader.isDisplayed(), "CATEGORIES header should be displayed");
        ExtentReportManager.logPass("CATEGORIES header is displayed");
        ExtentReportManager.captureScreenshot("Categories Sidebar");

        // Verify Phones category
        WebElement phonesCategory = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(), 'Phones')]")));
        Assertions.assertTrue(phonesCategory.isDisplayed(), "Phones category should be displayed");
        ExtentReportManager.logPass("Phones category is displayed");

        // Verify Laptops category
        WebElement laptopsCategory = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(), 'Laptops')]")));
        Assertions.assertTrue(laptopsCategory.isDisplayed(), "Laptops category should be displayed");
        ExtentReportManager.logPass("Laptops category is displayed");

        // Verify Monitors category
        WebElement monitorsCategory = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(), 'Monitors')]")));
        Assertions.assertTrue(monitorsCategory.isDisplayed(), "Monitors category should be displayed");
        ExtentReportManager.logPass("Monitors category is displayed");

        // Verify categories are in correct order
        List<WebElement> categoryElements = driver.findElements(By.cssSelector(".list-group a"));
        List<String> categoryNames = categoryElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        ExtentReportManager.logInfo("Categories found: " + String.join(", ", categoryNames));

        // Skip the first element which is the CATEGORIES header
        Assertions.assertTrue(categoryNames.size() >= 4, "Should have at least 4 category elements");
        Assertions.assertEquals("CATEGORIES", categoryNames.get(0), "First item should be CATEGORIES");
        Assertions.assertEquals("Phones", categoryNames.get(1), "Second item should be Phones");
        Assertions.assertEquals("Laptops", categoryNames.get(2), "Third item should be Laptops");
        Assertions.assertEquals("Monitors", categoryNames.get(3), "Fourth item should be Monitors");

        ExtentReportManager.logPass("Categories are displayed in the correct order");
        ExtentReportManager.logPass("Categories display test completed successfully");
    }

    /**
     * TC_CAT_002: Verify Phones category filter displays only phone products
     */
    @Test
    @DisplayName("TC_CAT_002: Verify Phones category filter displays only phone products")
    public void testPhonesCategory() {
        ExtentReportManager.logStep("Testing Phones category filter");

        // Expected phone products
        List<String> expectedPhoneProducts = Arrays.asList("Samsung galaxy s6", "Nokia lumia 1520", "Nexus 6",
                "Samsung galaxy s7", "Iphone 6 32gb", "Sony xperia z5", "HTC One M9");

        // Click on Phones category
        WebElement phonesCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Phones')]")));
        phonesCategory.click();

        // Wait for products to load
        try {
            Thread.sleep(2000); // Wait for products to load after category selection
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Phones Category Selected");

        // Get all product names
        List<WebElement> productElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".card-title")));

        List<String> displayedProductNames = productElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        ExtentReportManager.logInfo("Products displayed: " + String.join(", ", displayedProductNames));

        // Verify all displayed products are phones
        boolean allProductsArePhones = true;
        for (String productName : displayedProductNames) {
            if (!expectedPhoneProducts.contains(productName)) {
                allProductsArePhones = false;
                ExtentReportManager.logWarning("Non-phone product found: " + productName);
            }
        }

        Assertions.assertTrue(allProductsArePhones, "Only phone products should be displayed");

        // Verify at least some expected phone products are displayed
        boolean someExpectedPhonesDisplayed = false;
        for (String phoneName : expectedPhoneProducts) {
            if (displayedProductNames.contains(phoneName)) {
                someExpectedPhonesDisplayed = true;
                ExtentReportManager.logInfo("Expected phone product found: " + phoneName);
            }
        }

        Assertions.assertTrue(someExpectedPhonesDisplayed, "Some expected phone products should be displayed");

        // Check that we have at least 3 phone products displayed
        Assertions.assertTrue(displayedProductNames.size() >= 3,
                "At least 3 phone products should be displayed, found: " + displayedProductNames.size());

        ExtentReportManager.logPass("Phones category filter works correctly");
    }

    /**
     * TC_CAT_003: Verify Laptops category filter displays only laptop products
     */
    @Test
    @DisplayName("TC_CAT_003: Verify Laptops category filter displays only laptop products")
    public void testLaptopsCategory() {
        ExtentReportManager.logStep("Testing Laptops category filter");

        // Expected laptop products
        List<String> expectedLaptopProducts = Arrays.asList("Sony vaio i5", "Sony vaio i7",
                "MacBook air", "Dell i7 8gb", "2017 Dell 15.6 Inch", "MacBook Pro");

        // Click on Laptops category
        WebElement laptopsCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Laptops')]")));
        laptopsCategory.click();

        // Wait for products to load
        try {
            Thread.sleep(2000); // Wait for products to load after category selection
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Laptops Category Selected");

        // Get all product names
        List<WebElement> productElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".card-title")));

        List<String> displayedProductNames = productElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        ExtentReportManager.logInfo("Products displayed: " + String.join(", ", displayedProductNames));

        // Verify all displayed products are laptops
        boolean allProductsAreLaptops = true;
        for (String productName : displayedProductNames) {
            if (!expectedLaptopProducts.contains(productName)) {
                allProductsAreLaptops = false;
                ExtentReportManager.logWarning("Non-laptop product found: " + productName);
            }
        }

        Assertions.assertTrue(allProductsAreLaptops, "Only laptop products should be displayed");

        // Verify at least some expected laptop products are displayed
        boolean someExpectedLaptopsDisplayed = false;
        for (String laptopName : expectedLaptopProducts) {
            if (displayedProductNames.contains(laptopName)) {
                someExpectedLaptopsDisplayed = true;
                ExtentReportManager.logInfo("Expected laptop product found: " + laptopName);
            }
        }

        Assertions.assertTrue(someExpectedLaptopsDisplayed, "Some expected laptop products should be displayed");

        // Check that we have at least 3 laptop products displayed
        Assertions.assertTrue(displayedProductNames.size() >= 3,
                "At least 3 laptop products should be displayed, found: " + displayedProductNames.size());

        ExtentReportManager.logPass("Laptops category filter works correctly");
    }

    /**
     * TC_CAT_004: Verify Monitors category filter displays only monitor products
     */
    @Test
    @DisplayName("TC_CAT_004: Verify Monitors category filter displays only monitor products")
    public void testMonitorsCategory() {
        ExtentReportManager.logStep("Testing Monitors category filter");

        // Expected monitor products
        List<String> expectedMonitorProducts = Arrays.asList("Apple monitor 24", "ASUS Full HD", "ASUS Full HD");

        // Click on Monitors category
        WebElement monitorsCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Monitors')]")));
        monitorsCategory.click();

        // Wait for products to load
        try {
            Thread.sleep(2000); // Wait for products to load after category selection
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        ExtentReportManager.captureScreenshot("Monitors Category Selected");

        // Get all product names
        List<WebElement> productElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".card-title")));

        List<String> displayedProductNames = productElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        ExtentReportManager.logInfo("Products displayed: " + String.join(", ", displayedProductNames));

        // Verify all displayed products are monitors
        boolean allProductsAreMonitors = true;
        for (String productName : displayedProductNames) {
            if (!expectedMonitorProducts.contains(productName)) {
                allProductsAreMonitors = false;
                ExtentReportManager.logWarning("Non-monitor product found: " + productName);
            }
        }

        Assertions.assertTrue(allProductsAreMonitors, "Only monitor products should be displayed");

        // Verify at least some expected monitor products are displayed
        boolean someExpectedMonitorsDisplayed = false;
        for (String monitorName : expectedMonitorProducts) {
            if (displayedProductNames.contains(monitorName)) {
                someExpectedMonitorsDisplayed = true;
                ExtentReportManager.logInfo("Expected monitor product found: " + monitorName);
            }
        }

        Assertions.assertTrue(someExpectedMonitorsDisplayed, "Some expected monitor products should be displayed");

        // Check that we have at least 1 monitor product displayed (monitors category typically has fewer products)
        Assertions.assertTrue(displayedProductNames.size() >= 1,
                "At least 1 monitor product should be displayed, found: " + displayedProductNames.size());

        ExtentReportManager.logPass("Monitors category filter works correctly");
    }

    /**
     * TC_CAT_005: Verify clicking on "CATEGORIES" displays all products
     */
    @Test
    @DisplayName("TC_CAT_005: Verify clicking on CATEGORIES displays all products")
    public void testCategoriesHeader() {
        ExtentReportManager.logStep("Testing CATEGORIES header functionality");

        // First verify all product categories are initially displayed
        List<WebElement> initialProductElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".card-title")));
        int initialProductCount = initialProductElements.size();

        ExtentReportManager.logInfo("Initial product count: " + initialProductCount);
        ExtentReportManager.captureScreenshot("Initial Products");

        // Click on Phones category
        ExtentReportManager.logStep("Applying Phones category filter");
        WebElement phonesCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Phones')]")));
        phonesCategory.click();

        // Wait for products to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-title")));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Get phone products count
        List<WebElement> phoneProductElements = driver.findElements(By.cssSelector(".card-title"));
        int phoneProductCount = phoneProductElements.size();

        ExtentReportManager.logInfo("Phone products count: " + phoneProductCount);
        ExtentReportManager.captureScreenshot("Phones Category Products");

        // Click on CATEGORIES header
        ExtentReportManager.logStep("Clicking on CATEGORIES header to clear filter");
        WebElement categoriesHeader = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@id='cat' and contains(text(), 'CATEGORIES')]")));
        categoriesHeader.click();

        // Wait for all products to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Get all products count after clicking CATEGORIES header
        List<WebElement> allProductsElements = driver.findElements(By.cssSelector(".card-title"));
        int allProductsCount = allProductsElements.size();

        ExtentReportManager.logInfo("All products count after clicking CATEGORIES: " + allProductsCount);
        ExtentReportManager.captureScreenshot("All Products After Clicking CATEGORIES");

        // Verify all products are displayed
        Assertions.assertTrue(allProductsCount >= initialProductCount,
                "All products should be displayed after clicking CATEGORIES");
        Assertions.assertTrue(allProductsCount > phoneProductCount,
                "Product count after clicking CATEGORIES should be greater than phone product count");

        // Now click on Laptops category
        ExtentReportManager.logStep("Applying Laptops category filter");
        WebElement laptopsCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Laptops')]")));
        laptopsCategory.click();

        // Wait for products to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-title")));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Get laptop products count
        List<WebElement> laptopProductElements = driver.findElements(By.cssSelector(".card-title"));
        int laptopProductCount = laptopProductElements.size();

        ExtentReportManager.logInfo("Laptop products count: " + laptopProductCount);
        ExtentReportManager.captureScreenshot("Laptops Category Products");

        // Click on CATEGORIES header again
        ExtentReportManager.logStep("Clicking on CATEGORIES header again to clear filter");
        categoriesHeader = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@id='cat' and contains(text(), 'CATEGORIES')]")));
        categoriesHeader.click();

        // Wait for all products to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Get all products count after clicking CATEGORIES header
        allProductsElements = driver.findElements(By.cssSelector(".card-title"));
        int allProductsCountAfterLaptops = allProductsElements.size();

        ExtentReportManager.logInfo("All products count after clicking CATEGORIES again: " + allProductsCountAfterLaptops);
        ExtentReportManager.captureScreenshot("All Products After Clicking CATEGORIES Again");

        // Verify all products are displayed
        Assertions.assertTrue(allProductsCountAfterLaptops >= initialProductCount,
                "All products should be displayed after clicking CATEGORIES");
        Assertions.assertTrue(allProductsCountAfterLaptops > laptopProductCount,
                "Product count after clicking CATEGORIES should be greater than laptop product count");

        // Finally, click on Monitors category
        ExtentReportManager.logStep("Applying Monitors category filter");
        WebElement monitorsCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Monitors')]")));
        monitorsCategory.click();

        // Wait for products to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-title")));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Get monitor products count
        List<WebElement> monitorProductElements = driver.findElements(By.cssSelector(".card-title"));
        int monitorProductCount = monitorProductElements.size();

        ExtentReportManager.logInfo("Monitor products count: " + monitorProductCount);
        ExtentReportManager.captureScreenshot("Monitors Category Products");

        // Click on CATEGORIES header one more time
        ExtentReportManager.logStep("Clicking on CATEGORIES header one more time to clear filter");
        categoriesHeader = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@id='cat' and contains(text(), 'CATEGORIES')]")));
        categoriesHeader.click();

        // Wait for all products to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Wait interrupted: " + e.getMessage());
        }

        // Get all products count after clicking CATEGORIES header
        allProductsElements = driver.findElements(By.cssSelector(".card-title"));
        int allProductsCountAfterMonitors = allProductsElements.size();

        ExtentReportManager.logInfo("All products count after clicking CATEGORIES one more time: " + allProductsCountAfterMonitors);
        ExtentReportManager.captureScreenshot("All Products After Clicking CATEGORIES One More Time");

        // Verify all products are displayed
        Assertions.assertTrue(allProductsCountAfterMonitors >= initialProductCount,
                "All products should be displayed after clicking CATEGORIES");
        Assertions.assertTrue(allProductsCountAfterMonitors > monitorProductCount,
                "Product count after clicking CATEGORIES should be greater than monitor product count");

        ExtentReportManager.logPass("CATEGORIES header functionality test completed successfully");
    }
}