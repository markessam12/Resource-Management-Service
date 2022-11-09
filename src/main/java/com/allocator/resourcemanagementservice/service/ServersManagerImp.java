package com.allocator.resourcemanagementservice.service;

import com.allocator.resourcemanagementservice.exception.RefusedRequestException;
import com.allocator.resourcemanagementservice.exception.ServerNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of ServersManager Interface
 */
public class ServersManagerImp implements ServersManager{

  private static final List<Server> servers = new ArrayList<>();

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ServersManagerImp.class);

  private ServersManagerImp(){
    super();
  }

  private static final class InstanceHolder {
    private static final ServersManagerImp INSTANCE = new ServersManagerImp();
  }

  /**
   * Get the Singleton instance of the serversManager.
   *
   * @return the serversManager Instance
   */
  public static ServersManagerImp getINSTANCE(){
    return InstanceHolder.INSTANCE;
  }

  public Server allocateServer(float requestedMemory)
      throws RefusedRequestException, InterruptedException {
    LOGGER.info("Requested {}GB of memory", requestedMemory);
    if(requestedMemory > Server.SERVER_SIZE || requestedMemory < 1){
      LOGGER.warn("Maximum server size is {}GB and minimum request is 1GB, can't allocate {}GB", Server.SERVER_SIZE, requestedMemory);
      throw new RefusedRequestException("Request denied. The Maximum server this is 100GB and minimum request is 1GB.");
    }
    Server allocatedServer = allocateToSuitableServer(requestedMemory);
    waitUntilServerActivate(allocatedServer);
    return allocatedServer;
  }

  private synchronized Server allocateToSuitableServer(float requestedMemory){
    Server availableServer = findFreeServer(requestedMemory);
    String newlyCreated = "";
    if(availableServer == null){
      availableServer = createNewServer();
      newlyCreated = "the newly created ";
    }
    availableServer.allocate(requestedMemory);
    LOGGER.info("Allocated {}GB of memory from {}server {}", requestedMemory, newlyCreated, availableServer.getId());
    return availableServer;
  }

  private Server findFreeServer(float requestedMemory){
    // #Iterators aren't thread safe. So, the old for-each implementation was replaced
//    #Another implementation, not sure if it's thread safe/faster to execute
    synchronized (servers){
      Optional<Server> freeServer = servers.stream()
          .filter(server -> server.isAvailable(requestedMemory))
          .findAny();
      return freeServer.orElse(null);
    }
  }

  private Server createNewServer(){
    Server newServer = new Server();
    servers.add(newServer);
    return newServer;
  }

  private void waitUntilServerActivate(@NotNull Server allocatedServer) throws InterruptedException {
    if (!allocatedServer.isActive()){
      CountDownLatch latch = new CountDownLatch(1);
      allocatedServer.addWaitingLatch(latch);
      latch.await();
      LOGGER.info("Notification to user that his server {} is active", allocatedServer.getId());
    }
  }

  public List<Server> getServers() throws ServerNotFoundException{
    if(servers.isEmpty())
      throw new ServerNotFoundException("No servers allocated in the system.");
    return servers;
  }

  public Server getServer(long id) throws ServerNotFoundException {
    for (Server server : servers) {
      if(server.getId() == id)
        return server;
    }
    // arriving here means that server wasn't found, so we throw an error message
    throw new ServerNotFoundException("No server exists with that id");
  }
}
