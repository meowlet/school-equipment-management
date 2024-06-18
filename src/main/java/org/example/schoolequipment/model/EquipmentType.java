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
}
