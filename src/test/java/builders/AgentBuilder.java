package builders;

import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Person;

public class AgentBuilder {

    private Person person = PersonBuilder.builder().build();

    public static AgentBuilder builder() {
        return new AgentBuilder();
    }

    public AgentBuilder person(Person person) {
        this.person = person;
        return this;
    }

    public Agent build() {
        Agent agent = new Agent();
        agent.setPerson(person);
        return agent;
    }
}
