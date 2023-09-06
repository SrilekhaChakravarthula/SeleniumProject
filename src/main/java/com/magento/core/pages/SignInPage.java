package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignInPage extends AbstractPage {
    @FindBy(xpath = "//h1//*[text()='Customer Login']")
    private WebElement pageTitle;

    @FindBy(css = "input[title='Email']")
    private WebElement email;

    @FindBy(css = "input[title='Password']")
    private WebElement password;

    @FindBy(css = "button[class='action login primary']")
    private WebElement signIn;

    public SignInPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Sign in Page Loaded");
    }

    public void waitForPageLoad() {
        waitForElementToBeDisplayed(pageTitle);
    }

    public HomePage doSignIn(String userEmail, String userPassword) {
        email.sendKeys(userEmail);
        ExtentReporter.logPass("Entered email : " + userEmail);
        password.sendKeys(userPassword);
        ExtentReporter.logPass("Entered password : " + userPassword);
        signIn.click();
        ExtentReporter.logPass("Clicked on Sign In : " + userPassword);
        return new HomePage(driver);
    }

    public HomePage doSignIn() {
        email.sendKeys(environment.getAppUserEmail());
        ExtentReporter.logPass("Entered email : " + environment.getAppUserEmail());
        password.sendKeys(environment.getAppUserPassword());
        ExtentReporter.logPass("Entered password : " + environment.getAppUserPassword());
        signIn.click();
        ExtentReporter.logPass("Clicked on Sign In");
        return new HomePage(driver);
    }

    public void doSignInXSS() {
        String[] xssPayloads = {
                "<script>alert('XSS attack');</script>",
                "<img src='x' onerror='alert(\"XSS attack\")'>",
                "<a href=\"javascript:alert('XSS attack!')\">Click me</a>"
        };
        for (String payload : xssPayloads) {
            email.clear();
            password.clear();
            email.sendKeys(payload);
            password.sendKeys("johncena@123");
            signIn.click();
            try {
                waitForAlertToBeDisplayed();
                driver.switchTo().alert().accept();
                System.out.println("XSS Vulnerability detected with payload " + payload);
            } catch (Exception e) {
                System.out.println("No XSS Vulnerability detected with payload " + payload);
            }
        }
    }

    public void doSignInSQLInjection() {
        String[] sqlPayloads = {
                " ‘ OR ‘1’=’1",
                " ‘ OR ‘1’=’1′ –",
                " ‘ UNION SELECT null, username, password FROM users –"
        };
        for (String payload : sqlPayloads) {
            email.clear();
            password.clear();
            email.sendKeys("admin" + payload);
            password.sendKeys("johncena@123");
            signIn.click();
            if (driver.getCurrentUrl().equals(environment.getAppUrl())) {
            System.out.println("SQL Injection is successful with payload: " + payload);
        } else {
            System.out.println("Login failed with payload: " + payload);
        }
    }
    }

}
