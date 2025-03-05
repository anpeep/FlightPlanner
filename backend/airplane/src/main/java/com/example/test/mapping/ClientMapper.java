package com.example.test.mapping;

import com.example.test.dto.ClientDTO;
import com.example.test.model.Client;
import org.mapstruct.Mapper;

@Mapper
public interface ClientMapper {
    ClientDTO toDTO(Client client);
    Client toEntity(ClientDTO dto);
}
