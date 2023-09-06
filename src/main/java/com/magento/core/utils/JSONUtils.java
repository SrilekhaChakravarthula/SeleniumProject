package com.magento.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class JSONUtils {
    public static Map<String, Object> convertJSONToMap(String filePath, String testName) {
        try {
            // Converting JSON to String
            String jsonContent = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            // Converting String to Map using jackson-databind
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> allData = mapper.readValue(jsonContent, new TypeReference<List<Map<String, Object>>>() {
            });
            Map<String, Object> testData = allData.stream().filter(el -> el.get("testName").toString().equalsIgnoreCase(testName)).findFirst().get();
            return testData;
        } catch (Exception e) {
            Assert.fail("Unable to fetch test data for the test : " + testName);
            return null;
        }
    }
}
