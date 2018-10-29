package com.liuyiling.microservice.core.generator;

import lombok.Data;

/**
 * velocity的配置
 *
 * @author liuyiling
 */
@Data
public class VelocityConfiguration {

    private String targetDir;

    private String modelPackage;

    private String xmlPackage;

    private String mapperPackage;

    private String examplePackage;

    private String restControllerPackage;

    private String controllerPackage;

}
