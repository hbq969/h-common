package com.github.hbq969.code.common.spring.context;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.*;

import java.util.*;


/**
 * @author : hbq969@gmail.com
 * @description : Spring环境变量增强实现
 * @createTime : 18:10:23, 2023.03.28, 周二
 */
@Slf4j
public class SpringEnvImpl implements SpringEnv, InitializingBean {

  public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

  @Autowired
  private Environment environment;

  @Value("${spring.profiles.active:default}")
  private String profiles;

  private Map<String, Object> variables = Maps.newHashMapWithExpectedSize(256);

  private Set<String> varKeys = new LinkedHashSet<>(256);

  @Override
  public void afterPropertiesSet() throws Exception {
    if (environment != null) {

      AbstractEnvironment abstractEnvironment = AbstractEnvironment.class.cast(environment);
      MutablePropertySources mutablePropertySources = abstractEnvironment.getPropertySources();
      for (PropertySource<?> propertySource : mutablePropertySources) {
        if (propertySource instanceof EnumerablePropertySource) {
          EnumerablePropertySource source = EnumerablePropertySource.class.cast(propertySource);
          for (String propertyName : source.getPropertyNames()) {
            variables.put(propertyName, source.getProperty(propertyName));
          }
        }
      }
      if (!variables.containsKey(SPRING_PROFILES_ACTIVE)) {
        variables.put("spring.profiles.active", profiles);
      }
      List<String> keyList = new ArrayList<>(variables.size());
      variables.keySet().forEach(k -> keyList.add(k));
      Collections.sort(keyList);
      keyList.forEach(k -> varKeys.add(k));
    }
  }

  @Override
  public Set<String> getEnvironmentPropertyKeys() {
    return Collections.unmodifiableSet(varKeys);
  }
}
