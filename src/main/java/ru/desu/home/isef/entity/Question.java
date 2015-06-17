package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(nullable = false)
    String text;
    
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "question")
    Set<Answer> answers = new HashSet<>();
    
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    @OneToOne(fetch = FetchType.EAGER, optional = false)
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
