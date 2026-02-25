package com.infrastructure.domain.permission.mapper;

import com.infrastructure.domain.permission.dto.PermissionDto;
import com.infrastructure.mapper.BaseMapper;
import com.infrastructure.model.Permission;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PermissionMapper extends BaseMapper<Permission, PermissionDto> {
}
