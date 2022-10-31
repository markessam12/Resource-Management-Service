package com.allocator.resourcemanagementservice.service;

import com.allocator.resourcemanagementservice.exception.RefusedRequestException;
import com.allocator.resourcemanagementservice.exception.ServerNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.jetbrains.annotations.NotNull;

public class ServersManager {
  private static ServersManager INSTANCE = null;

  private static final List<Server> servers = new ArrayList<>();

  private ServersManager(){
    super();
  }

  public static ServersManager getINSTANCE(){
    if(INSTANCE == null){
      INSTANCE = new ServersManager();
    }
    return INSTANCE;
  }

  public Server allocateServer(float requestedMemory)
      throws InterruptedException, RefusedRequestException {
    if(requestedMemory > Server.SERVER_SIZE)
      throw new RefusedRequestException("Request denied. The Maximum server this is 100GB.");
    Server allocatedServer = allocateToSuitableServer(requestedMemory);
    waitUntilServerActivate(allocatedServer);
    return allocatedServer;
  }

  private synchronized Server allocateToSuitableServer(float requestedMemory){
    Server availableServer = searchInExistingServers(requestedMemory);
    if(availableServer == null){
      availableServer = createNewServer();
    }
    availableServer.allocate(requestedMemory);
    return availableServer;
  }

  private Server searchInExistingServers(float requestedMemory){
    for (Server server:servers) {
      if(server.isAvailable(requestedMemory)){
        return server;
      }
    }
    return null;
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
