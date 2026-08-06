package builders;

import com.project.healthsystem.model.Role;
import com.project.healthsystem.model.Roles;

public class RoleBuilder {
    private Roles role = Roles.PATIENT;

    public static RoleBuilder builder() { return new RoleBuilder(); }

    public RoleBuilder withRole(Roles role) {
        this.role = role;
        return this;
    }

    public Role build() {
        return new Role(role);
    }
}
