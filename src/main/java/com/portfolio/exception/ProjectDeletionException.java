package com.portfolio.exception;

import com.portfolio.enums.ProjectStatus;

public class ProjectDeletionException extends BusinessRuleException {

    public ProjectDeletionException(ProjectStatus status) {
        super(String.format("Cannot delete project with status: %s", status));
    }
}
