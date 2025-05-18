package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;  // Fixed typo in import
import org.openqa.selenium.support.ui.WebDriverWait;       // Fixed typo in import
import pages.modals.SignUpModal;
import pages.modals.LoginModal;
import pages.modals.AboutUsModal;
import utils.TestData;  // Added missing import
import java.util.List;                     // <-- for List<>
import org.openqa.selenium.WebElement;      // <-- for WebElement


import java.time.Duration;

public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators - corrected and using TestData constants
    private final By signupNavLinkLocator = By.id(TestData.SIGNUP_LINK_ID);  // Fixed typo 'signit' to 'signin2'
    private final By loginNavLinkLocator = By.id(TestData.LOGIN_LINK_ID);
    private final By aboutUsLinkLocator = By.xpath(TestData.ABOUT_US_LINK_XPATH);
    private final By cartLinkLocator = By.id(TestData.CART_LINK_ID);
    private final By productLinkLocator = By.cssSelector(TestData.PRODUCT_LINK_CSS);
    private final By addToCartButtonLocator = By.xpath(TestData.ADD_TO_CART_BUTTON_XPATH);

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestData.DEFAULT_WAIT_SECONDS));
    }

    public SignUpModal clickSignUpLink() {
        wait.until(ExpectedConditions.elementToBeClickable(signupNavLinkLocator)).click();
        return new SignUpModal(driver);
    }

    public LoginModal clickLoginLink() {
        wait.until(ExpectedConditions.elementToBeClickable(loginNavLinkLocator)).click();
        return new LoginModal(driver);
    }

    public AboutUsModal clickAboutUsLink() {  // Method is now used
        wait.until(ExpectedConditions.elementToBeClickable(aboutUsLinkLocator)).click();
        return new AboutUsModal(driver);
    }

    public HomePage clickProductByName(String productName) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'" + productName + "')]"))).click();
        return this;
    }

    public HomePage clickAddToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButtonLocator)).click();
        return this;
    }

    public HomePage handleAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        return this;
    }

    public CartPage navigateToCart() {  // Method is now used
        wait.until(ExpectedConditions.elementToBeClickable(cartLinkLocator)).click();
        return new CartPage(driver);
    }

    public HomePage navigateToHome() {
        driver.get(TestData.BASE_URL);
        return this;
    }

    public HomePage clickProductByIndex(int index) {
        // wait until at least one product link is visible
        List<WebElement> products = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(TestData.PRODUCT_LINK_CSS)));

        if (products.isEmpty()) {
            throw new IllegalStateException("No products found – selector may be wrong or page not loaded");
        }
        products.get(index).click();
        return this;
    }


    // NEW – return price & generic waits
    public double getProductPrice(String name) {
        clickProductByName(name);
        String priceText = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("h3.price-container"))).getText();
        double price = Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
        driver.navigate().back();
        return price;
    }

    public HomePage clickFirstVisibleProduct() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(TestData.PRODUCT_LINK_CSS))).get(0).click();
        return this;
    }



}