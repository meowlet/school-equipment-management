package org.example.schoolequipment.model;

import java.util.Date;

public class Equipment {
    private String _id;
    private String equipmentName;
    private String description;
    private String status;
    private Location location;
    private Supplier supplier;
    private double price;
    private EquipmentType type;
    public Date createdAt;
    public Date updatedAt;
    public int __v;

    public String get_id() {
        return _id;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Location getLocation() {
        return location;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public EquipmentType getType() {
        return type;
    }

    public double getPrice() {
        return price;
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
}


