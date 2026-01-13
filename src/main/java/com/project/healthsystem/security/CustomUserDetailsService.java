package com.project.healthsystem.security;

import com.project.healthsystem.model.Login;
import com.project.healthsystem.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginService loginService;

    @Override
    public UserDetails loadUserByUsername(String loginReceived) throws UsernameNotFoundException {
        Login login = loginService.findByLogin(loginReceived);
        System.out.println("Login recebido: " + login.getLogin());

        if(login == null){
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        return User.builder()
                .username(login.getLogin())
                .password(login.getPassword())
                .authorities(
                    login.getPerson()
                    .getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getRole().getLabel()))
                    .toList()
                )
                .build();
    }
}
