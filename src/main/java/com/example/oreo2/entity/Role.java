package com.example.oreo2.entity;

public enum Role {
    CENTRAL, BRANCH;

    public String authority() {
        return "ROLE_" + this.name();
    }
}
