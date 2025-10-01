package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.repository.CategoryGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryGroupValidator {

    @Autowired
    private CategoryGroupRepository repository;

    public void validate(CategoryGroup categoryGroup){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não existe CategoryGroup com este id.");
        }
    }

    private boolean isDuplicatedCategoryGroup(CategoryGroup categoryGroup){
        return true;
    }

    private boolean exists(long id){
        return repository.existsById(id);
    }
}
