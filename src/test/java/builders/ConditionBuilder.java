package builders;

import com.project.healthsystem.model.Condition;

public class ConditionBuilder {

    private String specification = "Hypertension";

    public static ConditionBuilder builder() {
        return new ConditionBuilder();
    }

    public ConditionBuilder specification(String specification) {
        this.specification = specification;
        return this;
    }

    public Condition build() {
        Condition condition = new Condition();
        condition.setSpecification(specification);
        return condition;
    }
}
