package builders;

import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.model.ServiceTypes;

import java.math.BigDecimal;

public class ServiceTypeBuilder {
    private CategoryGroup categoryGroup;
    private String sigtapCode = "01230497123097109237";
    private String name = "Endocrinologia";
    private BigDecimal value = BigDecimal.valueOf(340.00);
    private ServiceTypes type = ServiceTypes.EXAM;
    
    public static ServiceTypeBuilder builder(){
        return new ServiceTypeBuilder();
    }

    public ServiceTypeBuilder withCategoryGroup(CategoryGroup categoryGroup) {
        this.categoryGroup = categoryGroup;
        return this;
    }

    public ServiceTypeBuilder withSigtapCode(String sigtapCode) {
        this.sigtapCode = sigtapCode;
        return this;
    }

    public ServiceTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ServiceTypeBuilder withValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public ServiceTypeBuilder withType(ServiceTypes type) {
        this.type = type;
        return this;
    }

    public ServiceType build() {
        ServiceType serviceType = new ServiceType();
        serviceType.setCategoryGroup(categoryGroup);
        serviceType.setSigtapCode(sigtapCode);
        serviceType.setName(name);
        serviceType.setValue(value);
        serviceType.setType(type);

        return serviceType;
    }
}
