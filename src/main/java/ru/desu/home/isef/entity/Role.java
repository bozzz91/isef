package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Entity(name = "person_role") 
@Data @NoArgsConstructor @Log
public class Role implements Serializable {
    
    public enum Roles {
        ANONYMOUS, USER, ADMIN
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String roleName;
}
