package com.magento.core.pojo.environment;

import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
public class Environment {
    private String name;
    private String appUrl;
    private String appUserEmail;
    private String appUserPassword;
    private String userName;
    private String firstName;
    private String lastName;
    private String restBaseURL;
    private String dbURL;
    private String dbUserName;
    private String dbPassword;
}
