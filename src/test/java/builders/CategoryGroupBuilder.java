package builders;

import com.project.healthsystem.model.CategoryGroup;

public class CategoryGroupBuilder {
    private String name = "Default Category Group";

    public static CategoryGroupBuilder builder() {
        return new CategoryGroupBuilder();
    }

    public CategoryGroupBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CategoryGroup build() {
        CategoryGroup categoryGroup = new CategoryGroup();

        categoryGroup.setName(name);

        return categoryGroup;
    }
}
