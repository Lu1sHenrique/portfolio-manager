package com.portfolio.service;

import com.portfolio.view.request.ExternalMemberRequest;
import com.portfolio.view.response.MemberResponse;
import com.portfolio.model.entity.Member;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.view.mapper.MemberMapper;
import com.portfolio.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(Long id) {
        return memberMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberMapper.toResponseList(memberRepository.findAll());
    }

    @Transactional
    public MemberResponse createFromExternal(ExternalMemberRequest request) {
        Member member = memberMapper.toEntity(request);
        member.setExternalId(UUID.randomUUID().toString());
        Member saved = memberRepository.save(member);
        return memberMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return memberRepository.existsById(id);
    }
}
