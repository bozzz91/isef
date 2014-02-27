package ru.desu.home.isef.entity;

import java.io.Serializable;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
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
}
