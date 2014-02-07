package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
import ru.desu.home.isef.utils.PasswordUtil;

@Entity 
@Data @NoArgsConstructor @Log @EqualsAndHashCode(exclude = "referals")
public class Person implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    boolean active;
    
    @Column(nullable = false, length = 255)
    private String userName;
    
    @Column(nullable = false, length = 255)
    private String userPassword;
    
    @ManyToOne
    @JoinColumn(name = "role", nullable = false)
    private Role role;
    
    @ManyToOne
    @JoinColumn(name = "inviter")
    @Cascade(CascadeType.ALL)
    private Person inviter;
        
    @OneToMany(mappedBy = "inviter")
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    private Set<Person> referals = new HashSet<>();
    
    @Column()
    private byte[] photo;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTime;
    
    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date();
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    
    @PrePersist
    public void prePersist() {
        Date now = new Date();
        creationTime = now;
        modificationTime = now;
    }
    
    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastConnect;
    
    @Column(length = 100)
    private String fio;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100, nullable = false)
    private String referalLink;
    
    @Column(length = 100, unique = true)
    @IndexColumn(name = "person_email_idx")
    private String email;
    
    @OneToMany(mappedBy = "owner")
    @Cascade(CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();
    
    @Column(precision = 10, scale = 2, nullable = false)
    private Double cash = 0.0;
    
    @ManyToMany
    @JoinTable(name = "person_task", catalog = "public", 
        joinColumns =        { @JoinColumn(name = "id",     nullable = false) }, 
	inverseJoinColumns = { @JoinColumn(name = "taskId", nullable = false) })
    private Set<Task> executedTasks = new HashSet<>();
    
    public void addReferal(Person p) {
        if (!this.id.equals(p.id)) {
            referals.add(p);
        }
    }

    public String getHashPassword(String salt) {
        return PasswordUtil.asHex(userPassword, salt);
    }
    
    @Override
    public String toString() {
        return userName + " (" + email + ", id=" + id + ")";
    }
}
