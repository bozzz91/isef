package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PersonWalletId implements Serializable {
    @ManyToOne
    Person person;
    @ManyToOne
    Wallet wallet;
    @Column
    String code;
    
    @Override
    public String toString() {
        return person.getId()+"-"+wallet.getId()+"-"+code;
    }
}
