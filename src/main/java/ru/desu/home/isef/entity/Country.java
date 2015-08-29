package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column(unique = true)
	String code;

	@Column
	String name;
}
