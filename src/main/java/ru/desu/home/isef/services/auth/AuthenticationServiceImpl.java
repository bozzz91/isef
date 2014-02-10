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

@Service("authService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthenticationServiceImpl implements AuthenticationService, Serializable {

    @Autowired
    PersonService personService;

    @Override
    public UserCredential getUserCredential() {
        Session sess = Sessions.getCurrent();
        UserCredential cre = (UserCredential) sess.getAttribute("userCredential");
        if (cre == null) {
            cre = new UserCredential();
            sess.setAttribute("userCredential", cre);
        }
        return cre;
    }

    @Override
    public boolean login(String nm, String pd) {
        Person p = personService.find(nm);
        //a simple plan text password verification
        if (p == null || !p.getUserPassword().equals(pd)) {
            return false;
        }

        Session sess = Sessions.getCurrent();
        UserCredential cre = new UserCredential(p.getEmail(), p.getUserName());
        cre.addRole(p.getRole().getRoleName());
        cre.setPerson(p);
        //just in case for this demo.
        if (cre.isAnonymous()) {
            return false;
        }
        sess.setAttribute("userCredential", cre);
        
        p.setLastConnect(new Date());
        personService.save(p);

        //TODO handle the role here for authorization
        return true;
    }

    @Override
    public void logout() {
        Session sess = Sessions.getCurrent();
        sess.removeAttribute("userCredential");
    }
}
