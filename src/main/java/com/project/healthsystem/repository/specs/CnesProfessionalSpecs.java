package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.datasus_data.cnes.CnesProfessional;
import org.springframework.data.jpa.domain.Specification;
import java.util.Locale;

public class CnesProfessionalSpecs {
    public static Specification<CnesProfessional> search(String search, String cnes, String cbo) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (search != null && !search.isBlank()) {
                String term = search.trim().toLowerCase(Locale.ROOT)
                    .replace("!", "!!").replace("%", "!%").replace("_", "!_");
                String pattern = "%" + term + "%";
                predicate = cb.and(predicate, cb.or(
                    cb.like(cb.lower(root.get("name")), pattern, '!'),
                    cb.like(cb.lower(root.get("cns")), pattern, '!')));
            }
            if (cnes != null && !cnes.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("cnes"), cnes.trim()));
            }
            if (cbo != null && !cbo.isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.upper(root.get("cbo")), cbo.trim().toUpperCase(Locale.ROOT)));
            }
            return predicate;
        };
    }
}
