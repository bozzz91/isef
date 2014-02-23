package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;
import org.hibernate.annotations.Cascade;

@Entity
@Data @NoArgsConstructor @Log
@EqualsAndHashCode(exclude = {"owner","executors"})
@ToString(exclude = {"owner", "executors"})
public class Task implements Serializable {
    
    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long taskId;
    
    @ManyToOne
    Person owner;
    
    @ManyToOne
    @JoinColumn(name = "task_type", nullable = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    TaskType taskType;
    
    @Column(nullable = false)
    boolean done = false;
    
    @Column(nullable = false)
    boolean publish = false;
    
    @Column(nullable = false)
    Integer countComplete = 0;
    
    @Column(nullable = false)
    String subject;
    
    @Column(nullable = false)
    String description;
    
    //@ManyToMany(mappedBy = "executedTasks")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OneToMany(mappedBy = "pk.task")
    Set<PersonTask> executors = new HashSet<>();
    
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
