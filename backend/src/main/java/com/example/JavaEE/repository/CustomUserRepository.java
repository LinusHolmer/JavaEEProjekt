package com.example.JavaEE.repository;

import com.example.JavaEE.model.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository extends MongoRepository<CustomUser, String> {
    CustomUser findByUsername(String username);
}
