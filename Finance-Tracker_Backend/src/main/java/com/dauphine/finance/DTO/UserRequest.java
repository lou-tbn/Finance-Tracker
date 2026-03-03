package com.dauphine.finance.DTO;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public class UserRequest {
    private String username;
    private String email;
    private String password;
    private LocalDateTime dateCreated;

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
