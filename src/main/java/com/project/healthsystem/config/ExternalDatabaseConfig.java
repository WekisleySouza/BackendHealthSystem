package com.project.healthsystem.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ExternalDatabaseConfig {
    @Bean
    public CommandLineRunner testDatasource(DataSource dataSource) {
        return args -> {
            System.out.println("Datasource usado pelo JPA:");
            System.out.println(dataSource.getConnection().getMetaData().getURL());
        };
    }

    @Bean(name = "externalDataSource")
    @ConfigurationProperties("spring.external-datasource")
    public DataSource externalDataSource(){
        return DataSourceBuilder
            .create()
            .type(com.zaxxer.hikari.HikariDataSource.class)
            .build();
    }

    @Bean
    public JdbcTemplate externalJdbcTemplate(
        @Qualifier("externalDataSource")
        DataSource dataSource
    ){
        return new JdbcTemplate(dataSource);
    }
}
