package com.allocator.resourcemanagementservice.controller;

import com.allocator.resourcemanagementservice.model.ServerDTO;
import com.allocator.resourcemanagementservice.model.ServerMapper;
import com.allocator.resourcemanagementservice.service.Server;
import com.allocator.resourcemanagementservice.service.ServersManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/servers")
public class ServerController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Server> getExistingServers(){
    return ServersManager.getINSTANCE().getServers();
  }

  @GET
  @Path("/{size}")
  @Produces(MediaType.APPLICATION_JSON)
  public ServerDTO allocateServer(@PathParam("size") float size) throws InterruptedException {
    return ServerMapper.INSTANCE.serverToDto(
        ServersManager.getINSTANCE().allocateServer(size));
  }
}