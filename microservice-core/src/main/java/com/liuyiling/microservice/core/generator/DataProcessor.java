package com.liuyiling.microservice.core.generator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        Map<String, Map<String, Map<String, Object>>> oracleTableInfoMap = getOracleTableInfoMap(tableNamePattern);
        List<Table> tableInfoList = oracleTableInfoMap2TableInfoList(oracleTableInfoMap);
        prepareProcessTableInfos(tableInfoList);
        return tableInfoList;
    }

    /**
     * 从Oracle中获取所有的表结构信息
     *
     * @param tableNamePattern
     * @return <tableName, <columnName, <column_property_name,column_property_value>>>
     */
    public Map<String, Map<String, Map<String, Object>>> getOracleTableInfoMap(String tableNamePattern) {
        Map<String, Map<String, Map<String, Object>>> tableInfoMap = new HashMap<>();

        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getColumns(null, null, tableNamePattern, null);

            while (rs.next()) {
                String tableName = rs.getString(TABLE_NAME);
                String columnName = rs.getString(COLUMN_NAME);
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

                Integer oracleTypeInt = rs.getInt(DATA_TYPE);
                String remarks = rs.getString(REMARKS);
                Map<String, Map<String, Object>> table = tableInfoMap.get(tableName);
                if (table == null) {
                    table = new HashMap<>();
                    tableInfoMap.put(tableName, table);
                }

                Map<String, Object> columnPropertyMap = new HashMap<>();
                columnPropertyMap.put("jdbcType", oracleTypeStr);
                columnPropertyMap.put("remark", remarks);
                columnPropertyMap.put("dataType", oracleTypeInt);
                columnPropertyMap.put("length", rs.getInt(COLUMN_SIZE));
                columnPropertyMap.put("isnull", rs.getInt(NULLABLE));
                table.put(columnName, columnPropertyMap);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }
        return tableInfoMap;
    }

    /**
     * 将oracle中的tableInfoMap转换成tableList
     *
     * @param oracleTableInfoMap
     * @return
     */
    public List<Table> oracleTableInfoMap2TableInfoList(Map<String, Map<String, Map<String, Object>>> oracleTableInfoMap) {
        List<Table> tableInfoList = new ArrayList<>();

        for (Entry<String, Map<String, Map<String, Object>>> entry : oracleTableInfoMap.entrySet()) {
            Table table = new Table();
            String tableName = entry.getKey();
            table.setTableName(tableName);
            List<Column> columns = new ArrayList<>();
            table.setColumns(columns);

            tableInfoList.add(table);

            Map<String, Map<String, Object>> oracleColumns = entry.getValue();
            for (Entry<String, Map<String, Object>> oracleConlumn : oracleColumns.entrySet()) {
                Column column = new Column();
                column.setColumn(oracleConlumn.getKey());
                Map<String, Object> columnPropertyMap = oracleConlumn.getValue();
                column.setJdbcType((String) columnPropertyMap.get("jdbcType"));
                column.setDataType((int) columnPropertyMap.get("dataType"));
                column.setMaxLength((int) columnPropertyMap.get("length"));
                column.setRemark((String) columnPropertyMap.get("remark"));
                int isnull = (int) columnPropertyMap.get("isnull");
                if (isnull == 0) {
                    column.setAllowNull(false);
                } else {
                    column.setAllowNull(true);
                }
                //将JDBCType转换为JavaType
                String javaType = TypeUtils.getJavaType(column.getDataType());
                column.setJavaType(javaType);
                if ("Date".equals(javaType)) {
                    table.setHasDate(true);
                } else if ("BigDecimal".equals(javaType)) {
                    table.setHasBigDecimal(true);
                }
                columns.add(column);
            }
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
                    String primaryKey = keysSet.getString("COLUMN_NAME");
                    primaryKeys.add(primaryKey);
                }
                table.setPrimaryKeys(primaryKeys);
                table.setBeanName(StringUtils.underLine2Camel(StringUtils.firstChar2UpperCase(table.getTableName().toLowerCase())));
                table.setPojoBeanName(table.getBeanName().toLowerCase());
                prepareProcessColumns(table.getColumns());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }
    }

    public void prepareProcessColumns(List<Column> columnList) {
        for (Column column : columnList) {
            String lowerProperty = StringUtils.underLine2Camel(column.getColumn().toLowerCase());
            column.setLowerProperty(lowerProperty);
            column.setProperty(StringUtils.firstChar2UpperCase(StringUtils.underLine2Camel(lowerProperty)));
        }
    }
}
