package com.mssql.demo.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
//import org.springframework.vault.core.lease.domain.RequestedSecret.Mode;
//import org.springframework.vault.core.lease.domain.RequestedSecret.Mode.ROTATE;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;
import javax.annotation.PostConstruct;
import com.mssql.demo.entity.Credential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConditionalOnBean(SecretLeaseContainer.class)
@Configuration
public class VaultConfig {
    private final ConfigurableApplicationContext applicationContext;
    private final HikariDataSource hikariDataSource;
    private final SecretLeaseContainer leaseContainer;
    private final String databaseRole;

    public VaultConfig(ConfigurableApplicationContext applicationContext,
                       HikariDataSource hikariDataSource,
                       SecretLeaseContainer leaseContainer,
                       @Value("${spring.cloud.vault.database.role}") String databaseRole) {
        this.applicationContext = applicationContext;
        this.hikariDataSource = hikariDataSource;
        this.leaseContainer = leaseContainer;
        this.databaseRole = databaseRole;
    }

    @PostConstruct
    private void postConstruct() {
        final String vaultCredsPath = String.format("database/creds/%s", databaseRole);
        Logger logger = LoggerFactory.getLogger(VaultConfig.class);
  
        logger.info("----------------------------------------");
        logger.info("Configuration properties");

        leaseContainer.addLeaseListener(event -> {
          logger.info("==> Received event: {}", event);
    
          if (vaultCredsPath.equals(event.getSource().getPath())) {
            if (event instanceof SecretLeaseExpiredEvent &&
              event.getSource().getMode() == RequestedSecret.Mode.RENEW) {
                logger.info("==> Replace RENEW lease by a ROTATE one.");
              leaseContainer.requestRotatingSecret(vaultCredsPath);
            } else if (event instanceof SecretLeaseCreatedEvent && event.getSource().getMode() == RequestedSecret.Mode.ROTATE) {
              SecretLeaseCreatedEvent secretLeaseCreatedEvent = (SecretLeaseCreatedEvent) event;
    
              Credential credential = getCredentials(secretLeaseCreatedEvent);
        
              refreshDatabaseConnection(credential);
              logger.info("==> DONE updateDataSource");
            }
    
            logger.info("==> DONE HANDLE event: {}", event);
          }
        });
      }

    private void refreshDatabaseConnection(Credential credential) {
        updateDbProperties(credential);
        updateDataSource(credential);
    }

    private Credential getCredentials(SecretLeaseCreatedEvent event) {
        String username = (String) event.getSecrets().get("username");
        String password = (String) event.getSecrets().get("password");
        if (username == null || password == null) {
            return null;
        }
        return new Credential(username, password);
    }

    private void updateDbProperties(Credential credential) {
      String username = credential.getUsername();
      String password = credential.getPassword();
      System.setProperty("spring.datasource.username", username);
      System.setProperty("spring.datasource.password", password);
    }
  
    private void updateDataSource(Credential credential) {
      String username = credential.getUsername();
      String password = credential.getPassword();
      hikariDataSource.getHikariConfigMXBean().setUsername(username);
      hikariDataSource.getHikariConfigMXBean().setPassword(password);
      hikariDataSource.getHikariPoolMXBean().softEvictConnections();
    }
}