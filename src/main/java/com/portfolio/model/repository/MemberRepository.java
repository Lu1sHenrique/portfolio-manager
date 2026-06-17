package com.portfolio.model.repository;

import com.portfolio.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);
}
