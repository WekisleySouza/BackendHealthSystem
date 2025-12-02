package com.project.healthsystem.repository.specs;

import org.springframework.data.jpa.domain.Specification;

public class SpecsCommon {

    public static  <T> Specification<T> addSpec(Specification<T> base, Specification<T> next) {
        return (base == null) ? next : base.and(next);
    }
}
