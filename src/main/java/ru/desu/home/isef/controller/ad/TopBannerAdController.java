package ru.desu.home.isef.controller.ad;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TopBannerAdController extends SelectorComposer<Component> {
    
    //components
    @Wire Label text;
    @Wire Image image;
    
    //data for the view
    protected ListModelList<Person> topPersonModel;
    
    //services
    protected @WireVariable PersonService personService;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        /*List<Person> persons = personService.findTop(10);
        topPersonModel = new ListModelList<>(persons);
        topList.setModel(topPersonModel);*/
        
        text.setValue("AD");
        //image.setSrc("/imgs/logo_90.png");
    }
}
