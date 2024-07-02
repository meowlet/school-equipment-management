package org.example.schoolequipment.model;


import java.util.Date;

public class Location {
    private String _id;
    private String locationName;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private int __v;

    public String get_id() {
        return _id;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getDescription() {
        return description;
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

    @Override
    public String toString() {
        return locationName;
    }

    public String getID() {
        return _id;
    }
}

