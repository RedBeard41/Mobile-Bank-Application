package com.bank.izbank.Job;

import com.google.gson.annotations.Expose;

public class Job {

    @Expose private String name;
    @Expose private String maxCreditAmount;
    @Expose private String maxCreditInstallment;
    @Expose private String interestRate;

    public Job(String name, String maxCreditAmount, String maxCreditInstallment, String interestRate) {
        this.name = name;
        this.maxCreditAmount = maxCreditAmount;
        this.maxCreditInstallment = maxCreditInstallment;
        this.interestRate = interestRate;
    }

    public Job() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaxCreditAmount() {
        return maxCreditAmount;
    }

    public void setMaxCreditAmount(String maxCreditAmount) {
        this.maxCreditAmount = maxCreditAmount;
    }

    public String getMaxCreditInstallment() {
        return maxCreditInstallment;
    }

    public void setMaxCreditInstallment(String maxCreditInstallment) {
        this.maxCreditInstallment = maxCreditInstallment;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }
}
