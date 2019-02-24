package com.ajna.workshiftlogger.model;

import java.io.Serializable;

public class Project implements Serializable{
    public static final long serialVersionUID = 20190215L;

    private String name;
    private String clientName;
    private long clientId;

    public Project(String name, long clientId) {
        this.name = name;
        this.clientId = clientId;
    }

    public Project(String name, String clientName) {
        this.name = name;
        this.clientName = clientName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
