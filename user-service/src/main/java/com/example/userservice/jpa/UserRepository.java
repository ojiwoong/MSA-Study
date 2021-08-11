package com.example.userservice.jpa;

import com.example.userservice.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByUserID(String userId);
    UserEntity findByEmail(String email);
}