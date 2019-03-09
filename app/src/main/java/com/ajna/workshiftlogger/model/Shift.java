package com.ajna.workshiftlogger.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Shift implements Serializable {
    private static final String TAG = "Shift";

    public static final long serialVersionUID = 20190215L;

    private long _id;
    private long startTime;
    private long endTime;
    private long pause;
    private String projectName;
    private long clientId;
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

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
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
        Log.d(TAG, "setFactors: factors.size() = " + factors.size());
        this.factors = new ArrayList<>(factors);
    }
    public Factor getActualFactorInPercent(){
        long workHours = calculateDuration() / 3600000;
        int factorValue = 100;
        int factorHour = (int) workHours;

        Log.d(TAG, "calculatePayment: factors.size() = " + factors.size());
        if(factors != null && factors.size() > 0){
            for(int i =0; i<factors.size(); i++){
                Factor factor = factors.get(i);

                Log.d(TAG, "calculatePayment: factor: h: " + factor.getHours() + " v: " + factor.getFactorInPercent());
                if(factor.getHours() < workHours){
                    factorValue = factor.getFactorInPercent();
                    factorHour = factor.getHours();
                } else if(factor.getHours() >= workHours){
                    break;
                }
            }
        }
        return new Factor(factorHour, factorValue);
    }

    public long calculateDuration(){
        long pauseMillis = pause * 60000;
        return endTime - startTime - pauseMillis;
    }

    public double calculatePayment(){
        // TODO calculate also with rounding to minutes or half an hour depending on the settings
        long workHours = calculateDuration() / 3600000;

        Factor factor = getActualFactorInPercent();

        if(paymentType == 0){
            // flat payment
            return basePayment * factor.getFactorInPercent() /100.0;
        } else {
            // payment per hour
            long overhours = workHours - factor.getHours();
            return (factor.getHours() * basePayment) + (overhours * basePayment * factor.getFactorInPercent() / 100.0);
        }
    }
}
