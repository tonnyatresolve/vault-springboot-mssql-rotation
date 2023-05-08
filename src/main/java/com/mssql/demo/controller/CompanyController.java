package com.mssql.demo.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mssql.demo.entity.Company;
import com.mssql.demo.exception.ResourceNotFoundException;
import com.mssql.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    @Autowired
    private CompanyRepository userRepository;
    private Logger logger;

    public CompanyController() {
        this.logger = LoggerFactory.getLogger(CompanyController.class);
    }

    // get all company
    @GetMapping("/companies")
    public List<Company> getAllCompanies(@Value("${spring.datasource.username}") String databaseUsername, @Value("${spring.datasource.password}") String databasePassword) {
        List companies = userRepository.findAll();

        for (int i = 0; i < companies.size(); i++) {
            Company company = (Company)companies.get(i);
            company.setAPICallTime(LocalDateTime.now());
            company.setDatabaseUsername(databaseUsername);
            company.setDatabasePassword(databasePassword);
        }
        
        return companies;
    }
}
