package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "person_task")
@AssociationOverrides({
    @AssociationOverride(name = "pk.person",
            joinColumns = @JoinColumn(name = "person_id")),
    @AssociationOverride(name = "pk.task",
            joinColumns = @JoinColumn(name = "task_id"))})
public class PersonTasks implements Serializable {

    @EmbeddedId
    private PersonTaskId pk = new PersonTaskId();

    public void setPersonTaskId(PersonTaskId id) {
        this.pk = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    Date added = new Date();

    @PrePersist
    public void prePersist() {
        added = new Date();
    }

    @Transient
    public Person getPerson() {
        return getPk().getPerson();
    }

    @Transient
    public Task getTask() {
        return getPk().getTask();
    }
    
    public void setPerson(Person p) {
        getPk().setPerson(p);
    }
    
    public void setTask(Task t) {
        getPk().setTask(t);
    }
}
