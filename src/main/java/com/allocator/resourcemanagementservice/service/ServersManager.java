package com.allocator.resourcemanagementservice.service;

import java.util.ArrayList;
import java.util.List;

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

  public synchronized Server allocateServer(float requestedMemory) throws InterruptedException {
    for (Server server:servers) {
      if(server.isAvailable(requestedMemory)){
        server.allocate(requestedMemory);
        return server;
      }
    }
    Server newServer = new Server();
    newServer.allocate(requestedMemory);
    servers.add(newServer);
    return newServer;
  }

  public List<Server> getServers(){
    return servers;
  }
}
