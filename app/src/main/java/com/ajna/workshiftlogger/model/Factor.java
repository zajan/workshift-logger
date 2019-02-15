package com.ajna.workshiftlogger.model;

import java.io.Serializable;

public class Factor implements Serializable {
    public static final long serialVersionUID = 20190215L;

    private long _id;
    private int hours;
    private int factorInPercent;
    private long clientId;

    public Factor(long _id, int hours, int factorInPercent, long clientId) {
        this._id = _id;
        this.hours = hours;
        this.factorInPercent = factorInPercent;
        this.clientId = clientId;
    }

    public long getId() {
        return _id;
    }

    public int getHours() {
        return hours;
    }

    public int getFactorInPercent() {
        return factorInPercent;
    }

    public long getClientId() {
        return clientId;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setFactorInPercent(int factorInPercent) {
        this.factorInPercent = factorInPercent;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }
}
