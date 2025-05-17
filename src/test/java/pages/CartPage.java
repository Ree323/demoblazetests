package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestData;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators using TestData constants
    private final By cartItemsLocator = By.cssSelector(TestData.CART_ITEMS_CSS);
    private final By placeOrderButtonLocator = By.xpath(TestData.PLACE_ORDER_BUTTON_XPATH);
    private final By deleteButtonLocator = By.xpath(TestData.DELETE_BUTTON_XPATH);
    private final By totalPriceLocator = By.id(TestData.TOTAL_PRICE_ID);
    private final By orderFormNameLocator = By.id("name");
    private final By orderFormCountryLocator = By.id("country");
    private final By orderFormCityLocator = By.id("city");
    private final By orderFormCardLocator = By.id("card");
    private final By orderFormMonthLocator = By.id("month");
    private final By orderFormYearLocator = By.id("year");
    private final By purchaseButtonLocator = By.xpath("//button[contains(text(),'Purchase')]");
    private final By confirmationModalLocator = By.xpath("//h2[contains(text(),'Thank you')]");
    private final By okButtonLocator = By.xpath("//button[contains(text(),'OK')]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestData.DEFAULT_WAIT_SECONDS));
    }

    public void navigateToCart() {
        driver.get(TestData.CART_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(cartItemsLocator));
    }

    public int getCartItemCount() {
        return driver.findElements(cartItemsLocator).size();
    }

    public void clickPlaceOrderButton() {
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderButtonLocator)).click();
    }

    public void deleteFirstItem() {
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(deleteButtonLocator));
        deleteButton.click();
        wait.until(ExpectedConditions.numberOfElementsToBe(cartItemsLocator, getCartItemCount() - 1));
    }

    public void deleteAllItems() {
        List<WebElement> deleteButtons = driver.findElements(deleteButtonLocator);
        for (WebElement button : deleteButtons) {
            button.click();
            wait.until(ExpectedConditions.stalenessOf(button));
        }
    }

    public double getTotalPrice() {
        String priceText = wait.until(ExpectedConditions.visibilityOfElementLocated(totalPriceLocator)).getText();
        return Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
    }

    public List<WebElement> getAllCartItems() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cartItemsLocator));
    }

    public boolean isProductInCart(String productName) {
        return !driver.findElements(By.xpath("//tr[td[contains(.,'" + productName + "')]]")).isEmpty();
    }

    public void verifyCartItemElements(WebElement item) {
        WebElement image = item.findElement(By.cssSelector("td img"));
        WebElement title = item.findElement(By.cssSelector("td:nth-child(2)"));
        WebElement price = item.findElement(By.cssSelector("td:nth-child(3)"));
        WebElement deleteBtn = item.findElement(deleteButtonLocator);

        assert image.isDisplayed() : "Product image should be visible";
        assert title.isDisplayed() : "Product title should be visible";
        assert price.isDisplayed() : "Product price should be visible";
        assert deleteBtn.isDisplayed() : "Delete button should be visible";
    }

    public CartPage fillOrderForm(String name, String country, String city,
                                  String card, String month, String year) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderFormNameLocator)).sendKeys(name);
        driver.findElement(orderFormCountryLocator).sendKeys(country);
        driver.findElement(orderFormCityLocator).sendKeys(city);
        driver.findElement(orderFormCardLocator).sendKeys(card);
        driver.findElement(orderFormMonthLocator).sendKeys(month);
        driver.findElement(orderFormYearLocator).sendKeys(year);
        return this;
    }

    public CartPage clickPurchaseButton() {
        wait.until(ExpectedConditions.elementToBeClickable(purchaseButtonLocator)).click();
        return this;
    }

    public CartPage handleConfirmation() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationModalLocator));
        wait.until(ExpectedConditions.elementToBeClickable(okButtonLocator)).click();
        return this;
    }

    public CartPage completePurchase() {
        clickPlaceOrderButton();
        fillOrderForm(TestData.TEST_NAME, TestData.TEST_COUNTRY, TestData.TEST_CITY,
                TestData.TEST_CREDIT_CARD, TestData.TEST_MONTH, TestData.TEST_YEAR);
        clickPurchaseButton();
        handleConfirmation();
        return this;
    }
}