package ru.desu.home.isef.controller.pay;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
public class ResponsePay extends ResponseAPI {

    @XmlElement
    int onpay_id;
}