package com.fisa.appcard.repository;

import com.fisa.appcard.domain.AuthenticationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthenticationSession, String> {

}