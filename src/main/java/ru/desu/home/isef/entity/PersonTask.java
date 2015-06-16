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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "person_task")
@AssociationOverrides({
    @AssociationOverride(name = "pk.person",
            joinColumns = @JoinColumn(name = "person_id")),
    @AssociationOverride(name = "pk.task",
            joinColumns = @JoinColumn(name = "task_id"))})
public class PersonTask implements Serializable {

    @EmbeddedId
    PersonTaskId pk = new PersonTaskId();

    public Person getPerson() {
        return pk.person;
    }

    public Task getTask() {
        return pk.task;
    }

    @Temporal(TemporalType.TIMESTAMP)
    Date added;
    
    @Temporal(TemporalType.TIMESTAMP)
    Date executed;

    @Column
    String ip;

    @Column
    String confirm;
    
    @Column
    String remark;
    
    /**
     * 0 - не проверено автором
     * 1 - проверено автором успешно и выдана монетка
     * 2 - отправлено на доработку с замечанием
     */
    @Column(nullable = false)
    int status;
}
