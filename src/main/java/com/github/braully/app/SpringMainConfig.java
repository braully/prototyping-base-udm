package com.github.braully.app;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan({"com.github.braully"})
@EntityScan(basePackages = {"com.github.braully.domain",
    "com.github.braully.domain.util", "com.github.braully.tmp",
    "com.github.braully.veritas"})
@EnableJpaRepositories("com.github.braully")
@EnableScheduling
@EnableAsync
//@EnableOAuth2Sso
public class SpringMainConfig {

    @Value("${http.port:8080}")
    private Integer httpPort;

    @Value("${server.port:8443}")
    private Integer serverPort;

    //@Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(httpPort);
        connector.setRedirectPort(serverPort);
        connector.setSecure(false);
        return connector;
    }

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
