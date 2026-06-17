package com.portfolio.view.mapper;

import com.portfolio.view.request.ExternalMemberRequest;
import com.portfolio.view.response.MemberResponse;
import com.portfolio.model.entity.Member;
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
