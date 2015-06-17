package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Answer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(nullable = false)
    String text;
    
    @Column(nullable = false)
    boolean correct = false;
    
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    Question question;
}
