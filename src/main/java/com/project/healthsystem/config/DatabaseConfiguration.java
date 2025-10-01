package com.project.healthsystem.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfiguration {

    @Value("${spring.datasource.url}")
    private String url;

    @Bean
    public DataSource hikariDataSource() throws URISyntaxException {
        HikariConfig config = new HikariConfig();

        // Se vier algo como: postgres://user:pass@host:5432/db
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            URI uri = new URI(url);

            String userInfo = uri.getUserInfo(); // "user:pass"
            String username = userInfo.split(":")[0];
            String password = userInfo.split(":")[1];

            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath()
                    + "?sslmode=require";

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
        } else {
            config.setJdbcUrl(url);
        }

        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setPoolName("healthsystem-db-pool");
        config.setMaxLifetime(600000);
        config.setConnectionTimeout(100000);
        config.setConnectionTestQuery("select 1");

        return new HikariDataSource(config);
    }
}
