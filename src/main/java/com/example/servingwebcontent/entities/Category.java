package com.example.servingwebcontent.entities;

import javax.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    private String category;
    @Column(name = "parent")
    private String parentCategory;
    @Column(name = "expiration_state")
    private String expirationState;
    @Column(name = "has_child")
    private boolean hasChild;

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
}
