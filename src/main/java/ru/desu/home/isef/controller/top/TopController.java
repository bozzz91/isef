package ru.desu.home.isef.controller.top;

import java.util.List;
import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TopController extends SelectorComposer<Component> {
    
    //components
    @Wire Listbox topList;
    
    //data for the view
    protected ListModelList<Person> topPersonModel;
    
    //services
    protected @WireVariable PersonService personService;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        List<Person> persons = personService.findTop(10);
        topPersonModel = new ListModelList<>(persons);
        topList.setModel(topPersonModel);
    }
}
