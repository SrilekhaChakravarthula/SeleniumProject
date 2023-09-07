package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class ReviewAndPaymentsPage extends AbstractPage {

    @FindBy(xpath = "//div[text()='Payment Method']")
    private WebElement paymentMethodTitle;

    @FindBy(id = "billing-address-same-as-shipping-checkmo")
    private WebElement billingAndShippingSameCheckbox;

    @FindBy(xpath = "//div[@class='billing-address-details']")
    private WebElement billingAddress;

    @FindBy(xpath = "//span[text()='Ship To:']/../following-sibling::div")
    private WebElement shippingAddress;

    @FindBy(xpath = "//span[text()='Flat Rate - Fixed']")
    private WebElement flatRateFixed;

    @FindBy(xpath = "//span[@data-th='Cart Subtotal']")
    private WebElement cartSubtotalAmount;

    @FindBy(xpath = "//span[@data-th='Shipping']")
    private WebElement shippingAmount;

    @FindBy(xpath = "//td[@data-th='Order Total']//span")
    private WebElement orderTotalAmount;

    @FindBy(xpath = "//li[@class='product-item']")
    private List<WebElement> totalProducts;

    @FindBy(xpath = "//span[text()='Place Order']")
    private WebElement placeOrderButton;

    @FindBy(xpath = "//span[contains(@data-bind, 'CartSummaryItemsCount')]")
    private WebElement itemsCount;

    @FindBy(xpath = "//span[text()='Thank you for your purchase!']")
    private WebElement thankYouForYourPurchase;

    @FindBy(xpath = "//p[text()='Your order number is: ']/a/strong")
    private WebElement orderNumber;

    @FindBy(xpath = "//p[text()='Your order # is: ']")
    private WebElement orderNumberWithoutSignIn;

    @FindBy(xpath = "//span[text()='Email Address']//following-sibling::span")
    private WebElement emailID;

    @FindBy(xpath = "//*[text()='Create an Account']//parent::a[@class='action primary']")
    private WebElement createAnAccount;


    By productItemName = By.xpath(".//*[@class='product-item-name']");
    By productQty = By.xpath(".//span[contains(@data-bind,'parent.qty')]");
    By productPrice = By.xpath(".//span[contains(@data-bind,'FormattedPrice')]");

    public ReviewAndPaymentsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        waitForElementToBeDisplayed(paymentMethodTitle);
    }

    /*
       Select billing address to be same as the shipping address in Review and Payments page.
     */

    public void selectBillingAddressSameAsShippingAddress() {
        if (!billingAndShippingSameCheckbox.isSelected()) {
            billingAndShippingSameCheckbox.click();
            ExtentReporter.logPass("Selecting billing address same as shipping address");
        }
    }

    /*
       Validate billing address with the address details in the test data
     */

    public void validateBillingAddress(Map<String, Object> data) {
        String addressText = billingAddress.getDomProperty("innerText");
        String[] allData = addressText.split("\\n");
        String expFullName;
        if (data.get("firstName") == null) {
            expFullName = environment.getFirstName() + " " + environment.getLastName();
        } else {
            expFullName = data.get("firstName") + " " + data.get("lastName");
        }
        Assert.assertEquals(allData[0], expFullName, "Full Name didn't match");
        ExtentReporter.logPass("Validated Full Name in Review and Payments page");
        Assert.assertEquals(allData[1], data.get("shippingAddressStreet"), "Street Name didn't match");
        ExtentReporter.logPass("Validated Street Address in Review and Payments page");
        String expCityStateZipName = data.get("shippingAddressCity") + ", " + data.get("shippingAddressState") + " " + data.get("shippingAddressZipCode");
        Assert.assertEquals(allData[2], expCityStateZipName, "CityStateZipName didn't match");
        ExtentReporter.logPass("Validated city,state and zipcode in Review and Payments page");
        Assert.assertEquals(allData[3], data.get("shippingAddressCountry"), "Country didn't match");
        ExtentReporter.logPass("Validated country in Review and Payments page");
        Assert.assertEquals(allData[4], data.get("shippingAddressPhoneNumber"), "Phone Number didn't match");
        ExtentReporter.logPass("Validated phone number in Review and Payments page");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(billingAddress), "Validated all the details in billing address");
    }


    /*
      Validate shipping address with the address details in the test data
    */

    public void validateShippingAddress(Map<String, Object> data) {
        String addressText = shippingAddress.getDomProperty("innerText");
        String[] allData = addressText.split("\\n");
        String expFullName;
        if (data.get("firstName") == null) {
            expFullName = environment.getFirstName() + " " + environment.getLastName();
        } else {
            expFullName = data.get("firstName") + " " + data.get("lastName");
        }
        Assert.assertEquals(allData[0], expFullName, "Full Name didn't match");
        ExtentReporter.logPass("Validated Full Name in Review and Payments page");
        Assert.assertEquals(allData[1], data.get("shippingAddressStreet"), "Street Name didn't match");
        ExtentReporter.logPass("Validated shipping address street in Review and Payments page");
        String expCityStateZipName = data.get("shippingAddressCity") + ", " + data.get("shippingAddressState") + " " + data.get("shippingAddressZipCode");
        Assert.assertEquals(allData[2], expCityStateZipName, "CityStateZipName didn't match");
        ExtentReporter.logPass("Validated  city,state and zip code in Review and Payments page");
        Assert.assertEquals(allData[3], data.get("shippingAddressCountry"), "Country didn't match");
        ExtentReporter.logPass("Validated country in Review and Payments page");
        Assert.assertEquals(allData[4], data.get("shippingAddressPhoneNumber"), "Phone Number didn't match");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(shippingAddress), "Validated all the details in shipping address");
    }


    /*
      Validate fixed shipping method is displayed
    */

    public void validateFixedShippingMethod() {
        Assert.assertTrue(flatRateFixed.isDisplayed(), "Flat Rate - Fixed is not displayed");
        ExtentReporter.logPass("Validated Flat Rate - Fixed is present");
    }

    /*
      Validate Order summary subTotal and orderTotal amounts
    */
    public void validateOrderSummaryTotalAmount() {
        int cartSubTotalValue = Integer.parseInt(formatPrice(cartSubtotalAmount.getText()));
        int shippingValue = Integer.parseInt(formatPrice(shippingAmount.getText()));
        int orderTotalValue = Integer.parseInt(formatPrice(orderTotalAmount.getText()));
        Assert.assertEquals(cartSubTotalValue, ShoppingCartPage.summarySubTotalValue, "Cart Subtotal didn't match");
        ExtentReporter.logPass("Validated subtotal value in summary page : "+cartSubTotalValue);
        Assert.assertEquals(shippingValue, ShoppingCartPage.fixedRateValue, "Shipping Rate didn't match");
        ExtentReporter.logPass("Validated fixed rate value in summary page : "+shippingValue);
        Assert.assertEquals(orderTotalValue, ShoppingCartPage.orderTotal, "Order Total didn't match");
        ExtentReporter.logPass("Validated total amount in order summary");
    }


    /*
      Validate the product count in the Review and Payments page
    */

    public void validateItemsCount() {
        int expQuantity = 0;
        for (Map<String, String> map : ShoppingCartPage.allProducts) {
            expQuantity = expQuantity + Integer.parseInt(map.get("quantity"));
        }
        String count = itemsCount.getText();
        int actQuantity = Integer.parseInt(count);
        Assert.assertEquals(actQuantity, expQuantity, "item Quantity didn't match");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(itemsCount), "Validated items count");
    }

     /*
       Validate products in the cart
     */

    public void validateItemsInCart() {
        totalProducts.forEach(ele -> {
            String productName = ele.findElement(productItemName).getText();
            String quantity = ele.findElement(productQty).getText();
            String price = ele.findElement(productPrice).getText();
            Map<String, String> expectedProductData = ShoppingCartPage.allProducts.stream().filter(map -> map.get("productName").equalsIgnoreCase(productName)).findFirst().get();
            Assert.assertEquals(productName, expectedProductData.get("productName"), "Product Name didn't match");
           ExtentReporter.logPass("Validated the product name in cart");
            Assert.assertEquals(quantity, expectedProductData.get("quantity"), "Quantity didn't match");
            ExtentReporter.logPass("Validated the quantity of item in cart");
            Assert.assertEquals(price, expectedProductData.get("price"), "Price didn't match");
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(ele), "Validated the item " + productName + " in the cart");
        });
    }

    /*
      Click on place order button
    */

    public void placeOrder() {
        placeOrderButton.click();
        ExtentReporter.logPass("Clicked on place order");
    }

    /*
      Validate that the order is placed successfully.
      Thank you message is displayed.
    */

    public void validateOrderIsPlacedSuccessfully() {
        waitForElementToBeDisplayed(thankYouForYourPurchase);
        Assert.assertTrue(thankYouForYourPurchase.isDisplayed(), "Thank you For Your Purchase is not displayed");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Order placed successfully");
    }

    /*
      Validate that the order number is generated successfully and is a hyperlink
    */

    public void validateOrderNumberIsGenerated() {
        Assert.assertTrue(orderNumber.isDisplayed(), "Order id is not displayed");
        String orderNum = orderNumber.getText();
        Assert.assertFalse(orderNum.isEmpty(), "Order id is empty");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(orderNumber), "Order id captured");
    }

    /*
      Validate that the order is placed successfully even when user doesnot signin to the application.
      Validate thank you message along with additional information
    */

    public void validateOrderIsPlacedSuccessfullyWithoutSignIn() {
        waitForElementToBeDisplayed(thankYouForYourPurchase);
        Assert.assertTrue(thankYouForYourPurchase.isDisplayed(), "Thank you For Your Purchase is not displayed");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Order placed successfully");
    }

    /*
      Validate that the order number is generated successfully and is not a hyperlink
    */

    public void validateOrderNumberIsGeneratedWithoutSignIn() {
        String orderNum = orderNumberWithoutSignIn.getText();
        Assert.assertFalse(orderNum.isEmpty(), "Order Number is empty");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(orderNumberWithoutSignIn), "Order id captured");
    }

    /*
     Validate that the email id is displayed on the order success page when user places an order without signing in.
   */

    public void validateEmailIdIsDisplayed(Map<String, Object> testData) {
        Assert.assertEquals(emailID.getText(), testData.get("shippingEmailAddress").toString(), "Email Id didn't match");
    }

    /*
     Validate that Create an Account button is present on the order success page when user places an order without signing in.
   */

    public void validateCreateAccountButtonIsPresent() {
        createAnAccount.isDisplayed();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(createAnAccount), "Create an Account button is present");
    }
}
