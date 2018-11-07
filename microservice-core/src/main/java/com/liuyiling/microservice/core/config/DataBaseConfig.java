package com.liuyiling.microservice.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

/**
 * 数据库配置
 * MapperScan定义了需要扫描的mapper位置和使用的sqlSession模板
 *
 * @author liuyiling
 * @date on 2018/11/7
 */
@Configuration
@MapperScan(basePackages = "com.liuyiling.microservice.core.storage.dao.mapper", sqlSessionTemplateRef = "dbSqlSessionTemplate")
public class DataBaseConfig {

    /**
     * 生成数据池
     * 在同样的DataSource中，首先使用被标注的DataSource
     * DruidDataSource是 name为dataSource 的优先选择
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
     * 将dataSource使用spring自带的事务管理器进行管理
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

    /**
     * Mybaits的监控统计页面
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // IP白名单
        //servletRegistrationBean.addInitParameter("allow", "127.0.0.1,192.168.1.83");
        // IP黑名单（优先级高于白名单）
        servletRegistrationBean.addInitParameter("deny", "192.168.1.100");
        // 控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        // 是否能够重置数据
        servletRegistrationBean.addInitParameter("resetEnable", "false");

        return servletRegistrationBean;
    }

    /**
     * Mybaits的监控统计页面
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean statFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        // 添加过滤规则
        filterRegistrationBean.addUrlPatterns("/*");
        // 忽略过滤的格式
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.css,*.jpg,*.ico,/druid/*");

        return filterRegistrationBean;
    }
}