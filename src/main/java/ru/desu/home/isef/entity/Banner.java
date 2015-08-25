package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Banner {

	public enum Type {
		TEXT, IMAGE, MARQUEE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column
	String text;

	@Column
	String url;

	@Enumerated(EnumType.ORDINAL)
	Type type = Type.TEXT;

	@Temporal(TemporalType.TIMESTAMP)
	Date created;

	@PrePersist
	public void prePersist() {
		created = new Date();
	}

	@Lob
	byte[] image;
}
