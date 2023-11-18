package com.cdnspringboot.cdn.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class PermissionFileUser {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileDados file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private boolean readAccess;

    @Column
    private boolean writeAccess;

    @Column
    private boolean deleteAccess;

    public PermissionFileUser(){

    }

    public void setFullAccess(){
        this.setAccessFromString("DRW"); //Full access
    }

    public void setAccessFromString(String permissions){
        char[] permissionCharArray = permissions.toCharArray();

        for (char c : permissionCharArray) {
            if(c == 'W')
                this.setWriteAccess(true);
            else if(c == 'R')
                this.setReadAccess(true);
            else if(c == 'D')
                this.setDeleteAccess(true);
        }
    }
}
