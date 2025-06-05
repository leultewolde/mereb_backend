package app.mereb.mereb_backend.user;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toDto(User user);
    List<UserResponseDTO> toDtoList(List<User> users);
}
