package com.cofisweak.cloudstorage.mapper;

import com.cofisweak.cloudstorage.domain.User;
import com.cofisweak.cloudstorage.web.dto.RegisterDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterDto dto);
}
