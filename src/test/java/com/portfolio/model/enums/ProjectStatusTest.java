package com.portfolio.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectStatusTest {

    @Nested
    @DisplayName("Status Transition Tests")
    class StatusTransitionTests {

        @Test
        @DisplayName("IN_ANALYSIS can only transition to ANALYSIS_DONE")
        void inAnalysisCanOnlyTransitionToAnalysisDone() {
            assertThat(ProjectStatus.IN_ANALYSIS.canTransitionTo(ProjectStatus.ANALYSIS_DONE)).isTrue();
            assertThat(ProjectStatus.IN_ANALYSIS.canTransitionTo(ProjectStatus.ANALYSIS_APPROVED)).isFalse();
            assertThat(ProjectStatus.IN_ANALYSIS.canTransitionTo(ProjectStatus.STARTED)).isFalse();
            assertThat(ProjectStatus.IN_ANALYSIS.canTransitionTo(ProjectStatus.PLANNED)).isFalse();
            assertThat(ProjectStatus.IN_ANALYSIS.canTransitionTo(ProjectStatus.IN_PROGRESS)).isFalse();
            assertThat(ProjectStatus.IN_ANALYSIS.canTransitionTo(ProjectStatus.CLOSED)).isFalse();
        }

        @Test
        @DisplayName("ANALYSIS_DONE can only transition to ANALYSIS_APPROVED")
        void analysisDoneCanOnlyTransitionToAnalysisApproved() {
            assertThat(ProjectStatus.ANALYSIS_DONE.canTransitionTo(ProjectStatus.ANALYSIS_APPROVED)).isTrue();
            assertThat(ProjectStatus.ANALYSIS_DONE.canTransitionTo(ProjectStatus.IN_ANALYSIS)).isFalse();
            assertThat(ProjectStatus.ANALYSIS_DONE.canTransitionTo(ProjectStatus.STARTED)).isFalse();
        }

        @Test
        @DisplayName("ANALYSIS_APPROVED can only transition to STARTED")
        void analysisApprovedCanOnlyTransitionToStarted() {
            assertThat(ProjectStatus.ANALYSIS_APPROVED.canTransitionTo(ProjectStatus.STARTED)).isTrue();
            assertThat(ProjectStatus.ANALYSIS_APPROVED.canTransitionTo(ProjectStatus.IN_ANALYSIS)).isFalse();
            assertThat(ProjectStatus.ANALYSIS_APPROVED.canTransitionTo(ProjectStatus.PLANNED)).isFalse();
        }

        @Test
        @DisplayName("STARTED can only transition to PLANNED")
        void startedCanOnlyTransitionToPlanned() {
            assertThat(ProjectStatus.STARTED.canTransitionTo(ProjectStatus.PLANNED)).isTrue();
            assertThat(ProjectStatus.STARTED.canTransitionTo(ProjectStatus.IN_PROGRESS)).isFalse();
            assertThat(ProjectStatus.STARTED.canTransitionTo(ProjectStatus.CLOSED)).isFalse();
        }

        @Test
        @DisplayName("PLANNED can only transition to IN_PROGRESS")
        void plannedCanOnlyTransitionToInProgress() {
            assertThat(ProjectStatus.PLANNED.canTransitionTo(ProjectStatus.IN_PROGRESS)).isTrue();
            assertThat(ProjectStatus.PLANNED.canTransitionTo(ProjectStatus.STARTED)).isFalse();
            assertThat(ProjectStatus.PLANNED.canTransitionTo(ProjectStatus.CLOSED)).isFalse();
        }

        @Test
        @DisplayName("IN_PROGRESS can only transition to CLOSED")
        void inProgressCanOnlyTransitionToClosed() {
            assertThat(ProjectStatus.IN_PROGRESS.canTransitionTo(ProjectStatus.CLOSED)).isTrue();
            assertThat(ProjectStatus.IN_PROGRESS.canTransitionTo(ProjectStatus.PLANNED)).isFalse();
            assertThat(ProjectStatus.IN_PROGRESS.canTransitionTo(ProjectStatus.STARTED)).isFalse();
        }

        @Test
        @DisplayName("CLOSED cannot transition to any status")
        void closedCannotTransitionToAnyStatus() {
            for (ProjectStatus status : ProjectStatus.values()) {
                if (status != ProjectStatus.CANCELLED) {
                    assertThat(ProjectStatus.CLOSED.canTransitionTo(status)).isFalse();
                }
            }
        }

        @ParameterizedTest
        @EnumSource(ProjectStatus.class)
        @DisplayName("Any status can transition to CANCELLED")
        void anyStatusCanTransitionToCancelled(ProjectStatus status) {
            assertThat(status.canTransitionTo(ProjectStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("CANCELLED cannot transition to any status except CANCELLED")
        void cancelledCannotTransitionToAnyStatus() {
            assertThat(ProjectStatus.CANCELLED.canTransitionTo(ProjectStatus.IN_ANALYSIS)).isFalse();
            assertThat(ProjectStatus.CANCELLED.canTransitionTo(ProjectStatus.CLOSED)).isFalse();
        }
    }

    @Nested
    @DisplayName("Deletion Rules Tests")
    class DeletionRulesTests {

        @Test
        @DisplayName("STARTED status is not deletable")
        void startedIsNotDeletable() {
            assertThat(ProjectStatus.STARTED.isDeletable()).isFalse();
        }

        @Test
        @DisplayName("IN_PROGRESS status is not deletable")
        void inProgressIsNotDeletable() {
            assertThat(ProjectStatus.IN_PROGRESS.isDeletable()).isFalse();
        }

        @Test
        @DisplayName("CLOSED status is not deletable")
        void closedIsNotDeletable() {
            assertThat(ProjectStatus.CLOSED.isDeletable()).isFalse();
        }

        @Test
        @DisplayName("IN_ANALYSIS status is deletable")
        void inAnalysisIsDeletable() {
            assertThat(ProjectStatus.IN_ANALYSIS.isDeletable()).isTrue();
        }

        @Test
        @DisplayName("ANALYSIS_DONE status is deletable")
        void analysisDoneIsDeletable() {
            assertThat(ProjectStatus.ANALYSIS_DONE.isDeletable()).isTrue();
        }

        @Test
        @DisplayName("ANALYSIS_APPROVED status is deletable")
        void analysisApprovedIsDeletable() {
            assertThat(ProjectStatus.ANALYSIS_APPROVED.isDeletable()).isTrue();
        }

        @Test
        @DisplayName("PLANNED status is deletable")
        void plannedIsDeletable() {
            assertThat(ProjectStatus.PLANNED.isDeletable()).isTrue();
        }

        @Test
        @DisplayName("CANCELLED status is deletable")
        void cancelledIsDeletable() {
            assertThat(ProjectStatus.CANCELLED.isDeletable()).isTrue();
        }
    }

    @Nested
    @DisplayName("Active For Allocation Tests")
    class ActiveForAllocationTests {

        @Test
        @DisplayName("CLOSED is not active for allocation")
        void closedIsNotActiveForAllocation() {
            assertThat(ProjectStatus.CLOSED.isActiveForAllocation()).isFalse();
        }

        @Test
        @DisplayName("CANCELLED is not active for allocation")
        void cancelledIsNotActiveForAllocation() {
            assertThat(ProjectStatus.CANCELLED.isActiveForAllocation()).isFalse();
        }

        @Test
        @DisplayName("All other statuses are active for allocation")
        void otherStatusesAreActiveForAllocation() {
            assertThat(ProjectStatus.IN_ANALYSIS.isActiveForAllocation()).isTrue();
            assertThat(ProjectStatus.ANALYSIS_DONE.isActiveForAllocation()).isTrue();
            assertThat(ProjectStatus.ANALYSIS_APPROVED.isActiveForAllocation()).isTrue();
            assertThat(ProjectStatus.STARTED.isActiveForAllocation()).isTrue();
            assertThat(ProjectStatus.PLANNED.isActiveForAllocation()).isTrue();
            assertThat(ProjectStatus.IN_PROGRESS.isActiveForAllocation()).isTrue();
        }
    }
}
