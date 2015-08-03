package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class BecomeReferal {

	@Id
	Long id;

	@OneToOne(optional = false)
	Person person;

	@Column
	Integer cost;

	@Temporal(TemporalType.TIMESTAMP)
	Date created;

	@Temporal(TemporalType.TIMESTAMP)
	Date modified;

	@PrePersist
	public void prePersist() {
		created = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		modified = new Date();
	}
}
