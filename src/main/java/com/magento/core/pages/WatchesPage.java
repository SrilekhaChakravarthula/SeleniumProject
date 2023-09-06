package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class WatchesPage extends AbstractPage {

    public WatchesPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Watches page loaded");
    }

    public void waitForPageLoad() {
        waitForElementToHaveText(pageTitle, "Watches");
    }
}
