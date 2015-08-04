package ru.desu.home.isef.controller.admin;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PersonsViewController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    @Wire Grid payGrid;

    @WireVariable PersonService personService;
 
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        List<Person> allPersons = personService.findAll();
        ListModelList<Person> model = new ListModelList<>(allPersons);
        payGrid.setModel(model);
    }
}
