package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Log
@Entity
@Getter @Setter
@NoArgsConstructor
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

	@Column
	Boolean vip = false;

	@Column
	Boolean activeWindow = false;

	@Column
	Integer showTo = 0;

	@Column
	Integer uniqueIp = 0;

	@Column
	String sex = "U";

	@Column
	Integer period = 24; //hours

	//допускать только тех кто зарегистрирован в системе N дней назад
	@Column
	Integer registrationDayAgo = 0; //days, 0 - don't check

	@Column
	Double watchTime; //sec

	@ManyToMany(fetch = FetchType.EAGER)
	Set<Country> countries = new HashSet<>();
    
    //описание задания
    @Column(nullable = false, length = 1000)
    String description;
    
    //что нужно для подтверждения задания
    @Column
    String confirmation;
    
    //коммент модератора\админа
    @Column
    String remark;
    
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DELETE, CascadeType.REMOVE})
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "task", orphanRemoval = true)
    Set<Question> questions = new HashSet<>();
    
    //список людей которые выполнили данное задание
    @OneToMany(mappedBy = "pk.task")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DELETE, CascadeType.REMOVE})
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

	public Double getWatchTime() {
		if (watchTime != null) {
			return watchTime;
		}
		return taskType.getWatchTime();
	}
    
    public Integer incCountComplete() {
        return this.countComplete+1;
    }

    @Override
    public String toString() {
        return taskId+"-"+subject;
    }
}
