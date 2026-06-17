package com.portfolio.controller;

import com.portfolio.dto.request.ExternalMemberRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/external/members")
@RequiredArgsConstructor
@Tag(name = "External Member API", description = "Mocked external API for member management")
public class ExternalMemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "Create a new member (mocked external API)")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody ExternalMemberRequest request) {
        MemberResponse response = memberService.createFromExternal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all members (mocked external API)")
    public ResponseEntity<List<MemberResponse>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID (mocked external API)")
    public ResponseEntity<MemberResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getById(id));
    }
}
