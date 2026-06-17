package com.portfolio.specification;

import com.portfolio.dto.request.ProjectFilterRequest;
import com.portfolio.entity.Project;
import com.portfolio.enums.ProjectStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> withFilters(ProjectFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getManagerId() != null) {
                predicates.add(cb.equal(root.get("manager").get("id"), filter.getManagerId()));
            }

            if (filter.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }

            if (filter.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }

            if (filter.getBudgetMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalBudget"), filter.getBudgetMin()));
            }

            if (filter.getBudgetMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalBudget"), filter.getBudgetMax()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
