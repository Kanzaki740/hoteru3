package com.example.moattravel3.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moattravel3.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
