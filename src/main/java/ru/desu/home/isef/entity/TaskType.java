package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

@Entity
@Getter @Setter
@NoArgsConstructor @Log
public class TaskType implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long taskTypeId;

    @Column(nullable = false, precision = 10, scale = 2)
    Double multiplier;
    
    @Column(nullable = false, precision = 10, scale = 2)
    Double gift;
    
    @Column(nullable = false, precision = 10, scale = 2)
    Double giftReferal;
    
    @Column(nullable = false)
    String type;
    
    @Override
    public String toString() {
        return type;
    }
}
