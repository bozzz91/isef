package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Log;
import java.util.List;

public interface MyService {

    Log addLog(Log log);

    List<Log> getLogs();

    void deleteLog(Log log);
}
