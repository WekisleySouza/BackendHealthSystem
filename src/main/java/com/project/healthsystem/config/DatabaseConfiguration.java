package com.project.healthsystem.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${spring.datasource.driverClassName}")
    String driver;

    @Bean
    public DataSource hikariDataSource(){
        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);

        config.setMaximumPoolSize(10); // Configurado para 10 conexões no máximo
        config.setMinimumIdle(1); // Tamanho mínimo do pool
        config.setPoolName("healthsystem-db-pool");
        config.setMaxLifetime(600000); // 10 min máximo para conexão
        config.setConnectionTimeout(100000); // Tempo máximo para conectar
        config.setConnectionTestQuery("select 1"); // Testar conexão com o banco

        return new HikariDataSource(config);
    }
}
