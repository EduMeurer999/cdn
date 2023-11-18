package com.cdnspringboot.cdn.controllers;

import java.util.List;
import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdnspringboot.cdn.models.User;
import com.cdnspringboot.cdn.repositories.UserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody User userModel) {
         var user = this.userRepository.findByEmail(userModel.getEmail());
        if(user != null){
            System.out.println("Usuário já existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario já existe");
        }
        var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getSenha().toCharArray());

        userModel.setSenha(passwordHashed);

        var userCreated = this.userRepository.save(userModel);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PutMapping("{id}")
    public User updateUser(@PathVariable UUID id, @RequestBody User updatedUser) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setNome(updatedUser.getNome());
            user.setEmail(updatedUser.getEmail());
            user.setSenha(updatedUser.getSenha());
            userRepository.save(user);
        }
        return user;
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable UUID id) {
        userRepository.deleteById(id);
    }
}
