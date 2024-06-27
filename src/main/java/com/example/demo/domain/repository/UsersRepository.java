package com.example.demo.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.domain.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
  Users findByMail(String mail);

  boolean existsByMail(String mail);
}

