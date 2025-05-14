package com.fisa.appcard.repository;

import com.fisa.appcard.domain.AppCardKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppCardKeyRepository extends JpaRepository<AppCardKey, String> {}