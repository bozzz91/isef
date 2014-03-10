package ru.desu.home.isef;

import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.extern.java.Log;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.PersonTaskId;
import ru.desu.home.isef.entity.Role;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.TaskTypeService;

@Log
public class App {

    private static final String admin = "admin@admin.ru";
    private static final String user1 = "user1@user1.ru";
    private static final String user2 = "user2@user2.ru";

    private static PersonService pServ;
    private static TaskService tServ;
    private static TaskTypeService tyServ;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/applicationContext.xml");
        context.start();

        pServ = context.getBean("personService", PersonService.class);
        tServ = context.getBean("taskService", TaskService.class);
        tyServ = context.getBean("taskTypeService", TaskTypeService.class);

        App bean = context.getBean("app", App.class);
        bean.test2();
    }

    public void createTaskByAdmin() {
        Person p1 = pServ.find(admin);
        p1.setCash(30d);
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
        //pAdmin.addReferal(p1);

        Person saved1 = pServ.save(p1);

        p1 = new Person();
        p1.setEmail(user2);
        p1.setFio("User 2");
        p1.setRole(role);
        p1.setUserName("user2");
        p1.setUserPassword("user2");
        p1.setInviter(saved1);
        p1.setReferalLink("ref2");
        //old.addReferal(p1);

        Person saved2 = pServ.save(p1);
        Person savedAdmin = pServ.save(pAdmin);

        Set<Person> referals1 = savedAdmin.getReferals();
        Set<Person> referals2 = saved1.getReferals();
        Set<Person> referals3 = saved2.getReferals();
        //savedAdmin.getExecutedTasks();

        log.info("----1");
        for (Person p : referals1) {
            log.info(p.toString());
        }
        log.info("\n----2");
        for (Person p : referals2) {
            log.info(p.toString());
        }
        log.info("\n----3");
        for (Person p : referals3) {
            log.info(p.toString());
        }
    }

    @Transactional
    public void test2() {
        Person p1 = pServ.find(admin);

        Task t1 = tServ.getTask(3l);
        
        PersonTask pt = new PersonTask();
        pt.setAdded(new Date());
        pt.setIp("127.0.0.1");
        pt.setPk(new PersonTaskId(p1, t1));
        
        //p1.getExecutedTasks().add(pt);

        pServ.save(p1);
        Set<PersonTask> executors = tServ.getTask(3l).getExecutors();
        System.out.println(executors.size());
        //System.out.println(executors.iterator().next().getPerson());
    }

    @Transactional
    public void test3() {
        Person pAdmin = pServ.find(admin);
        for (Person p : pAdmin.getReferals()) {
            log.info(p.toString());
        }
    }

    @Transactional
    public void test4() {
        Person pAdmin = pServ.find(admin);
        List<Task> tasksForWork = tServ.getTasksForWork(pAdmin);

        for (Task t : tasksForWork) {
            log.info(t.toString());
        }
    }
    
    @Transactional
    public void test5() {
        Status s = new Status();
    }
}
