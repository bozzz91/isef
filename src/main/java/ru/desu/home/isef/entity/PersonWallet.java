package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "person_wallet")
@AssociationOverrides({
    @AssociationOverride(name = "pk.person",
            joinColumns = @JoinColumn(name = "person_id")),
    @AssociationOverride(name = "pk.wallet",
            joinColumns = @JoinColumn(name = "wallet_id"))})
public class PersonWallet implements Serializable {
    
    @EmbeddedId
    PersonWalletId pk = new PersonWalletId();
    
    public Person getPerson() {
        return pk.person;
    }
    
    public Wallet getWallet() {
        return pk.wallet;
    }
    
    public String getCode() {
        return pk.code;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.pk);
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
        final PersonWallet other = (PersonWallet) obj;
        return Objects.equals(this.pk, other.pk);
    }
}
