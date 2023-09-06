package com.magento.core.pages;

import com.magento.core.pojo.environment.Environment;
import com.magento.core.utils.EnvironmentUtil;
import com.magento.core.utils.ExtentReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractPage {
    public WebDriver driver;
    Environment environment = EnvironmentUtil.getEnvironment();
    public static int totalItemPrice;
    public static Map<String, String> itemDetails;

    @FindBy(css = "[aria-label='store logo']")
    public WebElement storeLogo;

    @FindBy(xpath = "//*[@class='page-title']/span")
    public WebElement pageTitle;

    @FindBy(xpath = "//*[@class='filter-options']//*[@class='filter-options-title']")
    private List<WebElement> filterCategories;

    @FindBy(css = ".filter-current-subtitle")
    private WebElement filterCurrentSubtitle;

    @FindBy(css = ".filter-label")
    private WebElement appliedFilterCategory;

    @FindBy(css = ".filter-value")
    private WebElement appliedFilterValue;

    @FindBy(xpath = "//*[contains(@class,'product-items')]/li//*[contains(@class,'product-item-name')]/a")
    private List<WebElement> actualItemNames;

    @FindBy(css = ".action.showcart .counter.qty .counter-number")
    private WebElement cartCount;

    @FindBy(css = "[class='action showcart']")
    private WebElement cartIcon;

    @FindBy(xpath = "//*[@role='alert'][@class='messages']")
    private WebElement alertOnAddingToCart;

    @FindBy(css = ".loader")
    protected WebElement loader;

    By filterValueText = By.xpath("./following-sibling::div//a");
    By productCard = By.xpath(".//ancestor::li");

    By price = By.xpath(".//span[@class='price']");

    By addToCart = By.xpath(".//button[@title='Add to Cart']");

    By added = By.xpath(".//button[@title='Added']");

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void applyFilters(Object testData) {
        List<Map<String, String>> filters = (List<Map<String, String>>) testData;
        filters.stream().forEach(filter -> {
            String filterName = filter.get("filterName");
            String filterValue = filter.get("filterValue");
            WebElement filterNameElement = null;
            WebElement filterValueElement = null;
            try {
                filterNameElement = filterCategories.stream().filter(el -> el.getText().trim().equalsIgnoreCase(filterName)).findFirst().get();
            } catch (Exception e) {
                Assert.fail("Filter category : " + filterName + " is not found");
            }
            filterNameElement.click();
            try {
                filterValueElement = filterNameElement.findElements(filterValueText).stream().filter(el -> el.getText().trim().contains(filterValue)).findFirst().get();
            } catch (Exception e) {
                Assert.fail("Filter value : " + filterValue + " in the category : " + filterName + " is not found");
            }
            filterValueElement.click();
            ExtentReporter.logPass("Selected filter : " + filterValue + " under filter category : " + filterName);
            waitForElementToHaveText(filterCurrentSubtitle, "Now Shopping by");
            waitForElementToHaveText(appliedFilterCategory, filterName);
            waitForElementToHaveText(appliedFilterValue, filterValue);
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(), "Applied filter-" + filterName + ":" + filterValue);
        });
    }

    public void addProductToCart(Object testData) {
        List<Map<String, String>> items = (List<Map<String, String>>) testData;
        int noOfProductsExpected = items.size();
        int noOfProductsFound = actualItemNames.size();
        Assert.assertTrue(noOfProductsExpected <= noOfProductsFound, "Expected " + noOfProductsExpected + " products to be present on the page but found only :" + noOfProductsFound);
        itemDetails = new HashMap<String, String>();
        items.stream().forEach(el -> {
            String productName = el.get("name");
            WebElement item = null;
            try {
                item = actualItemNames.stream().filter(e -> e.getText().equalsIgnoreCase(productName)).findFirst().get();
            } catch (Exception e) {
                Assert.fail("Item : " + productName + " not found in the page");
            }
            WebElement currentProductCard = item.findElement(productCard);
            String totalItemPriceString = currentProductCard.findElement(price).getText();
            itemDetails.put(productName, totalItemPriceString);
            int currentItemPrice = Integer.parseInt(totalItemPriceString.split("\\$")[1].replaceAll(".00", ""));
            totalItemPrice = totalItemPrice + currentItemPrice;
            hoverOverElement(item.findElement(productCard));
            moveToElementAndClick(currentProductCard.findElement(addToCart));
            ExtentReporter.logPass("Clicked on add to cart button on " + productName + " card");
            waitForElementToBeDisplayed(currentProductCard.findElement(added));
            waitForElementToHaveText(alertOnAddingToCart, "You added " + productName);
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(alertOnAddingToCart), "Added product : " + productName + " to the cart");
        });
    }

    public void validateCartCount(Object testData) {
        List<Map<String, String>> items = (List<Map<String, String>>) testData;
        int expectedCount = items.size();
        int actualCount = Integer.parseInt(cartCount.getText());
        boolean countMatch = actualCount == expectedCount ? true : false;
        if (countMatch) {
            ExtentReporter.attachScreenshot(getScreenshotAsBase64(cartCount), "Cart count matched");
        } else {
            Assert.fail("Expected cart count after adding the products is : " + expectedCount + ". But found " + actualCount);
        }
    }

    public CartSummaryPage clickOnCartIcon() {
        cartIcon.click();
        return new CartSummaryPage(driver);
    }

    public void scrollAndClick(WebElement element) {
        Actions actions = new Actions(driver);
        actions.scrollToElement(element).click().build().perform();
    }

    public void moveToElementAndClick(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).click().build().perform();
    }

    public void moveToElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).build().perform();
    }

    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    public void hoverOverElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).build().perform();
    }

    public void selectByVisibleText(WebElement element, String text) {
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    public void waitForElementToBeDisplayed(WebElement element) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementToDisappear(WebElement element) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void waitForElementToHaveText(WebElement element, String text) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        wait.until(driver -> element.getText().contains(text));
    }

    public String getScreenshotAsBase64() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    public String getScreenshotAsBase64(WebElement element) {
        moveToElement(element);
        return element.getScreenshotAs(OutputType.BASE64);
    }

    public String formatPrice(String price) {
        return price.split("\\$")[1].replaceAll(".00", "");
    }

    public void waitForLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    public void waitForAlertToBeDisplayed(){
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.alertIsPresent());
    }
}

