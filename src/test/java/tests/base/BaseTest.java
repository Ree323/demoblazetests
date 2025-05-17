package tests.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExtentReportManager;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    public static void setupReport() {
        // Initialize ExtentReports
        ExtentReportManager.init();
    }

    @BeforeAll
    public static void setupClass() {
        // Setup WebDriverManager
        WebDriverManager.chromedriver().setup();
        // Initialize ExtentReports
        ExtentReportManager.init();
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        // Create test in ExtentReports
        String testName = testInfo.getDisplayName();
        ExtentReportManager.createTest(testName, "Testing registration functionality");

        // Set up browser
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Set driver for screenshots
        ExtentReportManager.setDriver(driver);

        // Create a wait object for better element handling
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to the website
        ExtentReportManager.logStep("Navigating to DemoBlaze website");
        driver.get("https://www.demoblaze.com/");
        ExtentReportManager.captureScreenshot("Home Page");

        // Wait for page to fully load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            ExtentReportManager.logWarning("Page load wait was interrupted");
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void tearDownReport() {
        // Generate the report - THIS IS CRITICAL
        ExtentReportManager.flush();
    }

    // In BaseTest.java, update the setupClass method

}