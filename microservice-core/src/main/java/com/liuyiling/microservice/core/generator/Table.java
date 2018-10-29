package com.liuyiling.microservice.core.generator;

import lombok.Data;

import java.util.List;

/**
 * velocity需要用的表模型
 *
 * @author liuyiling
 */
@Data
public class Table {

    private String pojoBeanName;

    private String tableName;

    private String remark;

    private List<String> primaryKeys;

    private List<Column> columns;

    private boolean hasDate;

    private boolean hasBigDecimal;
}
