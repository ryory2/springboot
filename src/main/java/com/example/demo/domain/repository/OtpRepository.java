package com.example.demo.domain.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.domain.model.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
  Optional<Otp> findByMail(String mail);

  void deleteByMail(String mail);
}

