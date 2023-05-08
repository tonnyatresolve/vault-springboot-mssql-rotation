package com.mssql.demo.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.SecretLeaseEventPublisher;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;
import javax.annotation.PostConstruct;
import com.mssql.demo.entity.Credential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConditionalOnBean(SecretLeaseContainer.class)
@Configuration
public class VaultConfig {
    //private final ConfigurableApplicationContext applicationContext;
    private final HikariDataSource hikariDataSource;
    private final SecretLeaseContainer leaseContainer;
    private final String databaseRole;
    private Logger logger;

    public VaultConfig(ConfigurableApplicationContext applicationContext,
                       HikariDataSource hikariDataSource,
                       SecretLeaseContainer leaseContainer,
                       @Value("${spring.cloud.vault.database.role}") String databaseRole) {
        //this.applicationContext = applicationContext;
        this.hikariDataSource = hikariDataSource;
        this.leaseContainer = leaseContainer;
        this.databaseRole = databaseRole;
        this.logger = LoggerFactory.getLogger(VaultConfig.class);
    }

    @PostConstruct
    private void postConstruct() {
        final String vaultCredsPath = String.format("database/creds/%s", databaseRole);
  
        logger.info("\n\n");
        logger.info("**********************************************************************");
        logger.info("* Database credential initialization and configure password rotation event ");
        logger.info("**********************************************************************\n\n");

        leaseContainer.addLeaseListener(event -> {
          logger.info("\n\n");
          logger.info("**********************************************************************");
          logger.info("* Received event: {}", event);
          logger.info("**********************************************************************\n\n");

          leaseContainer.removeLeaseErrorListener(SecretLeaseEventPublisher.LoggingErrorListener.INSTANCE);
    
          if (vaultCredsPath.equals(event.getSource().getPath())) {
            if (event instanceof SecretLeaseExpiredEvent && event.getSource().getMode() == RequestedSecret.Mode.RENEW) {
              //logger.info("\n\n");
              //logger.info("**********************************************************************");
              //logger.info("* Replace RENEW lease by a ROTATE one.");
              //logger.info("**********************************************************************\n\n");
                
              leaseContainer.requestRotatingSecret(vaultCredsPath);
            } else if (event instanceof SecretLeaseCreatedEvent && event.getSource().getMode() == RequestedSecret.Mode.ROTATE) {
              SecretLeaseCreatedEvent secretLeaseCreatedEvent = (SecretLeaseCreatedEvent) event;
    
              Credential credential = getCredentials(secretLeaseCreatedEvent);
              logger.info("\n\n");
              logger.info("**********************************************************************");
              logger.info("* Retrieved new username from Vault: {}", credential.getUsername());
              logger.info("* Retrieved new password from Vault: {}", credential.getPassword());
              logger.info("**********************************************************************\n\n");
        
              refreshDatabaseConnection(credential);
            }
    
            //logger.info("**********************************************************************");
            //logger.info("* DONE HANDLE event: {}", event);
            //logger.info("**********************************************************************");
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
      logger.info("\n\n");
      logger.info("**********************************************************************");
      String username = credential.getUsername();
      String password = credential.getPassword();
      System.setProperty("spring.datasource.username", username);
      System.setProperty("spring.datasource.password", password);
      logger.info("* Updated system properties spring.datasource.username: {}", username);
      logger.info("* Updated system properties spring.datasource.password: {}", password);
      logger.info("**********************************************************************\n\n");
    }
  
    private void updateDataSource(Credential credential) {
      String username = credential.getUsername();
      String password = credential.getPassword();
      logger.info("\n\n");
      logger.info("**********************************************************************");
      logger.info("* Start reloading DB connection");
      hikariDataSource.getHikariConfigMXBean().setUsername(username);
      hikariDataSource.getHikariConfigMXBean().setPassword(password);
      hikariDataSource.getHikariPoolMXBean().softEvictConnections();
      logger.info("* Finish reloading DB connection");
      logger.info("**********************************************************************\n\n");
    }
}