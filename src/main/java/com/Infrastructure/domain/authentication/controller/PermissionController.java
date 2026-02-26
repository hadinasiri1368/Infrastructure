package com.infrastructure.domain.authentication.controller;

import com.infrastructure.constants.Consts;
import com.infrastructure.controller.BaseController;
import com.infrastructure.domain.authentication.dto.PermissionDto;
import com.infrastructure.domain.authentication.mapper.PermissionMapper;
import com.infrastructure.model.Permission;
import com.infrastructure.service.BaseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(Consts.DEFAULT_PREFIX_API_URL + Consts.DEFAULT_VERSION_API_URL + "/authentication/permission")
public class PermissionController extends BaseController<Permission, Long, PermissionDto> {
    public PermissionController(BaseService<Permission, Long> service, PermissionMapper mapper) {
        super(service, mapper);
    }
}
