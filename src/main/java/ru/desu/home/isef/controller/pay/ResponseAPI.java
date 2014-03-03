package ru.desu.home.isef.controller.pay;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
public class ResponseAPI {

    @XmlElement
    int code;

    @XmlElement
    int pay_for;

    @XmlElement
    String comment;

    @XmlElement
    String md5;
}
