package com.magento.core.utils;

import com.magento.core.constants.CommonFilePaths;
import com.magento.core.pojo.environment.Environment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

public class Hooks {
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private String className;
    public Environment environment = EnvironmentUtil.getEnvironment();

    @BeforeSuite
    public void initReport() {
        ExtentReporter.init();
    }

    @BeforeClass
    public void initWebDriver() {
        String fullClassName = this.getClass().getName();
        className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        System.setProperty("webdriver.chrome.driver", CommonFilePaths.chromeDriverPath);
        //WebDriverManager.chromedriver().proxy("http://wpad/wpad.dat").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("start-maximized");
        WebDriver webDriver = new ChromeDriver(options);
        driver.set(webDriver);
        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        //driver.get().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        //driver.get().manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.get().get(environment.getAppUrl());
    }

    @BeforeMethod
    public void createTest(Method method) {
        String testCaseName = method.getAnnotation(Test.class).testName();
        ExtentReporter.createTest(testCaseName);
        ExtentReporter.assignTestCategory(className);
    }

    @AfterMethod
    public void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.SUCCESS) {
            ExtentReporter.logPass("Test is successful");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            String img = ((TakesScreenshot) Hooks.driver.get()).getScreenshotAs(OutputType.BASE64);
            ExtentReporter.attachScreenshot(img,"Failed Screenshot");
            ExtentReporter.logFail(result);
        } else if (result.getStatus() == ITestResult.SKIP) {
            ExtentReporter.logFail(result);
        }
    }

    @AfterClass
    public void closeWebDriver() {
        driver.get().quit();
        driver.remove();
    }

    @AfterSuite
    public void flushReport() throws IOException {
        ExtentReporter.flushReport();
        File file = new File(System.getProperty("user.dir") + "//target/extentReport.html");
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(file.toURI());
    }
}
