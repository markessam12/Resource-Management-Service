package com.allocator.resourcemanagementservice.model;

import com.allocator.resourcemanagementservice.service.Server;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * The interface Server mapper is a mapStruct interface to map Server to ServerDTO
 */
@Mapper
public interface ServerMapper {
  ServerMapper INSTANCE = Mappers.getMapper(ServerMapper.class);

  ServerDTO serverToDto(Server server);
  ArrayList<ServerDTO> serverListToDto(List<Server> server);
}
