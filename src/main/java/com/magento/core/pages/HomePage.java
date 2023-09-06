package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class HomePage extends AbstractPage {
    @FindBy(xpath = "//*[@class='panel header']//*[@class='greet welcome']//span")
    private WebElement greetingMessage;

    @FindBy(xpath = "//*[@class='action skip contentarea']//following-sibling::ul//*[@data-action='customer-menu-toggle']")
    private WebElement customerMenu;

    By menuList = By.xpath("./..//following-sibling::div//li/a");

    public HomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Home Page loaded");
    }

    public void waitForPageLoad() {
        waitForElementToBeDisplayed(storeLogo);
        waitForElementToHaveText(greetingMessage, "Welcome, " + environment.getFirstName() + " " + environment.getLastName() + "!");
        ExtentReporter.logPass("Validated greeting message on sign in");
    }

    public SignOutPage signOut() {
        customerMenu.click();
        WebElement signOut = null;
        try {
            signOut = customerMenu.findElements(menuList).stream().filter(el -> el.getText().equalsIgnoreCase("Sign Out")).findFirst().get();
        } catch (Exception e) {
            Assert.fail("Sign out option not present in the customer menu dropdown");
        }
        signOut.click();
        return new SignOutPage(driver);
    }
}
