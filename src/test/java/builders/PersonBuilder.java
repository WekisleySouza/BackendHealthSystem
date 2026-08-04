package builders;

import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Role;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PersonBuilder {
    private String personSequenceId = "SEQ-001";
    private String uniquePersonId = "UPI-001";
    private String name = "Wekisley Souza";
    private String gender = "Masculino";
    private String sex = "Masculino";
    private String cpf = "14828169628";
    private String cellPhone = "(31)99999-9999";
    private String residentialPhone = "(31)3333-3333";
    private String contactPhone = "(31)98888-8888";
    private String address = "123 Main Street";
    private LocalDate birthday = LocalDate.of(1990, 1, 1);
    private String email = "wekisleysouza.a@gmail.com";
    private Set<Role> roles = new HashSet<>();

    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    public PersonBuilder personSequenceId(String personSequenceId) {
        this.personSequenceId = personSequenceId;
        return this;
    }

    public PersonBuilder uniquePersonId(String uniquePersonId) {
        this.uniquePersonId = uniquePersonId;
        return this;
    }

    public PersonBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder gender(String gender) {
        this.gender = gender;
        return this;
    }

    public PersonBuilder sex(String sex) {
        this.sex = sex;
        return this;
    }

    public PersonBuilder cpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public PersonBuilder cellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
        return this;
    }

    public PersonBuilder residentialPhone(String residentialPhone) {
        this.residentialPhone = residentialPhone;
        return this;
    }

    public PersonBuilder contactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        return this;
    }

    public PersonBuilder address(String address) {
        this.address = address;
        return this;
    }

    public PersonBuilder birthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public PersonBuilder email(String email) {
        this.email = email;
        return this;
    }

    public PersonBuilder roles(Role... roles) {
        this.roles = new HashSet<>(Arrays.asList(roles));
        return this;
    }

    public Person build() {
        Person person = new Person();

        person.setPersonSequenceId(personSequenceId);
        person.setUniquePersonId(uniquePersonId);
        person.setName(name);
        person.setGender(gender);
        person.setSex(sex);
        person.setCpf(cpf);
        person.setCellPhone(cellPhone);
        person.setResidentialPhone(residentialPhone);
        person.setContactPhone(contactPhone);
        person.setAddress(address);
        person.setBirthday(birthday);
        person.setEmail(email);
        person.setRoles(roles);

        return person;
    }
}
