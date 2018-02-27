package com.ebi.ega;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
@Configuration
@PropertySource("file:${config}")
public class AppConfig {
    @Autowired
    private Environment env;

    @Bean
    public GetMD5 getMD5() { return new GetMD5(); }

    @Bean
    public UpdateMD5 updateMD5() { return new UpdateMD5(); }

    @Bean
    public DataSource audit() {
        DriverManagerDataSource audit = new DriverManagerDataSource();
        audit.setDriverClassName(env.getRequiredProperty("audit_test.driverClassName"));
        audit.setUrl(env.getRequiredProperty("audit_test.url"));
        audit.setUsername(env.getRequiredProperty("audit_test.user"));
        audit.setPassword(env.getRequiredProperty("audit_test.password"));
        return audit;
    }

    @Bean
    public DataSource erapro() {
        DriverManagerDataSource erapro = new DriverManagerDataSource();
        erapro.setDriverClassName(env.getRequiredProperty("erapro.driverClassName"));
        erapro.setUrl(env.getRequiredProperty("erapro.url"));
        erapro.setUsername(env.getRequiredProperty("erapro.user"));
        erapro.setPassword(env.getRequiredProperty("erapro.password"));
        return erapro;
    }
}
