package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;

public class MegaMenuPage extends AbstractPage {
    @FindBy(xpath = "//*[@id='store.menu']//nav/ul/li/a")
    private List<WebElement> megaMenuBar;
    By siblingUl = By.xpath("./following-sibling::ul");
    By listAnchor = By.xpath("./li/a");

    public MegaMenuPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /*
       Navigate to any item in the Mega menu by passing in the menu hierarchy delimited by ':' as a parameter
     */

    public void navigateToItemsInMegaMenu(String menuHierarchyWithDelimiter) {
        String[] itemHierarchy = menuHierarchyWithDelimiter.split(":");
        int levels = itemHierarchy.length;
        WebElement parent = null;
        String menuHierarchy = itemHierarchy[0];
        try {
            parent = megaMenuBar.stream().filter(el -> el.getText().equalsIgnoreCase(itemHierarchy[0])).findFirst().get();
        } catch (Exception e) {
            Assert.fail(menuHierarchy + " not found in the mega menu bar");
        }
        ExtentReporter.logPass(menuHierarchy + " is found in the mega menu bar");
        if (levels == 0) {
            parent.click();
        } else {
            hoverOverElement(parent);
            for (int i = 1; i < levels; i++) {
                menuHierarchy =menuHierarchy+" : "+itemHierarchy[i];
                List<WebElement> children = null;
                try {
                    children = parent.findElement(siblingUl).findElements(listAnchor);
                } catch (Exception e) {
                    Assert.fail(itemHierarchy[i] + " not found in the menu hierarchy " +menuHierarchy);
                }
                ExtentReporter.logPass(itemHierarchy[i] + " found in the menu hierarchy " +menuHierarchy);
                boolean childFound = false;
                for (WebElement el : children) {
                    if (el.getText().equalsIgnoreCase(itemHierarchy[i])) {
                        childFound = true;
                        hoverOverElement(el);
                        parent = el;
                        break;
                    }
                }
                Assert.assertTrue(childFound,itemHierarchy[i] + " not found in the menu hierarchy " + itemHierarchy[i - 1]);
                ExtentReporter.logPass(itemHierarchy[i] + " found in the menu hierarchy " + itemHierarchy[i - 1]);
            }
            parent.click();
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Navigated to " + menuHierarchy);
        }
    }
}
