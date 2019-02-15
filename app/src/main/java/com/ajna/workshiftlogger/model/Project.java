package com.ajna.workshiftlogger.model;

import java.io.Serializable;

public class Project implements Serializable{
    public static final long serialVersionUID = 20190215L;

    private long _id;
    private String name;
    private Client client;

    public Project(long _id, String name, Client client) {
        this._id = _id;
        this.name = name;
        this.client = client;
    }

    public long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Client getClient() {
        return client;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
