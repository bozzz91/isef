package ru.desu.home.isef.controller.zul;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Component(value = "sidebarPageConfig")
public class SidebarPageConfigAjaxBasedImpl implements SidebarPageConfig {

    HashMap<String, SidebarPage> pageMap = new LinkedHashMap<>();

    public SidebarPageConfigAjaxBasedImpl() {
        pageMap.put("Home",    new SidebarPage("Home",         "Домашняя страница ISef", "/imgs/site.png", "HOME_PAGE"));
        pageMap.put("Work",    new SidebarPage("Work",         "Рабочая область",        "/imgs/demo.png", "/work/home.zul"));

        pageMap.put("profile", new SidebarPage("profile",      "Мой профиль",            "/imgs/fn.png",   "MY_PROFILE"));
        pageMap.put("profile1",new SidebarPage("profile1",     "Настройки",              "/imgs/fn.png",   "/work/profile/profile.zul"));
        pageMap.put("profile2",new SidebarPage("profile2",     "Добавить баннеры",       "/imgs/fn.png",   "/work/profile/createBanners.zul"));
        
        pageMap.put("todo",    new SidebarPage("todo",         "Задания для выполнения", "/imgs/doc.png",  "/work/todolist/todolist.zul"));
        
        pageMap.put("myTasks", new SidebarPage("myTasks",      "Мои задания",            "/imgs/doc.png",  "MY_TASKS"));
        pageMap.put("myTask1", new SidebarPage("myTaskDrafts", "Создать задание",        "/imgs/doc.png",  "/work/mytasks/mytasks-on-draft.zul"));
		pageMap.put("myTask2", new SidebarPage("myTaskOnModer","На модерации",           "/imgs/doc.png",  "/work/mytasks/mytasks-on-moder.zul"));
        pageMap.put("myTask3", new SidebarPage("myTaskOnExec", "На выполнении",          "/imgs/doc.png",  "/work/mytasks/mytasks-on-exec.zul"));
        pageMap.put("myTask4", new SidebarPage("myTaskDone",   "Выполненные",            "/imgs/doc.png",  "/work/mytasks/mytasks-on-done.zul"));
        
        pageMap.put("admin",   new SidebarPage("admin",        "Админка",                "/imgs/doc.png",  "ADMIN"));
		pageMap.put("admin1",  new SidebarPage("adminAllTask", "Все задания",            "/imgs/doc.png",  "/work/admin/allTasks.zul"));
		pageMap.put("admin2",  new SidebarPage("adminModer",   "На модерации",           "/imgs/doc.png",  "/work/admin/tasks-on-moder.zul"));
        pageMap.put("admin3",  new SidebarPage("adminPay",     "Выплаты",                "/imgs/doc.png",  "/work/admin/payments.zul"));
        pageMap.put("admin4",  new SidebarPage("adminPersons", "Пользователи",           "/imgs/doc.png",  "/work/admin/persons.zul"));
        //pageMap.put("admin5",  new SidebarPage("adminUtils",   "Утилиты",                "/imgs/doc.png",  "/work/admin/utils.zul"));
		pageMap.put("admin6",  new SidebarPage("adminBlack",   "Черный список",          "/imgs/doc.png",  "/work/admin/blackList.zul"));
		pageMap.put("admin7",  new SidebarPage("adminConfig",  "Настройки",              "/imgs/doc.png",  "CONFIG"));
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
