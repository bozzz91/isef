package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(nullable = false)
    String text;
    
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DELETE, CascadeType.REMOVE})
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "question", orphanRemoval = true)
    Set<Answer> answers = new HashSet<>();
    
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DELETE, CascadeType.REMOVE})
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "task_id")
    Task task;
    
    public Answer getCorrectAnswer() {
        for (Answer ans : answers) {
            if (ans.isCorrect()) {
                return ans;
            }
        }
        return null;
    }
}
