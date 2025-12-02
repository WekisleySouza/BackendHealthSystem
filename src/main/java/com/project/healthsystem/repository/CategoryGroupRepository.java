package com.project.healthsystem.repository;

import com.project.healthsystem.model.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long>, JpaSpecificationExecutor<CategoryGroup> {
}
