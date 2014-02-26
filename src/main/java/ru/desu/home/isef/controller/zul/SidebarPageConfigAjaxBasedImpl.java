package ru.desu.home.isef.controller.zul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Component;

@Component(value = "sidebarPageConfig")
public class SidebarPageConfigAjaxBasedImpl implements SidebarPageConfig {

    HashMap<String, SidebarPage> pageMap = new LinkedHashMap<>();

    public SidebarPageConfigAjaxBasedImpl() {
        pageMap.put("Home",    new SidebarPage("Home",         "Домашняя страница ISef", "/imgs/site.png", "HOME_PAGE"));
        pageMap.put("Work",    new SidebarPage("Work",         "Рабочая область",        "/imgs/demo.png", "/work/home.zul"));

        pageMap.put("fn1",     new SidebarPage("fn1",          "Мой профиль",            "/imgs/fn.png",   "/work/profile/profile-mvc.zul"));
        
        pageMap.put("myTasks", new SidebarPage("myTasks",      "Мои задания",            "/imgs/doc.png",  "MY_TASKS"));
        pageMap.put("myTask1", new SidebarPage("myTaskDrafts", "Созданные",              "/imgs/doc.png",  "/work/mytasks/mytasks-mvc.zul"));
        pageMap.put("myTask2", new SidebarPage("myTaskOnExec", "На выполнении",          "/imgs/doc.png",  "/work/mytasks/mytasks-on-exec.zul"));
        pageMap.put("myTask3", new SidebarPage("myTaskDone",   "Готовые для проверки",   "/imgs/doc.png",  "/work/mytasks/mytasks-done.zul"));
        pageMap.put("myTask4", new SidebarPage("myTaskModer",  "На модерации",           "/imgs/doc.png",  "/work/mytasks/mytasks-on-moder.zul"));
        pageMap.put("fn5",     new SidebarPage("fn5",          "Задания для выполнения", "/imgs/doc.png",  "/work/todolist/todolist-mvc.zul"));
    }

    @Override
    public List<SidebarPage> getPages() {
        return new ArrayList<>(pageMap.values());
    }

    @Override
    public SidebarPage getPage(String name) {
        return pageMap.get(name);
    }

}
