package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter @Setter
@NoArgsConstructor @Log
public class TaskType implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long taskTypeId;

    @Column(nullable = true, precision = 10, scale = 2)
    Double multiplier;
    
    @Column(nullable = true, precision = 10, scale = 2)
    Double gift;

	@Column(nullable = true, precision = 10, scale = 2)
	Double giftReferal;

	@Column(nullable = true, precision = 10, scale = 2)
	Double watchTime;
    
    @Column(nullable = false)
    String type;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	WatchTime config;

    @Column
    Boolean question;
    
    @Column
    Boolean surfing;

    @Column(unique = true)
    Integer order;
    
    //файл zul шаблона для окна создания таски
    @Column(nullable = false)
    String template = "";
    
    public boolean isQuestion() {
        return question != null ? question : false;
    }
    
    public boolean isSurfing() {
        return surfing != null ? surfing : false;
    }

	public Double getGift() {
		if (config != null) {
			return config.getGift();
		}
		return gift;
	}

	public Double getGiftReferal() {
		if (config != null) {
			return config.getGiftReferal();
		}
		return giftReferal;
	}

	public Double getMultiplier() {
		if (config != null) {
			return config.getMultiplier();
		}
		return multiplier;
	}

	public Double getWatchTime() {
		if (config != null) {
			return config.getTime();
		}
		return multiplier;
	}

    @Override
    public String toString() {
        return type;
    }
}
