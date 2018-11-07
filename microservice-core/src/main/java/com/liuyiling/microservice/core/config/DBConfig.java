package com.liuyiling.microservice.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
@Configuration
@MapperScan(basePackages = "com.liuyiling.microservice.core.storage.dao.mapper", sqlSessionTemplateRef = "dbSqlSessionTemplate")
public class DBConfig {

    private Logger logger = LoggerFactory.getLogger(DBConfig.class);


    /**
     * 生成数据池
     * 在同样的DataSource中，首先使用被标注的DataSource
     *
     * @return
     */
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "db.spring.datasource")
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    /**
     * 指定sql连接池生成的工厂
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "dbSqlSessionFactory")
    public SqlSessionFactory dbSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver
                .getResources("classpath:mapper/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 数据库事务管理
     *
     * @param dataSource
     * @return
     */
    @Bean(name = "dbTransactionManager")
    public DataSourceTransactionManager dbTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * SqlSessionTemplate是SqlSession的实现类，较SqlSession的默认实现类DefaultSqlSession来说，是线程安全的。
     *
     * @param sqlSessionFactory
     * @return
     * @throws Exception
     */
    @Bean(name = "dbSqlSessionTemplate")
    public SqlSessionTemplate dbSqlSessionTemplate(@Qualifier("dbSqlSessionFactory") SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}