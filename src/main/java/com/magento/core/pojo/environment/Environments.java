package com.magento.core.pojo.environment;

import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
@Jacksonized
@Data
public class Environments {
    List<Environment> environments;
}
