package com.cdnspringboot.cdn.repositories;

import com.cdnspringboot.cdn.models.PermissionFileUser;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface PermissonFileUserRepository extends JpaRepository<PermissionFileUser, UUID>{
    @Query("select pfur from PermissionFileUser pfur join pfur.file f join pfur.user u where f.id = ?1 and u.id = ?2")
    Optional<PermissionFileUser> findByFileIdAndUserId(UUID fileId, UUID userId);

    @Query("select pfur from PermissionFileUser pfur join pfur.file f where f.id = ?1")
    Optional<PermissionFileUser[]> findByFileId(UUID fileId);


}
