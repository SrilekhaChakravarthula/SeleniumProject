package com.magento.core.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.magento.core.constants.CommonFilePaths;
import com.magento.core.pages.AbstractPage;
import com.magento.core.pojo.environment.Environment;
import com.magento.core.pojo.environment.Environments;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class EnvironmentUtil {
    private static Environment environment;

    private static Environment getEnvironmentInstance() {
        try {
            FileInputStream fis = new FileInputStream(CommonFilePaths.configPropertiesFilePath);
            Properties properties = new Properties();
            properties.load(fis);
            String env = properties.getProperty("environment");

            ObjectMapper mapper = new ObjectMapper();
            Environments allEnvs = mapper.readValue(new File(CommonFilePaths.envConfigFilePath), Environments.class);
            environment = allEnvs.getEnvironments().stream().filter(e -> e.getName().equalsIgnoreCase(env)).findFirst().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return environment;
    }

    public static Environment getEnvironment() {
        if (environment == null) {
            getEnvironmentInstance();
        }
        return environment;
    }
}

