package ru.desu.home.isef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.services.impl.LogDao;

public class App {

    @Autowired
    private LogDao logDao;
    
    public static void main(String[] args) {
        new App().go();
    }

    @Transactional
    private void go() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();//"classpath:WEB-INF/applicationContext.xml");
        context.start();
        long sizeByUser = context.getBean("LogDao", LogDao.class).hashCode();
        System.out.println(sizeByUser);
    }
}