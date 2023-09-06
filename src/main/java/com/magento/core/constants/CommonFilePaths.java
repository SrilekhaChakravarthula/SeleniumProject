package com.magento.core.constants;

import com.magento.core.pages.AbstractPage;

import java.io.IOException;

public class CommonFilePaths {
    public static String currentDirectory = System.getProperty("user.dir");
    public static final String chromeDriverPath = currentDirectory + "/src/main/resources/chromeDriver/chromedriver.exe";
    public static final String testDataFilePath = currentDirectory + "/src/test/java/com/magento/testData/addProductsData.json";
    public static final String configPropertiesFilePath = currentDirectory + "/config.properties";
    public static final String envConfigFilePath = currentDirectory + "/envConfig.json";
}
