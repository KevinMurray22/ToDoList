package com.example.servingwebcontent.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy= GenerationType.AUTO) private Long id;
    private String category;
    @Column(name = "parent")
    private String parentCategory;
    @Column(name = "expiration_state")
    private String expirationState;
    @Column(name = "has_child")
    private boolean hasChild;
    @Column(name = "user_name")
    private String userName;

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpirationState() {
        return expirationState;
    }

    public void setExpirationState(String expirationState) {
        this.expirationState = expirationState;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
