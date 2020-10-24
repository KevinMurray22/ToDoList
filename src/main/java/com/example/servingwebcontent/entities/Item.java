package com.example.servingwebcontent.entities;

import javax.persistence.*;
import java.util.Calendar;

@Entity
public class Item {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long expiration;
    private String category;
    private String expirationState;
    private int daysToWarn;
    private String repeatState;
    @Column(name="user_name")
    private String userName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getExpiration() {
        return expiration;
    }

    public String getExpirationState() {
        return expirationState;
    }

    public void setExpirationState(String expirationState) {
        this.expirationState = expirationState;

    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public int getDaysToWarn() {
        return daysToWarn;
    }

    public void setDaysToWarn(int daysToWarn) {
        this.daysToWarn = daysToWarn;
    }

    public String getRepeatState() {
        return repeatState;
    }

    public void setRepeatState(String repeatState) {
        this.repeatState = repeatState;
    }

    public String getTime(){
        Calendar calendarItem = Calendar.getInstance();
        calendarItem.setTimeInMillis(expiration);
        return calendarItem.getTime().toString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString(){
        return "Id: " + getId() + ", Name: " + getName() + ", Expiration date: " + getExpiration();
    }
}
