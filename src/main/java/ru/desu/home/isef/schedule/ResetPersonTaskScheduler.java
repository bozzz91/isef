package ru.desu.home.isef.schedule;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.repo.PersonTaskRepo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Log
@Component
@Transactional
public class ResetPersonTaskScheduler {

	@Autowired PersonTaskRepo ptRepo;

	@Scheduled(fixedRate = 300000)
	public void resetTasks() {
		int removed = 0;
		List<PersonTask> pts = ptRepo.findByStatus(1);
		for (PersonTask pt : pts) {
			Calendar cal = Calendar.getInstance();
			Task t = pt.getTask();
			cal.add(Calendar.HOUR, -t.getPeriod());
			Date expired = cal.getTime();
			if (pt.getExecuted().before(expired)) {
				ptRepo.delete(pt);
				removed++;
			}
		}
		if (removed > 0) {
			log.info("Removed " + removed + " executed tasks by scheduler.");
		}
	}

	@Scheduled(fixedRate = 300000)
	public void resetWrongAnswers() {
		int removed = 0;
		List<PersonTask> pts = ptRepo.findByStatus(3);
		for (PersonTask pt : pts) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -1);
			Date expired = cal.getTime();
			if (pt.getAdded().before(expired)) {
				ptRepo.delete(pt);
				removed++;
			}
		}
		if (removed > 0) {
			log.info("Removed " + removed + " wrong answers by scheduler.");
		}
	}
}
