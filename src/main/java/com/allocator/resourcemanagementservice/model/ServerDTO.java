package com.allocator.resourcemanagementservice.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerDTO {
  float capacity;
  int users;
  long id;
  int state;

  public ServerDTO() {
  }

  public float getCapacity() {
    return capacity;
  }

  public void setCapacity(float capacity) {
    this.capacity = capacity;
  }

  public int getUsers() {
    return users;
  }

  public void setUsers(int users) {
    this.users = users;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }
}
