# Vitalya Backend

API de gestão de atendimentos e cadastros do Vitalya.

## Documentação para o frontend

- [Guia da API: fluxos, endpoints, campos e enums](docs/frontend-api.md)
- Swagger interativo: /swagger-ui/index.html na URL do backend.
- OpenAPI JSON: /v3/api-docs.

As descrições dos endpoints ficam em controller/docs; as descrições dos campos ficam nos DTOs. Rotas, permissões e implementação permanecem nos controllers.

Valide a documentação com: ./mvnw -Dtest=OpenApiDocumentationTest test

O teste usa dependências simuladas e não acessa o banco. O documento validado é salvo em target/openapi-frontend.json.
