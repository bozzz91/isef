package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Wallet implements Serializable {
    
    @Id
    Long id;
    
    @Column
    String name;
    
    @Column
    String regex;
}
