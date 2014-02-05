package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Entity
@Data @NoArgsConstructor @Log
public class Task implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    Person owner;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private TaskType taskType;
    
    @Column
    private Boolean done;
    
    @ManyToMany(mappedBy = "executedTasks")
    private List<Person> executors = new ArrayList<>();
}
