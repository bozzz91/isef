package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Banner {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column
	String text;

	@Column
	String url;

	@Lob
	byte[] image;
}
