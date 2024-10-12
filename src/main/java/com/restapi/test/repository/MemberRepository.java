package com.restapi.test.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.restapi.test.model.Member;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

  Page<Member> findByNameContaining(String name, Pageable pageable);

}

