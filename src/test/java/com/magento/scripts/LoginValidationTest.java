package com.magento.scripts;

import com.magento.core.constants.CommonFilePaths;
import com.magento.core.pages.LandingPage;
import com.magento.core.pages.SignInPage;
import com.magento.core.utils.Hooks;
import com.magento.core.utils.JSONUtils;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginValidationTest extends Hooks {
    Map<String, Object> data;
    LandingPage landingPage;
    SignInPage signInPage;

    @Test(testName = "Login with invalid username")
    public void loginWithInvalidUserName(){
        data = JSONUtils.convertJSONToMap(CommonFilePaths.testDataFilePath, "LoginValidations");
        landingPage = new LandingPage(driver.get());
        signInPage = landingPage.navigateToSignInPage();
        signInPage.doSignForNegativeValidations(data.get("invalidUserName").toString(),environment.getAppUserPassword());
        signInPage.validateSignInWithInvalidUserName();
        landingPage.validatePanelMenuHeader();
    }

    @Test(testName = "Login with incorrect username",priority = 1)
    public void loginWithWrongUserName(){
        signInPage = landingPage.navigateToSignInPage();
        signInPage.doSignForNegativeValidations(data.get("incorrectUserName").toString(),environment.getAppUserPassword());
        signInPage.validateSignInWithIncorrectUserNameOrPassword();
        landingPage.validatePanelMenuHeader();
    }
    @Test(testName = "Login with incorrect password",priority = 2)
    public void loginWithWrongPassword(){
        signInPage = landingPage.navigateToSignInPage();
        signInPage.doSignForNegativeValidations(environment.getAppUserEmail(),data.get("incorrectPassword").toString());
        signInPage.validateSignInWithIncorrectUserNameOrPassword();
        landingPage.validatePanelMenuHeader();
    }

    @Test(testName = "Login with empty userName",priority = 3)
    public void loginWithEmptyUserName(){
        landingPage = new LandingPage(driver.get());
        signInPage = landingPage.navigateToSignInPage();
        signInPage.doSignForNegativeValidations("",environment.getAppUserPassword());
        signInPage.validateSignInWithEmptyUserNameOrPassword();
    }
    @Test(testName = "Login with empty password",priority = 4)
    public void loginWithEmptyPassword(){
        landingPage = new LandingPage(driver.get());
        signInPage = landingPage.navigateToSignInPage();
        signInPage.doSignForNegativeValidations(environment.getAppUserEmail(),"");
        signInPage.validateSignInWithEmptyUserNameOrPassword();
    }
}
