package com.liuyiling.microservice.core.generator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据表的预处理
 *
 * @author liuyiling
 * @date on 2018/7/04
 */
public class DataProcessor {

    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String COLUMN_SIZE = "COLUMN_SIZE";
    public static final String NULLABLE = "NULLABLE";
    public static final String DATA_TYPE = "DATA_TYPE";
    public static final String REMARKS = "REMARKS";

    private Connection connection;

    public DataProcessor(Connection con) {
        connection = con;
    }

    public List<Table> getTableInfoList(String tableNamePattern) {
        List<Table> tableInfoList = getOracleTableInfoMap(tableNamePattern);
        prepareProcessTableInfos(tableInfoList);
        return tableInfoList;
    }

    /**
     * 从Oracle中获取所有的表结构信息
     *
     * @param tableNamePattern
     * @return
     */
    public List<Table> getOracleTableInfoMap(String tableNamePattern) {
        List<Table> tableInfoList = new ArrayList<>();
        Map<String, Table> tableInfoMap = new HashMap<>();

        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getColumns(null, null, tableNamePattern, null);

            while (rs.next()) {
                String tableName = rs.getString(TABLE_NAME);
                Column column = new Column();
                column.setColumnName(rs.getString(COLUMN_NAME));
                String oracleTypeStr = rs.getString(TYPE_NAME);
                if (oracleTypeStr.equals("DATETIME")) {
                    oracleTypeStr = "DATE";
                }
                if (oracleTypeStr.equals("INT")) {
                    oracleTypeStr = "INTEGER";
                }
                if (oracleTypeStr.equals("VARCHAR2")) {
                    oracleTypeStr = "VARCHAR";
                }
                if (oracleTypeStr.equals("NUMBER")) {
                    oracleTypeStr = "DECIMAL";
                }
                column.setJdbcType(oracleTypeStr);
                column.setMaxLength(rs.getInt(COLUMN_SIZE));
                column.setRemark(rs.getString(REMARKS));
                if (rs.getInt(NULLABLE) == 0) {
                    column.setAllowNull(false);
                } else {
                    column.setAllowNull(true);
                }
                String javaType = TypeUtils.getJavaType(rs.getInt(DATA_TYPE));
                column.setJavaType(javaType);

                Table table = tableInfoMap.get(tableName);
                if (table == null) {
                    table = new Table();
                    table.setTableName(tableName);
                    table.setColumns(new ArrayList<>());
                    tableInfoMap.put(tableName, table);
                }
                if ("Date".equals(javaType)) {
                    table.setHasDate(true);
                } else if ("BigDecimal".equals(javaType)) {
                    table.setHasBigDecimal(true);
                }
                table.getColumns().add(column);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }

        for (Map.Entry<String, Table> entry : tableInfoMap.entrySet()) {
            tableInfoList.add(entry.getValue());
        }

        return tableInfoList;
    }

    public void prepareProcessTableInfos(List<Table> tableInfoList) {
        try {
            for (Table table : tableInfoList) {
                //设置主键列表
                List<String> primaryKeys = new ArrayList<>();
                ResultSet keysSet = connection.getMetaData().getPrimaryKeys(null, null, table.getTableName());
                while (keysSet.next()) {
                    String primaryKey = keysSet.getString(COLUMN_NAME);
                    primaryKeys.add(primaryKey);
                }
                table.setPrimaryKeys(primaryKeys);
                table.setPojoBeanName(StringUtils.underLine2Camel(StringUtils.firstChar2UpperCase(table.getTableName().toLowerCase())) + "DO");
                prepareProcessColumns(table.getColumns());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }
    }

    public void prepareProcessColumns(List<Column> columnList) {
        for (Column column : columnList) {
            String lowerProperty = StringUtils.underLine2Camel(column.getColumnName().toLowerCase());
            column.setCamelProperty(lowerProperty);
        }
    }
}
