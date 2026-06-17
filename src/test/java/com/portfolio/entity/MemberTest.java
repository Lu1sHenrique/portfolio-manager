package com.portfolio.entity;

import com.portfolio.enums.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("isEmployee returns true for EMPLOYEE role")
    void isEmployeeReturnsTrueForEmployeeRole() {
        Member member = Member.builder()
                .name("Test")
                .role(MemberRole.EMPLOYEE)
                .build();

        assertThat(member.isEmployee()).isTrue();
    }

    @Test
    @DisplayName("isEmployee returns false for MANAGER role")
    void isEmployeeReturnsFalseForManagerRole() {
        Member member = Member.builder()
                .name("Test")
                .role(MemberRole.MANAGER)
                .build();

        assertThat(member.isEmployee()).isFalse();
    }

    @Test
    @DisplayName("isEmployee returns false for DIRECTOR role")
    void isEmployeeReturnsFalseForDirectorRole() {
        Member member = Member.builder()
                .name("Test")
                .role(MemberRole.DIRECTOR)
                .build();

        assertThat(member.isEmployee()).isFalse();
    }

    @Test
    @DisplayName("isEmployee returns false for CONTRACTOR role")
    void isEmployeeReturnsFalseForContractorRole() {
        Member member = Member.builder()
                .name("Test")
                .role(MemberRole.CONTRACTOR)
                .build();

        assertThat(member.isEmployee()).isFalse();
    }
}
