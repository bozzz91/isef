package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Currency implements Serializable {
    @Id @Getter @Setter
    private Long id;

    @Column @Getter @Setter
    double currency;
}
