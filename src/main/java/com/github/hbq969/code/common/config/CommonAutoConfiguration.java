package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.filter.SwaggerFilter;
import com.github.hbq969.code.common.initial.ScriptInitialProcessor;
import com.github.hbq969.code.common.restful.HealthControl;
import com.github.hbq969.code.common.restful.version.VersionRegistrations;
import com.github.hbq969.code.common.spring.advice.conf.AdviceProperties;
import com.github.hbq969.code.common.spring.advice.ex.GlobalExceptionHandler;
import com.github.hbq969.code.common.spring.advice.limit.RestfulLimitAdvice;
import com.github.hbq969.code.common.spring.advice.log.LogRestfulHandler;
import com.github.hbq969.code.common.spring.advice.rest.RestfulAdvice;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.spring.context.SpringEnvImpl;
import com.github.hbq969.code.common.spring.i18n.ClassPathReloadableResourceBundleMessageSource;
import com.github.hbq969.code.common.spring.i18n.I18nCtrl;
import com.github.hbq969.code.common.spring.interceptor.*;
import com.github.hbq969.code.common.utils.GsonUtils;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    @Bean("common-OpenAPI")
    public OpenAPI customOpenAPI(SwaggerProperties conf) {
        return new OpenAPI()
                .info(conf.getApiInfo())
                .servers(conf.getServers());
    }

    @Bean("common-GroupedOpenApi")
    public GroupedOpenApi customGroupedOpenApi(SwaggerProperties conf) {
        return GroupedOpenApi.builder()
                .group(conf.getGroup())
                .packagesToScan(conf.getBasePackage())
                .pathsToMatch("/**")
                .build();
    }

    @Bean("common-WebMvcRegistrations")
    WebMvcRegistrations registrations() {
        return new VersionRegistrations();
    }

    @Bean("common-springEnv")
    SpringEnvImpl springEnv() {
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

    @ConditionalOnExpression("${advice.log.enabled:true}")
    @Bean("common-RestfulAdvice")
    RestfulAdvice restfulAdvice() {
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
        if (log.isDebugEnabled()) {
            log.debug("启用restful接口限流功能。");
        }
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
    public WebMvcConfigurer webMvcConfigurer() {
        SpringInterceptorProperties conf = context.getBean(SpringInterceptorProperties.class);
        return new WebMvcConfigurer() {

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("swagger-ui.html", "doc.html")
                        .addResourceLocations("classpath:/META-INF/resources/");
                if (log.isDebugEnabled()) {
                    log.debug("注册MVC资源, 资源: swagger-ui.html, doc.html, 加载路径: classpath:/META-INF/resources/");
                }
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
                if (log.isDebugEnabled()) {
                    log.debug("注册MVC资源, 资源: /webjars/**, 加载路径: classpath:/META-INF/resources/webjars/");
                }
                registry.addResourceHandler("/ui/**")
                        .addResourceLocations("classpath:/static/ui/");
                if (log.isDebugEnabled()) {
                    log.debug("注册MVC资源, 资源: /ui/**, 加载路径: classpath:/static/ui/");
                }
                Optional.ofNullable(conf.getResourceHandlerRegistry().getEntries())
                        .ifPresent(entries -> entries.forEach(entry -> {
                            registry.addResourceHandler(entry.getHandlers())
                                    .addResourceLocations(entry.getLocations());
                            if (log.isDebugEnabled()) {
                                log.debug("注册MVC资源, 资源: {}, 加载路径: {}",
                                        GsonUtils.toJson(entry.getHandlers()),
                                        GsonUtils.toJson(entry.getLocations()));
                            }
                        }));
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                Map<String, AbstractHandlerInterceptor> map = context.getBeansOfType(AbstractHandlerInterceptor.class);
                if (MapUtils.isEmpty(map)) {
                    return;
                }
                List<AbstractHandlerInterceptor> list = new ArrayList<>(map.values());
                list.stream()
                        .sorted(Comparator.comparingInt(h -> (h instanceof InterceptorOrder) ?
                                ((InterceptorOrder) h).order() : Integer.MAX_VALUE))
                        .forEach(h -> {
                            if (log.isDebugEnabled()) {
                                log.debug("注册拦截器, {}, 优先级: {}, 拦截路径: {}, 例外路径: {}",
                                        h.getClass().getName(), h.order(),
                                        h.getPathPatterns(), h.getExcludedPathPatterns());
                            }
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
    public FilterRegistrationBean<SwaggerFilter> swaggerFilterFilterRegistrationBean() {

        String apiSafeHeaderName = context.getEnvironment().getProperty("spring.mvc.interceptors.api-safe.header-name");
        String apiSafeHeaderValue = context.getEnvironment().getProperty("spring.mvc.interceptors.api-safe.header-value-regex");
        FilterRegistrationBean<SwaggerFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SwaggerFilter(context, apiSafeHeaderName, apiSafeHeaderValue));
        registration.addUrlPatterns("/*");
        registration.setName("swaggerFilter");
        registration.setOrder(0);
        if (log.isDebugEnabled()) {
            log.debug("创建 /v2/api-docs 拦截过滤器");
        }
        return registration;
    }

    // 覆盖spring缺省的messageSource，参考org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration.MessageSourceAutoConfiguration
    @Bean("messageSource")
    public MessageSource messageSource() {
        ClassPathReloadableResourceBundleMessageSource messageSource = new ClassPathReloadableResourceBundleMessageSource();
        String basename = environment.getProperty("spring.messages.basename", "i18n/messages");
        String charset = environment.getProperty("spring.messages.encoding", "utf-8");
        if (!basename.startsWith("classpath")) {
            basename = "classpath*:" + basename;
        }
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(charset);
        messageSource.setUseCodeAsDefaultMessage(true);
        if (log.isDebugEnabled()) {
            log.debug("初始化国际化功能: {}, {}", messageSource.getBasenameSet(), charset);
        }
        return messageSource;
    }

    @Bean("common-restful-I18nCtrl")
    I18nCtrl i18nCtrl() {
        return new I18nCtrl();
    }

    @Bean("common-ScriptInitialProcessor")
    ScriptInitialProcessor scriptInitialProcessor() {
        return new ScriptInitialProcessor();
    }

    public static void main(String[] args) throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:i18n/messages.properties");
        for (Resource resource : resources) {
            System.out.println(resource);
        }
    }
}
