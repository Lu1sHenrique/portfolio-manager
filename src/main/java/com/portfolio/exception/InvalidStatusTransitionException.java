package com.portfolio.exception;

import com.portfolio.model.enums.ProjectStatus;

public class InvalidStatusTransitionException extends BusinessRuleException {

    public InvalidStatusTransitionException(ProjectStatus current, ProjectStatus target) {
        super(String.format("Invalid status transition from %s to %s", current, target));
    }
}
