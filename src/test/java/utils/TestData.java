package utils;

/**
 * Utility class to centralize all test data for DemoBlaze test automation
 */
public class TestData {
    // Base URLs
    public static final String BASE_URL = "https://www.demoblaze.com/";
    public static final String CART_URL = BASE_URL + "cart.html";
    public static final String PRODUCT_URL = BASE_URL + "prod.html?idp_=1";

    // Test user credentials - using same credentials for all test cases as requested
    public static final String TEST_USERNAME = "Rama27";
    public static final String TEST_PASSWORD = "rama123";

    // For backward compatibility with other tests that might expect different credentials
    public static final String LOGOUT_TEST_USERNAME = TEST_USERNAME;
    public static final String LOGOUT_TEST_PASSWORD = TEST_PASSWORD;

    public static final String LOGOUT_STATE_USERNAME = TEST_USERNAME;
    public static final String LOGOUT_STATE_PASSWORD = TEST_PASSWORD;

    public static final String LOGOUT_REDIRECT_USERNAME = TEST_USERNAME;
    public static final String LOGOUT_REDIRECT_PASSWORD = TEST_PASSWORD;

    // Login test users
    public static final String VALID_USERNAME = TEST_USERNAME;
    public static final String VALID_PASSWORD = TEST_PASSWORD;

    public static final String INVALID_USERNAME = "nonexistentuser";
    public static final String INVALID_PASSWORD = "wrongpass";

    // Registration test users
    // Note: For registration tests, you might want to generate unique usernames
    public static String getUniqueUsername() {
        return "user" + System.currentTimeMillis();
    }

    // Product IDs for product tests
    public static final String VALID_PRODUCT_ID = "1"; // Samsung Galaxy S6
    public static final String INVALID_PRODUCT_ID = "999";

    // Category IDs
    public static final String PHONES_CATEGORY = "phone";
    public static final String LAPTOPS_CATEGORY = "notebook";
    public static final String MONITORS_CATEGORY = "monitor";

    // Expected text values for validation
    public static final String WELCOME_MESSAGE_PREFIX = "Welcome ";
    public static final String PRODUCT_ADDED_MESSAGE = "Product added";

    // Timeouts and waits
    public static final int SHORT_WAIT = 1000; // 1 second
    public static final int MEDIUM_WAIT = 2000; // 2 seconds
    public static final int LONG_WAIT = 5000; // 5 seconds
    public static final int PAGE_LOAD_TIMEOUT = 30; // 30 seconds
    public static final int IMPLICIT_WAIT = 10; // 10 seconds

    // Element locators (if you want to centralize them)
    public static final String LOGIN_LINK_ID = "login2";
    public static final String LOGOUT_LINK_ID = "logout2";
    public static final String USERNAME_FIELD_ID = "loginusername";
    public static final String PASSWORD_FIELD_ID = "loginpassword";
    public static final String WELCOME_MESSAGE_ID = "nameofuser";

    // Add to TestData.java
    public static final String CAROUSEL_CONTAINER_ID = "carouselExampleIndicators";
    public static final String CAROUSEL_ACTIVE_SLIDE_CSS = ".carousel-item.active";
    public static final String CAROUSEL_SLIDES_CSS = ".carousel-item";
    public static final String CAROUSEL_INDICATORS_CSS = ".carousel-indicators li";
    public static final String CAROUSEL_NEXT_BUTTON_CSS = ".carousel-control-next";
    public static final String CAROUSEL_PREV_BUTTON_CSS = ".carousel-control-prev";

    // Test Products
    public static final String PRODUCT_SAMSUNG_S7 = "Samsung galaxy s7";
    public static final String PRODUCT_SAMSUNG_S6 = "Samsung galaxy s6";
    public static final String PRODUCT_NEXUS_6 = "Nexus 6";

    // Locators (for HomePage)
    public static final String SIGNUP_LINK_ID = "signin2";
    public static final String ABOUT_US_LINK_XPATH = "//a[contains(text(),'About us')]";
    public static final String CART_LINK_ID = "cartur";
    public static final String PRODUCT_LINK_CSS = "a.hrefch";
    public static final String ADD_TO_CART_BUTTON_XPATH = "//a[contains(text(),'Add to cart')]";

    // Locators (for CartPage)
    public static final String CART_ITEMS_CSS = "#tbodyid tr";
    public static final String PLACE_ORDER_BUTTON_XPATH = "//button[contains(text(),'Place Order')]";
    public static final String DELETE_BUTTON_XPATH = "//a[contains(text(),'Delete')]";
    public static final String TOTAL_PRICE_ID = "totalp";

    // Order Form Data
    public static final String TEST_NAME = "Test User";
    public static final String TEST_COUNTRY = "Test Country";
    public static final String TEST_CITY = "Test City";
    public static final String TEST_CREDIT_CARD = "1234123412341234";
    public static final String TEST_MONTH = "12";
    public static final String TEST_YEAR = "2025";

    // Timeouts
    public static final int DEFAULT_WAIT_SECONDS = 10;
    public static final int ALERT_WAIT_SECONDS = 5;
    public static final int CART_UPDATE_DELAY_MS = 2000;


}