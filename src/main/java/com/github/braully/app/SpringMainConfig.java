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

    @Bean
    //@Qualifier("auditorProviderUserLoginId")@Primary
    public AuditorAware<Long> auditorProvider() {

        return new AuditorAware<Long>() {
            @Override
            public Optional<Long> getCurrentAuditor() {
                Long idUser = null;
                try {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    idUser = ((UserLogin) authentication.getPrincipal()).getId();
                } catch (Exception e) {
                }
                return Optional.ofNullable(idUser);
            }
        };
    }

    /*@Bean
    @Qualifier("auditorProviderUserLogin")
    public AuditorAware<UserLogin> auditorProviderUserLogin() {
        UserLogin user = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = (UserLogin) authentication.getPrincipal();
        } catch (Exception e) {
        }
        final UserLogin ulambda = user;
        return () -> Optional.ofNullable(ulambda);
    }*/
    public static void main(String... args) {
        SpringApplication.run(SpringMainConfig.class, args);
    }

    //https://stackoverflow.com/questions/4159802/how-can-i-restart-a-java-application
    public static void restartApplication() throws URISyntaxException, IOException {
        String javabin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        logutil.info("Java-Bin: " + javabin);
        //
        File currentJar = new File(SpringMainConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        ArrayList<String> command = new ArrayList<String>();

        logutil.info("Jvmarg: ");
        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar")) {
            logutil.info(currentJar.getName() + "is not jar file, aborting");
            return;
        }

        /* Build command: java -jar application.jar */
        command.add("nohup");
        command.add(javabin);

        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            command.add(jvmArg);
        }

        command.add("-jar");
        command.add(currentJar.getPath());
        command.add("&");

        logutil.info("Restarting app");
        String toString = command.toString();
        System.out.println(toString);
        logutil.info(toString);

        final ProcessBuilder builder = new ProcessBuilder(command);
        Process start = builder.start();
        System.out.println(start);
        logutil.info("Process: " + start);
        System.exit(0);
    }
}
