package com.liuyiling.microservice.core.generator;

import lombok.Data;

/**
 * velocity需要用的列模型
 *
 * @author liuyiling
 */
@Data
public class Column {

    private String lowerProperty;

    private String property;

    private String column;

    private int maxLength;

    private String javaType;

    private String jdbcType;

    private String remark;

    private int dataType;

    private boolean allowNull;
}
