package com.agaram.eln.primary.config;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.nativejdbc.SimpleNativeJdbcExtractor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.agaram.eln.primary.repository.*",
      entityManagerFactoryRef = "entityManagerFactory",
      transactionManagerRef= "transactionManager"
)
@ComponentScan(basePackages = {"com.agaram.eln.primary"})
@EntityScan({"com.agaram.eln.primary"})
public class HibernateConfig {
	@Autowired
    private org.springframework.core.env.Environment env;
	
	@Autowired
    private JpaProperties jpaProperties;
	
	@Autowired
    private TenantSchemaResolver tenantSchemaResolver;
	
	@Autowired
    private DataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProviderImpl;

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }
    
    @Bean
	@Primary
	@ConfigurationProperties("app.datasource.eln")
	public DataSourceProperties elnDataSourceProperties() {
	    return new DataSourceProperties();
	}
    
    @Bean
	@Primary
	@ConfigurationProperties("app.datasource.eln.configuration")
	public DataSource elnDataSource() {
	    return elnDataSourceProperties().initializeDataSourceBuilder()
	            .type(HikariDataSource.class).build();
	}

    @Primary
    @Bean(name = "entityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            MultiTenantConnectionProvider multiTenantConnectionProviderImpl,
            CurrentTenantIdentifierResolver currentTenantIdentifierResolverImpl
    ) {

    	 Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
        jpaPropertiesMap.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        jpaPropertiesMap.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, dataSourceBasedMultiTenantConnectionProviderImpl);
        jpaPropertiesMap.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantSchemaResolver);
        jpaPropertiesMap.put(Environment.FORMAT_SQL, true);
        jpaPropertiesMap.put(Environment.SHOW_SQL, true);
        
     
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(elnDataSource());
        em.setPackagesToScan(new String[]{"com.agaram.eln.primary.model.cfr"
        		, "com.agaram.eln.primary.model.configuration"
        		, "com.agaram.eln.primary.model.general"
        		, "com.agaram.eln.primary.model.instrumentDetails"
        		, "com.agaram.eln.primary.model.inventory"
        		, "com.agaram.eln.primary.model.report"
        		, "com.agaram.eln.primary.model.sheetManipulation"
        		, "com.agaram.eln.primary.model.usermanagement"
				, "com.agaram.eln.primary.model.protocols"
				, "com.agaram.eln.primary.model.templates"
				, "com.agaram.eln.primary.model.multitenant"
				, "com.agaram.eln.primary.model.jwt"
				, "com.agaram.eln.primary.model.cloudFileManip"});
        em.setJpaVendorAdapter(this.jpaVendorAdapter());
        
        Properties jpaProperties = new Properties();
        if(env.getProperty("spring.jpa.hibernate.ddl-auto") != null)
        {
        	jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        }
        else
        {
        	jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        }
        
        jpaProperties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
        jpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
        jpaProperties.put("hibernate.connection.useUnicode", true);
        jpaProperties.put("hibernate.connection.characterEncoding", "UTF-8");
        
        em.setJpaProperties(jpaProperties);
        
//        em.setJpaPropertyMap(jpaPropertiesMap);
        
//        Properties jpaProperties = new Properties();
//      if(env.getProperty("spring.jpa.hibernate.ddl-auto") != null)
//      {
//      	jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
//      }
//      else
//      {
//      	jpaProperties.put("hibernate.hbm2ddl.auto", "update");
//      }
//      
//      jpaProperties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
//      jpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
//      jpaProperties.put("hibernate.connection.useUnicode", true);
//      jpaProperties.put("hibernate.connection.characterEncoding", "UTF-8");
      
//      em.setJpaProperties(jpaProperties);
        
        return em;
    }
    
    @Primary
  @Bean(name="transactionManager")
  public PlatformTransactionManager elnTransactionManager(
          final @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
      return new JpaTransactionManager(entityManagerFactory.getObject());
  }
}
