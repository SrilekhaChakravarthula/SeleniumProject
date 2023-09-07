package com.magento.core.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class Listeners implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {

    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testSetName = result.getTestClass().getRealClass().getSimpleName();
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        Test test = method.getAnnotation(Test.class);
        String testName = test.testName();
        ExtentTest extentTest = ExtentReporter.eReports.get().createTest(testName);
        ExtentReporter.eTest.set(extentTest);
        ExtentReporter.eTest.get().assignCategory(testSetName);
        ExtentReporter.eTest.get().log(Status.SKIP, "Test Skipped: " + testName + ". Assigned Category: " + testSetName);
        org.testng.Reporter.log("Thread - " + Thread.currentThread().getId() + ": " + "Test Skipped: " + testName + ". Assigned Category: " + testSetName, true);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
