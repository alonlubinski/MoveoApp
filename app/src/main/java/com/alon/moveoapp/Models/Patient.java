package com.alon.moveoapp.Models;

import java.io.Serializable;

public class Patient extends User implements Serializable {

    public Patient() {
    }

    public Patient(String firstName, String lastName, String email, String userType) {
        super(firstName, lastName, email, userType);
    }
}
