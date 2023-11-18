package com.cdnspringboot.cdn.repositories;

import com.cdnspringboot.cdn.models.Permission;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>{}
