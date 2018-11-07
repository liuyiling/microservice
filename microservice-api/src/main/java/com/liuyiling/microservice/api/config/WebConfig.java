package com.liuyiling.microservice.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuyiling.microservice.api.apiversion.VersionHandlerMapping;
import com.liuyiling.microservice.api.util.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 如果某个类的头上带有特定的注解[@Component/@Repository/@Service/@Controller]，就会将这个对象作为Bean注册进Spring容器
 *
 * @Component是所有受Spring管理组件的通用形式，@Component注解可以放在类的头上，@Component不推荐使用。
 * @Controller对应表现层的Bean，也就是Action spring默认scope是单例模式(scope = “ singleton ”).这样只会创建一个Action对象，每次访问都是同一个Action对象
 * @Service对应的是业务层Bean
 * @Configuration把一个类作为一个IoC容器，它的某个方法头上如果注册了@Bean，就会作为这个Spring容器中的Bean。
 */
//@Import({TracingHandlerInterceptor.class})
@Configuration
@ComponentScan(basePackages = {"com.liuyiling.microservice.core.*", "com.liuyiling.microservice.api.*"})
public class WebConfig extends WebMvcConfigurationSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    public MappingJackson2HttpMessageConverter jsonConverter;

//    @Autowired
//    private TracingHandlerInterceptor serverZipkinInterceptor;

    //其他的maven依赖中可能也会生成JSON转换器，我们需要强制工程使用该方法返回的JSON转换器，所以必须加Primary
    @Primary
    @Bean
    public MappingJackson2HttpMessageConverter getCustomJacksonConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        //设置中文编码格式
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        list.add(MediaType.APPLICATION_JSON);
        //请求头Accept是application/json;charset=UTF-8 或 application/json时，采用这个JSON转换器
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
        return mappingJackson2HttpMessageConverter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(serverZipkinInterceptor)
//                .excludePathPatterns("/metrics/**")
//                .excludePathPatterns("/v2/api-docs", "/configuration/**", "/swagger-resources/**");
//
//        registry.addInterceptor(new PrometheusMetricsInterceptor()).addPathPatterns("/**");
    }


    /**
     * 增加字符串转日期的功能
     */
    @PostConstruct
    public void initEditableValidation() {
        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer) handlerAdapter
                .getWebBindingInitializer();
        if (initializer.getConversionService() != null) {
            GenericConversionService genericConversionService = (GenericConversionService) initializer
                    .getConversionService();
            genericConversionService.addConverter(new DateConverter());
        }
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("get", "post")
                .allowedOrigins("*")
                .allowedHeaders("*");
    }


    //添加protobuf支持，需要client指定accept-type：application/x-protobuf
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setDefaultCharset(Charset.forName("utf-8"));
        List<MediaType> stringMapperMediaTypeList = new ArrayList<MediaType>();
        stringMapperMediaTypeList.add(MediaType.TEXT_PLAIN);
        stringConverter.setSupportedMediaTypes(stringMapperMediaTypeList);

        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        xmlConverter.setDefaultCharset(Charset.forName("utf-8"));
        List<MediaType> xmlMapperMediaTypeList = new ArrayList<MediaType>();
        xmlMapperMediaTypeList.add(MediaType.APPLICATION_XML);
        xmlConverter.setSupportedMediaTypes(xmlMapperMediaTypeList);
        //虽然会根据Accept请求头来判断使用哪个解析器，但是如果Accept为空，会使用默认解析器，也就是第一个JackSon
        converters.add(0, stringConverter);
        converters.add(0, xmlConverter);
        converters.add(0, getCustomJacksonConverter(objectMapper));
    }


    @Override
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        //RequestMappingHandlerMapping根据@RequestMapping注解生成 RequestMappingInfo,同时提供isHandler实现
        RequestMappingHandlerMapping handlerMapping = new VersionHandlerMapping("v");
        handlerMapping.setOrder(0);

        handlerMapping.setInterceptors(getInterceptors());
        AntPathMatcher pathMatcher = new AntPathMatcher();
        pathMatcher.setCaseSensitive(false);
        handlerMapping.setPathMatcher(pathMatcher);
        return handlerMapping;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false).setUseTrailingSlashMatch(true);
    }


    /**
     * 处理Spring中的静态资源，
     * swagger的静态资源时在META-INF下的
     * 发现如果继承了WebMvcConfigurationSupport，则在yml中配置的相关内容会失效。
     * 需要重新指定静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }

    /**
     * addViewControllers可以方便的实现一个请求直接映射成视图，而无需书写controller
     * registry.addViewController("请求路径").setViewName("请求页面文件路径")
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(registry);
    }
}