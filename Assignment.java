
package AmitySoftware;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Selenium Automation Script for testing web interactions including:
 * - Iframe handling
 * - Drag and drop operations
 * - Browser navigation
 * - Multiple window handling
 * - JavaScript execution
 * - Screenshot capture
 *
 * @author Amity Software
 * @version 2.0
 */
public class Assignment {
    private static final Logger LOGGER = Logger.getLogger(Assignment.class.getName());
    private static final int EXPLICIT_WAIT_TIMEOUT = 15;
    private static final String BASE_URL = "https://www.globalsqa.com/demo-site/draganddrop/";
    private static final String SCREENSHOT_DIR = "Screenshots";

    private static WebDriver driver;
    private static WebDriverWait wait;

    public static void main(String[] args) {
        try {
            LOGGER.info("Starting Selenium automation test...");

            // Initialize WebDriver with proper configuration
            initializeDriver();

            // Execute test steps
            performDragAndDropTest();
            navigateToCheatSheets();
            performNavigationTest();
            handleMultipleWindows();
            captureScreenshot();

            LOGGER.info("Test completed successfully!");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Test execution failed: " + e.getMessage(), e);
            captureScreenshotOnFailure();
        } finally {
            // Ensure browser is closed properly
            quitDriver();
        }
    }

    /**
     * Initializes the WebDriver with Chrome browser and sets up wait configurations
     */
    private static void initializeDriver() {
        try {
            LOGGER.info("Initializing Chrome WebDriver...");
            WebDriverManager.chromedriver().setup();

            // Configure Chrome options for better stability
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

            LOGGER.info("WebDriver initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize WebDriver", e);
            throw new RuntimeException("WebDriver initialization failed", e);
        }
    }

    /**
     * Performs drag and drop test on GlobalSQA demo page
     */
    private static void performDragAndDropTest() {
        try {
            LOGGER.info("Navigating to drag and drop demo page...");
            driver.get(BASE_URL);

            // Wait for and switch to iframe
            LOGGER.info("Switching to iframe...");
            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//iframe[contains(@data-src,'photo-manager')]")));
            driver.switchTo().frame(iframe);

            // Wait for elements to be visible
            WebElement image1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='The peaks of High Tatras']")));
            WebElement image2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='The chalet at the Green mountain lake']")));
            WebElement trash = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("trash")));

            // Perform drag and drop operations
            LOGGER.info("Performing drag and drop operations...");
            Actions actions = new Actions(driver);
            actions.dragAndDrop(image1, trash).build().perform();
            LOGGER.info("Image 1 dragged to trash");

            actions.dragAndDrop(image2, trash).build().perform();
            LOGGER.info("Image 2 dragged to trash");

            // Switch back to main content
            driver.switchTo().defaultContent();
            LOGGER.info("Switched back to main content");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Drag and drop test failed", e);
            throw new RuntimeException("Drag and drop operation failed", e);
        }
    }

    /**
     * Navigates to CheatSheets section
     */
    private static void navigateToCheatSheets() {
        try {
            LOGGER.info("Clicking on CheatSheets menu...");
            WebElement cheatsheet = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[@id='menu-item-6898']//a[normalize-space()='CheatSheets']")));
            cheatsheet.click();
            LOGGER.info("Navigated to CheatSheets page");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to CheatSheets", e);
            throw new RuntimeException("CheatSheets navigation failed", e);
        }
    }

    /**
     * Performs browser back and forward navigation
     */
    private static void performNavigationTest() {
        try {
            LOGGER.info("Testing browser navigation...");
            driver.navigate().back();
            LOGGER.info("Navigated back");

            // Wait for page to load
            wait.until(ExpectedConditions.urlContains("draganddrop"));

            driver.navigate().forward();
            LOGGER.info("Navigated forward");

            // Wait for page to load
            wait.until(ExpectedConditions.urlContains("cheatsheets"));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Navigation test failed", e);
            throw new RuntimeException("Browser navigation failed", e);
        }
    }

    /**
     * Handles multiple browser windows and switches to SQL Cheat Sheet
     */
    private static void handleMultipleWindows() {
        try {
            LOGGER.info("Opening SQL Cheat Sheet in new window...");
            String parentWindow = driver.getWindowHandle();

            WebElement sqlCheatSheet = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='SQL Cheat Sheet']")));
            sqlCheatSheet.click();

            // Wait for new window to open
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));

            Set<String> allWindows = driver.getWindowHandles();
            LOGGER.info("Total windows open: " + allWindows.size());

            // Switch to new window
            for (String window : allWindows) {
                if (!window.equals(parentWindow)) {
                    driver.switchTo().window(window);
                    LOGGER.info("Switched to SQL Cheat Sheet window");
                    break;
                }
            }

            // Wait for page to load completely
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to handle multiple windows", e);
            throw new RuntimeException("Window switching failed", e);
        }
    }

    /**
     * Captures screenshot after scrolling to specific image
     */
    private static void captureScreenshot() {
        try {
            LOGGER.info("Scrolling to target image...");
            WebElement joinImage = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//img[@data-id='6452']")));

            // Scroll to element using JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", joinImage);

            // Wait for scroll to complete and element to be visible
            wait.until(ExpectedConditions.visibilityOf(joinImage));

            LOGGER.info("Taking screenshot...");
            saveScreenshot("test_success");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to capture screenshot", e);
            throw new RuntimeException("Screenshot capture failed", e);
        }
    }

    /**
     * Saves screenshot with dynamic filename and path
     *
     * @param filePrefix prefix for screenshot filename
     */
    private static void saveScreenshot(String filePrefix) {
        try {
            // Create screenshots directory if it doesn't exist
            String screenshotPath = System.getProperty("user.dir") + File.separator + SCREENSHOT_DIR;
            Files.createDirectories(Paths.get(screenshotPath));

            // Generate timestamp for unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = filePrefix + "_" + timestamp + ".png";

            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File tempFile = screenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath + File.separator + fileName);

            FileUtils.copyFile(tempFile, destFile);
            LOGGER.info("Screenshot saved: " + destFile.getAbsolutePath());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save screenshot", e);
        }
    }

    /**
     * Captures screenshot on test failure for debugging
     */
    private static void captureScreenshotOnFailure() {
        try {
            if (driver != null) {
                LOGGER.info("Capturing failure screenshot...");
                saveScreenshot("test_failure");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to capture failure screenshot", e);
        }
    }

    /**
     * Closes the browser and cleans up resources
     */
    private static void quitDriver() {
        if (driver != null) {
            try {
                LOGGER.info("Closing browser...");
                driver.quit();
                LOGGER.info("Browser closed successfully");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error while closing browser", e);
            }
        }
    }
}