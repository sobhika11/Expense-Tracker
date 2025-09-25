package com.ExpenseTracker.model;

import java.sql.Date;

public class Expense{
    private int cid;
    private String description;
    private int amount;
    private String date;
    
    public int getCid() {
        return cid;
    }
    public void setCid(int cid) {
        this.cid = cid;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Expense(int id ,String description,String date,int amount)
    {
        this.cid=id;
        this.description=description;
        this.date=date;
        this.amount=amount;
    }
    public Expense(String description,String date,int amount)
    {
        this.description=description;
        this.date=date;
        this.amount=amount;
    }

}