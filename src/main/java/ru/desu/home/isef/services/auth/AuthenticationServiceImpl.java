package ru.desu.home.isef.services.auth;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.utils.ConfigUtil;
import ru.desu.home.isef.utils.DecodeUtil;
import ru.desu.home.isef.utils.GeoUtil;

import java.io.Serializable;
import java.util.Date;

@Log
@Service("authService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthenticationServiceImpl implements AuthenticationService, Serializable {

    @Autowired PersonService personService;

    @Override
    public UserCredential getUserCredential() {
        Session session = Sessions.getCurrent();
        UserCredential cre = (UserCredential) session.getAttribute(UserCredential.USER_CREDENTIAL);
        if (cre == null) {
            cre = new UserCredential();
			session.setAttribute(UserCredential.USER_CREDENTIAL, cre);
        }
        return cre;
    }

    @Override
    public String login(String nm, String pd) {
        Person p = personService.find(nm);
        if (p!= null && !p.isActive()) {
            return "not_active";
        }
        //a simple plan text password verification
        if (p == null || !p.getUserPassword().equals(DecodeUtil.decodePass(pd))) {
            return "wrong_pass";
        }

        Session session = Sessions.getCurrent();
        String account = p.getEmail();
        String name = p.getUserName() + " (" + personService.getRating(p).getName() + ")";
        UserCredential cre = new UserCredential(account, name, p.getRole().getRoleName());
        cre.setPerson(p);
        //just in case for this demo.
        if (cre.isAnonymous()) {
            return "anonim";
        }
		String ip = ConfigUtil.getIp();
		cre.setIp(ip);
		try {
			String[] countryMeta = GeoUtil.detectCountry(ip);
			cre.setCountryCode(countryMeta[0]);
			cre.setCountryName(countryMeta[1]);
		} catch (Exception e) {
			log.warning("Can not detect country for ip: " + ip);
		}
		session.setAttribute(UserCredential.USER_CREDENTIAL, cre);
        
        p.setLastConnect(new Date());
        personService.save(p);

        return "ok";
    }

    @Override
    public void logout() {
        Session session = Sessions.getCurrent();
		session.removeAttribute(UserCredential.USER_CREDENTIAL);
    }
}
