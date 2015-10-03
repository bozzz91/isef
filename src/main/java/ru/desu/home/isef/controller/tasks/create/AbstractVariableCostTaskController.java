package ru.desu.home.isef.controller.tasks.create;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.utils.ConfigUtil;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class AbstractVariableCostTaskController extends AbstractCreateTaskController {

	protected @Wire Checkbox vip;
	protected @Wire Combobox uniqueIp;
	protected @Wire Combobox sex;

	protected @WireVariable ConfigUtil config;

	protected ListModelList<String> ipList;
	protected ListModelList<String> sexList;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		ipList = new ListModelList<>(config.getAllIps());
		ipList.addToSelection(config.getFirstIp());
		uniqueIp.setModel(ipList);

		sexList = new ListModelList<>(config.getAllSex());
		sexList.addToSelection(config.getFirstSex());
		sex.setModel(sexList);
	}

	protected Double calcMultiplier() {
		double multiplier = curTaskType.getMultiplier();
		if (uniqueIp.getSelectedIndex() > 0) {
			multiplier += config.getUniqueIpCost();
		}
		if (vip.isChecked()) {
			multiplier += config.getVipCost();
		}
		return multiplier;
	}
}
