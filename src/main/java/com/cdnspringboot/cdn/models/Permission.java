package com.cdnspringboot.cdn.models;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Permission{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private char permission;

    private String description;
    
}
