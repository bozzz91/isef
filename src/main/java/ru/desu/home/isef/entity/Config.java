package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Config {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	/**
	 * 0 - период задания
	 * 1 - фильтр по ip
	 * 2 - кому показывать
	 * 3 - показывать по полу
	 * null - нет группы, отдельный параметр
	 */
	@Column
	Integer groupId;

	@Column
	String name;

	@Column
	String stringValue;

	@Column
	Integer intValue;

	@Column
	Double doubleValue;

	@Column
	Integer orderNumber;
}
