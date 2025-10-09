package com.kartikey.kartikey.specification;

import com.kartikey.kartikey.entity.UserEntity;
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

    public static Specification<UserMetrics> withRole(UserEntity.Role role) {
        return (root, query, cb) -> {
            if (role == null) {
                return cb.conjunction();
            }
            return cb.equal(root.join("user").get("role"), role);
        };
    }
}
