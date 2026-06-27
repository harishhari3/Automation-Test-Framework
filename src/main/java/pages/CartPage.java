package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CartPage {
    private WebDriver driver;

    // Locators for Cart page
    private By checkoutButton = By.id("checkout");

    // Locators for Checkout Step One page (Information)
    private By firstNameField = By.id("first-name");
    private By lastNameField = By.id("last-name");
    private By postalCodeField = By.id("postal-code");
    private By continueButton = By.id("continue");

    // Locators for Checkout Step Two page (Overview)
    private By finishButton = By.id("finish");

    // Locators for Checkout Complete page
    private By completeHeader = By.className("complete-header");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    // Click checkout button in the cart
    public void clickCheckout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
        try {
            element.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
        } catch (org.openqa.selenium.TimeoutException e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
        }
    }

    // Helper method to set input values for React-controlled inputs using the prototype setter
    private void typeUsingJS(By locator, String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
            "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            element, text
        );
    }

    // Fill the checkout information form using JavaScript input injection to trigger React state updates
    public void fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        typeUsingJS(firstNameField, firstName);
        typeUsingJS(lastNameField, lastName);
        typeUsingJS(postalCodeField, postalCode);
    }

    // Click the continue button to go to Overview page
    public void clickContinue() {
        try {
            Thread.sleep(500); // Allow React to process input events and state updates
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        try {
            element.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(finishButton));
        } catch (org.openqa.selenium.TimeoutException e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            wait.until(ExpectedConditions.visibilityOfElementLocated(finishButton));
        }
    }

    // Click finish button on the Overview page
    public void clickFinish() {
        try {
            Thread.sleep(500); // Allow page state to fully settle
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(finishButton));
        try {
            element.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(completeHeader));
        } catch (org.openqa.selenium.TimeoutException e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            wait.until(ExpectedConditions.visibilityOfElementLocated(completeHeader));
        }
    }

    // Get the final confirmation header text (with explicit wait)
    public String getCompleteHeaderText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(completeHeader)).getText();
    }
}
