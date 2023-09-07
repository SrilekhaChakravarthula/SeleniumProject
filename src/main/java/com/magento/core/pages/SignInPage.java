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

    @FindBy(xpath = "//*[@id='email-error'][contains(text(),'Please enter a valid email address')]")
    private WebElement invalidUserNameErrorMessage;

    @FindBy(xpath = "//*[contains(text(),'The account sign-in was incorrect')]")
    private WebElement incorrectSignInMessage;

    @FindBy(xpath ="//*[text()='This is a required field.']")
    private WebElement mandatoryFieldsError;


    public SignInPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Sign in Page Loaded");
    }

    public void waitForPageLoad() {
        waitForElementToBeDisplayed(pageTitle);
    }

    /*
      Enter email and password details and signIn.
    */

    public HomePage doSignIn(String userEmail, String userPassword) {
        email.sendKeys(userEmail);
        ExtentReporter.logPass("Entered email : " + userEmail);
        password.sendKeys(userPassword);
        ExtentReporter.logPass("Entered password : " + userPassword);
        signIn.click();
        ExtentReporter.logPass("Clicked on Sign In");
        return new HomePage(driver);
    }

    /*
      Fetch email and password details from the environment utility and signIn.
    */

    public HomePage doSignIn() {
        email.sendKeys(environment.getAppUserEmail());
        ExtentReporter.logPass("Entered email : " + environment.getAppUserEmail());
        password.sendKeys(environment.getAppUserPassword());
        ExtentReporter.logPass("Entered password : " + environment.getAppUserPassword());
        signIn.click();
        ExtentReporter.logPass("Clicked on Sign In");
        return new HomePage(driver);
    }

    /*
        Try to sign in with multiple xssPayloads to inject in the email field to check XSS vulnerability.
     */

    public void doSignInXSS() {
        String[] xssPayloads = {
                "<script > alert(‘XSS Attack !’);</script >",
                "<img src =‘x‘ onerror =’alert(\"XSS Attack !\")’>",
                "<a href =\"javascript:alert(‘XSS Attack !’)\">Click Me</a>"
        };
        for (String payload : xssPayloads) {
            email.clear();
            password.clear();
            email.sendKeys(payload);
            password.sendKeys(environment.getAppUserPassword());
            signIn.click();
            try {
                waitForAlertToBeDisplayed();
                driver.switchTo().alert().accept();
                System.out.println("XSS Vulnerability detected with payload " + payload);
            } catch (Exception e) {
                ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "No alert");
                System.out.println("No XSS Vulnerability detected with payload " + payload);
            }
        }
    }

    /*
        Try to sign in with multiple sqlPayloads to inject in the email field to check SQL Injection vulnerability.
     */

    public void doSignInSQLInjection() {
        String[] sqlPayloads = {
                " ' OR '1'='1",
                " ' OR '1'='1' –",
                " ' UNION SELECT null, username, password FROM users –"
        };
        for (String payload : sqlPayloads) {
            email.clear();
            password.clear();
            email.sendKeys(payload);
            password.sendKeys(environment.getAppUserPassword());
            signIn.click();
            if (driver.getCurrentUrl().equals(environment.getAppUrl())) {
                System.out.println("SQL Injection is successful with payload: " + payload);
            } else {
                ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "login failed");

                System.out.println("Login failed with payload: " + payload);
            }
        }
    }

    public void doSignForNegativeValidations(String userEmail, String userPassword) {
        email.clear();
        password.clear();
        email.sendKeys(userEmail);
        ExtentReporter.logPass("Entered email : " + userEmail);
        password.sendKeys(userPassword);
        ExtentReporter.logPass("Entered password : " + userPassword);
        signIn.click();
        ExtentReporter.logPass("Clicked on Sign In");
    }

    public void validateSignInWithInvalidUserName() {
        invalidUserNameErrorMessage.isDisplayed();
    }

    public void validateSignInWithIncorrectUserNameOrPassword() {
        incorrectSignInMessage.isDisplayed();
    }

    public void validateSignInWithEmptyUserNameOrPassword() {
        mandatoryFieldsError.isDisplayed();
    }
}
