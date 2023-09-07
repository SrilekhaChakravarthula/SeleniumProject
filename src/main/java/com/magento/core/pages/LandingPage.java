package com.magento.core.pages;


import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;

public class LandingPage extends AbstractPage {
    @FindBy(xpath = "//div[@class='panel header']//a[contains(text(),'Sign In')]")
    private WebElement signIn;

    @FindBy(xpath = "//div[@class='panel header']//a[contains(text(),'Create an Account')]")
    private WebElement createAccount;

    @FindBy(xpath = "//*[@class='action skip contentarea']//following-sibling::ul/li")
    private List<WebElement> panelItems;

    public LandingPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Landing Page Loaded");
    }

    public LandingPage(WebDriver driver, String url) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Landing Page Loaded");
        driver.get(url);
    }

    public void waitForPageLoad() {
        waitForElementToBeDisplayed(storeLogo);
    }

    /*
       Click on Sign In link in the landing page panel header.
     */

    public SignInPage navigateToSignInPage() {
        signIn.click();
        ExtentReporter.logPass("Navigated to Sign in page");
        return new SignInPage(driver);
    }

    /*
       Click on Create an Account link in the landing page panel header.
     */

    public CreateAccountPage navigateToCreateAccountPage() {
        createAccount.click();
        ExtentReporter.logPass("Navigated to Create Account page");
        return new CreateAccountPage(driver);
    }

    /*
       Validate that the landing page panel header has 3 links.
     */

    public void validatePanelMenuHeader(){
        Assert.assertEquals(panelItems.size(), 3, "Expected number of items in the panel header is 3. But found : " + panelItems.size());
        ExtentReporter.logPass("Validated panel header details of Landing page");
    }
}
