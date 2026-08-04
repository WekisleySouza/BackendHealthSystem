package builders;

import com.project.healthsystem.model.Agreement;

public class AgreementBuilder {
    private String name = "SUS";

    public static AgreementBuilder builder() {
        return new AgreementBuilder();
    }

    public AgreementBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Agreement build() {
        Agreement agreement = new Agreement();

        agreement.setName(name);

        return agreement;
    }
}
