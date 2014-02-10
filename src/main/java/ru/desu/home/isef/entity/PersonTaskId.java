package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Embeddable
public class PersonTaskId implements Serializable {
    
    @ManyToOne
    Person person;
    
    @ManyToOne
    Task task;
}
