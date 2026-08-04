package builders;

import com.project.healthsystem.model.Instituition;

public class InstituitionBuilder {
    private String name;
    private String cep;
    private String cityName;
    private String address;
    private String phone;
    private String linkLogo;

    public InstituitionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public InstituitionBuilder withCep(String cep) {
        this.cep = cep;
        return this;
    }

    public InstituitionBuilder withCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public InstituitionBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public InstituitionBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public InstituitionBuilder withLinkLogo(String linkLogo) {
        this.linkLogo = linkLogo;
        return this;
    }

    public Instituition build() {
        Instituition instituition = new Instituition();
        instituition.setName(name);
        instituition.setCep(cep);
        instituition.setCityName(cityName);
        instituition.setAddress(address);
        instituition.setPhone(phone);
        instituition.setLinkLogo(linkLogo);

        return instituition;
    }
}
