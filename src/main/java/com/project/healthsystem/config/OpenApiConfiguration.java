package com.project.healthsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Vitalya — API para integração do frontend",
                description = "Documentação dos campos, filtros e respostas. Use os nomes JSON exibidos e os valores permitidos de cada campo. Datas e horas locais não incluem offset; códigos oficiais são strings.",
                version = "v0.1",
                contact = @Contact(
                        name = "Wekisley Souza",
                        email = "wekisleysouza.a@gmail.com"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfiguration {

    /**
     * Documents framework-generated wrappers and wire names without changing serialization.
     */
    @org.springframework.context.annotation.Bean
    public org.springdoc.core.customizers.OpenApiCustomizer frontendContractDocumentation() {
        return api -> {
            var descriptions = java.util.Map.ofEntries(
                java.util.Map.entry("content", "Registros da página atual; pode ser uma lista vazia."),
                java.util.Map.entry("number", "Número da página atual, começando em 0."),
                java.util.Map.entry("size", "Tamanho de página solicitado."),
                java.util.Map.entry("numberOfElements", "Quantidade de registros nesta página."),
                java.util.Map.entry("totalElements", "Quantidade total de registros que correspondem aos filtros."),
                java.util.Map.entry("totalPages", "Quantidade total de páginas."),
                java.util.Map.entry("first", "Indica se esta é a primeira página."),
                java.util.Map.entry("last", "Indica se esta é a última página."),
                java.util.Map.entry("empty", "Indica se não há elementos."),
                java.util.Map.entry("pageable", "Metadados da paginação aplicada."),
                java.util.Map.entry("sort", "Metadados da ordenação aplicada."),
                java.util.Map.entry("sorted", "Indica se há ordenação aplicada."),
                java.util.Map.entry("unsorted", "Indica se não há ordenação aplicada."),
                java.util.Map.entry("offset", "Quantidade de registros anteriores à página atual."),
                java.util.Map.entry("pageNumber", "Número da página, começando em 0."),
                java.util.Map.entry("pageSize", "Quantidade de registros solicitada por página."),
                java.util.Map.entry("paged", "Indica que a consulta usa paginação."),
                java.util.Map.entry("unpaged", "Indica que a consulta não usa paginação.")
            );
            if (api.getComponents() != null && api.getComponents().getSchemas() != null) {
                api.getComponents().getSchemas().forEach((name, schema) -> {
                    if (schema.getProperties() == null) return;
                    if (name.startsWith("Page") || name.equals("SortObject")) {
                        schema.getProperties().forEach((field, property) -> {
                            if (descriptions.containsKey(field)) ((io.swagger.v3.oas.models.media.Schema<?>)property).setDescription(descriptions.get(field));
                        });
                    }
                    var returnField = (io.swagger.v3.oas.models.media.Schema<?>) schema.getProperties().get("return");
                    if (returnField != null) {
                        returnField.setDescription("Indica se o atendimento é um retorno. No DTO de classe, a chave JSON é return; false quando omitida.");
                    }
                    if (name.equals("Role")) {
                        ((io.swagger.v3.oas.models.media.Schema<?>)schema.getProperties().get("id"))
                            .setDescription("ID interno do papel de acesso.");
                        ((io.swagger.v3.oas.models.media.Schema<?>)schema.getProperties().get("role"))
                            .setDescription("Papel de acesso: API (integração), ADMIN (administrador), MANAGER (gestor), EMPLOYEE (funcionário), PATIENT (paciente).");
                    }
                    if (name.equals("EmployeeRequestDTO") || name.equals("EmployeeResponseDTO")) {
                        var roles = (io.swagger.v3.oas.models.media.Schema<?>)schema.getProperties().get("roles");
                        if (roles != null && roles.getItems() != null) {
                            roles.setItems(new io.swagger.v3.oas.models.media.StringSchema()._enum(java.util.Arrays.stream(com.project.healthsystem.model.Roles.values())
                                .map(Enum::name).toList()));
                        }
                    }
                });
            }
            // Springdoc expands @ParameterObject using Java names, while Spring MVC binds @BindParam aliases.
            if (api.getPaths() != null && api.getPaths().get("/appointments/report-patients-pdf") != null) {
                var operation = api.getPaths().get("/appointments/report-patients-pdf").getGet();
                var type = com.project.healthsystem.controller.dto.reports_patients.PatientReportFilterDTO.class;
                var components = type.getRecordComponents();
                try {
                    var ctor = type.getDeclaredConstructor(java.util.Arrays.stream(components)
                        .map(java.lang.reflect.RecordComponent::getType).toArray(Class<?>[]::new));
                    var parameters = ctor.getParameters();
                    if (operation != null && operation.getParameters() != null) {
                        for (int i = 0; i < components.length; i++) {
                            var alias = parameters[i].getAnnotation(org.springframework.web.bind.annotation.BindParam.class);
                            String javaName = components[i].getName();
                            if (alias != null) operation.getParameters().stream()
                                .filter(p -> javaName.equals(p.getName())).forEach(p -> p.setName(alias.value()));
                        }
                    }
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Construtor do filtro de relatório não encontrado.", e);
                }
            }
        };
    }
}
