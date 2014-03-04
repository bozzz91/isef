package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.IndexColumn;
import ru.desu.home.isef.utils.DecodeUtil;

@Entity 
@Data @NoArgsConstructor @Log
public class Person implements Serializable {
    
    @Id
    @Column(name = "person_id")
    @SequenceGenerator(name = "SeqPerson", sequenceName = "SEQ_PERSON", allocationSize = 1, initialValue = 6 )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SeqPerson")
    Long id;
    
    @Column(nullable = false)
    boolean active = false;
    
    @Column(nullable = false)
    boolean webmaster = false;
    
    @Column(nullable = false, length = 255)
    String userName;
    
    @Column(nullable = false, length = 255)
    String userPassword;
    
    @Column(nullable = false, length = 255)
    String userPasswordOrigin;
    
    @ManyToOne
    @JoinColumn(name = "role", nullable = false)
    @Cascade(CascadeType.SAVE_UPDATE)
    Role role;
    
    @ManyToOne
    @JoinColumn(name = "inviter", updatable = false)
    Person inviter;
        
    @OneToMany(mappedBy = "inviter")
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.SAVE_UPDATE)
    Set<Person> referals = new HashSet<>();
    
    @Column
    byte[] photo;
    
    @Temporal(TemporalType.TIMESTAMP)
    Date modificationTime;
    
    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date();
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    Date creationTime;
    
    @PrePersist
    public void prePersist() {
        Date now = new Date();
        creationTime = now;
        modificationTime = now;
    }
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date lastConnect;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date birthday;
    
    @Column(length = 100)
    String fio;
    
    @Column(length = 20)
    String phone;
    
    @Column(length = 100, nullable = false)
    String referalLink;
    
    @Column(length = 100, unique = true, nullable = false, updatable = false)
    @IndexColumn(name = "person_email_idx")
    String email;
    
    @OneToMany(mappedBy = "owner")
    @Cascade(CascadeType.SAVE_UPDATE)
    Set<Task> tasks = new HashSet<>();
    
    @Column(precision = 10, scale = 2, nullable = false)
    Double cash = 0.0;

    @OneToMany(mappedBy = "pk.person")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    private Set<PersonTask> executedTasks = new HashSet<>();
    
    @OneToMany(mappedBy = "pk.person")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    private Set<PersonWallet> wallets = new HashSet<>();
    
    @OneToMany(mappedBy = "payOwner")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<Payment> payments = new ArrayList<>();
    
    public void addReferal(Person p) {
        if (!this.id.equals(p.id)) {
            referals.add(p);
        }
    }

    public String getHashPassword() {
        return DecodeUtil.decodePass(userPassword);
    }
    
    @Override
    public String toString() {
        return userName + " (" + email + ", id=" + id + ")";
    }

    public void addCash(double cost) {
        this.cash += cost;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.id);
        hash = 59 * hash + (this.active ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.userName);
        hash = 59 * hash + Objects.hashCode(this.creationTime);
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
        final Person other = (Person) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.active != other.active) {
            return false;
        }
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        return Objects.equals(this.creationTime, other.creationTime);
    }

}
