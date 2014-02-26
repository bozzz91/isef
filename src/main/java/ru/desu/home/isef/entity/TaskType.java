package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Entity
@Data @NoArgsConstructor @Log
public class TaskType implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long taskTypeId;

    @Column(nullable = false, precision = 10, scale = 2)
    Double multiplier;
    
    @Column(nullable = false, precision = 10, scale = 2)
    Double gift;
    
    @Column(nullable = false, length = 1)
    String type = "C";
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case "C": sb.append("Простой"); break;
            case "S": sb.append("Поисковый"); break;
        }
        return sb.toString();
    }
}
