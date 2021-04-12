package com.doesitwork.springboot;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.MariaDB10Dialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.flogger.FluentLogger;

import okhttp3.OkHttpClient;

@EnableTransactionManagement
@EnableAsync
@EnableJpaAuditing
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class,
                                   org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                                   org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class,
                                   RedisRepositoriesAutoConfiguration.class })
public class Application {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
        logger.atInfo().log("operation=startup, result=success");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    @Bean("jpaTransactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(10, TimeUnit.SECONDS)
               .readTimeout(60, TimeUnit.SECONDS)
               .writeTimeout(60, TimeUnit.SECONDS)
               .retryOnConnectionFailure(true);

        return builder.build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "com.doesitwork.springboot.domain" });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", MariaDB10Dialect.class);
        properties.put(AvailableSettings.HBM2DDL_AUTO, SchemaAutoTooling.UPDATE.name().toLowerCase());
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.put("org.hibernate.envers.audit_table_suffix", "Audit");
        properties.put("hibernate.listeners.envers.autoRegister", true);
        properties.put("hibernate.envers.autoRegisterListeners", true);
        properties.put("hibernate.envers.autoRegisterListeners", true);
        properties.put("spring.jpa.generate-ddl", true);
        properties.put("spring.jpa.hibernate.ddl-auto", true);
        return properties;
    }
}
