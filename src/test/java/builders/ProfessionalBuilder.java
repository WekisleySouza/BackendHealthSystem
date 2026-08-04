package builders;

import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Professional;

public class ProfessionalBuilder {

    private String cns = "123456789";
    private String cbo = "225125";
    private String vinculation = "Permanent";
    private String description = "Medical Professional";

    private Person person = PersonBuilder.builder().build();

    public static ProfessionalBuilder builder() {
        return new ProfessionalBuilder();
    }

    public ProfessionalBuilder cns(String cns) {
        this.cns = cns;
        return this;
    }

    public ProfessionalBuilder cbo(String cbo) {
        this.cbo = cbo;
        return this;
    }

    public ProfessionalBuilder vinculation(String vinculation) {
        this.vinculation = vinculation;
        return this;
    }

    public ProfessionalBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProfessionalBuilder person(Person person) {
        this.person = person;
        return this;
    }

    public Professional build() {
        Professional professional = new Professional();

        professional.setCns(cns);
        professional.setCbo(cbo);
        professional.setVinculation(vinculation);
        professional.setDescription(description);
        professional.setPerson(person);

        return professional;
    }
}
