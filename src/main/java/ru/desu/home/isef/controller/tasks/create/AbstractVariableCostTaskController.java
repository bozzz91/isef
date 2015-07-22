package ru.desu.home.isef.controller.tasks.create;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.utils.Config;
import ru.desu.home.isef.utils.FormatUtil;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class AbstractVariableCostTaskController extends AbstractCreateTaskController {

	protected @Wire Checkbox vip;
	protected @Wire Combobox uniqueIp;
	protected @Wire Combobox sex;

	protected ListModelList<String> ipList;
	protected ListModelList<String> sexList;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		ipList = new ListModelList<>(Config.getAllIps());
		ipList.addToSelection(Config.getFirstIp());
		uniqueIp.setModel(ipList);

		sexList = new ListModelList<>(Config.getAllSex());
		sexList.addToSelection(Config.getFirstSex());
		sex.setModel(sexList);
	}

	@Listen("onCheck = #vip")
	public void onVipChanged() {
		double multiplier = curTaskType.getMultiplier();
		if (uniqueIp.getSelectedIndex() > 0) {
			multiplier += Config.UNIQUE_IP_COST;
		}
		if (vip.isChecked()) {
			multiplier += Config.VIP_COST;
		}
		cost = calcCost(multiplier, countSpin.getValue());
		resultCost.setValue("Стоимость : " + FormatUtil.formatDouble(cost) + " iCoin");
	}

	@Listen("onChange = #uniqueIp")
	public void onUniqueIpChanged() {
		double multiplier = curTaskType.getMultiplier();
		if (uniqueIp.getSelectedIndex() > 0) {
			multiplier += Config.UNIQUE_IP_COST;
		}
		if (vip.isChecked()) {
			multiplier += Config.VIP_COST;
		}
		cost = calcCost(multiplier, countSpin.getValue());
		resultCost.setValue("Стоимость : " + FormatUtil.formatDouble(cost) + " iCoin");
	}
}
