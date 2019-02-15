package com.ajna.workshiftlogger.model;

import java.io.Serializable;

public class Shift implements Serializable {
    public static final long serialVersionUID = 20190215L;

    private long _id;
    private long startTime;
    private long endTime;
    private long pause;
    private Project project;

    public Shift(long _id, long startTime, long endTime, long pause, Project project) {
        this._id = _id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pause = pause;
        this.project = project;
    }

    public long get_id() {
        return _id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getPause() {
        return pause;
    }

    public void setPause(long pause) {
        this.pause = pause;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
