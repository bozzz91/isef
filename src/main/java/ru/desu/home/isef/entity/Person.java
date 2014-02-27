package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(exclude = {"referals","executedTasks","role","inviter","tasks"})
public class Person implements Serializable {
    
    @Id
    @Column(name = "person_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    
    @Column(nullable = false)
    boolean active = false;
    
    @Column(nullable = false)
    boolean webmaster = false;
    
    @Column(nullable = false, length = 255)
    String userName;
    
    @Column(nullable = false, length = 255)
    String userPassword;
    
    @ManyToOne
    @JoinColumn(name = "role", nullable = false)
    @Cascade(CascadeType.SAVE_UPDATE)
    Role role;
    
    @ManyToOne
    @JoinColumn(name = "inviter")
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
}
