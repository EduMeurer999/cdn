package com.cdnspringboot.cdn.repositories;

import com.cdnspringboot.cdn.models.FileDados;

import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FileRepository extends JpaRepository<FileDados, UUID>{}
