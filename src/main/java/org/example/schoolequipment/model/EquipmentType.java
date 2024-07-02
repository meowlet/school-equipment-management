package org.example.schoolequipment.model;


import com.google.gson.Gson;

import java.util.Date;

public class EquipmentType {
    private String _id;
    private String typeCode;
    private String typeName;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private int __v;
    // getters and setters

    public static EquipmentType fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, EquipmentType.class);
    }

    public String get_id() {
        return _id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
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

    @Override
    public String toString() {
        return typeName;
    }

    public String getID() {
        return _id;
    }
}
