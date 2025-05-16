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
}