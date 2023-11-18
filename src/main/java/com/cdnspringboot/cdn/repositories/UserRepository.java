package com.cdnspringboot.cdn.repositories;

import com.cdnspringboot.cdn.models.User;

import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
    User findByEmail(String email);
}
