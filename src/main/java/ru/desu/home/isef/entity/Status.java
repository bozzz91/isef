package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Status implements Serializable {

    /**
     * Статусы пока что 5 штук:
     * 1 - только создано (черновик)
     * 2 - на модерации
     * 3 - опубликовано модератором (на выполнении)
     * 4 - выполнено
     * 5 - корзина
     * @param state integer id of state in database
     */
    private Status(int state) {
        this.id = state;
    }
    
    public static Status _1_DRAFT   = new Status(1);
    public static Status _2_MODER   = new Status(2);
    public static Status _3_PUBLISH = new Status(3);
    public static Status _4_DONE    = new Status(4);
    public static Status _5_TRASH   = new Status(5);
    
    @Id
    int id;

    @Column
    String name;
}
