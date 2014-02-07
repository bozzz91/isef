package ru.desu.home.isef.controller.zul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SidebarPageConfigAjaxBasedImpl implements SidebarPageConfig {

    HashMap<String, SidebarPage> pageMap = new LinkedHashMap<>();

    public SidebarPageConfigAjaxBasedImpl() {
        pageMap.put("zk", new SidebarPage("zk", "www.zkoss.org", "/imgs/site.png", "http://www.zkoss.org/"));
        pageMap.put("demo", new SidebarPage("demo", "ZK Demo", "/imgs/demo.png", "http://www.zkoss.org/zkdemo"));
        pageMap.put("devref", new SidebarPage("devref", "ZK Developer Reference", "/imgs/doc.png", "http://books.zkoss.org/wiki/ZK_Developer's_Reference"));

        pageMap.put("fn1", new SidebarPage("fn1", "Profile (MVC)", "/imgs/fn.png", "/chapter5/profile-mvc.zul"));
        pageMap.put("fn2", new SidebarPage("fn2", "Profile (MVVM)", "/imgs/fn.png", "/chapter5/profile-mvvm.zul"));
        pageMap.put("fn3", new SidebarPage("fn3", "Todo List (MVC)", "/imgs/fn.png", "/chapter6/todolist-mvc.zul"));
        pageMap.put("fn4", new SidebarPage("fn4", "Todo List (MVVM)", "/imgs/fn.png", "/chapter6/todolist-mvvm.zul"));
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
