package cn.com.easyerp.core.timedTask.entity;

import java.util.ArrayList;
import java.util.List;

import cn.com.easyerp.core.cache.I18nDescribe;

public class TimeTaskDescribe {
    private String task_id;
    private String international_id;
    private I18nDescribe name_i18n;
    private List<TimeTaskSysTimeDescribe> timeTaskSysTimeDescribes;
    private List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTimeDescribes;
    private List<TimeTaskEventDescribe> timeTaskEventDescribes;
    private int saveType;

    public int getSaveType() {
        return this.saveType;
    }

    public void setSaveType(int saveType) {
        this.saveType = saveType;
    }

    public void setTimeTaskSysTimeDescribes(List<TimeTaskSysTimeDescribe> timeTaskSysTimeDescribes) {
        this.timeTaskSysTimeDescribes = timeTaskSysTimeDescribes;
    }

    public void setTimeTaskBusinessTimeDescribes(List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTimeDescribes) {
        this.timeTaskBusinessTimeDescribes = timeTaskBusinessTimeDescribes;
    }

    public void setTimeTaskEventDescribes(List<TimeTaskEventDescribe> timeTaskEventDescribes) {
        this.timeTaskEventDescribes = timeTaskEventDescribes;
    }

    public String getTask_id() {
        return this.task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public I18nDescribe getName_i18n() {
        return this.name_i18n;
    }

    public void setName_i18n(I18nDescribe name_i18n) {
        this.name_i18n = name_i18n;
    }

    public List<TimeTaskSysTimeDescribe> getTimeTaskSysTimeDescribes() {
        return this.timeTaskSysTimeDescribes;
    }

    public List<TimeTaskBusinessTimeDescribe> getTimeTaskBusinessTimeDescribes() {
        return this.timeTaskBusinessTimeDescribes;
    }

    public List<TimeTaskEventDescribe> getTimeTaskEventDescribes() {
        return this.timeTaskEventDescribes;
    }

    public void addTimeTaskSysTimeDescribes(TimeTaskSysTimeDescribe timeTaskSysTimeDescribe) {
        if (this.timeTaskSysTimeDescribes == null) {
            this.timeTaskSysTimeDescribes = new ArrayList<>();
        }
        this.timeTaskSysTimeDescribes.add(timeTaskSysTimeDescribe);
    }

    public void addTimeTaskBusinessTimeDescribes(TimeTaskBusinessTimeDescribe timeTaskBusinessTimeDescribe) {
        if (this.timeTaskBusinessTimeDescribes == null) {
            this.timeTaskBusinessTimeDescribes = new ArrayList<>();
        }
        this.timeTaskBusinessTimeDescribes.add(timeTaskBusinessTimeDescribe);
    }

    public void addTimeTaskEventDescribes(TimeTaskEventDescribe timeTaskEventDescribe) {
        if (this.timeTaskEventDescribes == null) {
            this.timeTaskEventDescribes = new ArrayList<>();
        }
        this.timeTaskEventDescribes.add(timeTaskEventDescribe);
    }
}
