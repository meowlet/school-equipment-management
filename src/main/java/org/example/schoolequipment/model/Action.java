package org.example.schoolequipment.model;

public enum Action {
    READ("read"),
    WRITE("write"),
    DELETE("delete");

    private final String value;

    Action(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }
}