package com.portfolio.dto.request;

import com.portfolio.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusRequest {

    @NotNull(message = "New status is required")
    private ProjectStatus newStatus;
}
