package ru.desu.home.isef.servlet;

import java.util.logging.Level;
import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.ActivationPerson;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.ActivationPersonService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.utils.PasswordUtil;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ActivationServlet extends SelectorComposer<Component> {

    @Wire
    Label message;
    @Wire
    Textbox id;
    @Wire
    Textbox code;

    @WireVariable
    PersonService personService;

    @WireVariable
    ActivationPersonService activation;

    @Listen("onChange=#id")
    public void activate() {
        try {
            ActivationPerson ap = activation.findById(Long.valueOf(id.getValue()));

            if (ap == null) {
                message.setValue("Ошибка активации, отправить код еще раз на тот же e-mail?");
                return;
            }

            Person p = personService.find(ap.getEmail());

            if (p == null) {
                message.setValue("Не найден пользователь для текущего запроса авторизации");
                return;
            }

            if (PasswordUtil.asHex(p.getEmail(), "pass").equals(code)) {
                p.setActive(true);
                activation.delete(ap);
                personService.save(p);
                message.setValue("Успешно актировано");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
