package com.example.event_driven_practice.repository;

import com.example.event_driven_practice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

}
