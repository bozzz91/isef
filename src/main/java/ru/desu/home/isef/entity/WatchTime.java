package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter @Setter
@NoArgsConstructor
public class WatchTime implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

	@Column(nullable = false)
	double time = 20.0;

	@Column(nullable = false, precision = 10, scale = 2)
	Double multiplier = 0.0;

	@Column(nullable = false, precision = 10, scale = 2)
	Double gift;

	@Column(nullable = false, precision = 10, scale = 2)
	Double giftReferal;
}
