package com.restapi.test.dto;


public class MemberDTO {
  private String name;
  private String position;
  private String reportsTo; // ID superior (optional)
  private String pictureUrl;
  private String userId;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getPosition() {
    return position;
  }
  public void setPosition(String position) {
    this.position = position;
  }
  public String getReportsTo() {
    return reportsTo;
  }
  public void setReportsTo(String reportsTo) {
    this.reportsTo = reportsTo;
  }
  public String getPictureUrl() {
    return pictureUrl;
  }
  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }

  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }

  // Getters and Setters
  
}

