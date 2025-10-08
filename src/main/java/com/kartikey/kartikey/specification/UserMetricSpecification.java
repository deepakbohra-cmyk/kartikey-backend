package com.kartikey.kartikey.specification;

import com.kartikey.kartikey.entity.UserMetrics;
import org.springframework.data.jpa.domain.Specification;

public class UserMetricSpecification {
    public static Specification<UserMetrics> withEmail(String email) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (email == null || email.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.join("user").get("email")), "%" + email.toLowerCase() + "%");
        };

    }
}
