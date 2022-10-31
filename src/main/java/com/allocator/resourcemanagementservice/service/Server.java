package com.allocator.resourcemanagementservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server{
  public static final float SERVER_SIZE = 100;
  public static final int SERVER_STARTUP_DELAY = 2000;
  public static final int CREATING_STATE = 0;
  public static final int ACTIVE_STATE = 1;

  float capacity;
  int users;
  long id;
  int state;
  List<CountDownLatch> waitingLatches = new ArrayList<>();

  public Server() throws InterruptedException {
    super();
    this.state = CREATING_STATE;
    this.capacity = SERVER_SIZE;
    this.id = new Date().getTime();
    startServer();
  }

  public boolean isAvailable(float requiredMemory){
    return capacity >= requiredMemory;
  }

  public float allocate(float memory){
    capacity -= memory;
    users++;
    return capacity;
  }

  private void startServer() throws InterruptedException {
    ScheduledExecutorService scheduler
        = Executors.newSingleThreadScheduledExecutor();
    Runnable task = () -> {
      setState(ACTIVE_STATE);
      notifyAllWaitingLatches();
    };
    scheduler.schedule(task, SERVER_STARTUP_DELAY, TimeUnit.MILLISECONDS);
    scheduler.shutdown();
  }

  private synchronized void notifyAllWaitingLatches(){
    for (CountDownLatch latch : waitingLatches) {
      latch.countDown();
    }
    waitingLatches.clear();
  }

  public synchronized void addWaitingLatch(CountDownLatch latch){
    waitingLatches.add(latch);
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

  public boolean isActive(){
    return state == ACTIVE_STATE;
  }
}
