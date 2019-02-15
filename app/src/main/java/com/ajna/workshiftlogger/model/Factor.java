package com.ajna.workshiftlogger.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Factor implements Serializable,Comparable<Factor> {
    public static final long serialVersionUID = 20190215L;

    private int hours;
    private int factorInPercent;

    public Factor(int hours, int factorInPercent) {
        this.hours = hours;
        this.factorInPercent = factorInPercent;
    }

    public int getHours() {
        return hours;
    }

    public int getFactorInPercent() {
        return factorInPercent;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setFactorInPercent(int factorInPercent) {
        this.factorInPercent = factorInPercent;
    }

    @Override
    public int compareTo(@NonNull Factor factor) {
        return Integer.compare(this.getHours(), factor.getHours());
    }
}
