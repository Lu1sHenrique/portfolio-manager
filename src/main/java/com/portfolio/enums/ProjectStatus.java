package com.portfolio.enums;

import java.util.Set;

public enum ProjectStatus {
    IN_ANALYSIS,
    ANALYSIS_DONE,
    ANALYSIS_APPROVED,
    STARTED,
    PLANNED,
    IN_PROGRESS,
    CLOSED,
    CANCELLED;

    private static final Set<ProjectStatus> NON_DELETABLE = Set.of(STARTED, IN_PROGRESS, CLOSED);

    public boolean canTransitionTo(ProjectStatus next) {
        if (next == CANCELLED) {
            return true;
        }
        return switch (this) {
            case IN_ANALYSIS -> next == ANALYSIS_DONE;
            case ANALYSIS_DONE -> next == ANALYSIS_APPROVED;
            case ANALYSIS_APPROVED -> next == STARTED;
            case STARTED -> next == PLANNED;
            case PLANNED -> next == IN_PROGRESS;
            case IN_PROGRESS -> next == CLOSED;
            case CLOSED, CANCELLED -> false;
        };
    }

    public boolean isDeletable() {
        return !NON_DELETABLE.contains(this);
    }

    public boolean isActiveForAllocation() {
        return this != CLOSED && this != CANCELLED;
    }
}
