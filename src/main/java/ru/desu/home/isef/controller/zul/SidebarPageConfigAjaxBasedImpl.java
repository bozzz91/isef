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
        pageMap.put("Home", new SidebarPage("Home", "Home Page", "/imgs/site.png", "HOME_PAGE"));
        pageMap.put("Work", new SidebarPage("Work", "Work Page", "/imgs/demo.png", "/work/home.zul"));

        pageMap.put("fn1", new SidebarPage("fn1", "Profile (MVC)", "/imgs/fn.png", "/work/profile/profile-mvc.zul"));
        pageMap.put("fn2", new SidebarPage("fn2", "My Task list (MVC)", "/imgs/doc.png", "/work/mytasks/mytasks-mvc.zul"));
        pageMap.put("fn3", new SidebarPage("fn3", "Todo List (MVC)", "/imgs/doc.png", "/work/todolist/todolist-mvc.zul"));
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
