package com.sparta.msa_exam.client.auth;

import com.sparta.msa_exam.client.auth.core.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
