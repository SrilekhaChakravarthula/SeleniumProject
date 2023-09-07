package com.magento.scripts;

import com.magento.core.constants.CommonFilePaths;
import com.magento.core.pages.*;
import com.magento.core.utils.Hooks;
import com.magento.core.utils.JSONUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class PlaceOrderWithoutSignInTest extends Hooks {
    Map<String, Object> data;
    MegaMenuPage megaMenuPage;
    WatchesPage watchesPage;
    ShippingPage shippingPage;
    CartSummaryPage cartSummaryPage;
    ReviewAndPaymentsPage reviewAndPaymentsPage;

    @Test(testName = "Navigate to watches section")
    public void navigateToMenuSection() {
        data = JSONUtils.convertJSONToMap(CommonFilePaths.testDataFilePath, "PlaceOrderWithoutSignIn");
        LandingPage landingPage = new LandingPage(driver.get());
        landingPage.waitForPageLoad();
        megaMenuPage = new MegaMenuPage(driver.get());
        megaMenuPage.navigateToItemsInMegaMenu(data.get("navigateMenuHierarchy").toString());
        watchesPage = new WatchesPage(driver.get());
    }

    @Test(testName = "Add watch to cart and validate cart count", dependsOnMethods = {"navigateToMenuSection"})
    public void validateCartCountByAddingWatchToCart() {
        watchesPage.addProductToCart(data.get("itemsToBeAdded"));
        watchesPage.validateCartCount(data.get("itemsToBeAdded"));
    }

    @Test(testName = "Proceed to Checkout and enter the shipping details", dependsOnMethods = {"validateCartCountByAddingWatchToCart"})
    public void validateShippingAndOrderDetails() {
        cartSummaryPage = watchesPage.clickOnCartIcon();
        cartSummaryPage.validateItemsInCartSummarySection();
        shippingPage = cartSummaryPage.proceedToCheckout();
        shippingPage.validateSignInLink();
        shippingPage.addShippingDetails(data);
    }

    @Test(testName = "Proceed to Review and Payments and validate Order Summary,Shipping and Billing Addresses", dependsOnMethods = {"validateShippingAndOrderDetails"})
    public void validateOrderSummaryAndBillingDetails() {
        reviewAndPaymentsPage = shippingPage.ProceedWithNext();
        reviewAndPaymentsPage.selectBillingAddressSameAsShippingAddress();
        reviewAndPaymentsPage.validateBillingAddress(data);
        reviewAndPaymentsPage.validateShippingAddress(data);
        reviewAndPaymentsPage.validateFixedShippingMethod();
    }

    @Test(testName = "Place the Order and validate order number is generated successfully and is not a hyperlink", dependsOnMethods = {"validateOrderSummaryAndBillingDetails"})
    public void validateOrderIsPlacedSuccessfully() {
        reviewAndPaymentsPage.placeOrder();
        reviewAndPaymentsPage.validateOrderIsPlacedSuccessfullyWithoutSignIn();
        reviewAndPaymentsPage.validateOrderNumberIsGeneratedWithoutSignIn();
        reviewAndPaymentsPage.validateEmailIdIsDisplayed(data);
        reviewAndPaymentsPage.validateCreateAccountButtonIsPresent();
    }

}
