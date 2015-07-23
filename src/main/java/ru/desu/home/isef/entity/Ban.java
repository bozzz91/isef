package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Ban {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column
	String url;

	@Temporal(TemporalType.TIMESTAMP)
	Date created;

	@PrePersist
	public void prePersist() {
		created = new Date();
	}
}
