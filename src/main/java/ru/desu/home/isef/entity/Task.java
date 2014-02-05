package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Data @NoArgsConstructor @Log @EqualsAndHashCode(exclude = "owner")
public class Task implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    Person owner;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private TaskType taskType;
    
    @Column
    private Boolean done;
    
    @ManyToMany(mappedBy = "executedTasks")
    @Cascade(CascadeType.ALL)
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
