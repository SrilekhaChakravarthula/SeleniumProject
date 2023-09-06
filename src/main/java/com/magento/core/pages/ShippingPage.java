package com.magento.core.pages;

import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class ShippingPage extends AbstractPage {

    @FindBy(xpath = "//div[text()='Shipping Address']")
    private WebElement shippingAddressTitle;

    @FindBy(xpath = "//input[@aria-labelledby='label_method_flatrate_flatrate label_carrier_flatrate_flatrate']")
    private WebElement fixedShippingMethod;

    @FindBy(xpath = "//div[@id='checkout-step-shipping']//div[@class='shipping-address-item selected-item']")
    private WebElement shippingAddress;

    @FindBy(xpath = "//span[text()='Items in Cart']/ancestor::div[@data-role='title']")
    private WebElement itemsInCartHeader;

    @FindBy(xpath = "//li[@class='product-item']")
    private List<WebElement> totalProducts;

    @FindBy(xpath = "//span[text()='Next']")
    private WebElement nextButton;

    @FindBy(xpath = "//*[text()='Sign In']//parent::button[@data-trigger='authentication']")
    private WebElement signIn;

    @FindBy(xpath = "//*[@id='customer-email'][contains(@data-bind,'textInput')]")
    private WebElement emailAddress;

    @FindBy(xpath = "//*[text()='You can create an account after checkout.']")
    private WebElement createAccountText;

    @FindBy(name = "firstname")
    private WebElement firstName;

    @FindBy(name = "lastname")
    private WebElement lastName;

    @FindBy(name = "street[0]")
    private WebElement street;

    @FindBy(name = "city")
    private WebElement city;

    @FindBy(name = "region_id")
    private WebElement stateDropdown;

    @FindBy(name = "postcode")
    private WebElement zip;

    @FindBy(name = "country_id")
    private WebElement countryDropdown;

    @FindBy(name = "telephone")
    private WebElement phoneNumber;


    By cartSummaryItemsCount = By.xpath(".//span[contains(@data-bind, 'CartSummaryItemsCount')]");
    By productItemName = By.xpath(".//*[@class='product-item-name']");
    By productQty = By.xpath(".//span[contains(@data-bind,'parent.qty')]");
    By productPrice = By.xpath(".//span[contains(@data-bind,'FormattedPrice')]");

    public ShippingPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        waitForElementToDisappear(loader);
        waitForElementToBeDisplayed(shippingAddressTitle);
    }

    public void validateShippingAddress(Map<String, Object> data) {
        String addressText = shippingAddress.getDomProperty("innerText");
        String[] allData = addressText.split("\\n");
        String expFullName = environment.getFirstName() + " " + environment.getLastName();
        Assert.assertEquals(allData[0], expFullName, "Full Name didn't match");
        Assert.assertEquals(allData[1], data.get("shippingAddressStreet"), "Street Name didn't match");
        String expCityStateZipName = data.get("shippingAddressCity") + ", " + data.get("shippingAddressState") + " " + data.get("shippingAddressZipCode");
        Assert.assertEquals(allData[2], expCityStateZipName, "CityStateZipName didn't match");
        Assert.assertEquals(allData[3], data.get("shippingAddressCountry"), "Country didn't match");
        Assert.assertEquals(allData[4], data.get("shippingAddressPhoneNumber"), "Phone Number didn't match");
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(shippingAddress), "Validated all the details in Shipping Address");
    }

    public void validateFixedShippingMethodIsSelected() {
        Assert.assertTrue(fixedShippingMethod.isSelected(), "Fixed Shipping method is not selected");
        ExtentReporter.logPass("Validated that the fixed rate radio button is turned on");
    }

    public void validateItemsCount() {
        int expQuantity = 0;
        for (Map<String, String> map : ShoppingCartPage.allProducts) {
            expQuantity = expQuantity + Integer.parseInt(map.get("quantity"));
        }
        String itemsCount = itemsInCartHeader.findElement(cartSummaryItemsCount).getText();
        int actQuantity = Integer.parseInt(itemsCount);
        Assert.assertEquals(actQuantity, expQuantity, "item Quantity didn't match");
        ExtentReporter.logPass("Validated the items count : " + itemsCount);
    }

    public void expandOrderSummary() {
        if (!itemsInCartHeader.getAttribute("aria-expanded").equalsIgnoreCase("true")) {
            itemsInCartHeader.click();
        }
        ExtentReporter.logPass("Order summary expanded");
    }

    public void validateOrderSummary() {
        totalProducts.forEach(ele -> {
            String productName = ele.findElement(productItemName).getText();
            String quantity = ele.findElement(productQty).getText();
            String price = ele.findElement(productPrice).getText();
            Map<String, String> expectedProductData = ShoppingCartPage.allProducts.stream().filter(map -> map.get("productName").equalsIgnoreCase(productName)).findFirst().get();
            Assert.assertEquals(productName, expectedProductData.get("productName"), "Product Name didn't match");
            Assert.assertEquals(quantity, expectedProductData.get("quantity"), "Quantity didn't match");
            Assert.assertEquals(price, expectedProductData.get("price"), "Price didn't match");
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(ele), "Validated details of the item :" + productName + " in the Order summary");
        });
    }

    public ReviewAndPaymentsPage ProceedWithNext() {
        nextButton.click();
        waitForElementToDisappear(loader);
        ExtentReporter.logPass("Clicked on next to proceed to the Review and Payments section");
        return new ReviewAndPaymentsPage(driver);
    }

    public void validateSignInLink() {
        signIn.isDisplayed();
        ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Signin link is present");
    }

    public void addShippingDetails(Map<String, Object> data) {
        emailAddress.sendKeys(data.get("shippingEmailAddress").toString());
        createAccountText.isDisplayed();
        firstName.sendKeys(data.get("firstName").toString());
        lastName.sendKeys(data.get("lastName").toString());
        street.sendKeys(data.get("shippingAddressStreet").toString());
        city.sendKeys(data.get("shippingAddressCity").toString());
        selectByVisibleText(stateDropdown, data.get("shippingAddressState").toString());
        zip.sendKeys(data.get("shippingAddressZipCode").toString());
        selectByVisibleText(countryDropdown, data.get("shippingAddressCountry").toString());
        phoneNumber.sendKeys(data.get("shippingAddressPhoneNumber").toString());
        fixedShippingMethod.click();
        ExtentReporter.logPass("Entered all shipping details");
    }
}
