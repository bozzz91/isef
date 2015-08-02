package ru.desu.home.isef.controller.zul;

import java.util.List;

public interface SidebarPageConfig {

    List<SidebarPage> getPages();

    SidebarPage getPage(String name);
}
