package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long taskId;
    
    @ManyToOne
    Person owner;
    
    @ManyToOne
    @JoinColumn(name = "task_type", nullable = false)
    TaskType taskType;
    
    @Column(nullable = false)
    boolean done = false;
    
    @Column(nullable = false)
    String subject;
    
    @Column(nullable = false)
    String description;
    
    @ManyToMany(mappedBy = "executedTasks")
    //@OneToMany(mappedBy = "pk.task", cascade = CascadeType.ALL)
    List<Person> executors = new ArrayList<>();
    
    @Temporal(TemporalType.TIMESTAMP)
    Date modificationTime;
    
    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date();
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    Date creationTime;
    
    @PrePersist
    public void prePersist() {
        Date now = new Date();
        creationTime = now;
        modificationTime = now;
    }
}
