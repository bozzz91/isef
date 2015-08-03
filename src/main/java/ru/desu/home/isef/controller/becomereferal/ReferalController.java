package ru.desu.home.isef.controller.becomereferal;

import lombok.extern.java.Log;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import ru.desu.home.isef.entity.BecomeReferal;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Rating;
import ru.desu.home.isef.services.BecomeReferalService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ReferalController  extends SelectorComposer<Component> {

	@Wire Image photo;
	@Wire Button referal;
	@Wire Label rating, reverse, name;

	//services
	@WireVariable BecomeReferalService becomeReferalService;
	@WireVariable AuthenticationService authService;
	@WireVariable PersonService personService;

	private Long lastOwner = -1l;

	@Listen("onClick = #add")
	public void addBecomeReferal() {
		BecomeReferal ref = becomeReferalService.get();
		Integer cost = Config.BECOME_REF_COST;
		if (ref != null) {
			cost = ref.getCost() + Config.BECOME_REF_COST_DIFF;
		}

		Map<String, String> params = new HashMap<>();
		params.put("width", "600");
		Messagebox.show("Добавить ссылку на Ваш профиль за " + cost + " iCoin?",
				"Подтверждение",
				new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.CANCEL},
				new String[] {"Да", "Нет"},
				Messagebox.QUESTION,
				Messagebox.Button.OK,
				new EventListener<Messagebox.ClickEvent>() {
					@Override
					public void onEvent(Messagebox.ClickEvent event) throws Exception {
						if (event.getName().equals(Messagebox.ON_YES)) {
							BecomeReferal newRef = becomeReferalService.get();;
							if (newRef == null) {
								newRef = new BecomeReferal();
								newRef.setCost(Config.BECOME_REF_COST);
							} else {
								newRef.setCost(newRef.getCost() + Config.BECOME_REF_COST_DIFF);
							}
							Person p = authService.getUserCredential().getPerson();
							p = personService.findById(p.getId());

							if (p.getCash() < newRef.getCost()) {
								Clients.showNotification("Недостаточно средств. Необходимо " + newRef.getCost() +" iCoin",
										"warning", null, "middle_center", 2000, true);
								return;
							}
							newRef.setPerson(p);
							becomeReferalService.save(newRef);

							//show message for user
							Clients.showNotification("Успешно добавлено!", "info", null, "middle_center", 2000);
						}
					}
				}, params);
	}

	@Listen("onTimer = #updateRefTimer")
	public void updateView() {
		BecomeReferal ref = becomeReferalService.get();
		if (ref != null) {
			Person owner = ref.getPerson();
			if (!Objects.equals(owner.getId(), lastOwner)) {
				byte[] data = owner.getPhoto();
				AImage img = null;
				try {
					if (data != null) {
						img = new AImage("img", data);
					}
				} catch (IOException e) {
					img = null;
				}
				if (img != null) {
					photo.setContent(img);
				} else {
					photo.setSrc("/img/no-ava.png");
				}
				Rating rate = personService.getRating(owner);
				name.setValue(owner.getUserName());
				rating.setValue(rate.getName());
				reverse.setValue("(Реверс " + (int) (rate.getReverse() * 100) + "%)");
				setVisibleComponents(true);
				lastOwner = owner.getId();
			}
		} else {
			if (lastOwner != -1l) {
				photo.setSrc("/imgs/no-ava.png");
				lastOwner = -1l;

				setVisibleComponents(false);
			}
		}
	}

	private void setVisibleComponents(boolean visible) {
		rating.setVisible(visible);
		referal.setVisible(visible);
		reverse.setVisible(visible);
		name.setVisible(visible);
	}

	@Listen("onClick = #referal")
	public void becomeReferal() {
		BecomeReferal ref = becomeReferalService.get();
		if (ref != null) {
			Person currentPerson = authService.getUserCredential().getPerson();
			currentPerson = personService.findById(currentPerson.getId());
			Person becomeOwner = ref.getPerson();
			if (!currentPerson.getId().equals(becomeOwner.getId())) {
				if (!currentPerson.getInviter().getId().equals(becomeOwner.getId())) {
					currentPerson.setInviter(becomeOwner);
					currentPerson = personService.save(currentPerson);
					authService.getUserCredential().setPerson(currentPerson);
					Clients.showNotification("Вы стали рефералом пользователя " + becomeOwner.getUserName());
				}
			}
		} else {
			Clients.showNotification("Неверные данные, повторите запрос позже.");
		}
	}
}
