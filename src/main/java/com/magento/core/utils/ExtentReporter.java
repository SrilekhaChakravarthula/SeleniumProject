package com.magento.core.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.common.base.Throwables;
import org.testng.ITestResult;

public class ExtentReporter {
    private static ExtentReports extentReport;
    private static ExtentTest extentTest;

    public static void init() {
        String path = System.getProperty("user.dir") + "/target/extentReport.html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(path);
        sparkReporter.config().setReportName("Test Automation Report");
        sparkReporter.config().setDocumentTitle("Automation Report");

        extentReport = new ExtentReports();
        extentReport.attachReporter(sparkReporter);
        extentReport.setSystemInfo("user", "srilekha");
        extentReport.setSystemInfo("system", "windows");
    }

    public static void createTest(String testName) {
        extentTest = extentReport.createTest(testName);
    }

    public static void assignTestCategory(String categoryName) {
        extentTest.assignCategory(categoryName);
    }

    public static void logInfo(String message) {
        extentTest.log(Status.INFO, message);
    }

    public static void logPass(String message) {
        extentTest.log(Status.PASS, message);
    }

    public static void logFail(ITestResult result) {
        extentTest.log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" - Test Case Failed", ExtentColor.RED));
        extentTest.log(Status.FAIL,MarkupHelper.createCodeBlock(Throwables.getStackTraceAsString(result.getThrowable())));
    }

    public static void logSkip(String message) {
        extentTest.log(Status.SKIP, message);
    }

    public static void logWarn(String message) {
        extentTest.log(Status.WARNING, message);
    }

    public static void attachScreenshot(String base64String){
        extentTest.info(MediaEntityBuilder.createScreenCaptureFromBase64String(base64String,"Screenshot").build());
    }
    public static void attachScreenshot(String base64String,String title){
        extentTest.info(MediaEntityBuilder.createScreenCaptureFromBase64String(base64String,title).build());
    }
    public static void flushReport() {
        extentReport.flush();
    }
}
