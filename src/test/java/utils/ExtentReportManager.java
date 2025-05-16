package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static Map<Long, ExtentTest> testMap = new HashMap<>();
    private static Map<Long, WebDriver> driverMap = new HashMap<>();

    /**
     * Initialize the ExtentReports instance
     */
    public static synchronized ExtentReports init() {
        if (extent == null) {
            // Create report directory if it doesn't exist
            String reportPath = System.getProperty("user.dir") + "/test-output/";
            File reportDir = new File(reportPath);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            // Create report filename with timestamp
            String reportFileName = "DemoBlaze-Test-Report-" +
                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".html";
            String filePath = reportPath + reportFileName;

            // Configure the HTML reporter
            ExtentSparkReporter spark = new ExtentSparkReporter(filePath);

            // Configure view order (dashboard first)
            spark.viewConfigurer()
                    .viewOrder()
                    .as(new ViewName[]{
                            ViewName.DASHBOARD,
                            ViewName.TEST,
                            ViewName.CATEGORY,
                            ViewName.DEVICE,
                            ViewName.AUTHOR
                    }).apply();

            // Set HTML report configuration
            spark.config().setTheme(Theme.DARK); // Dark theme looks more professional
            spark.config().setDocumentTitle("DemoBlaze Test Report");
            spark.config().setReportName("Navigation Tests");
            spark.config().setTimeStampFormat("MMMM dd, yyyy HH:mm:ss");

            // Add custom JS to change logo
            spark.config().setJs("document.getElementsByClassName('logo')[0].innerHTML = " +
                    "'<img src=\"https://www.svgrepo.com/show/303734/selenium-logo.svg\" height=\"45\" width=\"45\">" +
                    "<span style=\"margin-left:10px; font-size:24px; color:#fff;\">DemoBlaze Tests</span>';");

            // Custom CSS to make the report look better
            spark.config().setCss(
                    ".badge-primary { background-color: #7f3f98 !important; } " +
                            ".dashboard-view .card { border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); transition: all 0.3s; } " +
                            ".dashboard-view .card:hover { transform: translateY(-5px); box-shadow: 0 8px 16px rgba(0,0,0,0.3); } " +
                            ".test-content { border-radius: 10px; } " +
                            ".cat-container .card { border-radius: 8px; } " +
                            ".btn { border-radius: 20px; } " +
                            ".node { border-radius: 5px; } " +
                            ".badges { padding: 3px 8px; border-radius: 12px; } " +
                            ".text-pass { color: #3fca6b !important; } " +
                            ".text-fail { color: #ff5252 !important; } " +
                            ".text-skip { color: #ffaf00 !important; } " +
                            ".step-details { border-radius: 5px; padding: 10px; margin-top: 5px; }" +
                            ".test-wrapper { margin-bottom: 15px; background: rgba(0,0,0,0.05); padding: 15px; border-radius: 10px; }"
            );

            // Initialize ExtentReports and attach the HTML reporter
            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Set system info
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Browser", "Chrome");
            extent.setSystemInfo("Environment", "Test");
            extent.setSystemInfo("URL", "https://www.demoblaze.com/");
        }
        return extent;
    }

    /**
     * Create a new test in the report
     */
    public static synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest test = init().createTest(testName, description);
        testMap.put(Thread.currentThread().getId(), test);
        return test;
    }

    /**
     * Get the current test instance
     */
    public static synchronized ExtentTest getTest() {
        return testMap.get(Thread.currentThread().getId());
    }

    /**
     * Set WebDriver instance for screenshots
     */
    public static synchronized void setDriver(WebDriver driver) {
        driverMap.put(Thread.currentThread().getId(), driver);
    }

    /**
     * Get WebDriver instance
     */
    public static synchronized WebDriver getDriver() {
        return driverMap.get(Thread.currentThread().getId());
    }

    /**
     * Log step with numbered format and blue background
     */
    public static synchronized void logStep(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.info(MarkupHelper.createLabel("STEP: " + message, ExtentColor.BLUE));
        }
    }

    /**
     * Log passed condition with green label
     */
    public static synchronized void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.pass(MarkupHelper.createLabel("PASS: " + message, ExtentColor.GREEN));
        }
    }

    /**
     * Log failed condition with red label
     */
    public static synchronized void logFail(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.fail(MarkupHelper.createLabel("FAIL: " + message, ExtentColor.RED));
        }
    }

    /**
     * Log info message
     */
    public static synchronized void logInfo(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.info(message);
        }
    }

    /**
     * Log warning with orange label
     */
    public static synchronized void logWarning(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.warning(MarkupHelper.createLabel("WARNING: " + message, ExtentColor.ORANGE));
        }
    }

    /**
     * Capture screenshot and add to report
     */
    public static synchronized void captureScreenshot(String name) {
        WebDriver driver = getDriver();
        ExtentTest test = getTest();

        if (driver != null && test != null) {
            try {
                String base64Image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                test.info(name, MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
            } catch (Exception e) {
                test.info("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    /**
     * Generate the report
     */
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}