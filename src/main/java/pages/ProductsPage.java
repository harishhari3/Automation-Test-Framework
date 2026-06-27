package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ProductsPage {
    private WebDriver driver;

    // Locators
    private By pageTitle = By.className("title");
    private By addArcReactorButton = By.id("add-to-cart-iron-man-arc-reactor");
    private By cartBadge = By.className("shopping_cart_badge");
    private By cartLink = By.className("shopping_cart_link");
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
    }

    // Get the page title to verify we logged in successfully (with explicit wait)
    public String getPageTitleText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText();
    }

    // Add Iron Man Arc Reactor to the cart with retry click logic for React state updates
    public void addArcReactorToCart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By removeButton = By.cssSelector("#add-to-cart-iron-man-arc-reactor.added");
        
        long endTime = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < endTime) {
            try {
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addArcReactorButton));
                btn.click();
            } catch (Exception e) {
                // Ignore if clicked or stale
            }
            
            try {
                if (driver.findElements(removeButton).size() > 0) {
                    return; // Successfully clicked and updated!
                }
            } catch (Exception e) {
                // Ignore
            }
            
            try {
                Thread.sleep(250);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Final fallback to JS click if standard fails
        try {
            WebElement btn = driver.findElement(addArcReactorButton);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            // Ignore
        }
    }

    // Get number of items in the cart badge
    public String getCartItemsCount() {
        try {
            return driver.findElement(cartBadge).getText();
        } catch (Exception e) {
            return "0"; // If badge is not present, cart is empty
        }
    }

    // Wait for the cart badge text to be updated to a specific count
    public void waitForCartBadgeToUpdate(String expectedCount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(cartBadge, expectedCount));
    }

    // Click the shopping cart link to open the cart page
    public void clickCartLink() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(cartLink));
        try {
            element.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#cart-drawer:not(.hidden)")));
        } catch (org.openqa.selenium.TimeoutException e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#cart-drawer:not(.hidden)")));
        }
    }

    // Click the burger menu button and then click logout
    public void logout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuButton));
        try {
            menu.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
        }
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#sidebar:not(.hidden)")));
        } catch (org.openqa.selenium.TimeoutException e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#sidebar:not(.hidden)")));
        }
        
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        try {
            logout.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", logout);
        }
    }
}
