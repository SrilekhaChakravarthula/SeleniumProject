package com.magento.scripts;

import com.magento.core.pages.LandingPage;
import com.magento.core.pages.SignInPage;
import com.magento.core.utils.Hooks;
import org.testng.annotations.Test;

public class SQLInjectionTest extends Hooks {
    SignInPage signInPage;
    @Test(testName = "XSS Vulnerability Test")
    public void testXSSVulnerability(){
        LandingPage landingPage = new LandingPage(driver.get());
        signInPage = landingPage.navigateToSignInPage();
        signInPage.doSignInSQLInjection();
    }
}
