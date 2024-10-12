package com.restapi.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.restapi.test.dto.MemberDTO;
import com.restapi.test.model.Member;
import com.restapi.test.repository.MemberRepository;
import com.restapi.test.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository; 

    public Page<Member> findAll(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Member> membersPage;

        if (searchTerm != null && !searchTerm.isEmpty()) {
            membersPage = memberRepository.findByNameContaining(searchTerm, pageable);
        } else {
            membersPage = memberRepository.findAll(pageable);
        }

        membersPage.getContent().forEach(member -> {
            if (member.getReportsTo() != null) {
                Optional<Member> reportsToMember = memberRepository.findById(member.getReportsTo());
                reportsToMember.ifPresent(reports -> member.setReportsToName(reports.getName()));
            }
        });

        return membersPage;
    }

    public Optional<Member> findById(String memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);

        memberOptional.ifPresent(member -> {
            if (member.getReportsTo() != null) {
                Optional<Member> reportsToMember = memberRepository.findById(member.getReportsTo());
                reportsToMember.ifPresent(reports -> member.setReportsToName(reports.getName()));
            }
        });

        return memberOptional;
    }

    public Member createMember(MemberDTO memberDTO) {
        Member member = new Member();
        member.setName(memberDTO.getName());
        member.setPosition(memberDTO.getPosition());
        member.setReportsTo(memberDTO.getReportsTo());
        member.setPictureUrl(memberDTO.getPictureUrl());
        member.setUserId(memberDTO.getUserId());
        member.setCreatedAt(LocalDateTime.now());
        return memberRepository.save(member);
    }

    public void deleteById(String memberId) throws Exception {
        Optional<Member> memberOptional = memberRepository.findById(memberId);

        if (!memberOptional.isPresent()) throw new Exception("Member not found with id: " + memberId);

        Member member = memberOptional.get();
        String userId = member.getUserId();  

        memberRepository.deleteById(memberId);

        if (userId != null) userRepository.deleteById(userId);
    }

    public Member updateMember(String id, MemberDTO memberDTO) {
        if (!memberRepository.existsById(id)) return null;

        Member member = new Member();
        member.setId(id);
        member.setName(memberDTO.getName());
        member.setPosition(memberDTO.getPosition());
        member.setReportsTo(memberDTO.getReportsTo());
        member.setPictureUrl(memberDTO.getPictureUrl());
        member.setUserId(memberDTO.getUserId());
        member.setCreatedAt(LocalDateTime.now());

        return memberRepository.save(member);
    }
}

