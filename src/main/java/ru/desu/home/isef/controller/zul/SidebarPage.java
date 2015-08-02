package ru.desu.home.isef.controller.zul;

import lombok.Data;

import java.io.Serializable;

@Data
public class SidebarPage implements Serializable {
    private static final long serialVersionUID = 1L;

    String name,label, iconUri, uri;

    public SidebarPage(String name, String label, String iconUri, String uri) {
        this.name = name;
        this.label = label;
        this.iconUri = iconUri;
        this.uri = uri;
    }
}
