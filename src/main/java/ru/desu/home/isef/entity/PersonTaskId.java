package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PersonTaskId implements Serializable {
    @ManyToOne
    Person person;
    @ManyToOne
    Task task;
    
    @Override
    public String toString() {
        return person.toString() + " - " + task.toString();
    }
}
