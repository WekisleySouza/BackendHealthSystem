package com.project.healthsystem.security;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.service.EmployeeService;
import com.project.healthsystem.service.LoginService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    @Value("${security.access.expiration}")
    private long EXPIRATION_TIME;

    private final LoginService loginService;

    public String generateToken(String username, String role){
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            System.out.println("Erro ao validar token: " + e);
            return false;
        }
    }

    public String getUsername(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRole(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Employee getEmployee(String token){
        String username = this.getUsername(token);
        Login login = this.loginService.findByLogin(username);
        return login.getEmployee();
    }

    public Person getPerson(String token){
        String username = this.getUsername(token);
        Login login = this.loginService.findByLogin(username);
        return login.getPerson();
    }

    public Login getLogin(String token){
        String username = this.getUsername(token);
        return this.loginService.findByLogin(username);
    }
}
