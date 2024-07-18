package org.example.schoolequipment.model;

import java.util.List;

public class Permission {
    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    private Resource resource;
    private List<Action> actions;

    public Permission(Resource resource, List<Action> actions) {
        this.resource = resource;
        this.actions = actions;
    }

    @Override
    public String toString() {
        //the body must be in this format {
        //    "name": "Equipment Manager",
        //    "description": "Manage equipment, suppliers, types, and locations",
        //    "permissions": [
        //        {
        //            "resource": "equipment",
        //            "actions": [
        //                "read",
        //                "write",
        //                "delete"
        //            ]
        //        },
        //        {
        //            "resource": "supplier",
        //            "actions": [
        //                "read",
        //                "write"
        //            ]
        //        },
        //        {
        //            "resource": "equipment-type",
        //            "actions": [
        //                "read",
        //                "write"
        //            ]
        //        },
        //        {
        //            "resource": "loan-history",
        //            "actions": [
        //                "read",
        //                "write"
        //            ]
        //        },
        //        {
        //            "resource": "maintenance",
        //            "actions": [
        //                "read",
        //                "write"
        //            ]
        //        },
        //        {
        //            "resource": "location",
        //            "actions": [
        //                "read",
        //                "write"
        //            ]
        //        }
        //    ]
        //}
        return String.format("""
                {
                    "resource": "%s",
                    "actions": %s
                }""", resource.getValue(), actions);
    }

}
