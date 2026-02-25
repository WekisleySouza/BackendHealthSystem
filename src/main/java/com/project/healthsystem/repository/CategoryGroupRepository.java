package com.project.healthsystem.repository;

import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.repository.projections.simplified_lists.CategoryGroupSimplifiedInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long>, JpaSpecificationExecutor<CategoryGroup> {
    Page<CategoryGroupSimplifiedInfoProjection> getAllBy(Pageable pageable);
}
