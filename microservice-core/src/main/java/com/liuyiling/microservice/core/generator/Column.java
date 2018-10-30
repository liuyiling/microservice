package com.liuyiling.microservice.core.generator;

import lombok.Data;

/**
 * velocity需要用的列模型
 *
 * @author liuyiling
 */
@Data
public class Column {

    private String camelProperty;

    private String columnName;

    private int maxLength;

    private String javaType;

    private String jdbcType;

    private String remark;

    private boolean allowNull;
}
