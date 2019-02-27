package com.ajna.workshiftlogger.model;

import java.io.Serializable;
import java.util.List;

public class Shift implements Serializable {
    public static final long serialVersionUID = 20190215L;

    private long _id;
    private long startTime;
    private long endTime;
    private long pause;
    private String projectName;
    private String clientName;
    private String clientOfficialName;
    private String clientAddress;
    private int basePayment;
    private int paymentType;
    private List<Factor> factors;


    public Shift(){
    }
    public Shift(long startTime, long endTime, long pause, String projectName, String clientName, String clientOfficialName, String clientAddress, int basePayment, int paymentType, List<Factor> factors) {
        this._id = _id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pause = pause;
        this.projectName = projectName;
        this.clientName = clientName;
        this.clientOfficialName = clientOfficialName;
        this.clientAddress = clientAddress;
        this.basePayment = basePayment;
        this.paymentType = paymentType;
        this.factors = factors;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientOfficialName() {
        return clientOfficialName;
    }

    public void setClientOfficialName(String clientOfficialName) {
        this.clientOfficialName = clientOfficialName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public int getBasePayment() {
        return basePayment;
    }

    public void setBasePayment(int basePayment) {
        this.basePayment = basePayment;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public List<Factor> getFactors() {
        return factors;
    }

    public void setFactors(List<Factor> factors) {
        this.factors = factors;
    }

    public long calculateDuration(){
        return endTime - startTime - pause;
    }
}
