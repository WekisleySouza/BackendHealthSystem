package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.datasus_data.cid.Cid10;
import org.springframework.data.jpa.domain.Specification;
import java.util.Locale;

public class Cid10Specs {
    public static Specification<Cid10> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            String term = search.trim().toLowerCase(Locale.ROOT)
                .replace("!", "!!").replace("%", "!%").replace("_", "!_");
            String pattern = "%" + term + "%";
            return cb.or(
                cb.like(cb.lower(root.get("code")), pattern, '!'),
                cb.like(cb.lower(root.get("description")), pattern, '!')
            );
        };
    }
}
