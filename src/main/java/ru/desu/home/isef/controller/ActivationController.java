package ru.desu.home.isef.controller;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import ru.desu.home.isef.entity.ActivationPerson;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.ActivationPersonService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.DecodeUtil;

import java.util.logging.Level;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ActivationController extends SelectorComposer<Component> {

    @Wire Label message;

    @WireVariable PersonService personService;
    @WireVariable ActivationPersonService activationService;
    @WireVariable AuthenticationService authService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        try {
            String code = Executions.getCurrent().getParameter("code");
            String   id = Executions.getCurrent().getParameter("id");
            if (id == null || code == null || id.isEmpty() || code.isEmpty()) {
                message.setValue("Ошибка активации, неверные данные");
                return;
            }
            
            ActivationPerson ap = activationService.findById(Long.valueOf(id));
            if (ap == null) {
                message.setValue("Ошибка активации, отправить код еще раз на тот же e-mail?");
                return;
            }
            if (ap.isDone()) {
                message.setValue("Вы уже прошли активацию");
                return;
            }

            final Person p = personService.find(ap.getEmail());
            if (p == null) {
                message.setValue("Не найден пользователь для текущего запроса авторизации");
                return;
            }

            if (DecodeUtil.decodeEmail(p.getEmail()).equals(code)) {
                p.setActive(true);
                ap.setDone(true);
                activationService.save(ap);
                personService.save(p);
                Messagebox.show("Успешно актировано",
                    "Info", Messagebox.OK,
                    Messagebox.INFORMATION,
                    new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            authService.login(p.getEmail(), p.getUserPassword());
                            Executions.sendRedirect("/work/");
                        }
                });
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
