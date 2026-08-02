package com.jsp.ojpms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.ojpms.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
