package com.magento.scripts;

import com.magento.core.pages.*;
import com.magento.core.utils.Hooks;
import org.testng.annotations.Test;

public class SessionValidationOnSignOutTest extends Hooks {
    SignInPage signInPage;
    HomePage homePage;
    LandingPage landingPage;

    @Test(testName = "Sign in to the application")
    public void signin() {
        landingPage = new LandingPage(driver.get());
        signInPage = landingPage.navigateToSignInPage();
        homePage = signInPage.doSignIn();
    }

    @Test(testName = "Signout of the application and validate that you land on the landing page",dependsOnMethods = {"signin"})
    public void signout() {
        homePage.signOut();
        landingPage.validatePanelMenuHeader();
    }

    @Test(testName = "Navigate back and validate that you land on the Landing page",dependsOnMethods = {"signout"})
    public void navigateBack() {
        driver.get().navigate().back();
        landingPage.validatePanelMenuHeader();
    }
}
