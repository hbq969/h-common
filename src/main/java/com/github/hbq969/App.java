package com.github.hbq969;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@EnableCaching
@EnableTransactionManagement(proxyTargetClass = true)
@EnableSwagger2
@EnableWebMvc
@EnableScheduling
@EnableKnife4j
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
