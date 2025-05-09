
package AmitySoftware;

import java.io.File;
import java.time.Duration;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Assignment {
    public static void main(String[] args) throws Exception {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://www.globalsqa.com/demo-site/draganddrop/");

        WebElement iframe = driver.findElement(By.xpath("//iframe[contains(@data-src,'photo-manager')]"));
        driver.switchTo().frame(iframe);

        WebElement image1 = driver.findElement(By.xpath("//img[@alt='The peaks of High Tatras']"));
        WebElement image2 = driver.findElement(By.xpath("//img[@alt='The chalet at the Green mountain lake']"));
        WebElement trash = driver.findElement(By.id("trash"));

        Actions actions = new Actions(driver);
        actions.dragAndDrop(image1, trash).build().perform();
        actions.dragAndDrop(image2, trash).build().perform();

        driver.switchTo().defaultContent();

        WebElement cheatsheet = driver.findElement(By.xpath("//li[@id='menu-item-6898']//a[normalize-space()='CheatSheets']"));
        cheatsheet.click();

        driver.navigate().back();
        driver.navigate().forward();

        driver.findElement(By.xpath("//a[normalize-space()='SQL Cheat Sheet']")).click();

        String parentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();

        for (String window : allWindows) {
            if (!window.equals(parentWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        WebElement joinImage = driver.findElement(By.xpath("//img[@data-id='6452']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", joinImage);
        
        Thread.sleep(3000);

        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File temp = screenshot.getScreenshotAs(OutputType.FILE);
        File destFile = new File("/Users/rakesh/Desktop/Workspace/Automation/Screenshots/_image.png");
        
        FileUtils.copyFile(temp, destFile);

        driver.quit();
    }
}