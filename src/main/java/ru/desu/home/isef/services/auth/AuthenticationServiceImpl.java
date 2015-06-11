package ru.desu.home.isef.services.auth;

import java.io.Serializable;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.utils.DecodeUtil;

@Service("authService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthenticationServiceImpl implements AuthenticationService, Serializable {

    @Autowired
    PersonService personService;

    @Override
    public UserCredential getUserCredential() {
        Session sess = Sessions.getCurrent();
        UserCredential cre = (UserCredential) sess.getAttribute(UserCredential.USER_CREDENTIAL);
        if (cre == null) {
            cre = new UserCredential();
            sess.setAttribute(UserCredential.USER_CREDENTIAL, cre);
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

        Session sess = Sessions.getCurrent();
        String name = p.getEmail() + " (" + personService.getRating(p) + ")";
        UserCredential cre = new UserCredential(name, p.getUserName());
        cre.addRole(p.getRole().getRoleName());
        cre.setPerson(p);
        //just in case for this demo.
        if (cre.isAnonymous()) {
            return "anonim";
        }
        sess.setAttribute(UserCredential.USER_CREDENTIAL, cre);
        
        p.setLastConnect(new Date());
        personService.save(p);

        //TODO handle the role here for authorization
        return "ok";
    }

    @Override
    public void logout() {
        Session sess = Sessions.getCurrent();
        sess.removeAttribute(UserCredential.USER_CREDENTIAL);
    }
}
