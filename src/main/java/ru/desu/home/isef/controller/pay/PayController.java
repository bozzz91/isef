package ru.desu.home.isef.controller.pay;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.PersonService;

@Log
@Controller(value = "/")
public class PayController {
    
    private static final String ISEF_CODE = "Cnfhsqkjcm1";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_ORDER_AMOUNT = "order_amount";
    private static final String PARAM_PAY_FOR = "pay_for";
    private static final String PARAM_ORDER_CURRENCY = "order_currency";
    private static final String PARAM_ONPAY_ID = "onpay_id";
    private static final String PARAM_ORDER_ID = "order_id";
    private static final String PARAM_BALANCE_AMOUNT = "balance_amount";
    
    @Autowired
    PaymentService paymentService;
    @Autowired
    PersonService personService;
    
    @RequestMapping
    public @ResponseBody ResponseAPI answerCheck(HttpServletRequest req) {
        ResponseAPI res;
        try {
            String type = req.getParameter(PARAM_TYPE);
            String amount = req.getParameter(PARAM_ORDER_AMOUNT);
            String pay_for = req.getParameter(PARAM_PAY_FOR);
            String order_currency = req.getParameter(PARAM_ORDER_CURRENCY);
            String onpay_id = req.getParameter(PARAM_ONPAY_ID);
            String order_id = req.getParameter(PARAM_ORDER_ID);
            String balance_amount = req.getParameter(PARAM_BALANCE_AMOUNT);
            
            StringBuilder for_md5 = new StringBuilder();
            Payment currPay = paymentService.findOne(Long.valueOf(pay_for));

            switch (type) {
                case "check":
                    res = new ResponseCheck();
                    if (currPay != null) {
                        if (currPay.getOrderAmountRub()-Double.valueOf(amount) < 0.01) {
                            for_md5.append(type).append(";")
                               .append(pay_for).append(";")
                               .append(amount).append(";")
                               .append(order_currency).append(";")
                               .append(0).append(";")
                               .append(ISEF_CODE);

                            res.code = 0;
                            res.comment = "ok";
                            res.pay_for = Integer.valueOf(pay_for);
                            res.md5 = md5(for_md5.toString()).toUpperCase();
                        } else {
                            res.code = 1;
                            res.comment = "wrong amount to pay";
                            res.pay_for = Integer.valueOf(pay_for);
                            res.md5 = "*";
                        }
                    } else {
                        res.code = 1;
                        res.comment = "this order doesn't exist";
                        res.pay_for = Integer.valueOf(pay_for);
                        res.md5 = "*";
                    }
                    return res;
                case "pay":
                    res = new ResponsePay();
                    
                    if (currPay != null) {
                        double amount_rub = Double.valueOf(balance_amount);
                        Double amount_icoin = amount_rub / paymentService.getCurrency().getCurrency();
                        currPay.setBalanceAmountRub(amount_rub);
                        currPay.setBalanceAmount(amount_icoin);
                        currPay.setOnpayId(Integer.valueOf(onpay_id));
                        currPay.setPayDate(new Date());
                        currPay.setStatus(1);
                        paymentService.save(currPay);

                        Person p = personService.findById(currPay.getPayOwner().getId());
                        p.addCash(amount_icoin);
                        personService.save(p);
                        
                        for_md5.append(type).append(";")
                                .append(pay_for).append(";")
                                .append(onpay_id).append(";")
                                .append(order_id!=null ? order_id : "").append(";")
                                .append(amount).append(";")
                                .append(order_currency).append(";")
                                .append(0).append(";")
                                .append(ISEF_CODE);

                        res.code = 0;
                        res.comment = "ok";
                        ((ResponsePay)res).onpay_id = Integer.valueOf(onpay_id);
                        res.pay_for = Integer.valueOf(pay_for);
                        res.md5 = md5(for_md5.toString()).toUpperCase();
                        
                        try {
                            EventQueues.lookup("cash", true).publish(new Event("onChange", null, p.getCash()));
                        } catch (Exception e) {
                            log.log(Level.SEVERE, e.toString());
                        }
                        
                        return res;
                    }
            }
            return null;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
            res = new ResponseAPI();
            res.code = 10;
            res.comment = "common error "+e.getMessage();
            res.md5 = "*";
            return res;
        }
    }
    
    private String md5(String s) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException ex) {
            throw new FatalBeanException("md5 not found");
        }
        md.update(s.getBytes());
        byte[] buf = md.digest();

        BigInteger bigInt = new BigInteger(1, buf);
        String res = bigInt.toString(16);
        while (res.length() < 32) {
            res = "0"+res;
        }
        return res;
    }
}
