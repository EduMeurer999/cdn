package com.cdnspringboot.dtos;

import java.util.List;
import java.util.UUID;

import com.cdnspringboot.cdn.models.FileDados;
import com.cdnspringboot.cdn.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

public class FileDadosDTO {
    
    private UUID id;
    private String nome;
    private String fileBase64;
    private String extensao;
    private UUID usuarioId;
    private List<UUID> usuariosPermitidos;


    public FileDadosDTO(FileDados fileDados){
    
    
    }

}
