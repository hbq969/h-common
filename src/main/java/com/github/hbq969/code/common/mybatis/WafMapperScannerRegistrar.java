package com.github.hbq969.code.common.mybatis;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author : hbq969@gmail.com
 * @description : 支持动态占位符解析的扫描器
 * @createTime : 14:41:02, 2023.03.30, 周四
 */
@Slf4j
public class WafMapperScannerRegistrar implements ImportBeanDefinitionRegistrar,
    ResourceLoaderAware, EnvironmentAware {

  public static final Pattern SEPARATOR_PATTERN = Pattern.compile("[,;]");

  private ResourceLoader resourceLoader;

  private Environment environment;

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {

    AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
        importingClassMetadata.getAnnotationAttributes(WafMapperScan.class.getName()));
    ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

    // this check is needed in Spring 3.1
    if (resourceLoader != null) {
      scanner.setResourceLoader(resourceLoader);
    }

    Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
    if (!Annotation.class.equals(annotationClass)) {
      scanner.setAnnotationClass(annotationClass);
    }

    Class<?> markerInterface = annoAttrs.getClass("markerInterface");
    if (!Class.class.equals(markerInterface)) {
      scanner.setMarkerInterface(markerInterface);
    }

    Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
    if (!BeanNameGenerator.class.equals(generatorClass)) {
      scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
    }

    Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
    if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
      scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
    }

    scanner.setSqlSessionTemplateBeanName(annoAttrs.getString("sqlSessionTemplateRef"));
    scanner.setSqlSessionFactoryBeanName(annoAttrs.getString("sqlSessionFactoryRef"));

    List<String> basePackages = new ArrayList<String>();
    for (String pkg : annoAttrs.getStringArray("value")) {
      if (StringUtils.hasText(pkg)) {
        parsePlaceHolder(pkg).forEach(p -> basePackages.add(p));
      }
    }
    for (String pkg : annoAttrs.getStringArray("basePackages")) {
      if (StringUtils.hasText(pkg)) {
        parsePlaceHolder(pkg).forEach(p -> basePackages.add(p));
      }
    }
    for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
      basePackages.add(ClassUtils.getPackageName(clazz));
    }
    scanner.setAnnotationClass(Mapper.class);
    scanner.registerFilters();
    scanner.doScan(StringUtils.toStringArray(basePackages));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  private List<String> parsePlaceHolder(String pro) {
    if (pro != null && pro.contains(
        PropertySourcesPlaceholderConfigurer.DEFAULT_PLACEHOLDER_PREFIX)) {
      String value = environment.getProperty(pro.substring(2, pro.length() - 1));

      if (null == value) {
        throw new IllegalArgumentException("property " + pro + " can not find!!!");
      }

      List<String> values = Splitter.on(SEPARATOR_PATTERN).trimResults().splitToList(value);
      if (log.isInfoEnabled()) {
        log.info("find placeholder value {} for key {}", values, pro);
      }

      return values;
    }
    return Lists.newArrayList(pro);
  }
}
