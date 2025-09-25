package com.ExpenseTracker.model;

import java.sql.Date;

public class Expense{
    private int eid;
    private int cid;
    private String description;
    private double amount;
    private String date;
    private String category;
    public Expense(){
        
    }
    public Expense(int id ,String description,double amount,String date,String category)
    {
        this.eid=id;
        this.description=description;
        this.date=date;
        this.amount=amount;
        this.category=category;
    }
    public Expense(int id ,String description,String date,double amount)
    {
        this.eid=id;
        this.description=description;
        this.date=date;
        this.amount=amount;
    }
    public Expense(String description,String date,double amount)
    {
        this.description=description;
        this.date=date;
        this.amount=amount;
    }
    public Expense(String description,String date,double amount,String cat){
        this.description=description;
        this.date=date;
        this.amount=amount;
        this.category=cat;
    }
    public int getId() {
        return eid;
    }
    public void setId(int id) {
        this.eid = eid;
    }
    public int getCid() {
        return cid;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
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
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}