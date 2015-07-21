package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.TaskType;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.TaskTypeService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.FormatUtil;
import ru.desu.home.isef.utils.SessionUtil;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class AbstractCreateTaskController extends SelectorComposer<Component> {
    
    //components
    protected @Wire Spinner countSpin;
    protected @Wire Label personCashLabel;
    protected @Wire Textbox resultCost;
    protected @Wire Window createTaskWin;
    
    protected @Wire("#taskPropertyGrid #curTaskSubjectEdit")    Textbox curTaskSubjectEdit;
    protected @Wire("#taskPropertyGrid #curTaskDescription")    Textbox curTaskDescription;
    protected @Wire("#taskPropertyGrid #labelTaskType")         Label labelTaskType;
    
    //services
    protected @WireVariable AuthenticationService authService;
    protected @WireVariable PersonService personService;
    protected @WireVariable PaymentService paymentService;
    protected @WireVariable TaskService taskService;
    protected @WireVariable TaskTypeService taskTypeService;
    
    protected Double cost = 0.0;
    protected TaskType curTaskType;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        Person p = authService.getUserCredential().getPerson();
        p = personService.find(p.getEmail());
        personCashLabel.setValue("Ваш баланс: " + p.getCash());

        curTaskType = SessionUtil.getCurTaskType();
        labelTaskType.setValue(curTaskType.getType());
        cost = calcCost(curTaskType.getMultiplier(), countSpin.getValue());
        resultCost.setValue("Стоимость : " + cost);
    }
    
    public abstract void doCreateTask();
    
    protected void setVisible(Component comp, boolean visible) {
        if (comp != null) {
            comp.setVisible(visible);
        }
    }
    
    @Listen("onClick = #doCreateTask")
    public void validateAndCreateTask() {
        if (curTaskSubjectEdit.getValue() != null && curTaskSubjectEdit.getValue().isEmpty()) {
            Clients.showNotification("Задайте имя задания", "error", curTaskSubjectEdit, "after_end", 3000);
            return;
        }
        doCreateTask();
    }
    
    @Listen("onChange = #countSpin")
    public void onChangeClickCount() {
        double multiplier = curTaskType.getMultiplier();
        cost = calcCost(multiplier, countSpin.getValue());
        resultCost.setValue("Стоимость : " + FormatUtil.formatDouble(cost) + " iCoin");
    }
    
    protected abstract Double calcCost(Double multi, Integer count);
}
