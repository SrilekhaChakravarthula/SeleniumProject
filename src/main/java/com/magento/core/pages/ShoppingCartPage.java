package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.*;

public class ShoppingCartPage extends AbstractPage {

    @FindBy(xpath = "//*[@id='shopping-cart-table']/tbody[@class='cart item']//*[@class='product-item-name']/a")
    private List<WebElement> cartItemNames;

    @FindBy(css = "button[title='Update Shopping Cart']")
    private WebElement updateShoppingCart;

    @FindBy(xpath = "//span[@data-th='Subtotal']")
    private WebElement summarySubTotal;

    @FindBy(xpath = "//*[text()='Order Total']/ancestor::tr/td//span")
    private WebElement summaryOrderTotal;

    @FindBy(id = "block-shipping")
    private WebElement summaryBlock;

    @FindBy(id = "block-summary")
    private WebElement summaryBlockExpand;

    @FindBy(name = "country_id")
    private WebElement countryDropdown;

    @FindBy(name = "region_id")
    private WebElement stateDropdown;

    @FindBy(name = "postcode")
    private WebElement postCode;

    @FindBy(id = "s_method_flatrate_flatrate")
    private WebElement fixedRateRadio;

    @FindBy(xpath = "//label[text()='Fixed']/span/span")
    private WebElement fixedRate;

    @FindBy(xpath = "//button/span[text()='Proceed to Checkout']")
    private WebElement proceedToCheckout;

    By itemInCart = By.xpath(".//ancestor::tbody[@class='cart item']");

    By itemPriceInCart = By.xpath(".//*[@class='col price']//*[@class='price']");

    By itemSubTotalPriceInCart = By.xpath(".//*[@class='col subtotal']//*[@class='price']");

    By itemQuantityInCart = By.xpath(".//*[@title='Qty']");


    static int fixedRateValue;
    static int summarySubTotalValue;
    static int orderTotal;

    public static List<Map<String, String>> allProducts;

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Shopping Cart page loaded");
    }

    public void waitForPageLoad() {
        waitForElementToHaveText(pageTitle, "Shopping Cart");
    }

    /*
       Validate products in shopping cart in the Cart page.
     */

    public void validateItemsInShoppingCart(Object testData) {
        List<Map<String, String>> itemDetailList = (List<Map<String, String>>) testData;
        for (Map<String, String> itemDetail : itemDetailList) {
            Iterator<String> itr = itemDetail.keySet().iterator();
            String itemName = itemDetail.get("name");
            String quantity = itemDetail.get("oldQuantity");
            WebElement cartItem = null;
            try {
                cartItem = cartItemNames.stream().filter(el -> el.getText().equalsIgnoreCase(itemName)).findFirst().get();
            } catch (Exception e) {
                Assert.fail("Item : " + itemName + " is not present in the shopping cart page");
            }
            ExtentReporter.logPass("Validated that the item : " + itemName + " is present in the shopping cart page");
            WebElement item = cartItem.findElement(itemInCart);
            String priceOfItemInShoppingCart = item.findElement(itemPriceInCart).getText();
            String priceOfItemInCartSummary = itemDetails.get(itemName);
            Assert.assertEquals(priceOfItemInShoppingCart, priceOfItemInCartSummary, "Price of item in shopping cart page does not match with the price in the cart summary page");
            ExtentReporter.logPass("Price of the item : " + itemName + " is validated in the shopping cart page");
            String actualQuantity = item.findElement(itemQuantityInCart).getDomAttribute("value");
            Assert.assertEquals(actualQuantity, quantity, "Quantity of item in shopping cart page does not match with the quantity in the test data");
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(cartItem), "Validated the item details : " + itemName);
        }
    }

    /*
       Update the quantity , calculate the subTotal amount and verify if it is displayed correctly.
     */

    public void validateAmountOnQuantityUpdate(Object testData) {
        List<Map<String, String>> itemDetailList = (List<Map<String, String>>) testData;
        allProducts = new ArrayList<>();
        for (Map<String, String> itemDetail : itemDetailList) {
            Map<String, String> productData = new HashMap<>();
            Iterator<String> itr = itemDetail.keySet().iterator();
            String itemName = itemDetail.get("name");
            String quantityString = itemDetail.get("newQuantity");
            WebElement cartItem = getCurrentCartItem(itemName);
            WebElement item = cartItem.findElement(itemInCart);
            int priceOfItemInShoppingCart = Integer.parseInt(formatPrice(item.findElement(itemPriceInCart).getText()));
            WebElement quantityInput = item.findElement(itemQuantityInCart);
            quantityInput.clear();
            quantityInput.sendKeys(quantityString);
            ExtentReporter.logPass("Entered new quantity " + quantityString + " for the item " + itemName);
            int quantity = Integer.parseInt(quantityString);
            updateShoppingCart.click();
            ExtentReporter.logPass("Clicked on update shopping cart");
            waitForElementToDisappear(loader);
            cartItem = getCurrentCartItem(itemName);
            item = cartItem.findElement(itemInCart);
            String subTotalText = item.findElement(itemSubTotalPriceInCart).getText();
            int subTotalPrice = Integer.parseInt(formatPrice(subTotalText));
            summarySubTotalValue = summarySubTotalValue + subTotalPrice;
            Assert.assertEquals(subTotalPrice, priceOfItemInShoppingCart * quantity);
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(cartItem), "Validated that the subtotal price is quantity times the price of item.Here for " + itemName + " with quantity " + quantity + " the subtotal price is : " + subTotalPrice);
            productData.put("productName", itemName);
            productData.put("quantity", quantityString);
            productData.put("price", subTotalText);
            allProducts.add(productData);
        }
    }

    /*
       Utility to get current product in the cart
     */

    private WebElement getCurrentCartItem(String itemName) {
        WebElement cartItem = null;
        try {
            cartItem = cartItemNames.stream().filter(el -> el.getText().equalsIgnoreCase(itemName)).findFirst().get();
        } catch (Exception e) {
            Assert.fail("Item : " + itemName + " is not present in the shopping cart page");
        }
        return cartItem;
    }

    /*
       Expand Order summary section.
     */

    public void expandSummarySection() {
        if (!summaryBlockExpand.getAttribute("aria-hidden").equalsIgnoreCase("false")) {
            summaryBlock.click();
        }
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(summaryBlock), "Summary section expanded");
    }

    /*
       Add shipping details in summary.
     */

    public void setShippingDetails(String country, String state, String zipCode) {
        selectByVisibleText(countryDropdown, country);
        ExtentReporter.logPass("Shipping details in Summary : Country selected as " + country);
        selectByVisibleText(stateDropdown, state);
        ExtentReporter.logPass("Shipping details in Summary : State selected as " + state);
        postCode.clear();
        postCode.sendKeys(zipCode);
        ExtentReporter.logPass("Shipping details in Summary : Zip code entered as " + zipCode);
    }

    /*
       Select fixed rate option.
     */

    public void checkFixedRate() {
        fixedRateValue = Integer.parseInt(formatPrice(fixedRate.getText()));
        fixedRateRadio.click();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(fixedRate), "Turned on fixed rate radio button");
        waitForElementToDisappear(loader);
    }

    /*
      Validate summary subTotal amount the summary section.
    */

    public void validateSummarySubTotalAmount() {
        int actSubTotal = Integer.parseInt(formatPrice(summarySubTotal.getText()));
        Assert.assertEquals(actSubTotal, summarySubTotalValue, "Summary Subtotal Amount value didn't match");
        ExtentReporter.logPass("Validated Summary Subtotal amount : " + summarySubTotalValue);
    }

    /*
      Validate summary orderTotal amount the summary section
    */

    public void validateSummaryOrderTotalAmount() {
        int actOrderTotal = Integer.parseInt(formatPrice(summaryOrderTotal.getText()));
        orderTotal = summarySubTotalValue + fixedRateValue;
        Assert.assertEquals(actOrderTotal, orderTotal, "Order total Amount value didn't match");
        ExtentReporter.logPass("Validated Summary Order Total amount : " + orderTotal);
    }

    /*
      Click on proceed to checkout button to navigate to the Shipping page.
    */

    public ShippingPage proceedToCheckout() {
        proceedToCheckout.click();
        ExtentReporter.logPass("Clicked on proceed to checkout");
        return new ShippingPage(driver);
    }

}
