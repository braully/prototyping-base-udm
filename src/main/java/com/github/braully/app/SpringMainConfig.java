package com.github.braully.app;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan({"com.github.braully"})
@EntityScan(basePackages = {"com.github.braully.domain", "com.github.braully.tmp"})
@EnableScheduling
@EnableAsync
//@EnableOAuth2Sso
public class SpringMainConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource rootDatasource() {
        return DataSourceBuilder.create().build();
    }

    public static void main(String... args) {
        SpringApplication.run(SpringMainConfig.class, args);
    }
}
