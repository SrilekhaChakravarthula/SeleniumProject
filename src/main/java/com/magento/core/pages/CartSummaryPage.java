package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.Iterator;
import java.util.List;

public class CartSummaryPage extends AbstractPage {
    @FindBy(css = "[title='Proceed to Checkout']")
    private WebElement proceedToCheckOut;

    @FindBy(xpath = "//*[@id='mini-cart']/li")
    private List<WebElement> minicart;

    @FindBy(xpath = "//a[@class='action viewcart']")
    private WebElement viewAndEditCart;

    By productPrice = By.xpath(".//*[@class='product-item-details']//*[@class='product-item-pricing']//*[@class='price']");


    By itemNames = By.xpath(".//*[@class='product-item-name']//a");

    By itemInList = By.xpath(".//ancestor::li");

    public CartSummaryPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Cart Summary dialog loaded");
    }

    public void waitForPageLoad() {
        waitForElementToBeDisplayed(proceedToCheckOut);
    }

    /*
       Validate the products in cart summary dialog using the map created with product details while adding them to the cart
     */

    public void validateItemsInCartSummarySection() {
        int expectedCountOfProducts = itemDetails.size();
        int actualCountOfProducts = minicart.size();
        ExtentReporter.logPass(itemDetails.toString());
        Assert.assertEquals(actualCountOfProducts, expectedCountOfProducts, "Expected number of products in Cart Summary section is " + expectedCountOfProducts + ". But found " + actualCountOfProducts);
        Iterator<String> itr = itemDetails.keySet().iterator();
        while (itr.hasNext()) {
            String itemNameInItemDetails = itr.next();
            String itemPrice = itemDetails.get(itemNameInItemDetails);
            WebElement cartItem = null;
            try {
                cartItem = minicart.stream()
                        .filter(el -> el.findElement(itemNames).getText().trim().equalsIgnoreCase(itemNameInItemDetails))
                        .findFirst().get();
            } catch (Exception e) {
                ExtentReporter.attachScreenshot(getScreenshotAsBase64(cartItem), "Item : " + itemNameInItemDetails + " not found in the cart summary section");
                Assert.fail("Item : " + itemNameInItemDetails + " not found in the cart summary section");
            }
            WebElement element = cartItem.findElement(itemInList);
            String itemPriceInCart = element.findElement(productPrice).getText();
            System.out.println(itemNameInItemDetails + ":" + itemPriceInCart);
            Assert.assertTrue(itemPriceInCart.equalsIgnoreCase(itemPrice));
            ExtentReporter.logPass("Validated that the details of "+itemNameInItemDetails+" in the summary section");
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(cartItem), "Validated cart item : " + itemNameInItemDetails + " and its price : " + itemPriceInCart);
        }
    }

    /*
        Click on View and Edit Cart to navigate to the Shopping Cart page
     */

    public ShoppingCartPage clickOnViewAndEditCart() {
        viewAndEditCart.isDisplayed();
        viewAndEditCart.click();
        return new ShoppingCartPage(driver);
    }

    /*
       Click on proceed to checkout button in the cart summary dialog.
     */

    public ShippingPage proceedToCheckout(){
        proceedToCheckOut.click();
        ExtentReporter.logPass("Clicked on proceed to checkout");
        return new ShippingPage(driver);
    }
}
