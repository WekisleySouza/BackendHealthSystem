package builders;

import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;

public class LoginBuilder {

    private Person person;
    private String login;
    private String password = "1234567";
    private boolean active = true;

    public static LoginBuilder builder(){ return new LoginBuilder(); }

    public LoginBuilder withPerson(Person person) {
        this.person = person;
        return this;
    }

    public LoginBuilder withLogin(String login) {
        this.login = login;
        return this;
    }

    public LoginBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public LoginBuilder withActive(boolean active) {
        this.active = active;
        return this;
    }

    public Login build() {
        Login login = new Login();
        login.setPerson(person);
        login.setLogin(this.login);
        login.setPassword(password);
        login.setActive(active);

        return login;
    }
}
