package com.portfolio.view.mapper;

import com.portfolio.view.request.ProjectRequest;
import com.portfolio.view.response.MemberResponse;
import com.portfolio.view.response.ProjectResponse;
import com.portfolio.model.entity.Member;
import com.portfolio.model.entity.Project;
import com.portfolio.model.entity.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberMapper.class})
public interface ProjectMapper {

    @Mapping(target = "allocatedMembers", source = "members", qualifiedByName = "mapProjectMembers")
    @Mapping(target = "riskLevel", expression = "java(project.getRiskLevel())")
    ProjectResponse toResponse(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    Project toEntity(ProjectRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    void updateEntity(ProjectRequest request, @MappingTarget Project project);

    @Named("mapProjectMembers")
    default List<MemberResponse> mapProjectMembers(List<ProjectMember> projectMembers) {
        if (projectMembers == null) {
            return Collections.emptyList();
        }
        return projectMembers.stream()
                .map(pm -> MemberResponse.builder()
                        .id(pm.getMember().getId())
                        .name(pm.getMember().getName())
                        .role(pm.getMember().getRole())
                        .externalId(pm.getMember().getExternalId())
                        .build())
                .toList();
    }
}
