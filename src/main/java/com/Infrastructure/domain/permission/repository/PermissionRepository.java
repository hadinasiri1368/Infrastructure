package com.infrastructure.domain.permission.repository;


import com.infrastructure.domain.authentication.dto.UserPermissionDto;
import com.infrastructure.model.Permission;
import com.infrastructure.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
    @Query("""
                select p,up.user.id as userId from userPermission up
                    inner join permission p on p.id=up.permission.id
            """)
    List<UserPermissionDto> findAllPermissionFromUser();

    @Query("""
                select new com.infrastructure.domain.authentication.dto.UserPermissionDto(p,ur.user.id) from userRole ur
                    inner join rolePermission rp on rp.role.id=ur.role.id
                    inner join permission p on p.id=rp.permission.id
            """)
    List<UserPermissionDto> findAllPermissionFromRole();

    @Query("""
                select p,ugd.user.id as userId from userGroupDetail ugd
                    inner join userGroup ug on ug.id=ugd.userGroup.id
                    inner join userGroupRole ugr on ugr.userGroup.id=ugd.userGroup.id
                    inner join rolePermission rp on rp.role.id=ugr.role.id
                    inner join permission p on p.id=rp.permission.id
            """)
    List<UserPermissionDto> findAllPermissionFromGroup();
}
