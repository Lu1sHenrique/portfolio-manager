package com.portfolio.mapper;

import com.portfolio.dto.request.ExternalMemberRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberResponse toResponse(Member member);

    List<MemberResponse> toResponseList(List<Member> members);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    Member toEntity(ExternalMemberRequest request);
}
