package com.magento.scripts;

import com.magento.core.constants.CommonFilePaths;
import com.magento.core.pages.*;
import com.magento.core.utils.Hooks;
import com.magento.core.utils.JSONUtils;
import org.testng.annotations.Test;

import java.util.Map;

public class PlaceOrderWithSignInTest extends Hooks {
    Map<String, Object> data;
    SignInPage signInPage;
    MegaMenuPage megaMenuPage;
    WatchesPage watchesPage;
    CartSummaryPage cartSummaryPage;
    ShoppingCartPage shoppingCartPage;
    ShippingPage shippingPage;
    ReviewAndPaymentsPage reviewAndPaymentsPage;

    @Test(testName = "Log in to the application")
    public void signInToApplication() {
        data = JSONUtils.convertJSONToMap(CommonFilePaths.testDataFilePath, "PlaceOrderWithSignIn");
        LandingPage landingPage = new LandingPage(driver.get());
        signInPage = landingPage.navigateToSignInPage();
        signInPage.doSignIn();
    }

    @Test(testName = "Navigate to watches section", dependsOnMethods = {"signInToApplication"})
    public void navigateToMenuSection() {
        megaMenuPage = new MegaMenuPage(driver.get());
        megaMenuPage.navigateToItemsInMegaMenu(data.get("navigateMenuHierarchy").toString());
        watchesPage = new WatchesPage(driver.get());
    }

    @Test(testName = "Validate Filer Material: Metal is applied", dependsOnMethods = {"navigateToMenuSection"})
    public void validateFilterMetalIsApplied() {
        watchesPage.applyFilters(data.get("filters"));
    }

    @Test(testName = "Add watch to cart and validate cart count", dependsOnMethods = {"validateFilterMetalIsApplied"})
    public void validateCartCountByAddingWatchToCart() {
        watchesPage.addProductToCart(data.get("itemsToBeAdded"));
        watchesPage.validateCartCount(data.get("itemsToBeAdded"));
    }

    @Test(testName = "Increase quantity in checkout page and validate item quantity and amount", dependsOnMethods = {"validateCartCountByAddingWatchToCart"})
    public void validateItemsCountAndAmountOnQuantityUpdate() {
        cartSummaryPage = watchesPage.clickOnCartIcon();
        cartSummaryPage.validateItemsInCartSummarySection();
        shoppingCartPage = cartSummaryPage.clickOnViewAndEditCart();
        shoppingCartPage.validateItemsInShoppingCart(data.get("itemsToBeUpdated"));
        shoppingCartPage.validateAmountOnQuantityUpdate(data.get("itemsToBeUpdated"));
        shoppingCartPage.expandSummarySection();
        shoppingCartPage.setShippingDetails(data.get("shippingEstimateCountry").toString(), data.get("shippingEstimateState").toString(), data.get("shippingEstimateZipCode").toString());
        shoppingCartPage.checkFixedRate();
        shoppingCartPage.validateSummarySubTotalAmount();
        shoppingCartPage.validateSummaryOrderTotalAmount();
    }

    @Test(testName = "Proceed to checkout and validate Shipping Details and Order Summary", dependsOnMethods = {"validateItemsCountAndAmountOnQuantityUpdate"})
    public void validateShippingAndOrderDetails() {
        shippingPage = shoppingCartPage.proceedToCheckout();
        shippingPage.validateShippingAddress(data);
        shippingPage.validateFixedShippingMethodIsSelected();
        shippingPage.validateItemsCount();
        shippingPage.expandOrderSummary();
        shippingPage.validateOrderSummary();
    }

    @Test(testName = "Proceed to Review And Payments and validate Order Summary, total amounts, Shipping and Billing Address", dependsOnMethods = {"validateShippingAndOrderDetails"})
    public void validateOrderSummaryAndBillingDetails() {
        reviewAndPaymentsPage = shippingPage.ProceedWithNext();
        reviewAndPaymentsPage.selectBillingAddressSameAsShippingAddress();
        reviewAndPaymentsPage.validateBillingAddress(data);
        reviewAndPaymentsPage.validateShippingAddress(data);
        reviewAndPaymentsPage.validateFixedShippingMethod();
        reviewAndPaymentsPage.validateOrderSummaryTotalAmount();
        reviewAndPaymentsPage.validateItemsCount();
        reviewAndPaymentsPage.validateItemsInCart();
    }

    @Test(testName = "Place the Order and validate order number generated successfully", dependsOnMethods = {"validateOrderSummaryAndBillingDetails"})
    public void validateOrderIsPlacedSuccessfully() {
        reviewAndPaymentsPage.placeOrder();
        reviewAndPaymentsPage.validateOrderIsPlacedSuccessfully();
        reviewAndPaymentsPage.validateOrderNumberIsGenerated();
    }
}
