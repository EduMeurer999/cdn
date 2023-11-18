package com.cdnspringboot.cdn.models;

import java.util.List;
import java.util.UUID;

import com.cdnspringboot.cdn.repositories.UserRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class FileDados
{
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Lob 
    @Column(name="file_base64", length=10000)
    private String fileBase64;

    @Column(nullable = false)
    private String extensao;

    @ManyToOne(fetch = FetchType.LAZY)
    private User usuario;

    public FileDados(){
        
    }

    public FileDados(String nome, String fileBase64, String extensao, User user){
        this.nome = nome;
        this.fileBase64 = fileBase64;
        this.extensao = extensao;
        this.usuario = user;
    }
}
