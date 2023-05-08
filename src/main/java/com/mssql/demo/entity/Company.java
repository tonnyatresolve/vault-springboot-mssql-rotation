package com.mssql.demo.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "company_address")
    private String companyAddress;
    @Column(name = "company_email")
    private String companyEmail;
    @Transient
    private LocalDateTime apiCallTime;
    @Transient
    private String databaseUsername;
    @Transient
    private String databasePassword;

    public Company() {
        super();
    }

    public Company(String companyName, String companyAddress, String companyEmail) {
        super();
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyEmail = companyEmail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public LocalDateTime getAPICallTime() {
        return this.apiCallTime;
    }

    public void setAPICallTime(LocalDateTime apiCallTime) {
        this.apiCallTime = apiCallTime;
    }

    public String getDatabaseUsername() {
        return this.databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePassword() {
        return this.databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }
}