package com.portfolio.view.request;

import com.portfolio.model.enums.ProjectStatus;
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
