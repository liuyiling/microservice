package com.liuyiling.microservice.core.generator;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


/**
 * 从数据库表结构生成MyBaits相关类的工具
 *
 * @author liuyiling
 */
public class OracleGenerator {

    private static String url = "jdbc:oracle:thin:@192.168.84.62:1521:yxdev11gdb";
    private static String driver = "oracle.jdbc.OracleDriver";
    private static String user = "ec_eip_adm";
    private static String password = "eip123abc";

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection connection;
        Class.forName(driver);
        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    public static String getProjectPath() throws Exception {
        URL url = OracleGenerator.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        //MAC和WIN有差别，substring的startIndex分别为0和1
        filePath = filePath.substring(0, filePath.length() - ("/target/classes/").length());
        return filePath;
    }

    /**
     * 运行该main函数,生成DB对应的POJO以及操作类
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //需要生成DB类的表名、支持正则匹配、推荐使用精确匹配、每次生成一张
        String wantedTableName = "TB_EIP_REQUIRE_POOL";

        VelocityConfiguration config = new VelocityConfiguration();
        config.setTargetDir(getProjectPath() + "/src/main/java/");
        config.setModelPackage("com.liuyiling.microservice.core.storage.dao.entity");
        config.setMapperPackage("com.liuyiling.microservice.core.storage.dao.mapper");
        config.setXmlPackage("mapper");

        Generator generator = new Generator(config);
        Connection connection = getConnection();
        DataProcessor dataProcessor = new DataProcessor(connection);

        List<Table> tableInfos = dataProcessor.getTableInfoList(wantedTableName);
        connection.close();

        //生成DB操作类代码
        try {
            String xmlDir = config.getTargetDir().replace("java/", "resources/");
            for (Table table : tableInfos) {
                generator.generateModel(config.getTargetDir(), table);
                generator.generateMapper(config.getTargetDir(), table);
                generator.generateXml(xmlDir, table);
            }
        } catch (Exception e) {
        }
    }

}
