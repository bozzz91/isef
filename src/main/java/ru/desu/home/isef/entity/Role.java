package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.persistence.*;
import java.io.Serializable;

@Getter @Setter
@Entity(name = "person_role") 
@NoArgsConstructor @Log
public class Role implements Serializable {
    
    @SuppressWarnings("unused")
	public enum Roles {
        ANONYMOUS, USER, ADMIN
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    
    @Column(nullable = false, length = 50)
    String roleName;
}
