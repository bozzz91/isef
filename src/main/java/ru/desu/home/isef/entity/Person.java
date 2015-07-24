package ru.desu.home.isef.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import ru.desu.home.isef.utils.DecodeUtil;
import ru.desu.home.isef.utils.FormatUtil;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;

@Log
@Entity
@Getter @Setter
@NoArgsConstructor
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
    
    @Lob
	@Basic(fetch = FetchType.LAZY)
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
    
    @Column(nullable = false, length = 1)
    String sex = "U";
    
    @Column(length = 100, nullable = false)
    String referalLink;
    
    @Column(length = 100, unique = true, nullable = false, updatable = false)
    @IndexColumn(name = "person_email_idx")
    String email;
    
    @OneToMany(mappedBy = "owner")
    @Cascade(CascadeType.SAVE_UPDATE)
    Set<Task> tasks = new HashSet<>();
    
    @Column(precision = 10, scale = 2, nullable = false)
    double cash = 0.0;
    
    public double getCash() {
        return FormatUtil.roundDouble(cash);
    }
    
    @Column(precision = 10, scale = 2)
    Double reserv = 0.0;
    
    @Column(precision = 10, scale = 2)
    double rating = 0.0;
    
    public double getRating() {
        return FormatUtil.roundDouble(rating);
    }

    /*@OneToMany(mappedBy = "pk.person")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<PersonTask> executedTasks = new ArrayList<>();*/
    
    @OneToMany(mappedBy = "pk.person")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<PersonWallet> wallets = new ArrayList<>();
    
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

    public void addCash(double gift, double exp) {
        this.cash += gift;
        this.rating += exp;
    }
    
    public void addCash(double gift) {
        addCash(gift, 0.0);
    }
    
    public void addRating(double rate) {
        this.rating += rate;
    }
}