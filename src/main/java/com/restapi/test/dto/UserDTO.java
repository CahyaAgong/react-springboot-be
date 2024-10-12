package com.restapi.test.dto;

public class UserDTO {
  private String email;
  private String name;
  private String password = null; // Nullable if using Google login
  private String googleId = null; // Nullable if using manual login

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getGoogleId() {
    return googleId;
  }
  public void setGoogleId(String googleId) {
    this.googleId = googleId;
  }

  // Getters and Setters
  
}

