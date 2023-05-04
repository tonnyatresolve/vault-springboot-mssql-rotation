package com.mssql.demo.configuration;

//import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.datasource")
public class MyConfiguration {

  //private String username;

  private String password;

  /*
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  */

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}