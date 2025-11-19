package com.project.healthsystem.config;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.EmployeeRepository;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DefaultInitializer implements ApplicationRunner {
    private final EmployeeRepository employeeRepository;
    private final LoginRepository loginRepository;
    private final LoginService loginService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.username}")
    private String DEFAULT_ADMIN_USERNAME;
    @Value("${app.default-admin.password}")
    private String DEFAULT_ADMIN_PASSWORD;

    private final String name = "ADMIN";
    private final String cpf = "12345678909";
    private final LocalDate birthday = LocalDate.now();
    private final String email = "admin@admin.com";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!loginRepository.existsByLogin(this.DEFAULT_ADMIN_USERNAME)){
            Employee employee = new Employee();
            employee.setName(this.name);
            employee.setCpf(this.cpf);
            employee.setBirthday(this.birthday);
            employee.setEmail(this.email);
            employeeRepository.save(employee);

            loginService.createDefaultLoginTo(employee, Roles.ADMIN);

            System.out.println("Usuário padrão criado com sucesso!");
        }
    }
}
