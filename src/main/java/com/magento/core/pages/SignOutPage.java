package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignOutPage extends AbstractPage {
    @FindBy(xpath="//*[text()='You are signed out']")
    private WebElement signOutText;

    @FindBy(xpath="//*[text()='You have signed out and will go to our homepage in 5 seconds']")
    private WebElement signOutMessage;

    public SignOutPage(WebDriver driver) {
        super(driver);
        waitForPageLoad();
        PageFactory.initElements(driver,this);
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(),"Sign out page loaded");
    }
    public void waitForPageLoad() {
        waitForElementToBeDisplayed(storeLogo);
    }

    /*
        Validate text on the Sign Out page.
     */

    public void validateTextInSignOutPage(){
        waitForElementToBeDisplayed(signOutText);
        waitForElementToBeDisplayed(signOutMessage);
    }
}
