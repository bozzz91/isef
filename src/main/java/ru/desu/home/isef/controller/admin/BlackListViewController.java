package ru.desu.home.isef.controller.admin;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.Ban;
import ru.desu.home.isef.services.BanService;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class BlackListViewController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    @Wire Listbox banList;
	@Wire Textbox link;

    @WireVariable BanService banService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        List<Ban> allBans = banService.findAll();
        ListModelList<Ban> model = new ListModelList<>(allBans);
		banList.setModel(model);
    }

    @Listen("onBanDelete = #banList")
    public void deletePayment(ForwardEvent evt) {
        Button btn = (Button) evt.getOrigin().getTarget();
        Listitem row = (Listitem) btn.getParent().getParent();
        Ban ban = row.getValue();
        
        banService.delete(ban);
        
        ((ListModelList<Ban>) banList.<Ban>getModel()).remove(ban);
    }

	@Listen("onClick = #addBan; onOK = #link")
	public void doSearchTask() {
		Ban ban = new Ban();
		ban.setUrl(link.getValue());

		banService.save(ban);
		((ListModelList<Ban>) banList.<Ban>getModel()).add(ban);
		link.setValue("");
	}
}
