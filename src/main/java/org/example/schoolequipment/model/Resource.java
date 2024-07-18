package org.example.schoolequipment.model;

public enum Resource {
    USER("user"),
    EQUIPMENT("equipment"),
    SUPPLIER("supplier"),
    EQUIPMENT_TYPE("equipment-type"),
    LOAN_HISTORY("loan-history"),
    MAINTENANCE("maintenance"),
    LOCATION("location"),
    ROLE("role");

    private final String value;

    Resource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
