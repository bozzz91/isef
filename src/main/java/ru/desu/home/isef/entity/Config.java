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
	 * 1 - период задания
	 * 2 - фильтр по ip
	 * 3 - кому показывать
	 * 4 - показывать по полу
	 * 0 - нет группы, отдельный параметр
	 */
	@Column
	Integer groupId;

	@Column
	String name;

	@Column
	String value;

	@Column
	Integer orderNumber;
}
