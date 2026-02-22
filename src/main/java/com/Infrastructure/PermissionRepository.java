package com.infrastructure;

import com.infrastructure.model.Permission;
import com.infrastructure.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
    Permission findByName(String name);
}
