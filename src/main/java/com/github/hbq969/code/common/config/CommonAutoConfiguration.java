package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.filter.SwaggerFilter;
import com.github.hbq969.code.common.restful.HealthControl;
import com.github.hbq969.code.common.restful.version.VersionRegistrations;
import com.github.hbq969.code.common.spring.advice.conf.AdviceProperties;
import com.github.hbq969.code.common.spring.advice.ex.GlobalExceptionHandler;
import com.github.hbq969.code.common.spring.advice.limit.RestfulLimitAdvice;
import com.github.hbq969.code.common.spring.advice.log.LogAdvice;
import com.github.hbq969.code.common.spring.advice.log.LogRestfulHandler;
import com.github.hbq969.code.common.spring.advice.rest.RestfulAdvice;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.spring.context.SpringEnv;
import com.github.hbq969.code.common.spring.context.SpringEnvImpl;
import com.github.hbq969.code.common.spring.interceptor.*;
import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;

/**
 * @author : hbq969@gmail.com
 * @description :
 * @createTime : 2024/4/29 17:46
 */
@Slf4j
public class CommonAutoConfiguration implements ApplicationContextAware, EnvironmentAware {
    private ApplicationContext context;
    private Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean("common-SpringContextProperties")
    public SpringContextProperties springContextProperties() {
        return new SpringContextProperties();
    }

    @Bean("common-HealthControl")
    public HealthControl healthControl() {
        return new HealthControl();
    }

    @Bean("common-SwaggerProperties")
    public SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
    }

    @Bean("common-docket")
    @Primary
    public Docket docket(SwaggerProperties conf) {
        log.info("启用swagger功能");
        Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo(conf)).select().apis(RequestHandlerSelectors.basePackage(conf.getBasePackage())).build();
        if (conf.getApiInfo().isPathMapping()) {
            docket.pathMapping(environment.getProperty("server.servlet.context-path", "/"));
        }
        return docket;
    }

    private ApiInfo apiInfo(SwaggerProperties conf) {
        return new ApiInfoBuilder().title(conf.getApiInfo().getTitle()).description(conf.getApiInfo().getDescription()).version(conf.getApiInfo().getVersion()).license(conf.getApiInfo().getLicense()).licenseUrl(conf.getApiInfo().getLicenseUrl()).build();
    }

    @Bean("common-WebMvcRegistrations")
    WebMvcRegistrations registrations() {
        return new VersionRegistrations();
    }

    @Bean("common-springEnv")
    SpringEnv springEnv() {
        return new SpringEnvImpl();
    }

    @Bean("common-springContext")
    SpringContext springContext() {
        return new SpringContext(context, environment);
    }

    @Bean("common-AdviceProperties")
    AdviceProperties adviceProperties() {
        return new AdviceProperties();
    }

//    @ConditionalOnExpression("${advice.log.enabled:true}")
//    @Bean("common-logAdvice")
//    LogAdvice logAdvice() {
//        log.info("初始化restful接口日志记录aop组件");
//        return new LogAdvice();
//    }

    @ConditionalOnExpression("${advice.log.enabled:true}")
    @Bean("common-RestfulAdvice")
    RestfulAdvice restfulAdvice(){
        return new RestfulAdvice();
    }

    @ConditionalOnExpression("${advice.log.enabled:true}")
    @Bean("common-LogRestfulHandler")
    LogRestfulHandler logRestfulHandler() {
        return new LogRestfulHandler();
    }

    @ConditionalOnExpression("${advice.ex.enabled:true}")
    @Bean("common-GlobalExceptionHandler")
    GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @ConditionalOnExpression("${advice.restful-limit.enabled:false}")
    @Bean("common-restfulLimitAdvice")
    RestfulLimitAdvice restfulLimitAdvice() {
        log.info("启用restful接口限流特性");
        return new RestfulLimitAdvice();
    }

    @Bean("common-InterceptorProperties")
    SpringInterceptorProperties interceptorProperties() {
        return new SpringInterceptorProperties();
    }

    @ConditionalOnExpression("${spring.mvc.interceptors.info.enabled:false}")
    @Bean("common-webmvc-handler-mdcHandlerInterceptor")
    InfoHandlerInterceptor infoHandlerInterceptor() {
        return new InfoHandlerInterceptor();
    }

    @ConditionalOnExpression("${spring.mvc.interceptors.mdc.enabled:true}")
    @Bean("common-webmvc-handler-mdcHandlerInterceptor")
    MDCHandlerInterceptor mdcHandlerInterceptor() {
        return new MDCHandlerInterceptor();
    }

    @ConditionalOnExpression("${spring.mvc.interceptors.header.enabled:false}")
    @Bean("common-webmvc-handler-headerHandlerInterceptor")
    HeaderHandlerInterceptor headerHandlerInterceptor() {
        return new HeaderHandlerInterceptor();
    }

    @ConditionalOnExpression("${spring.mvc.interceptors.api-safe.enabled:false}")
    @Bean("common-webmvc-handler-apiSecurityInterceptor")
    ApiSecurityInterceptor apiSecurityInterceptor() {
        return new ApiSecurityInterceptor();
    }

    @Bean("common-webMvcConfigurerAdapter")
    @ConditionalOnExpression("${spring.mvc.resource-handler-registry.enabled:true}")
    @Primary
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter(SpringInterceptorProperties conf) {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {

                log.info("初始化自定义WebMvcConfigurerAdapter。");

                registry.addResourceHandler("swagger-ui.html", "doc.html").addResourceLocations("classpath:/META-INF/resources/");

                registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

                registry.addResourceHandler("/ui/**").addResourceLocations("classpath:/static/ui/");

                Optional.ofNullable(conf.getResourceHandlerRegistry().getEntries()).ifPresent(entries -> {
                    entries.forEach(entry -> {
                        log.info("扩展的ResourceHandler, {}", GsonUtils.toJson(entry));
                        registry.addResourceHandler(entry.getHandlers()).addResourceLocations(entry.getLocations());
                    });
                });
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                Map<String, AbstractHandlerInterceptor> map = context.getBeansOfType(AbstractHandlerInterceptor.class);
                if (MapUtils.isEmpty(map)) {
                    return;
                }
                List<AbstractHandlerInterceptor> list = new ArrayList<>(map.size());
                for (Map.Entry<String, AbstractHandlerInterceptor> entry : map.entrySet()) {
                    list.add(entry.getValue());
                }
                list.stream().sorted(Comparator.comparingInt(h -> {
                    if (h instanceof InterceptorOrder) {
                        return InterceptorOrder.class.cast(h).order();
                    } else {
                        return Integer.MAX_VALUE;
                    }
                })).forEach(h -> {
                    log.info("注册拦截器, {}, order: {}, includeUris: {}, excludeUris: {}", h.getClass().getName(), h.order(), h.getPathPatterns(), h.getExcludedPathPatterns());
                    InterceptorRegistration registration = registry.addInterceptor(h);
                    if (CollectionUtils.isNotEmpty(h.getPathPatterns())) {
                        registration.addPathPatterns(h.getPathPatterns());
                    }
                    if (CollectionUtils.isNotEmpty(h.getExcludedPathPatterns())) {
                        registration.excludePathPatterns(h.getExcludedPathPatterns());
                    }
                });
            }
        };
    }

    @ConditionalOnExpression("#{${spring.mvc.interceptors.api-safe.enabled:false} || ${spring.mvc.interceptors.login.enabled:false}}")
    @Bean("common-swaggerFilter")
    public FilterRegistrationBean<SwaggerFilter> swaggerFilterFilterRegistrationBean(SpringContext sc) {
        log.info("开启/v2/api-docs禁用拦截器");
        String apiSafeHeaderName = sc.getProperty("spring.mvc.interceptors.api-safe.header-name");
        String apiSafeHeaderValue = sc.getProperty("spring.mvc.interceptors.api-safe.header-value-regex");
        FilterRegistrationBean<SwaggerFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SwaggerFilter(sc, apiSafeHeaderName, apiSafeHeaderValue));
        registration.addUrlPatterns("/*");
        registration.setName("swaggerFilter");
        registration.setOrder(0);
        return registration;
    }
}
