package ru.desu.home.isef;

import java.util.List;
import lombok.extern.java.Log;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Role;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.entity.TaskType;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.TaskTypeService;

@Log
@Transactional
public class App {
    private static final String admin = "admin@admin.ru";
    private static final String user1 = "user1@user1.ru";
    private static final String user2 = "user2@user2.ru";
    
    private static PersonService pServ;
    private static TaskService tServ;
    private static TaskTypeService tyServ;
    
    public static void main(String[] args) {
        new App().go();
    }

    @Transactional
    public void go() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/applicationContext.xml");
        context.start();
        
        pServ = context.getBean("PersonService", PersonService.class);
        tServ = context.getBean("TaskService", TaskService.class);
        tyServ = context.getBean("TaskTypeService", TaskTypeService.class);
        
        createPersonsAndRefs();
    }
    
    public void createTaskByAdmin() {
        Person p1 = pServ.find(admin);
        p1.setCash(30d);
        
        TaskType type = tyServ.findByCost(10d).get(0);

        if (p1.getCash()-type.getCost() > 0) {
            Task t1 = new Task();
            t1.setOwner(p1);
            t1.setTaskType(type);
            p1.setCash(p1.getCash()-type.getCost());
            pServ.update(p1);
            tServ.create(t1);
        }
    }
    
    @Transactional
    public void createPersonsAndRefs() {
        Person pAdmin = pServ.find(admin);
        
        Person p1 = new Person();
        p1.setEmail(user1);
        p1.setFio("User 1");
        Role role = new Role();
        role.setId(2l);
        role.setRoleName("user");
        p1.setRole(role);
        p1.setUserName("user1");
        p1.setUserPassword("user1");
        p1.setInviter(pAdmin);
        p1.setReferalLink("ref1");
        
        pServ.add(p1);
        
        Person old = p1;
        p1 = new Person();
        p1.setEmail(user2);
        p1.setFio("User 2");
        p1.setRole(role);
        p1.setUserName("user2");
        p1.setUserPassword("user2");
        p1.setInviter(old);
        p1.setReferalLink("ref2");
        
        pServ.add(p1);
        
        List<Person> referals1 = pServ.find(admin).getReferals();
        List<Person> referals2 = pServ.find(user1).getReferals();
        List<Person> referals3 = pServ.find(user2).getReferals();
        pServ.find(admin).getExecutedTasks();
        
        log.info("----1");
        for (Person p : referals1)
            log.info(p.toString());
        log.info("\n----2");
        for (Person p : referals2)
            log.info(p.toString());
        log.info("\n----3");
        for (Person p : referals3)
            log.info(p.toString());
    }
}