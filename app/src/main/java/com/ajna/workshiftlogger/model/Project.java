package com.ajna.workshiftlogger.model;

import java.io.Serializable;

public class Project implements Serializable{
    public static final long serialVersionUID = 20190215L;

    private long id;
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

    public Project(long id, String name, String clientName, long clientId) {
        this.id = id;
        this.name = name;
        this.clientName = clientName;
        this.clientId = clientId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
