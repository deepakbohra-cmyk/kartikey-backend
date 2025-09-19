package com.kartikey.kartikey.specification;

import com.kartikey.kartikey.dto.formdata.FormDataFilterDTO;
import com.kartikey.kartikey.entity.FormData;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class FormDataSpecification {

    public static Specification<FormData> withFilters(FormDataFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
            }
            if (filter.getWorkType() != null) {
                predicates.add(cb.equal(root.get("workType"), filter.getWorkType()));
            }
            if (filter.getGid() != null && !filter.getGid().isEmpty()) {
                predicates.add(cb.like(root.get("gid"), "%" + filter.getGid() + "%"));
            }
            if (filter.getDecision() != null && !filter.getDecision().isEmpty()) {
                predicates.add(cb.equal(root.get("decision"), filter.getDecision()));
            }
            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getFromDate().atStartOfDay()));
            }
            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getToDate().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
