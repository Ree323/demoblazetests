package tests.base;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.CartPage;
import pages.HomePage;
import tests.base.BaseTest;
import utils.ExtentReportManager;
import utils.TestData;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DemoBlaze Cart Test Suite")
public class CartTest extends BaseTest {
    private HomePage homePage;
    private CartPage cartPage;
    private WebDriverWait wait;

    @BeforeEach
    public void setUpTest(TestInfo testInfo) {
        ExtentReportManager.createTest(testInfo.getDisplayName(), "Cart Test Execution");
        homePage = new HomePage(driver);
        cartPage = new CartPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestData.DEFAULT_WAIT_SECONDS));
        homePage.navigateToHome();
    }

    @Test
    @DisplayName("TC_CART_001: Place Order button exists and is clickable when cart has items")
    public void testPlaceOrderButtonWithItems() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();

        assertAll(
                () -> assertTrue(cartPage.getCartItemCount() > 0, "Cart should have items"),
                () -> assertDoesNotThrow(cartPage::clickPlaceOrderButton, "Place Order button should be clickable")
        );
    }

    @Test
    @DisplayName("TC_CART_002: Verify cart is cleared after purchase")
    public void testCartClearedAfterPurchase() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S6)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart()
                .clickPlaceOrderButton()
                .fillOrderForm(TestData.TEST_NAME, TestData.TEST_COUNTRY, TestData.TEST_CITY,
                        TestData.TEST_CREDIT_CARD, TestData.TEST_MONTH, TestData.TEST_YEAR)
                .clickPurchaseButton()
                .handleConfirmation();

        cartPage.navigateToCart();
        assertEquals(0, cartPage.getCartItemCount(), "Cart should be empty after purchase");
    }

    @Test
    @DisplayName("TC_CART_003: Verify no delete confirmation alert appears")
    public void testDeleteConfirmationAlert() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        cartPage.deleteFirstItem();

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            fail("Unexpected alert appeared with text: " + alert.getText());
        } catch (TimeoutException e) {
            // Expected - no alert should appear
        }
    }

    @Test
    @DisplayName("TC_CART_004: Verify item is removed from cart when clicking Delete")
    public void testItemRemovalFromCart() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert()
                .navigateToHome()
                .clickProductByName(TestData.PRODUCT_NEXUS_6)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        int initialCount = cartPage.getCartItemCount();
        cartPage.deleteFirstItem();

        wait.until(d -> cartPage.getCartItemCount() == initialCount - 1);
        assertEquals(initialCount - 1, cartPage.getCartItemCount(), "Item count should decrease after deletion");
    }

    @Test
    @DisplayName("TC_CART_005: Verify quantity counters are displayed for cart items - EXPECTED FAILURE")
    public void testCartItemQuantities() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        List<WebElement> items = cartPage.getAllCartItems();

        try {
            items.forEach(item -> {
                WebElement quantity = item.findElement(By.cssSelector("input[type='number']"));
                assertTrue(quantity.isDisplayed(), "Quantity counter should be visible");
            });
            fail("Expected to fail - DemoBlaze doesn't show quantity counters");
        } catch (NoSuchElementException e) {
            // Expected failure
        }
    }

    @Test
    @DisplayName("TC_CART_006: Verify cart link works from all pages")
    public void testCartLinkOnAllPages() {
        // Test from home page
        homePage.navigateToCart();
        assertTrue(driver.getCurrentUrl().contains("cart.html"), "Should be on cart page");

        // Test from product page
        homePage.navigateToHome()
                .clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .navigateToCart();
        assertTrue(driver.getCurrentUrl().contains("cart.html"), "Should be on cart page");
    }

    @Test
    @DisplayName("TC_CART_007: Verify no cart limit exists by adding multiple products")
    public void testCartProductLimit() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            homePage.navigateToHome()
                    .clickProductByIndex(i % 3)
                    .clickAddToCart()
                    .handleAlert();
            Thread.sleep(1000);
        }

        cartPage.navigateToCart();
        assertTrue(cartPage.getCartItemCount() >= 5, "Should be able to add multiple products");
    }

    @Test
    @DisplayName("TC_CART_008: Verify cart persistence with delays")
    public void testCartPersistence() throws InterruptedException {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert();

        Thread.sleep(2000);
        driver.navigate().refresh();

        cartPage.navigateToCart();
        assertTrue(cartPage.getCartItemCount() > 0, "Cart items should persist after delay");
    }

    @Test
    @DisplayName("TC_CART_009: Verify same product can be added to cart multiple times")
    public void testAddSameProductMultipleTimes() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S6)
                .clickAddToCart()
                .handleAlert()
                .navigateToHome()
                .clickProductByName(TestData.PRODUCT_SAMSUNG_S6)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        assertTrue(cartPage.getCartItemCount() >= 2, "Same product should appear multiple times");
    }

    @Test
    @DisplayName("TC_CART_010: Verify total cart capacity")
    public void testCartCapacity() {
        // Add until we hit limit or max attempts
        int attempts = 0;
        while (attempts < 15) {
            homePage.navigateToHome()
                    .clickProductByIndex(attempts % 3)
                    .clickAddToCart()
                    .handleAlert();
            attempts++;
        }

        cartPage.navigateToCart();
        assertTrue(cartPage.getCartItemCount() > 10, "Cart should hold many items");
    }

    @Test
    @DisplayName("TC_CART_011: Verify cart total matches sum of individual item prices")
    public void testCartTotalCalculation() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert()
                .navigateToHome()
                .clickProductByName(TestData.PRODUCT_NEXUS_6)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        double sum = cartPage.getAllCartItems().stream()
                .mapToDouble(item -> parsePrice(item.findElement(By.cssSelector("td:nth-child(3)")).getText()))
                .sum();

        assertEquals(sum, cartPage.getTotalPrice(), 0.001, "Total should match sum of items");
    }

    @Test
    @DisplayName("TC_CART_012: Verify total updates when items are removed")
    public void testTotalUpdatesOnRemoval() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert()
                .navigateToHome()
                .clickProductByName(TestData.PRODUCT_NEXUS_6)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        double initialTotal = cartPage.getTotalPrice();
        cartPage.deleteFirstItem();

        assertNotEquals(initialTotal, cartPage.getTotalPrice(), "Total should update after removal");
    }

    @Test
    @DisplayName("TC_CART_013: Verify purchase attempt with empty cart")
    public void testPurchaseWithEmptyCart() {
        cartPage.navigateToCart()
                .deleteAllItems()
                .clickPlaceOrderButton();

        try {
            Alert alert = driver.switchTo().alert();
            fail("Unexpected alert: " + alert.getText());
        } catch (NoAlertPresentException e) {
            // Expected - no alert should appear
        }
    }

    @Test
    @DisplayName("TC_CART_014: Verify cart table is empty when no products are added")
    public void testEmptyCartTable() {
        cartPage.navigateToCart()
                .deleteAllItems();

        assertEquals(0, cartPage.getCartItemCount(), "Cart table should be empty");
    }

    @Test
    @DisplayName("TC_CART_015: Verify cart shows products when added")
    public void testCartShowsProducts() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        assertTrue(cartPage.isProductInCart(TestData.PRODUCT_SAMSUNG_S7), "Product should be in cart");
    }

    @Test
    @DisplayName("TC_CART_016: Verify non-empty cart displays items with all required elements")
    public void testNonEmptyCartDisplay() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        List<WebElement> items = cartPage.getAllCartItems();
        assertFalse(items.isEmpty(), "Cart should have items");

        items.forEach(item -> {
            assertNotNull(item.findElement(By.cssSelector("td img")), "Image should exist");
            assertNotNull(item.findElement(By.cssSelector("td:nth-child(2)")), "Title should exist");
            assertNotNull(item.findElement(By.cssSelector("td:nth-child(3)")), "Price should exist");
            assertNotNull(item.findElement(By.xpath(".//a[text()='Delete']")), "Delete button should exist");
        });
    }

    @Test
    @DisplayName("TC_CART_017: Verify delete button removes item from cart")
    public void testDeleteButtonFunctionality() {
        homePage.clickProductByName(TestData.PRODUCT_SAMSUNG_S7)
                .clickAddToCart()
                .handleAlert();

        cartPage.navigateToCart();
        int initialCount = cartPage.getCartItemCount();
        cartPage.deleteFirstItem();

        assertEquals(initialCount - 1, cartPage.getCartItemCount(), "Item should be removed");
    }

    private double parsePrice(String priceText) {
        if (priceText == null || priceText.trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
    }
}