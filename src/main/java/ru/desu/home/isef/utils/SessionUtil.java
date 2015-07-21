package ru.desu.home.isef.utils;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.entity.TaskType;

public class SessionUtil {
    public static final String CURRENT_TASK_ATTRIBUTE = "curTask";
    public static final String CURRENT_TASK_TYPE_ATTRIBUTE = "curTaskType";
    
    public static TaskType getCurTaskType() {
        Session sess = Sessions.getCurrent();
        return (TaskType)sess.getAttribute(CURRENT_TASK_TYPE_ATTRIBUTE);
    }
    
    public static void setCurTaskType(TaskType tt) {
        Session sess = Sessions.getCurrent();
        sess.setAttribute(CURRENT_TASK_TYPE_ATTRIBUTE, tt);
    }
    
    public static void removeCurTaskType() {
        Session sess = Sessions.getCurrent();
        sess.removeAttribute(CURRENT_TASK_TYPE_ATTRIBUTE);
    }
    
    public static Task getCurTask() {
        Session sess = Sessions.getCurrent();
        return (Task)sess.getAttribute(CURRENT_TASK_ATTRIBUTE);
    }
    
    public static void setCurTask(Task task) {
        Session sess = Sessions.getCurrent();
        sess.setAttribute(CURRENT_TASK_ATTRIBUTE, task);
    }
    
    public static void removeCurTask() {
        Session sess = Sessions.getCurrent();
        sess.removeAttribute(CURRENT_TASK_ATTRIBUTE);
    }
}
