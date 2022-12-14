package com.allocator.resourcemanagementservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Server.
 */
public class Server{

  /**
   * The maximum server size.
   */
  public static final float SERVER_SIZE = 100;
  /**
   * The time taken until the newly created server become in active state.
   */
  public static final int SERVER_STARTUP_DELAY = 5000;
  private static final Logger LOGGER =
      LoggerFactory.getLogger(Server.class);

  private float capacity;
  private int users;
  private final long id;

  private State state;

  /**
   * A list of latches representing all clients waiting for the server to be active
   */
  List<CountDownLatch> waitingLatches =
      Collections.synchronizedList(new ArrayList<>());

  /**
   * Instantiates a new Server.
   */
  public Server(){
    super();
    this.state = State.STARTING;
    this.capacity = SERVER_SIZE;
    this.id = new Date().getTime();
    startServer();
  }

  /**
   * Checks if the current server have enough memory for the request
   *
   * @param requiredMemory the required memory by the request
   * @return the boolean result
   */
  public boolean isAvailable(float requiredMemory){
    return capacity >= requiredMemory;
  }

  /**
   * Allocate the requested memory from the server capacity to the user.
   *
   * @param memory the memory
   */
  public void allocate(float memory){
    capacity -= memory;
    users++;
  }


  //not sure if this method is multithreaded in a correct way
  private void startServer() {
    LOGGER.info("Initializing server {} in CREATING state", this.id);
    ScheduledExecutorService scheduler
        = Executors.newSingleThreadScheduledExecutor();
    Runnable task = () -> {
      setState(State.ACTIVE);
      LOGGER.info("Server {} was created and in ACTIVE state", this.id);
      notifyAllWaitingLatches();
    };
    scheduler.schedule(task, SERVER_STARTUP_DELAY, TimeUnit.MILLISECONDS);
    scheduler.shutdown();
  }

  //waiting latches list should be thread save, new latches only added when the server is in starting state
  //And notifying all latches only occurs after the server is set to active state
  //So, we guaranteed that no new latch will be added during the notification of the existing latches
  private void notifyAllWaitingLatches(){
    waitingLatches.forEach(CountDownLatch::countDown);
    waitingLatches.clear();
  }

  /**
   * Add new latch to the latches list.
   * This latch represents a user's request waiting until the server is active.
   *
   * @param latch the latch
   */
  public void addWaitingLatch(CountDownLatch latch){
    waitingLatches.add(latch);
  }

  /**
   * Gets the current server capacity.
   *
   * @return the capacity
   */
  public float getCapacity() {
    return capacity;
  }

  /**
   * Gets all current users of server.
   *
   * @return the users
   */
  public int getUsers() {
    return users;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * Gets current server state.
   *
   * @return the state
   */
  public State getState() {
    return state;
  }

  /**
   * Sets state.
   *
   * @param state the state
   */
  public void setState(State state) {
    this.state = state;
  }

  /**
   * Checks if the server is active.
   *
   * @return the boolean
   */
  public boolean isActive(){
    return state == State.ACTIVE;
  }
}
