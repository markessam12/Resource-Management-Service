package com.allocator.resourcemanagementservice.service;

import com.allocator.resourcemanagementservice.exception.RefusedRequestException;
import com.allocator.resourcemanagementservice.exception.ServerNotFoundException;
import java.util.List;

/**
 * The interface for Servers Manager service.
 * The ServersManager is the service responsible for creating and allocating servers upon requests
 */
public interface ServersManager {

  /**
   * Method to allocate free server memory upon request,
   * or create a new server in case existing ones aren't of enough memory.
   *
   * @param requestedMemory the memory size requested
   * @return the server allocated to the request
   */
  Server allocateServer(float requestedMemory) throws RefusedRequestException, InterruptedException;

  /**
   * Gets all currently running servers.
   *
   * @return the servers list
   * @throws ServerNotFoundException Exception thrown in case no servers found
   */
  List<Server> getServers() throws ServerNotFoundException;

  /**
   * Gets server.
   *
   * @param id the server id
   * @return the server
   * @throws ServerNotFoundException Exception thrown in case server of given id wasn't found
   */
  Server getServer(long id) throws ServerNotFoundException;
}
