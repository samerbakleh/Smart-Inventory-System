package com.inventory.app.model.enums;

public enum Role {

    MANAGER,
    SUPERVISOR,
    EMPTY;

    public static String roleToText(Role role) {
        if (role == MANAGER) {
            return "Manager";
        }
        if (role == SUPERVISOR) {
            return "Supervisor";
        }
        return "Empty";
    }

    public static Role textToRole(String roleText) {
        if (roleText == null) {
            return EMPTY;
        }

        if (roleText.equals("Manager")) {
            return MANAGER;
        }
        if (roleText.equals("Supervisor")) {
            return SUPERVISOR;
        }
        return EMPTY;
    }
}
