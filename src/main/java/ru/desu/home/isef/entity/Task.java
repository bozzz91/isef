package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Entity
@Data @NoArgsConstructor @Log
@EqualsAndHashCode(exclude = "owner")
public class Task implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;
    
    @ManyToOne
    Person owner;
    
    @ManyToOne
    private TaskType taskType;
    
    @Column
    boolean done = false;
    
    @Column
    private String subject;
    
    @Column
    private String description;
    
    @ManyToMany(mappedBy = "executedTasks")
    private List<Person> executors = new ArrayList<>();
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTime;
    
    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date();
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    
    @PrePersist
    public void prePersist() {
        Date now = new Date();
        creationTime = now;
        modificationTime = now;
    }
}
