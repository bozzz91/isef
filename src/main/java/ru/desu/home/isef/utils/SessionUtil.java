package ru.desu.home.isef.utils;

import org.zkoss.zk.ui.Sessions;

import java.util.Calendar;
import java.util.Date;

public class SessionUtil {

	public static void setExecutingTask() {
		Sessions.getCurrent().setAttribute("executing", new Date());
	}

	public static void removeExecutingTask() {
		Sessions.getCurrent().setAttribute("executing", null);
	}

	public static boolean isExecutingTask() {
		Date date = (Date) Sessions.getCurrent().getAttribute("executing");
		if (date == null) {
			return false;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -2);
		return cal.getTime().before(date);
	}

}
