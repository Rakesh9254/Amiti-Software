# Selenium Automation Script: Drag and Drop, Navigation, and Screenshot Capture

This Java program is an automation script using **Selenium WebDriver** that demonstrates interaction with web elements such as **iframes**, **drag-and-drop**, **browser navigation**, **multiple windows**, **JavaScript scrolling**, and **screenshot capturing**.

## 🔧 Tools & Technologies Used
- Java
- Selenium WebDriver
- WebDriverManager (for managing browser drivers)
- Apache Commons IO (for file handling)
- Chrome browser

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
    - Take a screenshot and save it to:
      ```
      /Users/rakesh/Desktop/Workspace/Automation/Screenshots/_image.png
      ```

11. **Quit the browser**
    - Cleanly close the browser at the end.

## 🖼️ Screenshot Location
- The screenshot of the target image section is saved locally in your specified path.

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
- The script automatically closes all browser windows using `driver.quit()`.

---

✅ This script is ideal for learning and demonstrating real-world scenarios in **web automation testing**.
