package com.mssql.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mssql.demo.entity.User;
import com.mssql.demo.exception.ResourceNotFoundException;
import com.mssql.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.mssql.demo.configuration.MyConfiguration;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    private final MyConfiguration configuration;

    public UserController(MyConfiguration configuration) {
		this.configuration = configuration;
	}	

    // get all user
    @GetMapping("/users")
    public List<User> getAllUsers() {
        Logger logger = LoggerFactory.getLogger(UserController.class);
        logger.info("   example.password is {}", configuration.getPassword());
        return userRepository.findAll();
    }

    // create user rest API
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {

        return userRepository.save(user);
    }

    // get user by id rest api
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                      ("User not exist with id :" + id));

        return ResponseEntity.ok(user);
    }

    // update user rest api
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
             @RequestBody User userDetails) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                      ("User not exist with id :" + id));
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmailId(userDetails.getEmailId());
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(updatedUser);
    }

    // delete user rest api
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser
               (@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                    ("User not exist with id :" + id));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return ResponseEntity.ok(response);
    }
}
