package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Data @NoArgsConstructor @Log
public class Task implements Serializable {
    
    @Id
    @Column(name = "task_id")
    @SequenceGenerator(name = "SeqTask", sequenceName = "SEQ_TASK", allocationSize = 1, initialValue = 33 )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SeqTask")
    Long taskId;
    
    //кто создал задание
    @ManyToOne
    @JoinColumn(updatable = false)
    Person owner;
    
    //тип задания, описание в классе типа
    @ManyToOne
    @JoinColumn(name = "task_type", nullable = false, updatable = false)
    @Cascade(CascadeType.SAVE_UPDATE)
    TaskType taskType;

    //статус, описание в классе статуса
    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    @Cascade(CascadeType.SAVE_UPDATE)
    Status status = Status._1_DRAFT;
    
    //сколько человек выполнило его (связано триггером с табл. person_task)
    //когда туда попадает запись с данной задачей у этйо задачи счетчик +1
    @Column(nullable = false)
    Integer countComplete = 0;
    
    //сколько надо сделать кликов
    @Column(nullable = false)
    Integer count;
    
    //стоимость задания
    @Column(nullable = false)
    Double cost;
    
    //название задания
    @Column(nullable = false)
    String subject;
    
    //ссылка куда кликать для задания
    @Column
    String link;
    
    //описание задания
    @Column(nullable = false)
    String description;
    
    //что нужно для подтверждения задания
    @Column
    String confirmation;
    
    //коммент модератора\админа
    @Column
    String remark;
    
    //список людей которые выполнили данное задание
    //@ManyToMany(mappedBy = "executedTasks")
    @OneToMany(mappedBy = "pk.task")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    Set<PersonTask> executors = new HashSet<>();
    
    //когда последний раз изменено
    @Temporal(TemporalType.TIMESTAMP)
    Date modificationTime;
    
    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date();
    }
    
    //когда создали задание
    @Temporal(TemporalType.TIMESTAMP)
    Date creationTime;
    
    @PrePersist
    public void prePersist() {
        Date now = new Date();
        creationTime = now;
        modificationTime = now;
    }
    
    public Integer incCountComplete() {
        return this.countComplete+1;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.taskId);
        hash = 97 * hash + Objects.hashCode(this.cost);
        hash = 97 * hash + Objects.hashCode(this.subject);
        hash = 97 * hash + Objects.hashCode(this.link);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if (!Objects.equals(this.taskId, other.taskId)) {
            return false;
        }
        if (!Objects.equals(this.cost, other.cost)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        return Objects.equals(this.link, other.link);
    }
    
    @Override
    public String toString() {
        return taskId+"-"+subject;
    }
}
