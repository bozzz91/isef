package ru.desu.home.isef.controller.zul;

import java.util.List;

public interface SidebarPageConfig {
	public List<SidebarPage> getPages();
	
	public SidebarPage getPage(String name);
}