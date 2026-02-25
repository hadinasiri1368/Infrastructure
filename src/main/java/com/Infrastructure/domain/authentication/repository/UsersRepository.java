package com.infrastructure.domain.authentication.repository;


import com.infrastructure.model.Users;
import com.infrastructure.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends BaseRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}
