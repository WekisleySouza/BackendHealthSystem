package com.project.healthsystem.service;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Role;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void save(Role role){
        roleRepository.save(role);
    }

    public Role findByRole(Roles role){
        return roleRepository
            .findByRole(role)
            .orElseThrow(() -> new NotFoundException("Role não encontrado!"));
    }

    public boolean hasAnyRole(){
        return roleRepository.existsBy();
    }
}
