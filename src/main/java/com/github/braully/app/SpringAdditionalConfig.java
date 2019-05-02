package com.github.braully.app;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class SpringAdditionalConfig {

    //Database
    /* Flyway Database configuration
     * Workaround: Force flyway running post hibernate schema update
     * https://stackoverflow.com/questions/37097876/spring-boot-hibernate-and-flyway-boot-order
     */
    @Bean
    /* Execute before hibernate schema update */
    FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway);
    }

    @Bean
    @DependsOn("entityManagerFactory")
    /* Execute after hibernate schema update */
    FlywayMigrationInitializer delayedFlywayInitializer(Flyway flyway) {
        ClassicConfiguration configuration = (ClassicConfiguration) flyway.getConfiguration();
        /*
         * https://stackoverflow.com/questions/36077766/detected-resolved-migration-not-applied-to-database-on-flyway
         */
        configuration.setOutOfOrder(true);
        configuration.setValidateOnMigrate(false);
        configuration.setLocationsAsStrings("classpath:db/migration-data");
        Flyway flywaytmp = new Flyway(configuration);
        //flywaytmp.migrate();
        return new FlywayMigrationInitializer(flywaytmp);
    }

}
