package ru.desu.home.isef.services.auth;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class AuthenticationInit implements Initiator {

    @WireVariable
    AuthenticationService authService;

    @Override
    public void doInit(Page page, Map<String, Object> args) throws Exception {
        //wire service manually by calling Selectors API
        //Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), null));

        UserCredential cre = authService.getUserCredential();
        if (cre == null || cre.isAnonymous()) {
            Executions.sendRedirect("/login/");
        }
    }
}
