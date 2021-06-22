package com.example.justchat;

public class Contactde {
    private String name,status,profile;

    public Contactde(String name, String status, String profile) {
        this.name = name;
        this.status = status;
        this.profile = profile;
    }

    public Contactde() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}

