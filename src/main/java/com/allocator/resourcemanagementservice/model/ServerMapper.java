package com.allocator.resourcemanagementservice.model;

import com.allocator.resourcemanagementservice.service.Server;
import java.util.ArrayList;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServerMapper {
  ServerMapper INSTANCE = Mappers.getMapper(ServerMapper.class);

  ServerDTO serverToDto(Server server);
  ArrayList<ServerDTO> serverToDto(ArrayList<Server> server);
}
