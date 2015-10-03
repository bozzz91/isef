package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Answer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

    @Column(nullable = false)
    String text;
    
    @Column(nullable = false)
    boolean correct = false;
    
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DELETE, CascadeType.REMOVE})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    Question question;
}
