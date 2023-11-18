package com.cdnspringboot.cdn.controllers;

import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cdnspringboot.cdn.models.FileDados;
import com.cdnspringboot.cdn.models.Permission;
import com.cdnspringboot.cdn.models.PermissionFileUser;
import com.cdnspringboot.cdn.models.User;
import com.cdnspringboot.cdn.repositories.FileRepository;
import com.cdnspringboot.cdn.repositories.PermissionRepository;
import com.cdnspringboot.cdn.repositories.PermissonFileUserRepository;
import com.cdnspringboot.cdn.repositories.UserRepository;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissonFileUserRepository permissionFileUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;

    // Operação de criar (Create)
    @PostMapping
    public ResponseEntity<Object> setPermission(@RequestParam("file_id") UUID fileId,
            @RequestParam("user_id") UUID userId, @RequestParam("permission_string") String permissionString) {
        try {
            char[] permissionCharArray = permissionString.toCharArray();
            if(permissionCharArray.length > 3 || permissionCharArray.length < 1)
                throw new Exception("A string de permissões deve ter no máximo 3 e no mínimo 1 caractere");
            
            Optional<FileDados> fileDadosOpt = fileRepository.findById(fileId);
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent() || !fileDadosOpt.isPresent()) {
                // Erro de arquivo ou usuario não encontrado
                throw new Exception("Erro ao buscar o arquivo ou o usuario");
            }
            PermissionFileUser permUser = new PermissionFileUser();
            FileDados fileDados = fileDadosOpt.get();
            User user = userOpt.get();

            permUser.setUser(user);
            permUser.setFile(fileDados);
            permUser.setAccessFromString(permissionString);
            permissionFileUserRepository.save(permUser);

            return new ResponseEntity<>("Ok", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro", HttpStatus.BAD_REQUEST);
        }
    }

}
