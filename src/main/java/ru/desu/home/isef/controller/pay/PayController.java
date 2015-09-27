package ru.desu.home.isef.controller.pay;

import lombok.extern.java.Log;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.utils.ConfigUtil;
import ru.desu.home.isef.utils.FormatUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;

@Log
@Controller(value = "/")
public class PayController {

	private static final String PARAM_LMI_PAYEE_PURSE = "LMI_PAYEE_PURSE"; //757
	private static final String PARAM_PRE_REQUEST = "LMI_PREREQUEST"; //0
    private static final String PARAM_LMI_MODE = "LMI_MODE"; //0
    private static final String PARAM_PAYMENT_AMOUNT = "LMI_PAYMENT_AMOUNT"; //2.00
    private static final String PARAM_PAYMENT_NO = "LMI_PAYMENT_NO"; //151
    private static final String PARAM_SYS_INVS_NO = "LMI_SYS_INVS_NO"; //4464739
	private static final String PARAM_LMI_SYS_TRANS_NO = "LMI_SYS_TRANS_NO"; //763
    private static final String PARAM_SYS_TRANS_DATE = "LMI_SYS_TRANS_DATE"; //20150927 12:27:22
	private static final String PARAM_LMI_PAYER_PURSE = "LMI_PAYER_PURSE"; //bozzz91
	private static final String PARAM_LMI_PAYER_WM = "LMI_PAYER_WM"; //bozzz91@gmail.com
	private static final String PARAM_LMI_HASH = "LMI_HASH"; //E75AC4E2C3266D6FDE27C514E422BBF0
	private static final String PARAM_PKP_METHOD_PAYMENT = "PKP_METHOD_PAYMENT"; //zpayment
    private static final String PARAM_SECRET_KEY = "LMI_SECRET_KEY";

	private static final String SECRET_KEY = "PokupoSecretKey456";
    
    @Autowired PaymentService paymentService;
    @Autowired PersonService personService;
	@Autowired ConfigUtil config;

    @RequestMapping
    public @ResponseBody String answerCheck(HttpServletRequest req) {
        try {
			String preRequest = req.getParameter(PARAM_PRE_REQUEST);

			String amount = req.getParameter(PARAM_PAYMENT_AMOUNT);
			String pay_for = req.getParameter(PARAM_PAYMENT_NO);
			String onpay_id = req.getParameter(PARAM_SYS_INVS_NO);

			if (pay_for == null || amount == null) {
				log.severe("PAYMENT_NO or AMOUNT is null: [" + pay_for + ", " + amount + "]");
				return "NO";
			}

			String[] pokupoIps = config.getPokupoIps();
			String currIp = req.getRemoteAddr();
			boolean trustedIp = false;
			for (String trustIp : pokupoIps) {
				if (trustIp.equals(currIp)) {
					trustedIp = true;
					break;
				}
			}
			if (!trustedIp) {
				log.severe("Invalid host: " + req.getRemoteAddr() + " for pay_no: " + pay_for);
				return "NO";
			}

            Payment currPay = paymentService.findOne(Long.valueOf(pay_for));
			if (currPay == null) {
				log.severe("Incorrect payment with id: " + pay_for);
				return "NO";
			}

			if ("1".equals(preRequest)) {
				if (Math.abs(currPay.getOrderAmountRub() - Double.valueOf(amount)) < 0.01) {
					return "YES";
				} else {
					log.severe("Incorrect amount: " + amount + " for PAYMENT_NO: " + pay_for);
				}
			} else {
				double amount_rub = Double.valueOf(amount);
				Double amount_icoin = amount_rub / paymentService.getCurrency();
				String amount_icoin_format = FormatUtil.formatDouble(amount_icoin);
				currPay.setBalanceAmountRub(amount_rub);
				currPay.setBalanceAmount(Double.valueOf(amount_icoin_format));
				currPay.setOnpayId(Integer.valueOf(onpay_id));
				currPay.setPayDate(new Date());
				currPay.setStatus(1);
				paymentService.save(currPay);

				Person p = personService.findById(currPay.getPayOwner().getId());
				p.addCash(Double.valueOf(amount_icoin_format));
				personService.save(p);

				return "YES";
            }
            return "NO";
        } catch (NumberFormatException e) {
            log.log(Level.SEVERE, e.toString());
            return "NO";
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
