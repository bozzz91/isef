package ru.desu.home.isef.controller;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TodoListController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	//wire components
	@Wire
	Textbox todoSubject;
	@Wire
	Button addTodo;
	@Wire
	Listbox todoListbox;
	
	@Wire
	Component selectedTodoBlock;
	@Wire
	Checkbox selectedTodoCheck;
	@Wire
	Textbox selectedTodoSubject;
	@Wire
	Radiogroup selectedTodoPriority;
	@Wire
	Datebox selectedTodoDate;
	@Wire
	Textbox selectedTodoDescription;
	@Wire
	Button updateSelectedTodo;
	
	//services
        @WireVariable
	TaskService taskService;
        
        @WireVariable
        AuthenticationService authService;
        
        @WireVariable
        PersonService personService;
	
	//data for the view
	ListModelList<Task> todoListModel;
	Task selectedTodo;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		
		//get data from service and wrap it to list-model for the view
                Person p = authService.getUserCredential().getPerson();
		List<Task> todoList = taskService.getTasksForWork(p);
		todoListModel = new ListModelList<>(todoList);
		todoListbox.setModel(todoListModel);
	}
	
	//when user clicks on the button or enters on the textbox
	@Listen("onClick = #addTodo; onOK = #todoSubject")
	public void doTodoAdd(){
		//get user input from view
		String subject = todoSubject.getValue();
		if(Strings.isBlank(subject)){
			Clients.showNotification("Nothing to do ?",todoSubject);
		}else{
			//save data
                        //TODO: asdsd
			selectedTodo = taskService.save(new Task());
			//update the model of listbox
			todoListModel.add(selectedTodo);
			//set the new selection
			todoListModel.addToSelection(selectedTodo);
			
			//refresh detail view
			refreshDetailView();
			
			//reset value for fast typing.
			todoSubject.setValue("");
		}
	}
	
	//when user checks on the checkbox of each todo on the list
	@Listen("onTodoCheck = #todoListbox")
	public void doTodoCheck(ForwardEvent evt){
		//get data from event
		Checkbox cbox = (Checkbox)evt.getOrigin().getTarget();
		Listitem litem = (Listitem)cbox.getParent().getParent();
		
		boolean checked = cbox.isChecked();
		Task todo = (Task)litem.getValue();
		todo.setStatus(Status._4_DONE);
		
		//save data
		todo = taskService.save(todo);
		if(todo.equals(selectedTodo)){
			selectedTodo = todo;
			//refresh detail view
			refreshDetailView();
		}
		//update listitem style
		((Listitem)cbox.getParent().getParent()).setSclass(checked?"complete-todo":"");
	}
	
	//when user clicks the delete button of each todo on the list
	@Listen("onTodoDelete = #todoListbox")
	public void doTodoDelete(ForwardEvent evt){
		Button btn = (Button)evt.getOrigin().getTarget();
		Listitem litem = (Listitem)btn.getParent().getParent();
		
		Task todo = (Task)litem.getValue();
		
		//delete data
		taskService.delete(todo);
		
		//update the model of listbox
		todoListModel.remove(todo);
		
		if(todo.equals(selectedTodo)){
			//refresh selected todo view
			selectedTodo = null;
			refreshDetailView();
		}
	}

	//when user selects a todo of the listbox
	@Listen("onSelect = #todoListbox")
	public void doTodoSelect() {
		if(todoListModel.isSelectionEmpty()){
			//just in case for the no selection
			selectedTodo = null;
		}else{
			selectedTodo = todoListModel.getSelection().iterator().next();
		}
		refreshDetailView();
	}
	
	private void refreshDetailView() {
		//refresh the detail view of selected todo
		if(selectedTodo==null){
			//clean
			selectedTodoBlock.setVisible(false);
			selectedTodoCheck.setChecked(false);
			selectedTodoSubject.setValue(null);
			selectedTodoDate.setValue(null);
			selectedTodoDescription.setValue(null);
			updateSelectedTodo.setDisabled(true);
		}else{
			selectedTodoBlock.setVisible(true);
			selectedTodoSubject.setValue(selectedTodo.getSubject());
			selectedTodoDate.setValue(selectedTodo.getCreationTime());
			selectedTodoDescription.setValue(selectedTodo.getDescription());
			updateSelectedTodo.setDisabled(false);
		}
	}
	
	//when user clicks the update button
	@Listen("onClick = #updateSelectedTodo")
	public void doUpdateClick(){
		if(Strings.isBlank(selectedTodoSubject.getValue())){
			Clients.showNotification("Nothing to do ?",selectedTodoSubject);
			return;
		}
                int index = todoListModel.indexOf(selectedTodo);
		
		//selectedTodo.setDone(selectedTodoCheck.isChecked());
		selectedTodo.setSubject(selectedTodoSubject.getValue());
		selectedTodo.setModificationTime(selectedTodoDate.getValue());
		selectedTodo.setDescription(selectedTodoDescription.getValue());
		//selectedTodo.setPriority(priorityListModel.getSelection().iterator().next());
		
		//save data and get updated Todo object
		selectedTodo = taskService.save(selectedTodo);
		
		//replace original Todo object in listmodel with updated one
		todoListModel.set(index, selectedTodo);
		
		//show message for user
		Clients.showNotification("Todo saved");
	}
	
	//when user clicks the update button
	@Listen("onClick = #reloadSelectedTodo")
	public void doReloadClick(){
		refreshDetailView();
	}
}
