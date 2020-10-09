package com.github.braully.app;

import com.github.braully.util.logutil;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringAdditionalConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    DataSource dataSource;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent e) {
        logutil.info("Startup onApplication");
        try {
            logutil.info("Manual migrate flyway -- init");
            ClassicConfiguration configuration = new ClassicConfiguration();
            configuration.setDataSource(dataSource);
            configuration.setOutOfOrder(true);
            configuration.setValidateOnMigrate(false);
            configuration.setLocationsAsStrings("classpath:db/migration-data");
            Flyway flywaytmp = new Flyway(configuration);
            flywaytmp.migrate();
            logutil.info("Manual migrate flyway -- finish");
        } catch (Exception ex) {
            logutil.error("Fail on flyway automate", ex);
        }
    }

    // Database
    /*
     * Flyway Database configuration Workaround: Force flyway running post hibernate
     * schema update
     * https://stackoverflow.com/questions/37097876/spring-boot-hibernate-and-flyway
     * -boot-order
     */
    @Bean
    @Primary
    /* Execute before hibernate schema update */
    FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        // ((ClassicConfiguration) flyway.getConfiguration()).setOutOfOrder(true);
        return new FlywayMigrationInitializer(flyway);
    }

    // @Bean(initMethod = "migrate")
    // @DependsOn("entityManagerFactory")
    // Flyway delayedFlyway(DataSource dataSource) {
    // ClassicConfiguration configuration = new ClassicConfiguration();
    // configuration.setDataSource(dataSource);
    // configuration.setOutOfOrder(true);
    // configuration.setValidateOnMigrate(false);
    // configuration.setLocationsAsStrings("classpath:db/migration-data");
    // Flyway flywaytmp = new Flyway(configuration);
    // return flywaytmp;
    // }
    // @Bean
    // @DependsOn("entityManagerFactory")
    // /* Execute after hibernate schema update */
    // FlywayMigrationInitializer delayedFlywayInitializer(Flyway flyway) {
    // ClassicConfiguration configuration = (ClassicConfiguration)
    // flyway.getConfiguration();
    // /*
    // *
    // https://stackoverflow.com/questions/36077766/detected-resolved-migration-not-applied-to-database-on-flyway
    // */
    // configuration.setOutOfOrder(true);
    // configuration.setValidateOnMigrate(false);
    // configuration.setLocationsAsStrings("classpath:db/migration-data");
    // Flyway flywaytmp = new Flyway(configuration);
    // //flywaytmp.migrate();
    // return new FlywayMigrationInitializer(flywaytmp);
    // }
    
    /* WebDAV */
    
}
