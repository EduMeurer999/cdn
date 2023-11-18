package com.cdnspringboot.cdn.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

import javax.swing.text.html.Option;

import com.cdnspringboot.cdn.models.FileDados;
import com.cdnspringboot.cdn.models.Permission;
import com.cdnspringboot.cdn.models.PermissionFileUser;
import com.cdnspringboot.cdn.models.User;
import com.cdnspringboot.cdn.repositories.FileRepository;
import com.cdnspringboot.cdn.repositories.PermissonFileUserRepository;
import com.cdnspringboot.cdn.repositories.UserRepository;
import com.cdnspringboot.cdn.configs.FileStorageProperties;

import jakarta.el.ELException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissonFileUserRepository permissonFileUserRepository;

    // @RequestMapping(value = "/file", method = RequestMethod.GET)
    // public List<FileDados> Get() {
    // return fileRepository.findAll();
    // }

    @GetMapping("{id}")
    public ResponseEntity GetOne(@PathVariable UUID id, HttpServletRequest request) throws IOException {
        try {
            Optional<FileDados> fileDadosOpt = fileRepository.findById(id);
            UUID idUser = (UUID) request.getAttribute("idUser");

            if (!fileDadosOpt.isPresent()) {
                // Exceção para erro aqui.
                throw new Exception("O arquivo não existe");
            }

            FileDados fileDados = fileDadosOpt.get();
            Optional<PermissionFileUser> permFileOpt = permissonFileUserRepository.findByFileIdAndUserId(fileDados.getId(),
                    idUser);
            if (!permFileOpt.isPresent()) {
                throw new Exception("O usuário logado não tem permissão para acessar esse arquivo.");
            }

            PermissionFileUser permFile = permFileOpt.get();

            if(!permFile.isReadAccess()){
                throw new Exception("O usuário logado não tem permissão de leitura para esse arquivo.");
            }

            String base64 = fileDados.getFileBase64();
            byte[] fileBytes = Base64.getDecoder().decode(base64);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", id + "" + fileDados.getExtensao());
            headers.setContentLength(fileBytes.length);
            
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
             e.printStackTrace();
            return new ResponseEntity<>("Erro: "+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<String> Post(@RequestBody MultipartFile file, HttpServletRequest request) {

        // Obter o nome de usuário
        try {
            UUID idUser = (UUID) request.getAttribute("idUser");
            String nomeArquivoTemp = "";
            System.out.println(file.getOriginalFilename());
            String extensao = "";
            String nomeCompleto = "";
            String nomeFileEnviado = file.getOriginalFilename();
            String base64File = "";
            String fileId = "";
            if (!file.isEmpty()) {
                // Exemplo de salvamento no disco:
                nomeArquivoTemp = UUID.randomUUID().toString();
                int indexExt = nomeFileEnviado.lastIndexOf(".");
                if (indexExt > 0) {
                    extensao = nomeFileEnviado.substring(indexExt);
                }
                if (extensao.isEmpty()) {
                    nomeCompleto = nomeArquivoTemp;
                } else {
                    nomeCompleto = nomeArquivoTemp.concat(extensao);
                }

                File localFile = new File("uploads/" + nomeCompleto);

                FileOutputStream fileOutputStream = new FileOutputStream(localFile);
                fileOutputStream.write(file.getBytes());
                fileOutputStream.close();
                

                try (FileInputStream fileInputStream = new FileInputStream(localFile)) {
                    byte[] bytes = new byte[(int) localFile.length()];
                    fileInputStream.read(bytes);
                    base64File = Base64.getEncoder().encodeToString(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                localFile.delete();
                Optional<User> userTempOpt = userRepository.findById(idUser);

                User userTemp = userTempOpt.get();

                FileDados fileD = new FileDados(nomeFileEnviado.substring(0, indexExt), base64File, extensao,
                        userTemp);
                fileD = fileRepository.save(fileD);
                fileId = fileD.getId().toString();

                PermissionFileUser permissionFileUser = new PermissionFileUser();
                permissionFileUser.setFile(fileD);
                permissionFileUser.setUser(userTemp);
                permissionFileUser.setFullAccess();

                permissonFileUserRepository.save(permissionFileUser);
            }
            return new ResponseEntity<>("" + fileId.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erro", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<String> Put(@PathVariable UUID id, @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        UUID idUser = (UUID) request.getAttribute("idUser");
        String idFile = "";

        try {
            String nomeArquivo = "";
            String extensao = "";
            String nomeCompleto = "";
            if (!file.isEmpty()) {
                Optional<FileDados> fileDadosOpt = fileRepository.findById(id);
                if (!fileDadosOpt.isPresent()) {
                    // Errp de od não encontrado.
                }
                FileDados fileDados = fileDadosOpt.get();

                Optional<PermissionFileUser> permFileOpt = permissonFileUserRepository
                        .findByFileIdAndUserId(fileDados.getId(), idUser);
                if (!permFileOpt.isPresent()) {
                    throw new Exception("O usuário logado não tem permisão de escrita");
                }
                PermissionFileUser permFile = permFileOpt.get();
                if (!permFile.isWriteAccess()) {
                    throw new Exception("O usuário logado não tem permisão de escrita");
                }

                String nomeFileEnviado = file.getOriginalFilename();

                if (!file.isEmpty()) {
                    // Exemplo de salvamento no disco:
                    nomeArquivo = UUID.randomUUID().toString();
                    int indexExt = nomeFileEnviado.lastIndexOf(".");

                    if (indexExt > 0) {
                        extensao = nomeFileEnviado.substring(indexExt);
                    }

                    if (extensao.isEmpty()) {
                        nomeCompleto = nomeArquivo;
                    } else {
                        nomeCompleto = nomeArquivo.concat(extensao);
                    }
                }

                File localFile = new File("uploads/" + nomeCompleto);

                FileOutputStream fileOutputStream = new FileOutputStream(localFile);
                fileOutputStream.write(file.getBytes());
                fileOutputStream.close();

                String base64File = "";
                try (FileInputStream fileInputStream = new FileInputStream(localFile)) {
                    byte[] bytes = new byte[(int) localFile.length()];
                    fileInputStream.read(bytes);
                    base64File = Base64.getEncoder().encodeToString(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                localFile.delete();
                
                fileDados.setNome(nomeFileEnviado.substring(0, nomeFileEnviado.lastIndexOf(".")));
                fileDados.setExtensao(extensao);
                fileDados.setFileBase64(base64File);

                idFile = fileRepository.save(fileDados).getId().toString();
            }
            
            return new ResponseEntity<String>("" + idFile, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Erro: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> Delete(@PathVariable UUID id, HttpServletRequest request) {
        try {
            UUID idUser = (UUID) request.getAttribute("idUser");
            Optional<FileDados> fileOpt = fileRepository.findById(id);
            if(!fileOpt.isPresent())
                throw new Exception("Arquivo não encontrado!");

            FileDados file = fileOpt.get();
            Optional<PermissionFileUser> permFileUserOpt = permissonFileUserRepository.findByFileIdAndUserId(file.getId(), idUser);
            if(!permFileUserOpt.isPresent()){
                throw new Exception("O usuário logado não tem permissão para excluir esse arquivo");
            }
            PermissionFileUser permFileUser = permFileUserOpt.get();
            if(!permFileUser.isDeleteAccess()){
                throw new Exception("O usuário logado não tem permissão para excluir esse arquivo");
            }

            Optional<PermissionFileUser[]> permFileOpt = permissonFileUserRepository.findByFileId(file.getId());
            if (!permFileOpt.isPresent()) {
                throw new Exception("Erro ao excluir arquivo!");
            }

            List<UUID> ids =  new ArrayList<>();
            for (PermissionFileUser permFile : permFileOpt.get()) {
                ids.add(permFile.getId());
            }

            permissonFileUserRepository.deleteAllById(ids);
            fileRepository.delete(file);
            
            return new ResponseEntity<String>("Ok", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Erro: "+e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}
