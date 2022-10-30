package com.allocator.resourcemanagementservice.service;

import java.util.Date;
public class Server implements Runnable{
  public static final float SERVER_SIZE = 100;
  public static final int SERVER_STARTUP_DELAY = 20000;
  public static final int CREATING_STATE = 0;
  public static final int ACTIVE_STATE = 1;

  float capacity;
  int users;
  long id;
  int state;

  public Server(){
    super();
    this.state = CREATING_STATE;
    this.capacity = SERVER_SIZE;
    this.id = new Date().getTime();
    new Thread(this).start();
  }

  public boolean isAvailable(float requiredMemory){
    return capacity >= requiredMemory;
  }

  public float allocate(float memory){
    capacity -= memory;
    users++;
    return capacity;
  }

  @Override
  public void run() {
    try {
      startServer();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void startServer() throws InterruptedException {
    Thread.sleep(SERVER_STARTUP_DELAY);
    this.state = ACTIVE_STATE;
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
