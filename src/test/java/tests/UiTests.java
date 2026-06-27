package tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductsPage;

public class UiTests extends BaseTest {

    @Test
    public void testValidLogin() {
        driver.get("http://localhost:8080");
        LoginPage loginPage = new LoginPage(driver);
        
        // Login with valid credentials
        loginPage.login("standard_user", "secret_sauce");
        
        ProductsPage productsPage = new ProductsPage(driver);
        // Verify page title is 'Products'
        Assert.assertEquals(productsPage.getPageTitleText(), "Products");
    }

    @Test
    public void testInvalidLogin() {
        driver.get("http://localhost:8080");
        LoginPage loginPage = new LoginPage(driver);
        
        // Login with incorrect credentials
        loginPage.login("locked_out_user_or_invalid", "wrong_password");
        
        // Verify error message appears
        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
        Assert.assertTrue(loginPage.getErrorMessageText().contains("Username and password do not match"));
    }

    @Test
    public void testAddItemToCart() {
        driver.get("http://localhost:8080");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        
        ProductsPage productsPage = new ProductsPage(driver);
        // Add item and check the cart count
        productsPage.addArcReactorToCart();
        productsPage.waitForCartBadgeToUpdate("1");
        Assert.assertEquals(productsPage.getCartItemsCount(), "1");
    }

    @Test
    public void testCheckoutFlow() {
        driver.get("http://localhost:8080");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addArcReactorToCart();
        productsPage.waitForCartBadgeToUpdate("1");
        productsPage.clickCartLink();
        
        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        cartPage.fillCheckoutInformation("John", "Doe", "12345");
        cartPage.clickContinue();
        cartPage.clickFinish();
        
        // Verify order confirmation message
        Assert.assertEquals(cartPage.getCompleteHeaderText(), "Thank you for your order!");
    }

    @Test
    public void testLogout() {
        driver.get("http://localhost:8080");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        
        ProductsPage productsPage = new ProductsPage(driver);
        // Verify page header is visible
        Assert.assertEquals(productsPage.getPageTitleText(), "Products");
        
        // Perform logout
        productsPage.logout();
        
        // Verify we are redirected to login page
        Assert.assertTrue(driver.getCurrentUrl().contains("localhost:8080") || driver.getCurrentUrl().contains("127.0.0.1:8080"));
        Assert.assertTrue(loginPage.isErrorMessageDisplayed() == false);
    }

    @Test
    public void testLockedOutUserLogin() {
        driver.get("http://localhost:8080");
        LoginPage loginPage = new LoginPage(driver);
        
        // Login with locked_out_user
        loginPage.login("locked_out_user", "secret_sauce");
        
        // Verify locked out error message from database
        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
        Assert.assertTrue(loginPage.getErrorMessageText().contains("locked out"));
    }

    @Test
    public void testProblemUserGlitches() {
        driver.get("http://localhost:8080");
        LoginPage loginPage = new LoginPage(driver);
        
        // Login with problem_user
        loginPage.login("problem_user", "secret_sauce");
        
        ProductsPage productsPage = new ProductsPage(driver);
        Assert.assertEquals(productsPage.getPageTitleText(), "Products");
        
        // Verify body tag has the glitched-ui class applied by frontend
        String bodyClass = driver.findElement(By.tagName("body")).getAttribute("class");
        Assert.assertTrue(bodyClass.contains("glitched-ui"));
        
        // Logout cleanly
        productsPage.logout();
    }
}
