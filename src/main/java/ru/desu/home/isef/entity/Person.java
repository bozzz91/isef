package ru.desu.home.isef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.annotations.Cascade;
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
    
    @Column(nullable = false, length = 255)
    private String userName;
    
    @Column(nullable = false, length = 255)
    private String userPassword;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role", nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Person inviter;
        
    @OneToMany(mappedBy = "inviter")
    @Fetch(FetchMode.JOIN)
    private Set<Person> referals = new HashSet<>();
    
    @Column()
    private byte[] photo;
    
    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastConnect;
    
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();
    
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
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();
    
    @Column(precision = 10, scale = 2, nullable = false)
    private Double cash = 0.0;
    
    @ManyToMany
    @JoinTable(name = "person_task", catalog = "public", 
        joinColumns =        { @JoinColumn(name = "id",     nullable = false, updatable = false) }, 
	inverseJoinColumns = { @JoinColumn(name = "taskId", nullable = false, updatable = false) })
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
