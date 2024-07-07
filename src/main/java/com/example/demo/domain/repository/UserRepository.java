package com.example.demo.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByMail(String mail);

  boolean existsByMail(String mail);

  User findByUsername(String usernamel);
}

