package com.allocator.resourcemanagementservice.controller;

import com.allocator.resourcemanagementservice.exception.RefusedRequestException;
import com.allocator.resourcemanagementservice.exception.ServerNotFoundException;
import com.allocator.resourcemanagementservice.model.ErrorMessage;
import com.allocator.resourcemanagementservice.model.ServerDTO;
import com.allocator.resourcemanagementservice.model.ServerMapper;
import com.allocator.resourcemanagementservice.service.ServersManagerImp;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;

@Path("/servers")
public class ServerController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getExistingServers(){
    List<ServerDTO> serversDTO;
    try {
      serversDTO = ServerMapper.INSTANCE.serverListToDto(
          ServersManagerImp.getINSTANCE().getServers());
    }catch (ServerNotFoundException e){
      return Response.status(Response.Status.NOT_FOUND)
          .entity(new ErrorMessage(e.getMessage(), Status.NOT_FOUND.getStatusCode()))
          .build();
    }
    return Response.ok().entity(serversDTO).build();
  }

  @POST
  @Path("/{size}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allocateServer(@PathParam("size") float size)
      throws InterruptedException {
    ServerDTO allocatedServer;
    try{
      allocatedServer = ServerMapper.INSTANCE.serverToDto(
          ServersManagerImp.getINSTANCE().allocateServer(size));
    } catch (RefusedRequestException e) {
      return Response.status(Status.METHOD_NOT_ALLOWED)
          .entity(new ErrorMessage(e.getMessage(), Status.METHOD_NOT_ALLOWED.getStatusCode()))
          .build();
    }
    return Response.ok().entity(allocatedServer).build();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getExistingServers(@PathParam("id") long id){
    ServerDTO serverDTO;
    try {
      serverDTO = ServerMapper.INSTANCE.serverToDto(
          ServersManagerImp.getINSTANCE().getServer(id));
    }catch (ServerNotFoundException e){
      return Response.status(Response.Status.NOT_FOUND)
          .entity(new ErrorMessage(e.getMessage(), Status.NOT_FOUND.getStatusCode()))
          .build();
    }
    return Response.ok().entity(serverDTO).build();
  }
}