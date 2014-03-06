package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.Objects;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.person.email);
        hash = 97 * hash + Objects.hashCode(this.wallet.id);
        hash = 97 * hash + Objects.hashCode(this.code);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersonWalletId other = (PersonWalletId) obj;
        if (!Objects.equals(this.person.email, other.person.email)) {
            return false;
        }
        if (!Objects.equals(this.wallet.id, other.wallet.id)) {
            return false;
        }
        return Objects.equals(this.code, other.code);
    }
    
    
}
