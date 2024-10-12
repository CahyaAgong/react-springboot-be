package com.restapi.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restapi.test.dto.MemberDTO;
import com.restapi.test.model.Member;
import com.restapi.test.responses.ApiResponse;
import com.restapi.test.service.MemberService;

import java.util.Optional;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Member>>> getAllMembers(
        @RequestParam(defaultValue = "") String searchTerm,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<Member> members = memberService.findAll(searchTerm, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("members", members.getContent());
            response.put("currentPage", members.getNumber());
            response.put("totalItems", members.getTotalElements());
            response.put("totalPages", members.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(true, "Members retrieved successfully", members));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ApiResponse<>(false, "Failed to retrieve members: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getMemberById(@PathVariable String id) {
        try {
            Optional<Member> member = memberService.findById(id);
            return member
                .map(m -> ResponseEntity.ok(new ApiResponse<>(true, "Member retrieved successfully", m)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Member not found with ID: " + id, null)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ApiResponse<>(false, "Failed to retrieve member: " + e.getMessage(), null));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Member>> createMember(@RequestBody MemberDTO memberDTO) {
        try {
            Member member = memberService.createMember(memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(new ApiResponse<>(true, "Member created successfully", member));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ApiResponse<>(false, "Failed to create member: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> updateMember(@PathVariable String id, @RequestBody MemberDTO memberDTO) {
        try {
            Member updatedMember = memberService.updateMember(id, memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(new ApiResponse<>(true, "Member updated successfully", updatedMember));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ApiResponse<>(false, "Failed to update member: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable String id) {
        try {
            memberService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ApiResponse<>(false, "Failed to delete member: " + e.getMessage(), null));
        }
    }
}

