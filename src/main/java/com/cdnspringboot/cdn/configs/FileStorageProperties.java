package com.cdnspringboot.cdn.configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * FileStorageProperties
 */
@Component
public class FileStorageProperties {

    public List<String> contingenciaFiles;
    private Map<Integer, String> servers;

    public Map<Integer, String> getServers() {
        return servers;
    }
    public void setServers(Map<Integer, String> servers) {
        this.servers = servers;
    }

    public String convertImageTypes(String contentType) {
        switch (contentType) {
            case "image/png":
                return ".png";
            default:
                return ".file";
        }
    }

    public void replicarFile(File file){
        
    }

    public static void main(String[] args) throws FileNotFoundException {
        

        // Verifique se o índice desejado está dentro dos limites
        // int indiceDesejado = 1; // Por exemplo, o segundo item (índice 1)
        // if (indiceDesejado >= 0 && indiceDesejado < itens.size()) {
        //     String item = itens.get(indiceDesejado);
        //     System.out.println("Item " + indiceDesejado + ": " + item);
        // } else {
        //     System.out.println("O índice está fora dos limites da lista.");
        // }
        //f.moveFile(new File("uploads/"));
    }
}