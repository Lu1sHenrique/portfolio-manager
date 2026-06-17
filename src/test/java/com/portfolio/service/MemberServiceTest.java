package com.portfolio.service;

import com.portfolio.view.request.ExternalMemberRequest;
import com.portfolio.view.response.MemberResponse;
import com.portfolio.model.entity.Member;
import com.portfolio.model.enums.MemberRole;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.view.mapper.MemberMapper;
import com.portfolio.model.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .name("Test Member")
                .role(MemberRole.EMPLOYEE)
                .externalId("ext-123")
                .build();

        memberResponse = MemberResponse.builder()
                .id(1L)
                .name("Test Member")
                .role(MemberRole.EMPLOYEE)
                .externalId("ext-123")
                .build();
    }

    @Test
    @DisplayName("Should find member by id")
    void shouldFindMemberById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception when member not found")
    void shouldThrowExceptionWhenMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found");
    }

    @Test
    @DisplayName("Should get member by id")
    void shouldGetMemberById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberMapper.toResponse(member)).thenReturn(memberResponse);

        MemberResponse result = memberService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find all members")
    void shouldFindAllMembers() {
        List<Member> members = List.of(member);
        List<MemberResponse> responses = List.of(memberResponse);

        when(memberRepository.findAll()).thenReturn(members);
        when(memberMapper.toResponseList(members)).thenReturn(responses);

        List<MemberResponse> result = memberService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should create member from external request")
    void shouldCreateMemberFromExternalRequest() {
        ExternalMemberRequest request = ExternalMemberRequest.builder()
                .name("New Member")
                .role(MemberRole.EMPLOYEE)
                .build();

        Member newMember = Member.builder()
                .name("New Member")
                .role(MemberRole.EMPLOYEE)
                .build();

        when(memberMapper.toEntity(request)).thenReturn(newMember);
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberMapper.toResponse(member)).thenReturn(memberResponse);

        MemberResponse result = memberService.createFromExternal(request);

        assertThat(result).isNotNull();
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("Should check if member exists by id")
    void shouldCheckIfMemberExistsById() {
        when(memberRepository.existsById(1L)).thenReturn(true);

        boolean result = memberService.existsById(1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when member does not exist")
    void shouldReturnFalseWhenMemberDoesNotExist() {
        when(memberRepository.existsById(99L)).thenReturn(false);

        boolean result = memberService.existsById(99L);

        assertThat(result).isFalse();
    }
}
