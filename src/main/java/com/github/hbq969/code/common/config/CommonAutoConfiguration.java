package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.restful.ApiInfoProperties;
import com.github.hbq969.code.common.restful.HealthControl;
import com.github.hbq969.code.common.restful.version.VersionRegistrations;
import com.github.hbq969.code.common.spring.advice.limit.RestfulLimitAdvice;
import com.github.hbq969.code.common.spring.advice.log.LogAdvice;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.spring.context.SpringEnv;
import com.github.hbq969.code.common.spring.context.SpringEnvImpl;
import com.github.hbq969.code.common.spring.interceptor.*;
import com.github.hbq969.code.common.spring.mvc.MvcResourceHandlersProperties;
import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
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

    @Bean("common-HealthControl")
    public HealthControl healthControl() {
        return new HealthControl();
    }

    @Bean("common-apiInfoProperties")
    public ApiInfoProperties apiInfoProperties() {
        return new ApiInfoProperties();
    }

    @Bean("common-docket")
    @Primary
    @ConditionalOnExpression("${swagger.api-info.enable:true}")
    public Docket docket(ApiInfoProperties apiInfo) {
        String defaultBasePackage = environment.getProperty("swagger.base-package", "com.github.hbq969");
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo(apiInfo)).select().apis(RequestHandlerSelectors.basePackage(defaultBasePackage)).build();
    }

    private ApiInfo apiInfo(ApiInfoProperties apiInfo) {
        return new ApiInfoBuilder().title(apiInfo.getTitle()).description(apiInfo.getDescription()).version(apiInfo.getVersion()).license(apiInfo.getLicense()).licenseUrl(apiInfo.getLicenseUrl()).build();
    }

    @ConditionalOnExpression("${swagger.api-info.version.enable:true}")
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

    @ConditionalOnExpression("${advice.log.enabled:true}")
    @Bean("common-logAdvice")
    LogAdvice logAdvice() {
        log.info("初始化restful接口日志记录aop组件");
        return new LogAdvice();
    }

    @ConditionalOnExpression("${advice.restful-limit.enabled:false}")
    @Bean("common-restfulLimitAdvice")
    RestfulLimitAdvice restfulLimitAdvice() {
        log.info("启用restful接口限流特性");
        return new RestfulLimitAdvice();
    }

    @ConditionalOnExpression("${spring.mvc.adapter.interceptor.info.enable:false}")
    @Bean("common-webmvc-handler-mdcHandlerInterceptor")
    InfoHandlerInterceptor infoHandlerInterceptor() {
        return new InfoHandlerInterceptor();
    }

    @Bean("common-webmvc-handler-mdcHandlerInterceptor")
    MDCHandlerInterceptor mdcHandlerInterceptor() {
        return new MDCHandlerInterceptor();
    }

    @ConditionalOnExpression("${spring.mvc.adapter.interceptor.header.enable:false}")
    @Bean("common-webmvc-handler-headerHandlerInterceptor")
    HeaderHandlerInterceptor headerHandlerInterceptor() {
        return new HeaderHandlerInterceptor();
    }

    @Bean("common-MvcResourceHandlersProperties")
    @ConditionalOnExpression("${spring.mvc.adapter.enable:true}")
    MvcResourceHandlersProperties mvcResourceHandlersProperties() {
        return new MvcResourceHandlersProperties();
    }

    @Bean("common-webMvcConfigurerAdapter")
    @ConditionalOnExpression("${spring.mvc.adapter.enable:true}")
    @Primary
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter(MvcResourceHandlersProperties conf) {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {

                log.info("初始化自定义WebMvcConfigurerAdapter。");

                registry.addResourceHandler("swagger-ui.html", "doc.html").addResourceLocations("classpath:/META-INF/resources/");

                registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

                registry.addResourceHandler("/ui/**").addResourceLocations("classpath:/static/ui/");

                Optional.ofNullable(conf.getEntries()).ifPresent(entries -> {
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
                    log.info("注册拦截器, {}, order: {}, includeUris: {}, excludeUris: {}",
                            h.getClass().getName(), h.order(), h.getPathPatterns(), h.getExcludedPathPatterns());
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
}
