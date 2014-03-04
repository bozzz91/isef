package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "person_task")
@AssociationOverrides({
    @AssociationOverride(name = "pk.person",
            joinColumns = @JoinColumn(name = "person_id")),
    @AssociationOverride(name = "pk.task",
            joinColumns = @JoinColumn(name = "task_id"))})
public class PersonTask implements Serializable {

    @EmbeddedId
    PersonTaskId pk = new PersonTaskId();

    @Temporal(TemporalType.TIMESTAMP)
    Date added;

    @Column
    String ip;
    
    @Column
    String confirm;
}
