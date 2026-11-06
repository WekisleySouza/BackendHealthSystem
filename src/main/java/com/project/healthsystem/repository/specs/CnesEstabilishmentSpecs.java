package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.datasus_data.cnes.Estabilishment;
import org.springframework.data.jpa.domain.Specification;
import java.util.Locale;

public class CnesEstabilishmentSpecs {
    public static Specification<Estabilishment> search(String search) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (search != null && !search.isBlank()) {
                String term = search.trim().toLowerCase(Locale.ROOT)
                    .replace("!", "!!").replace("%", "!%").replace("_", "!_");
                String pattern = "%" + term + "%";
                predicate = cb.and(predicate, cb.or(
                    cb.like(cb.lower(root.get("legalName")), pattern, '!'),
                    cb.like(cb.lower(root.get("tradeName")), pattern, '!'),
                    cb.like(cb.lower(root.get("cnesCode")), pattern, '!'),
                    cb.like(cb.lower(root.get("cnpj")), pattern, '!')));
            }
            return predicate;
        };
    }
}
