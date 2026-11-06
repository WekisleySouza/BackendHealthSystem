package builders;

public class CnesFixtures {
    public static final String EST_HEADER = "CO_UNIDADE;CO_CNES;NU_CNPJ_MANTENEDORA;NO_RAZAO_SOCIAL;NO_FANTASIA;CO_CEP;NU_TELEFONE;NO_EMAIL;NU_CPF;NU_CNPJ;ENDERECO";
    public static final String PROF_HEADER = "name;cns;cityName;cbo;cboDescription;cnpj;cnes;estabilishmentName;legalNatureCode;legalNatureDescription;sus;resident;preceptor;management;estabilishmentBond;employerBond;bondDetails";
    public static String establishment(String code) {
        return "unit;" + code + ";;Razão;Unidade;00123456;;;;;Rua";
    }
    public static String professional(String cns, String cnes) {
        return "Pessoa;" + cns + ";Cidade;2231F9;Ocupação;00123456000100;" + cnes
            + ";Unidade;1000;Natureza;s;N;S;D;Vínculo;Empregador;Detalhes";
    }
}
