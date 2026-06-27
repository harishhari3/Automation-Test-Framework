package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    private static Process serverProcess;

    @BeforeSuite
    public void startServer() {
        try {
            System.out.println("Starting local Node.js server...");
            ProcessBuilder pb = new ProcessBuilder("node", "server.js");
            pb.directory(new java.io.File("."));
            serverProcess = pb.start();
            // Allow server to initialize
            Thread.sleep(1500);
            System.out.println("Local Node.js server started successfully.");
        } catch (Exception e) {
            System.err.println("Failed to start local Node.js server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @AfterSuite
    public void stopServer() {
        if (serverProcess != null) {
            System.out.println("Stopping local Node.js server...");
            serverProcess.destroy();
            System.out.println("Local Node.js server stopped.");
        }
    }

    @BeforeMethod
    public void setUp() {
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        
        options.addArguments("--headless=new"); // Enable headless mode for stable environment run
        
        options.addArguments("--window-size=1920,1080"); // Set viewport size
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);

        // Set implicit wait (window size is already set via ChromeOptions)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser after each test method
        if (driver != null) {
            driver.quit();
        }
    }
}
