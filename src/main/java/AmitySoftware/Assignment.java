
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
    static final int EXPLICIT_WAIT_TIMEOUT = 15;
    static final String BASE_URL = "https://www.globalsqa.com/demo-site/draganddrop/";
    static final String SCREENSHOT_DIR = "Screenshots";

    final WebDriver driver;
    final WebDriverWait wait;

    /**
     * Creates an Assignment instance with the provided WebDriver and WebDriverWait.
     * This constructor enables dependency injection for testability.
     *
     * @param driver the WebDriver instance to use
     * @param wait   the WebDriverWait instance to use
     */
    Assignment(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public static void main(String[] args) {
        WebDriver driver = null;
        try {
            LOGGER.info("Starting Selenium automation test...");

            driver = createDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT));
            Assignment assignment = new Assignment(driver, wait);

            assignment.performDragAndDropTest();
            assignment.navigateToCheatSheets();
            assignment.performNavigationTest();
            assignment.handleMultipleWindows();
            assignment.captureScreenshot();

            LOGGER.info("Test completed successfully!");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Test execution failed: " + e.getMessage(), e);
        } finally {
            quitDriver(driver);
        }
    }

    /**
     * Creates and configures a Chrome WebDriver instance.
     *
     * @return a configured WebDriver instance
     */
    static WebDriver createDriver() {
        try {
            LOGGER.info("Initializing Chrome WebDriver...");
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

            LOGGER.info("WebDriver initialized successfully");
            return driver;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize WebDriver", e);
            throw new RuntimeException("WebDriver initialization failed", e);
        }
    }

    /**
     * Factory method that creates an Actions instance for the current driver.
     * Extracted to a method to allow overriding in tests.
     *
     * @return a new Actions instance bound to this driver
     */
    Actions createActions() {
        return new Actions(driver);
    }

    /**
     * Performs drag and drop test on GlobalSQA demo page.
     */
    void performDragAndDropTest() {
        try {
            LOGGER.info("Navigating to drag and drop demo page...");
            driver.get(BASE_URL);

            LOGGER.info("Switching to iframe...");
            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//iframe[contains(@data-src,'photo-manager')]")));
            driver.switchTo().frame(iframe);

            WebElement image1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='The peaks of High Tatras']")));
            WebElement image2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='The chalet at the Green mountain lake']")));
            WebElement trash = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("trash")));

            LOGGER.info("Performing drag and drop operations...");
            Actions actions = createActions();
            actions.dragAndDrop(image1, trash).build().perform();
            LOGGER.info("Image 1 dragged to trash");

            actions.dragAndDrop(image2, trash).build().perform();
            LOGGER.info("Image 2 dragged to trash");

            driver.switchTo().defaultContent();
            LOGGER.info("Switched back to main content");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Drag and drop test failed", e);
            throw new RuntimeException("Drag and drop operation failed", e);
        }
    }

    /**
     * Navigates to CheatSheets section.
     */
    void navigateToCheatSheets() {
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
     * Performs browser back and forward navigation.
     */
    void performNavigationTest() {
        try {
            LOGGER.info("Testing browser navigation...");
            driver.navigate().back();
            LOGGER.info("Navigated back");

            wait.until(ExpectedConditions.urlContains("draganddrop"));

            driver.navigate().forward();
            LOGGER.info("Navigated forward");

            wait.until(ExpectedConditions.urlContains("cheatsheets"));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Navigation test failed", e);
            throw new RuntimeException("Browser navigation failed", e);
        }
    }

    /**
     * Handles multiple browser windows and switches to SQL Cheat Sheet.
     */
    void handleMultipleWindows() {
        try {
            LOGGER.info("Opening SQL Cheat Sheet in new window...");
            String parentWindow = driver.getWindowHandle();

            WebElement sqlCheatSheet = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='SQL Cheat Sheet']")));
            sqlCheatSheet.click();

            wait.until(ExpectedConditions.numberOfWindowsToBe(2));

            Set<String> allWindows = driver.getWindowHandles();
            LOGGER.info("Total windows open: " + allWindows.size());

            for (String window : allWindows) {
                if (!window.equals(parentWindow)) {
                    driver.switchTo().window(window);
                    LOGGER.info("Switched to SQL Cheat Sheet window");
                    break;
                }
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to handle multiple windows", e);
            throw new RuntimeException("Window switching failed", e);
        }
    }

    /**
     * Captures screenshot after scrolling to specific image.
     */
    void captureScreenshot() {
        try {
            LOGGER.info("Scrolling to target image...");
            WebElement joinImage = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//img[@data-id='6452']")));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", joinImage);

            wait.until(ExpectedConditions.visibilityOf(joinImage));

            LOGGER.info("Taking screenshot...");
            saveScreenshot("test_success");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to capture screenshot", e);
            throw new RuntimeException("Screenshot capture failed", e);
        }
    }

    /**
     * Saves screenshot with dynamic filename into the default screenshots directory.
     *
     * @param filePrefix prefix for the screenshot filename
     */
    void saveScreenshot(String filePrefix) {
        String screenshotPath = System.getProperty("user.dir") + File.separator + SCREENSHOT_DIR;
        saveScreenshot(filePrefix, screenshotPath);
    }

    /**
     * Saves screenshot to the specified directory path.
     *
     * @param filePrefix     prefix for the screenshot filename
     * @param screenshotPath directory path where the screenshot will be saved
     */
    void saveScreenshot(String filePrefix, String screenshotPath) {
        try {
            Files.createDirectories(Paths.get(screenshotPath));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = filePrefix + "_" + timestamp + ".png";

            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File tempFile = screenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath + File.separator + fileName);

            FileUtils.copyFile(tempFile, destFile);
            LOGGER.info("Screenshot saved: " + destFile.getAbsolutePath());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save screenshot", e);
        }
    }

    /**
     * Captures a failure screenshot for debugging when the driver is available.
     */
    void captureScreenshotOnFailure() {
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
     * Closes the browser and cleans up WebDriver resources.
     *
     * @param driver the WebDriver instance to quit; null is safely ignored
     */
    static void quitDriver(WebDriver driver) {
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
