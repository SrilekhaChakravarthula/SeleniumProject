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
    public static ThreadLocal<ExtentSparkReporter> eSparkReporter = new ThreadLocal<>();
    public static InheritableThreadLocal<ExtentReports> eReports = new InheritableThreadLocal<>();
    public static ThreadLocal<ExtentTest> eTest = new ThreadLocal<>();
    private static ExtentReports extentReport;
    private static ExtentTest extentTest;

    public static void init() {
        String path = System.getProperty("user.dir") + "/target/extentReport.html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(path);
        eSparkReporter.set(sparkReporter);
        eSparkReporter.get().config().setReportName("Test Automation Report");
        eSparkReporter.get().config().setDocumentTitle("Automation Report");

        extentReport = new ExtentReports();
        eReports.set(extentReport);
        eReports.get().attachReporter(sparkReporter);
        eReports.get().setSystemInfo("user", "srilekha");
        eReports.get().setSystemInfo("system", "windows");
    }

    public static void createTest(String testName) {
        extentTest = extentReport.createTest(testName);
        eTest.set(extentTest);
    }

    public static void assignTestCategory(String categoryName) {
        eTest.get().assignCategory(categoryName);
    }

    public static void logInfo(String message) {
        eTest.get().log(Status.INFO, message);
    }

    public static void logPass(String message) {
        eTest.get().log(Status.PASS, message);
    }

    public static void logFail(ITestResult result) {
        eTest.get().log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" - Test Case Failed", ExtentColor.RED));
        eTest.get().log(Status.FAIL,MarkupHelper.createCodeBlock(Throwables.getStackTraceAsString(result.getThrowable())));
    }

    public static void logSkip(String message) {
        eTest.get().log(Status.SKIP, message);
    }

    public static void logWarn(String message) {
        eTest.get().log(Status.WARNING, message);
    }

    public static void attachScreenshot(String base64String){
        eTest.get().info(MediaEntityBuilder.createScreenCaptureFromBase64String(base64String,"Screenshot").build());
    }
    public static void attachScreenshot(String base64String,String title){
        eTest.get().info(MediaEntityBuilder.createScreenCaptureFromBase64String(base64String,title).build());
    }
    public static void flushReport() {
        eReports.get().flush();
    }
}
