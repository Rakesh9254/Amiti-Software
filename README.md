# Selenium Automation Script: Drag and Drop, Navigation, and Screenshot Capture

This Java program is an automation script using **Selenium WebDriver** that demonstrates interaction with web elements such as **iframes**, **drag-and-drop**, **browser navigation**, **multiple windows**, **JavaScript scrolling**, and **screenshot capturing**.

## 🔧 Tools & Technologies Used
- Java
- Selenium WebDriver
- WebDriverManager (for managing browser drivers)
- Apache Commons IO (for file handling)
- Chrome browser
- Java Logging API (for comprehensive logging)

## ✨ Version 2.0 Improvements

### Code Quality & Best Practices
- **Modular Design**: Refactored code into logical methods for better readability and maintainability
- **Comprehensive JavaDoc**: Added detailed documentation for all methods and classes
- **Logging System**: Integrated Java Logger for tracking test execution and debugging
- **Constants Management**: Used constants for URLs, timeouts, and paths

### Error Handling & Reliability
- **Try-Catch-Finally Blocks**: Proper exception handling throughout the code
- **Failure Screenshots**: Automatic screenshot capture on test failures
- **Resource Cleanup**: Guaranteed browser closure in finally block
- **Explicit Waits**: Replaced Thread.sleep with WebDriverWait for better synchronization

### Dynamic Configuration
- **Dynamic Screenshot Path**: Screenshots saved in project directory with timestamps
- **Timestamp-based Filenames**: Unique filenames prevent overwrites (e.g., `test_success_20231115_143022.png`)
- **Auto-directory Creation**: Screenshots folder created automatically if not present
- **Cross-platform Compatibility**: Uses system-independent file paths

### Enhanced Selenium Features
- **ChromeOptions Configuration**: Better browser stability with optimized settings
- **Explicit Waits**: Improved element detection with ExpectedConditions
- **Page Load Timeout**: Added timeout for page loading operations
- **Smooth Scrolling**: Enhanced JavaScript scroll behavior
- **Window Handling**: Robust multi-window switching with proper waits

## 📌 Features Covered

1. **Open GlobalSQA Drag & Drop Demo Page**
   - Navigate to: [https://www.globalsqa.com/demo-site/draganddrop/](https://www.globalsqa.com/demo-site/draganddrop/)

2. **Switch to iframe**
   - Locate and switch to an iframe containing draggable images.

3. **Perform Drag-and-Drop**
   - Drag two images into the trash drop zone using the `Actions` class.

4. **Switch back to the main page**
   - Return to the main page from the iframe.

5. **Click on “CheatSheets” menu**
   - Navigate to the “CheatSheets” section.

6. **Perform Back and Forward Navigation**
   - Use browser navigation to go back and then forward.

7. **Open "SQL Cheat Sheet"**
   - Click on the "SQL Cheat Sheet" link that opens in a new window.

8. **Switch to the new browser window**
   - Handle multiple windows and switch control to the newly opened tab.

9. **Scroll to image using JavaScript**
   - Scroll down to a specific image using JavaScriptExecutor.

10. **Capture Screenshot**
    - Take a screenshot and save it dynamically to:
      ```
      <project-directory>/Screenshots/test_success_<timestamp>.png
      ```

11. **Quit the browser**
    - Cleanly close the browser at the end with proper resource cleanup.

## 🖼️ Screenshot Location
- Screenshots are saved in the `Screenshots/` folder within your project directory
- Filenames include timestamps for uniqueness (e.g., `test_success_20231115_143022.png`)
- Failure screenshots are automatically captured as `test_failure_<timestamp>.png` when tests fail
- The folder is created automatically if it doesn't exist

## 📁 Project Structure


## 🏁 How to Run
Make sure you have the following setup:
1. Java installed and configured
2. Maven or manually add dependencies:
   - Selenium Java
   - WebDriverManager
   - Apache Commons IO

Then, run the `Assignment.java` file from your IDE or terminal.

## 🧹 Cleanup
- The script automatically closes all browser windows using `driver.quit()` in a finally block
- Guaranteed cleanup even if exceptions occur during test execution

## 📊 What's Different from Version 1.0?

| Feature | Version 1.0 | Version 2.0 |
|---------|-------------|-------------|
| **Code Structure** | Single main method | Modular methods with clear separation of concerns |
| **Error Handling** | `throws Exception` only | Comprehensive try-catch-finally blocks |
| **Waits** | Implicit waits + Thread.sleep | Explicit waits with ExpectedConditions |
| **Logging** | No logging | Java Logger with INFO, WARNING, SEVERE levels |
| **Screenshots** | Hardcoded path | Dynamic project-relative path with timestamps |
| **Documentation** | Minimal comments | Full JavaDoc documentation |
| **Failure Handling** | No failure capture | Automatic failure screenshot capture |
| **Browser Options** | Default settings | Optimized ChromeOptions |
| **Resource Cleanup** | Basic quit() | Guaranteed cleanup in finally block |

## 🎯 Key Takeaways

This improved version demonstrates:
- **Production-ready code** with proper error handling and logging
- **Maintainability** through modular design and clear documentation
- **Reliability** with explicit waits and failure recovery
- **Portability** with cross-platform compatible paths
- **Best practices** following Selenium and Java standards

---

✅ This script is ideal for learning and demonstrating real-world scenarios in **web automation testing**.
