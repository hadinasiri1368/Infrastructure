package com.infrastructure.domain.authentication.repository;


import com.infrastructure.domain.authentication.dto.UserPermissionDto;
import com.infrastructure.model.Permission;
import com.infrastructure.model.Users;
import com.infrastructure.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
    @Query("""
                select p,up.user.id userId from userPermission up
                    inner join permission p on p.id=up.permission.id
                union all
                select p,ur.user.id userId from userRole ur\s
                    inner join rolePermission rp on rp.role.id=ur.role.id
                    inner join permission p on p.id=rp.permission.id
                union all
                select p,ugd.user.id userId from userGroupDetail ugd
                    inner join userGroup ug on ug.id=ugd.userGroup.id
                    inner join userGroupRole ugr on ugr.userGroup.id=ugd.userGroup.id
                    inner join rolePermission rp on rp.role.id=ugr.role.id
                    inner join permission p on p.id=rp.permission.id
            """)
    List<UserPermissionDto> findAllUserPermission();
}
