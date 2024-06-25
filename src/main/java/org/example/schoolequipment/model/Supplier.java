package org.example.schoolequipment.model;

import java.util.Date;

public class Supplier {
    private String _id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String website;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    private int __v;

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getWebsite() {
        return website;
    }

    public String getNotes() {
        return notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public int get__v() {
        return __v;
    }
    // getters and setters
}
