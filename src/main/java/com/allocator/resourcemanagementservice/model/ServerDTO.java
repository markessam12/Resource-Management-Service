package com.allocator.resourcemanagementservice.model;

import com.allocator.resourcemanagementservice.service.State;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Server DTO (Data Transfer Object) is the object sent and received from the rest API
 */
@XmlRootElement
public class ServerDTO {
  float capacity;
  int users;
  long id;
  State state;


  /**
   * This constructor is used by the mapStruct in converting the Server to ServerDTO.
   */
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

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }
}
