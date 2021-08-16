package com.alon.moveoapp.Models;

import java.io.Serializable;

public class Doctor extends User implements Serializable {

    private boolean isAvailable;

    public Doctor() {
    }

    public Doctor(String firstName, String lastName, String email, String userType, boolean isAvailable) {
        super(firstName, lastName, email, userType);
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
