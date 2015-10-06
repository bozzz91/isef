package ru.desu.home.isef.utils;

import lombok.extern.java.Log;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

@Log
public class SetupDatabase implements PersistenceUnitPostProcessor {

	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		Properties props = new Properties();
		try {
			String config = System.getProperty("isef.config", "../conf/isef.properties");
			props.load(new FileInputStream(config));
		} catch (IOException ex) {
			log.log(Level.SEVERE, null, ex);
		}

		pui.getProperties().setProperty("hibernate.connection.username", props.getProperty("db_login"));
		pui.getProperties().setProperty("hibernate.connection.password", props.getProperty("db_pass"));
		pui.getProperties().setProperty("hibernate.connection.url", props.getProperty("db_url"));
		pui.getProperties().setProperty("hibernate.connection.driver_class", props.getProperty("db_driver"));
	}

}
