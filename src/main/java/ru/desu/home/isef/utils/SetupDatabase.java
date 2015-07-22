package ru.desu.home.isef.utils;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

public class SetupDatabase implements PersistenceUnitPostProcessor {

	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		pui.getProperties().setProperty("hibernate.connection.username", Config.DB_LOGIN );
		pui.getProperties().setProperty("hibernate.connection.password", Config.DB_PASS);
		pui.getProperties().setProperty("hibernate.connection.url", Config.DB_URL );
		pui.getProperties().setProperty("hibernate.connection.driver_class", Config.DB_DRIVER );
	}

}
