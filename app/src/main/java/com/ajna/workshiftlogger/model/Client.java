package com.ajna.workshiftlogger.model;

import java.io.Serializable;
import java.util.List;

public class Client implements Serializable {
    public static final long serialVersionUID = 20190215L;

    private long _id;
    private String name;
    private String officialName;
    private String address;
    /**
     * paymentType = 0  -> flat payment
     * paymentType = 1 -> payment per hour
     */
    private int paymentType;
    private int basicPayment;
    private List<Factor> factors;

    public Client(String name, String officialName, String address, int paymentType, int basicPayment, List<Factor> factors) {
        this.name = name;
        this.officialName = officialName;
        this.address = address;
        this.paymentType = paymentType;
        this.basicPayment = basicPayment;
        this.factors = factors;
    }

    public long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getAddress() {
        return address;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public int getBasicPayment() {
        return basicPayment;
    }

    public List<Factor> getFactors() {
        return factors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public void setBasicPayment(int basicPayment) {
        this.basicPayment = basicPayment;
    }

    public void setFactors(List<Factor> factors) {
        this.factors = factors;
    }
}
