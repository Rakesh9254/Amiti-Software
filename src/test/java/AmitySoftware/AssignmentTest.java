package AmitySoftware;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for {@link Assignment}.
 *
 * <p>All WebDriver interactions are mocked so that tests run without a real browser.
 * The driver mock is set up to simultaneously implement {@link TakesScreenshot} and
 * {@link JavascriptExecutor} (both are implemented by the real Chrome driver) using
 * Mockito's {@code extraInterfaces} facility.
 */
@ExtendWith(MockitoExtension.class)
class AssignmentTest {

    /** Combined interface that mirrors what the real ChromeDriver implements. */
    interface FullDriver extends WebDriver, TakesScreenshot, JavascriptExecutor {}

    /** A mock that satisfies WebDriver, TakesScreenshot and JavascriptExecutor casts. */
    FullDriver driver;

    @Mock
    WebDriverWait wait;

    @Mock
    WebDriver.TargetLocator targetLocator;

    @Mock
    WebDriver.Navigation navigation;

    @Mock
    WebElement mockElement;

    @Mock
    Actions actions;

    @Mock
    Action compositeAction;

    Assignment assignment;

    @BeforeEach
    void setUp() {
        // Create the driver mock implementing all three interfaces at once.
        driver = mock(FullDriver.class);

        assignment = spy(new Assignment(driver, wait));

        // Delegate Actions creation to the mock so no real browser interaction happens.
        // Lenient because only drag-and-drop tests exercise createActions().
        lenient().doReturn(actions).when(assignment).createActions();
    }

    // -----------------------------------------------------------------------
    // Constructor / field wiring
    // -----------------------------------------------------------------------

    @Test
    void constructor_storesDriverAndWait() {
        Assignment a = new Assignment(driver, wait);
        assertSame(driver, a.driver);
        assertSame(wait, a.wait);
    }

    // -----------------------------------------------------------------------
    // performDragAndDropTest()
    // -----------------------------------------------------------------------

    @Test
    void performDragAndDropTest_happyPath_interactsWithDriver() {
        // Stub wait.until() to return different elements in sequence.
        when(wait.until(any())).thenReturn(mockElement, mockElement, mockElement, mockElement);
        when(driver.switchTo()).thenReturn(targetLocator);
        when(targetLocator.frame(any(WebElement.class))).thenReturn(driver);
        when(targetLocator.defaultContent()).thenReturn(driver);

        // Stub the Actions fluent chain.
        when(actions.dragAndDrop(any(WebElement.class), any(WebElement.class))).thenReturn(actions);
        when(actions.build()).thenReturn(compositeAction);

        assertDoesNotThrow(() -> assignment.performDragAndDropTest());

        verify(driver).get(Assignment.BASE_URL);
        verify(driver.switchTo()).frame(any(WebElement.class));
        verify(actions, times(2)).dragAndDrop(any(WebElement.class), any(WebElement.class));
        verify(compositeAction, times(2)).perform();
        verify(driver.switchTo()).defaultContent();
    }

    @Test
    void performDragAndDropTest_whenDriverGetThrows_wrapsInRuntimeException() {
        doThrow(new RuntimeException("network error")).when(driver).get(anyString());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.performDragAndDropTest());
        assertTrue(ex.getMessage().contains("Drag and drop operation failed"));
    }

    @Test
    void performDragAndDropTest_whenWaitThrows_wrapsInRuntimeException() {
        when(wait.until(any())).thenThrow(new RuntimeException("timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.performDragAndDropTest());
        assertEquals("Drag and drop operation failed", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // navigateToCheatSheets()
    // -----------------------------------------------------------------------

    @Test
    void navigateToCheatSheets_happyPath_clicksElement() {
        when(wait.until(any())).thenReturn(mockElement);

        assertDoesNotThrow(() -> assignment.navigateToCheatSheets());

        verify(mockElement).click();
    }

    @Test
    void navigateToCheatSheets_whenWaitThrows_wrapsInRuntimeException() {
        when(wait.until(any())).thenThrow(new RuntimeException("element not found"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.navigateToCheatSheets());
        assertEquals("CheatSheets navigation failed", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // performNavigationTest()
    // -----------------------------------------------------------------------

    @Test
    void performNavigationTest_happyPath_navigatesBackAndForward() {
        when(driver.navigate()).thenReturn(navigation);
        when(wait.until(any())).thenReturn(true);

        assertDoesNotThrow(() -> assignment.performNavigationTest());

        verify(navigation).back();
        verify(navigation).forward();
        // wait.until() should be called for both URL checks
        verify(wait, times(2)).until(any());
    }

    @Test
    void performNavigationTest_whenNavigateThrows_wrapsInRuntimeException() {
        when(driver.navigate()).thenReturn(navigation);
        doThrow(new RuntimeException("navigate back failed")).when(navigation).back();

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.performNavigationTest());
        assertEquals("Browser navigation failed", ex.getMessage());
    }

    @Test
    void performNavigationTest_whenWaitThrows_wrapsInRuntimeException() {
        when(driver.navigate()).thenReturn(navigation);
        when(wait.until(any())).thenThrow(new RuntimeException("URL wait timed out"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.performNavigationTest());
        assertEquals("Browser navigation failed", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // handleMultipleWindows()
    // -----------------------------------------------------------------------

    @Test
    void handleMultipleWindows_happyPath_switchesToNewWindow() {
        String parentHandle = "parent-window";
        String childHandle  = "child-window";

        when(driver.getWindowHandle()).thenReturn(parentHandle);
        when(wait.until(any())).thenReturn(mockElement, true, mockElement);
        when(driver.getWindowHandles()).thenReturn(new HashSet<>(Arrays.asList(parentHandle, childHandle)));
        when(driver.switchTo()).thenReturn(targetLocator);
        when(targetLocator.window(childHandle)).thenReturn(driver);

        assertDoesNotThrow(() -> assignment.handleMultipleWindows());

        verify(mockElement).click();
        verify(targetLocator).window(childHandle);
    }

    @Test
    void handleMultipleWindows_whenOnlyOneWindow_doesNotSwitch() {
        String parentHandle = "only-window";
        when(driver.getWindowHandle()).thenReturn(parentHandle);
        when(wait.until(any())).thenReturn(mockElement, true, mockElement);
        when(driver.getWindowHandles()).thenReturn(Set.of(parentHandle));

        // switchTo() should not be called if there is no other window.
        assertDoesNotThrow(() -> assignment.handleMultipleWindows());
        verify(driver, never()).switchTo();
    }

    @Test
    void handleMultipleWindows_whenGetWindowHandleThrows_wrapsInRuntimeException() {
        when(driver.getWindowHandle()).thenThrow(new RuntimeException("no window"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.handleMultipleWindows());
        assertEquals("Window switching failed", ex.getMessage());
    }

    @Test
    void handleMultipleWindows_whenWaitThrows_wrapsInRuntimeException() {
        when(driver.getWindowHandle()).thenReturn("parent");
        when(wait.until(any())).thenThrow(new RuntimeException("click failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.handleMultipleWindows());
        assertEquals("Window switching failed", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // captureScreenshot()
    // -----------------------------------------------------------------------

    @Test
    void captureScreenshot_happyPath_scrollsAndDelegatestoSaveScreenshot() throws IOException {
        when(wait.until(any())).thenReturn(mockElement);
        when(((JavascriptExecutor) driver).executeScript(anyString(), any())).thenReturn(null);

        // Prevent the real saveScreenshot(prefix) from running (it needs a real driver).
        doNothing().when(assignment).saveScreenshot(anyString());

        assertDoesNotThrow(() -> assignment.captureScreenshot());

        verify(((JavascriptExecutor) driver)).executeScript(
            contains("scrollIntoView"), eq(mockElement));
        verify(assignment).saveScreenshot("test_success");
    }

    @Test
    void captureScreenshot_whenWaitThrows_wrapsInRuntimeException() {
        when(wait.until(any())).thenThrow(new RuntimeException("element not found"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.captureScreenshot());
        assertEquals("Screenshot capture failed", ex.getMessage());
    }

    @Test
    void captureScreenshot_whenJsExecutorThrows_wrapsInRuntimeException() {
        when(wait.until(any())).thenReturn(mockElement);
        when(((JavascriptExecutor) driver).executeScript(anyString(), any()))
            .thenThrow(new RuntimeException("JS error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> assignment.captureScreenshot());
        assertEquals("Screenshot capture failed", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // saveScreenshot(prefix, path)
    // -----------------------------------------------------------------------

    @Test
    void saveScreenshot_withPath_createsDirectoryAndCopiesFile(@TempDir Path tempDir) throws IOException {
        // Create a real temp source file to simulate the driver screenshot.
        File fakeScreenshot = tempDir.resolve("source.png").toFile();
        Files.write(fakeScreenshot.toPath(), new byte[]{0x01, 0x02});

        when(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)).thenReturn(fakeScreenshot);

        String destDir = tempDir.resolve("Screenshots").toString();
        assignment.saveScreenshot("test_prefix", destDir);

        // The destination directory must have been created.
        assertTrue(Files.isDirectory(Paths.get(destDir)));

        // At least one file starting with "test_prefix_" should exist.
        File[] saved = new File(destDir).listFiles(
            f -> f.getName().startsWith("test_prefix_") && f.getName().endsWith(".png"));
        assertNotNull(saved);
        assertEquals(1, saved.length);
    }

    @Test
    void saveScreenshot_whenGetScreenshotThrows_logsAndDoesNotRethrow(@TempDir Path tempDir) {
        when(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE))
            .thenThrow(new RuntimeException("screenshot unavailable"));

        // Should swallow the exception (non-IO) without rethrowing.
        assertDoesNotThrow(() ->
            assignment.saveScreenshot("fail_prefix", tempDir.toString()));
    }

    @Test
    void saveScreenshot_whenIOExceptionOccurs_logsAndDoesNotRethrow(@TempDir Path tempDir) throws IOException {
        // Return a file that does not exist to trigger an IOException in FileUtils.copyFile.
        File nonExistentFile = tempDir.resolve("does_not_exist.png").toFile();
        when(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)).thenReturn(nonExistentFile);

        String destDir = tempDir.resolve("Screenshots").toString();

        // IOException from copyFile must be caught internally and not rethrown.
        assertDoesNotThrow(() -> assignment.saveScreenshot("io_prefix", destDir));
    }

    @Test
    void saveScreenshot_defaultOverload_usesUserDirAsBase(@TempDir Path tempDir) throws IOException {
        // The no-path overload calls saveScreenshot(prefix, userDir + SCREENSHOT_DIR).
        // We just verify the delegation happens without throwing when the driver is mocked.
        File fakeScreenshot = tempDir.resolve("src.png").toFile();
        Files.write(fakeScreenshot.toPath(), new byte[]{0x03});
        when(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)).thenReturn(fakeScreenshot);

        // Override user.dir temporarily to the temp dir so the file lands somewhere writable.
        String original = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());
            assertDoesNotThrow(() -> assignment.saveScreenshot("default_path_test"));
        } finally {
            System.setProperty("user.dir", original);
        }
    }

    // -----------------------------------------------------------------------
    // captureScreenshotOnFailure()
    // -----------------------------------------------------------------------

    @Test
    void captureScreenshotOnFailure_withDriver_callsSaveScreenshot() {
        doNothing().when(assignment).saveScreenshot(anyString());

        assignment.captureScreenshotOnFailure();

        verify(assignment).saveScreenshot("test_failure");
    }

    @Test
    void captureScreenshotOnFailure_whenSaveThrows_doesNotPropagate() {
        doThrow(new RuntimeException("disk full")).when(assignment).saveScreenshot(anyString());

        // The method must swallow all exceptions from saveScreenshot.
        assertDoesNotThrow(() -> assignment.captureScreenshotOnFailure());
    }

    @Test
    void captureScreenshotOnFailure_withNullDriver_doesNotCallSave() {
        // Build an Assignment with a null driver to exercise the null guard.
        Assignment nullDriverAssignment = spy(new Assignment(null, wait));

        nullDriverAssignment.captureScreenshotOnFailure();

        verify(nullDriverAssignment, never()).saveScreenshot(anyString());
    }

    // -----------------------------------------------------------------------
    // quitDriver(WebDriver)
    // -----------------------------------------------------------------------

    @Test
    void quitDriver_withNullDriver_doesNotThrow() {
        assertDoesNotThrow(() -> Assignment.quitDriver(null));
    }

    @Test
    void quitDriver_withDriver_callsQuit() {
        Assignment.quitDriver(driver);

        verify(driver).quit();
    }

    @Test
    void quitDriver_whenQuitThrows_doesNotPropagate() {
        doThrow(new RuntimeException("browser already closed")).when(driver).quit();

        // Must swallow the exception.
        assertDoesNotThrow(() -> Assignment.quitDriver(driver));
    }

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    @Test
    void constants_haveExpectedValues() {
        assertEquals(15, Assignment.EXPLICIT_WAIT_TIMEOUT);
        assertEquals("https://www.globalsqa.com/demo-site/draganddrop/", Assignment.BASE_URL);
        assertEquals("Screenshots", Assignment.SCREENSHOT_DIR);
    }

    // -----------------------------------------------------------------------
    // createActions()
    // -----------------------------------------------------------------------

    @Test
    void createActions_returnsActionsInstance() {
        // Use the real assignment (not the spy) so createActions() is not mocked.
        Assignment real = new Assignment(driver, wait);
        Actions result = real.createActions();

        assertNotNull(result);
        assertInstanceOf(Actions.class, result);
    }
}
