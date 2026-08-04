package builders;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;

public class EmployeeBuilder {

    private Person person;
    private boolean active;

    public EmployeeBuilder withPerson(Person person) {
        this.person = person;
        return this;
    }

    public EmployeeBuilder withActive(boolean active) {
        this.active = active;
        return this;
    }

    public Employee build() {
        Employee employee = new Employee();
        employee.setPerson(person);
        employee.setActive(active);

        return employee;
    }
}
