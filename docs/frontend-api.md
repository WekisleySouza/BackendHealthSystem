# Vitalya — guia da API para o frontend

A documentação interativa está em `/swagger-ui/index.html` e o OpenAPI em `/v3/api-docs`, relativos à URL do backend. No Swagger, use **Authorize** para informar o token de acesso.

## Índice

- [Autenticação e erros](#autenticacao)
- [Formatos e paginação](#formatos)
- [Atendimentos e regulação](#atendimentos)
- [Catálogos](#catalogos)
- [Enums](#enums)
- [Endpoints por módulo](#endpoints)
- [Campos de entrada e saída](#campos)

As interfaces em `controller/docs` documentam operações; os DTOs documentam os campos. Rotas, permissões e implementação permanecem nos controllers. As validações de parâmetros também são espelhadas nas interfaces para manter compatibilidade com Bean Validation.

<a id="autenticacao"></a>
## Autenticação e erros

`POST /auth/login` recebe JSON:

```json
{"login":"usuario-de-teste","password":"senha-de-teste"}
```

Exemplo ilustrativo de resposta 200:

```json
{
  "login":"usuario-de-teste",
  "access_token":"token-de-acesso",
  "refresh_token":"token-de-renovacao",
  "roles":[{"id":1,"role":"EMPLOYEE"}]
}
```

Use `Authorization: Bearer <access_token>` nas rotas protegidas. Tokens e IDs dos exemplos não são credenciais reais.

| Operação | Entrada | Saída |
|---|---|---|
| POST /auth/refresh | `{"refreshToken":"token-de-renovacao"}` | `{"accessToken":"novo-token"}` |
| POST /auth/logout | `{"refreshToken":"token-de-renovacao"}` | 204, sem corpo |
| GET /auth/profile | Bearer, sem corpo | Perfil |
| PUT /login | `{"password":"nova-senha"}` e Bearer | 204, sem corpo |
| POST /login/forgot-password | `{"email":"usuario@example.com"}` | 204, sem corpo |
| POST /login/reset-password | `{"token":"token-recebido","newPassword":"nova-senha"}` | 204, sem corpo |

**As chaves diferem:** login retorna `access_token`/`refresh_token`; renovação recebe `refreshToken` e retorna `accessToken`. A configuração atual libera `/auth/**`, mas exige autenticação para `/login/**`, inclusive recuperação de senha. Este guia registra esse comportamento sem alterá-lo.

Exemplo de erro:

```json
{"status":422,"message":"Erro ao validar campo!","errors":[{"fieldName":"patientId","message":"O paciente é obrigatório!"}]}
```

| HTTP | Significado |
|---|---|
| 400 | Parâmetro, formato ou regra de negócio inválida |
| 401 | Credencial ausente, inválida ou expirada |
| 403 | Sem permissão |
| 404 | Registro não encontrado |
| 409 | Duplicidade, integridade ou vínculo que impede exclusão |
| 413 | Arquivo excede o limite configurado |
| 422 | Validação: associe `errors[].fieldName` ao formulário |
| 500 | Erro não tratado |

`errors` é uma lista dentro de um objeto. Respostas de segurança, proxy ou infraestrutura podem vir sem esse JSON.

Cadastros normalmente retornam **201, Location e corpo vazio**. Consulte a URL de Location para obter o registro. Em **204**, não tente ler JSON.

<a id="formatos"></a>
## Formatos e paginação

- IDs locais são números. CNS, CNES, CBO, CPF, CNPJ e códigos SIGTAP/CID/CIAP são **strings**: preserve zeros.
- Data: `1990-05-20`. Data/hora local: `2026-11-08T09:30:00`, **sem Z/offset**. Não converta automaticamente para UTC.
- Opcional não significa que null apaga um vínculo. As regras de atualização variam por campo.
- Nomes legados como `instituition`, `estabilishments`, `priorit`, `emitionDate` e `anesthesicRisk` devem ser usados exatamente como documentados.
- Nas classes AppointmentRequestDTO, AppointmentResponseDTO e PatientAppointmentResponseDTO, a chave booleana é **`return`**. No record AppointmentGetByIdResponseDTO, é **`isReturn`**.

A paginação geralmente usa `page-number=0&page-length=10`. O histórico usa `pageNumber=0&pageLength=20`. Os catálogos DATASUS e o histórico validam tamanho de **1 a 100**; não presuma esse limite em todas as rotas antigas.

```json
{"content":[],"number":0,"size":10,"totalElements":0,"totalPages":0,"numberOfElements":0,"first":true,"last":true,"empty":true}
```

Esse exemplo omite `pageable` e `sort`. Todos os metadados estão descritos no dicionário. `content` contém os objetos de cada operação.

<a id="atendimentos"></a>
## Atendimentos e regulação

Exemplo de POST /appointments; substitua os IDs por registros existentes:

```json
{
  "patientId":101,
  "employeeId":12,
  "requestingProfessionalId":35,
  "serviceTypeId":7,
  "status":"Sem agendamento",
  "priority":"Eletivo",
  "return":false,
  "careModality":"Ambulatorial",
  "requestCharacter":"Eletivo",
  "requestingInstituitionId":2,
  "regulatingInstituitionId":3,
  "authorInstituitionId":3,
  "executingInstituitionId":4
}
```

`requestingProfessionalId` é necessário no validator, mesmo sem @NotNull no DTO original.

| Campo | Significado |
|---|---|
| instituitionId | Vínculo legado, independente dos quatro papéis |
| requestingInstituitionId | Instituição que solicita |
| regulatingInstituitionId | Instituição que regula |
| authorInstituitionId | Instituição autora do registro |
| executingInstituitionId | Instituição que executa |
| careModality | Ambulatorial ou Hospitalar |
| requestCharacter | Eletivo ou Urgente; independente da prioridade |
| clinicalReasonSystem + clinicalReasonCode | Sistema e código clínico; enviados juntos |
| requestedAt | Data efetiva da solicitação, não pode estar no futuro |
| executingCbo | CBO executante com seis dígitos |
| statusChangeReason | Justificativa opcional, até 1000 caracteres |

Os quatro IDs referem-se às instituições locais de `/instituition`, não aos IDs do catálogo CNES. Não há preenchimento automático entre o vínculo legado e os novos papéis.

Na atualização, **null/omissão dos quatro papéis e dos novos campos de regulação preserva o valor existente**. Já `createdAt`, `notes`, `scheduledAt` e `schedulingForecast` são atribuídos diretamente: envie os valores atuais quando quiser preservá-los. `return` omitido assume false.

O motivo clínico aceita `CID-10` ou `CIAP-2`. Selecione exatamente o `code` retornado pelo respectivo catálogo. Um par novo/alterado precisa existir; um par já persistido e não alterado é preservado sem nova verificação de existência. Os campos não são códigos de procedimento SIGTAP.

O CNES da instituição permanece opcional; conteúdo não vazio deve ter sete dígitos. O catálogo profissional pode conter CBO alfanumérico, mas `executingCbo` do atendimento aceita somente seis dígitos.

### Mudança e histórico de status

`PATCH /appointments/set-appointments-to-new-status`:

```json
{"appointmentsId":[101,102],"status":"Agendado","statusChangeReason":"Agendamento confirmado pela unidade."}
```

Para Pré agendado, use `/appointments/set-appointments-to-pre-scheduled` com a lista de IDs e justificativa opcional. A rota em lote muda status; não envia data de agendamento.

Consulte `/appointments/{id}/status-history?pageNumber=0&pageLength=20`. A ordem é recordedAt/id decrescente. O status anterior pode ser null. Eventos automáticos usam autor Sistema e changedById null.

### PDF

`GET /appointments/report-patients-pdf?patient-name=Maria&fields=patientName,status,scheduledAt&sort=scheduledAt,desc`

Leia a resposta como Blob (application/pdf). O cabeçalho Content-Disposition indica anexo. Trate JSON de erro separadamente.

Colunas disponíveis: `patientName`, `motherName`, `scheduledAt`, `createdAt`, `status`, `responsibleProfessionalName`, `requestingProfessionalName`, `priorit`, `serviceName`, `serviceType`, `agreement`.

Os filtros do PDF usam nomes como `patient-name`, `scheduled-at-start` e `created-at-end`. A listagem normal tem diferenças, como `scheduledAt` e `createdAt`. Consulte a operação específica.

<a id="catalogos"></a>
## Catálogos e cadastros locais

| Catálogo | Consulta | Uso local |
|---|---|---|
| Profissionais CNES | /datasus/cnes/professionals | Cadastrar em /professionals |
| Estabelecimentos CNES | /datasus/cnes/estabilishments | Cadastrar em /instituition |
| Procedimentos SIGTAP | /datasus/sigtap/procedures | Cadastrar em /service-types |
| CID-10 | /datasus/cid10 | Motivo clínico |
| CIAP-2 | /datasus/ciap2 | Motivo clínico |

Consultar um catálogo não cria o registro local. Os IDs pertencem a tabelas diferentes.

`GET /datasus/cnes/professionals?search=Maria&cnes=0000123&cbo=2231F9&page-number=0&page-length=20`

Cada resultado CNES profissional representa um **vínculo**; o mesmo CNS pode aparecer mais de uma vez. Os filtros CNES/CBO são exatos; a busca por nome/CNS é parcial.

Importações CNES usam multipart/form-data, campo `file` com CSV UTF-8 separado por ponto e vírgula. São restritas a ADMIN e substituem o catálogo após validação e commit. Não devem ser disparadas a cada consulta do frontend.

<a id="enums"></a>
## Valores dos enums

Strings convertidas por fromLabel recebem o **rótulo** em português. Campos tipados diretamente como enum retornam o nome técnico. As opções aparecem em cada campo do Swagger. Sexo, gênero e tipo de cirurgia são campos textuais, não enums fechados.


### Status

| Nome técnico | Rótulo |
|---|---|
| `PRE_SCHEDULED` | Pré agendado |
| `COMPLETED` | Completo |
| `CANCELED` | Cancelado |
| `EXCUSED_ABSENSE` | Falta justificada |
| `UNJUSTIFIED_ABSENSE` | Falta não justificada |
| `NO_SHOW` | Paciente faltou |
| `SCHEDULED` | Agendado |
| `PENDING_SCHEDULING` | Sem agendamento |
| `OVERDUE` | Atrasado |

### Priority

| Nome técnico | Rótulo |
|---|---|
| `ELECTIVE` | Eletivo |
| `PRIORITY` | Prioritário |
| `URGENT` | Urgente |

### CareModality

| Nome técnico | Rótulo |
|---|---|
| `AMBULATORY` | Ambulatorial |
| `HOSPITAL` | Hospitalar |

### RequestCharacter

| Nome técnico | Rótulo |
|---|---|
| `ELECTIVE` | Eletivo |
| `URGENT` | Urgente |

### ServiceTypes

| Nome técnico | Rótulo |
|---|---|
| `EXAM` | Exame |
| `SPECIALTY` | Especialidade |

### Agreements

| Nome técnico | Rótulo |
|---|---|
| `PRIVATE` | Particular |
| `INTERN` | Interno |
| `SUS` | SUS |
| `CISALV` | CISALV |
| `UNDEFINED` | Indefinido |

### Roles

| Nome técnico | Rótulo |
|---|---|
| `API` | API |
| `ADMIN` | ADMIN |
| `MANAGER` | MANAGER |
| `EMPLOYEE` | EMPLOYEE |
| `PATIENT` | PATIENT |

Agreements é um enum legado; os convênios locais usam agreementId e nomes consultados em /agreements. Não restrinja todos os convênios aos rótulos desse enum. Pré agendado não é aceito na rota de alteração para status comum: use a rota específica.

<a id="endpoints"></a>
## Endpoints por módulo

- [Agentes de saúde](#modulo-0)
- [Atendimentos](#modulo-1)
- [Autenticação](#modulo-2)
- [Categorias](#modulo-3)
- [Catálogos DATASUS](#modulo-4)
- [Cirurgias](#modulo-5)
- [Condições de saúde](#modulo-6)
- [Convênios](#modulo-7)
- [Funcionários](#modulo-8)
- [Instituições](#modulo-9)
- [Pacientes](#modulo-10)
- [Profissionais](#modulo-11)
- [Senhas](#modulo-12)
- [Serviços e procedimentos](#modulo-13)
- [Tipos de cirurgia](#modulo-14)

<a id="modulo-0"></a>
### Agentes de saúde

<details>
<summary><strong>GET /agents</strong> — Listar agentes de saúde</summary>

Consulta agentes de saúde com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `name` | query | string | Não | Nome de exibição do registro. |
| `gender` | query | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. |
| `cpf` | query | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. |
| `birthday` | query | string | Não | Data de nascimento. Formato AAAA-MM-DD. |
| `email` | query | string | Não | Endereço de e-mail de contato. |
| `sex` | query | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. |
| `cell-phone` | query | string | Não | Telefone celular de contato. |
| `residential-phone` | query | string | Não | Telefone residencial. |
| `contact-phone` | query | string | Não | Telefone de contato alternativo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageAgentResponseDTO](#schema-pageagentresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageAgentResponseDTO](#schema-pageagentresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageAgentResponseDTO](#schema-pageagentresponsedto).

</details>
<details>
<summary><strong>POST /agents</strong> — Cadastrar registro</summary>

Cria um registro de agentes de saúde. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [AgentRequestDTO](#schema-agentrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /agents/get-all-simplified</strong> — Listar agentes de saúde</summary>

Consulta agentes de saúde com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 20. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageAgentSimplifiedResponseDTO](#schema-pageagentsimplifiedresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageAgentSimplifiedResponseDTO](#schema-pageagentsimplifiedresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageAgentSimplifiedResponseDTO](#schema-pageagentsimplifiedresponsedto).

</details>
<details>
<summary><strong>DELETE /agents/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /agents/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /agents/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [AgentRequestDTO](#schema-agentrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-1"></a>
### Atendimentos

<details>
<summary><strong>GET /appointments</strong> — Listar atendimentos</summary>

Consulta atendimentos com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `notes` | query | string | Não | Observações do atendimento; até 1000 caracteres. |
| `scheduledAt` | query | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-at-start` | query | string | Não | Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-at-end` | query | string | Não | Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `createdAt` | query | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at-start` | query | string | Não | Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at-end` | query | string | Não | Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-forecast` | query | string | Não | Previsão de agendamento usada como filtro. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-forecast-start` | query | string | Não | Data e hora inicial do intervalo de previsão de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-forecast-end` | query | string | Não | Data e hora final do intervalo de previsão de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `priorit` | query | string | Não | Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente. |
| `agreement` | query | string | Não | Nome do convênio usado no relatório/filtro. |
| `status` | query | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. |
| `responsible-professional-name` | query | string | Não | Nome do profissional responsável usado no filtro. |
| `requesting-professional-name` | query | string | Não | Nome do profissional solicitante usado no filtro. |
| `employee-name` | query | string | Não | Nome do funcionário usado no filtro. |
| `patient-name` | query | string | Não | Nome do paciente usado no filtro. |
| `type` | query | string | Não | Tipo/classificação do registro. Valores: Exame, Especialidade. |
| `category-name` | query | string | Não | Nome da categoria do serviço. |
| `service-type-name` | query | string | Não | Nome do serviço/procedimento cadastrado. |
| `instituition-name` | query | string | Não | Nome da instituição do vínculo legado. |
| `is-sorted-by-name` | query | boolean | Não | true ordena pelo nome do serviço; false ordena por createdAt. |
| `is-return` | query | boolean | Não | Indica se o atendimento é um retorno. false quando omitido em DTO com boolean primitivo. |
| `is-descending` | query | boolean | Não | true ordena de forma decrescente; false, crescente. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageAppointmentResponseDTO](#schema-pageappointmentresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageAppointmentResponseDTO](#schema-pageappointmentresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageAppointmentResponseDTO](#schema-pageappointmentresponsedto).

</details>
<details>
<summary><strong>POST /appointments</strong> — Cadastrar registro</summary>

Cria um registro de atendimentos. Instituições dos quatro papéis são independentes do vínculo legado. Campos de regulação omitidos/null são preservados na atualização. Use os rótulos em português para status, priority, careModality e requestCharacter. Acesso: ADMIN, MANAGER, EMPLOYEE.


**Corpo (application/json):** [AppointmentRequestDTO](#schema-appointmentrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /appointments/generate-authorization-form/{id}</strong> — Consultar dados da autorização</summary>

Retorna JSON com paciente, instituição e procedimento para montar a autorização no frontend; não retorna PDF. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [AuthorizationFormResponseDTO](#schema-authorizationformresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [AuthorizationFormResponseDTO](#schema-authorizationformresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [AuthorizationFormResponseDTO](#schema-authorizationformresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /appointments/get-patient-appointments</strong> — Consultar meus atendimentos</summary>

Consulta os atendimentos do paciente identificado pelo token. Acesso efetivo na configuração atual: ADMIN, MANAGER e PATIENT. A constante de permissão não inclui EMPLOYEE nesta rota.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PagePatientAppointmentResponseDTO](#schema-pagepatientappointmentresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PagePatientAppointmentResponseDTO](#schema-pagepatientappointmentresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PagePatientAppointmentResponseDTO](#schema-pagepatientappointmentresponsedto).

</details>
<details>
<summary><strong>GET /appointments/report-patients-graph</strong> — Consultar totais de atendimentos</summary>

Retorna contagens por situação para os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `patient-name` | query | string | Não | Nome do paciente. |
| `mother-name` | query | string | Não | Nome da mãe usado para filtrar pacientes. |
| `responsible-professional-name` | query | string | Não | Nome do profissional responsável usado no filtro. |
| `requesting-professional-name` | query | string | Não | Nome do profissional solicitante usado no filtro. |
| `status` | query | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. |
| `agreement` | query | string | Não | Nome do convênio usado no relatório/filtro. |
| `priorit` | query | string | Não | Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente. |
| `type` | query | string | Não | Tipo/classificação do registro. Valores: Exame, Especialidade. |
| `service-name` | query | string | Não | Nome do serviço/procedimento. |
| `instituition-name` | query | string | Não | Nome da instituição do vínculo legado. |
| `scheduled-at` | query | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-at-start` | query | string | Não | Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-at-end` | query | string | Não | Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at` | query | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at-start` | query | string | Não | Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at-end` | query | string | Não | Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [ReportAppointmentGraphResponseDTO](#schema-reportappointmentgraphresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [ReportAppointmentGraphResponseDTO](#schema-reportappointmentgraphresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [ReportAppointmentGraphResponseDTO](#schema-reportappointmentgraphresponsedto).

</details>
<details>
<summary><strong>GET /appointments/report-patients-page</strong> — Consultar relatório paginado de pacientes</summary>

Retorna uma página de linhas de relatório com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `patient-name` | query | string | Não | Nome do paciente. |
| `mother-name` | query | string | Não | Nome da mãe usado para filtrar pacientes. |
| `responsible-professional-name` | query | string | Não | Nome do profissional responsável usado no filtro. |
| `requesting-professional-name` | query | string | Não | Nome do profissional solicitante usado no filtro. |
| `status` | query | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. |
| `agreement` | query | string | Não | Nome do convênio usado no relatório/filtro. |
| `priorit` | query | string | Não | Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente. |
| `type` | query | string | Não | Tipo/classificação do registro. Valores: Exame, Especialidade. |
| `service-name` | query | string | Não | Nome do serviço/procedimento. |
| `instituition-name` | query | string | Não | Nome da instituição do vínculo legado. |
| `scheduled-at` | query | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-at-start` | query | string | Não | Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `scheduled-at-end` | query | string | Não | Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at` | query | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at-start` | query | string | Não | Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `created-at-end` | query | string | Não | Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PagePatientReportReponseDTO](#schema-pagepatientreportreponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PagePatientReportReponseDTO](#schema-pagepatientreportreponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PagePatientReportReponseDTO](#schema-pagepatientreportreponsedto).

</details>
<details>
<summary><strong>GET /appointments/report-patients-pdf</strong> — Baixar relatório de pacientes em PDF</summary>

Retorna application/pdf como anexo. fields escolhe as colunas e sua ordem; omitido/vazio inclui todas. sort usa campo,asc ou campo,desc. Campo, ordenação ou limite de registros inválido retorna 400. Leia a resposta como Blob no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `patient-name` | query | string | Não | Nome do paciente. |
| `mother-name` | query | string | Não | Nome da mãe usado para filtrar pacientes. |
| `responsible-professional-name` | query | string | Não | Nome do profissional responsável. |
| `requesting-professional-name` | query | string | Não | Nome do profissional solicitante. |
| `status` | query | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. |
| `agreement` | query | string | Não | Nome do convênio usado no relatório/filtro. |
| `priorit` | query | string | Não | Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente. |
| `type` | query | string | Não | Tipo/classificação do registro. Valores: Exame, Especialidade. |
| `service-name` | query | string | Não | Nome do serviço/procedimento. |
| `instituition-name` | query | string | Não | Nome da instituição do vínculo legado. |
| `scheduled-at` | query | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. |
| `scheduled-at-start` | query | string | Não | Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. |
| `scheduled-at-end` | query | string | Não | Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. |
| `created-at` | query | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. |
| `created-at-start` | query | string | Não | Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. |
| `created-at-end` | query | string | Não | Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. |
| `fields` | query | Lista de string | Não | Colunas do PDF, na ordem desejada. Separe por vírgulas ou repita o parâmetro. Omitido/vazio inclui todas. Opções: patientName, motherName, scheduledAt, createdAt, status, responsibleProfessionalName, requestingProfessionalName, priorit, serviceName, serviceType, agreement. |
| `sort` | query | string | Não | Ordenação no formato campo,asc ou campo,desc. Exemplo: scheduledAt,desc. |

**Respostas:**

- **200:** Consulta/operação concluída. application/pdf: string.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.

</details>
<details>
<summary><strong>PATCH /appointments/set-appointments-to-new-status</strong> — Alterar status em lote</summary>

Envie appointmentsId, status e justificativa opcional. Não aceita Pré agendado: use a rota específica. IDs repetidos são processados uma vez; status já igual não cria novo evento. Acesso: ADMIN, MANAGER, EMPLOYEE.


**Corpo (application/json):** [NewCommonStatusDTO](#schema-newcommonstatusdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PATCH /appointments/set-appointments-to-pre-scheduled</strong> — Pré-agendar atendimentos em lote</summary>

Envie appointmentsId e, opcionalmente, statusChangeReason. Aplica Pré agendado e registra as mudanças no histórico. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [PreSchedulingRequestDTO](#schema-preschedulingrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /appointments/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /appointments/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [AppointmentGetByIdResponseDTO](#schema-appointmentgetbyidresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [AppointmentGetByIdResponseDTO](#schema-appointmentgetbyidresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [AppointmentGetByIdResponseDTO](#schema-appointmentgetbyidresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /appointments/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Instituições dos quatro papéis são independentes do vínculo legado. Campos de regulação omitidos/null são preservados na atualização. Use os rótulos em português para status, priority, careModality e requestCharacter. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [AppointmentRequestDTO](#schema-appointmentrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /appointments/{id}/status-history</strong> — Consultar histórico de status</summary>

Eventos do atendimento, do mais recente ao mais antigo (recordedAt e id decrescentes). Use pageNumber e pageLength, sem hífen. Alterações automáticas identificam o autor como Sistema. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |
| `0` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `20` | query | inteiro | Não | Quantidade de registros por página. Padrão: 20. Limite: 1 a 100. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageAppointmentStatusHistoryResponseDTO](#schema-pageappointmentstatushistoryresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageAppointmentStatusHistoryResponseDTO](#schema-pageappointmentstatushistoryresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageAppointmentStatusHistoryResponseDTO](#schema-pageappointmentstatushistoryresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-2"></a>
### Autenticação

<details>
<summary><strong>POST /auth/login</strong> — Autenticar usuário</summary>

Envie login e password. A resposta contém access_token, refresh_token, login e roles. Use access_token no Bearer das demais requisições. Não exige token de acesso.


**Corpo (application/json):** [AuthRequestDTO](#schema-authrequestdto). 

**Respostas:**

- **200:** Consulta/operação concluída. */*: [AuthResponseDTO](#schema-authresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [AuthResponseDTO](#schema-authresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [AuthResponseDTO](#schema-authresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>POST /auth/logout</strong> — Encerrar sessão</summary>

Envie refreshToken no corpo JSON para revogar o token de renovação. Resposta sem corpo. Não exige token de acesso.


**Corpo (application/json):** objeto. Objeto com refreshToken; use o refresh_token recebido no login.

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /auth/profile</strong> — Consultar meu perfil</summary>

Retorna o perfil identificado pelo token enviado no cabeçalho Authorization. Exige token de acesso válido.


**Respostas:**

- **200:** Consulta/operação concluída. */*: [ProfileResponseDTO](#schema-profileresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [ProfileResponseDTO](#schema-profileresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [ProfileResponseDTO](#schema-profileresponsedto).

</details>
<details>
<summary><strong>POST /auth/refresh</strong> — Renovar acesso</summary>

Envie {"refreshToken":"..."}; recebe {"accessToken":"..."}. Atenção: estas chaves usam camelCase, diferentemente da resposta de /auth/login. Não exige token de acesso.


**Corpo (application/json):** objeto. Objeto com refreshToken; use o refresh_token recebido no login.

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-3"></a>
### Categorias

<details>
<summary><strong>GET /category-groups</strong> — Listar categorias</summary>

Consulta categorias com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `name` | query | string | Não | Nome de exibição do registro. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageCategoryGroupResponseDTO](#schema-pagecategorygroupresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageCategoryGroupResponseDTO](#schema-pagecategorygroupresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageCategoryGroupResponseDTO](#schema-pagecategorygroupresponsedto).

</details>
<details>
<summary><strong>POST /category-groups</strong> — Cadastrar registro</summary>

Cria um registro de categorias. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [CategoryGroupRequestDTO](#schema-categorygrouprequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /category-groups/get-all-simplified</strong> — Listar categorias</summary>

Consulta categorias com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 20. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageCategoryGroupSimplifiedResponseDTO](#schema-pagecategorygroupsimplifiedresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageCategoryGroupSimplifiedResponseDTO](#schema-pagecategorygroupsimplifiedresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageCategoryGroupSimplifiedResponseDTO](#schema-pagecategorygroupsimplifiedresponsedto).

</details>
<details>
<summary><strong>DELETE /category-groups/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /category-groups/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /category-groups/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [CategoryGroupRequestDTO](#schema-categorygrouprequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-4"></a>
### Catálogos DATASUS

<details>
<summary><strong>GET /datasus/ciap2</strong> — Consultar catálogo CIAP-2</summary>

Busca parcial por código ou descrição, sem distinguir maiúsculas. Ordenação por code. Não afirma elegibilidade do código na RNDS. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. Limite: 1 a 100. |
| `search` | query | string | Não | Texto de busca; consulte os campos pesquisados na descrição desta operação. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageCiap2ResponseDTO](#schema-pageciap2responsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageCiap2ResponseDTO](#schema-pageciap2responsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageCiap2ResponseDTO](#schema-pageciap2responsedto).

</details>
<details>
<summary><strong>GET /datasus/cid10</strong> — Consultar catálogo CID-10</summary>

Busca parcial por código ou descrição, sem distinguir maiúsculas. Ordenação por code. Inclui códigos inativos no PEC. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. Limite: 1 a 100. |
| `search` | query | string | Não | Texto de busca; consulte os campos pesquisados na descrição desta operação. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageCid10ResponseDTO](#schema-pagecid10responsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageCid10ResponseDTO](#schema-pagecid10responsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageCid10ResponseDTO](#schema-pagecid10responsedto).

</details>
<details>
<summary><strong>GET /datasus/cnes/estabilishments</strong> — Consultar estabelecimentos CNES</summary>

Busca parcial por razão social, nome fantasia, CNES ou CNPJ, sem distinguir maiúsculas. Ordenação por tradeName e id. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. Limite: 1 a 100. |
| `search` | query | string | Não | Texto de busca; consulte os campos pesquisados na descrição desta operação. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageCnesEstabilishmentResponseDTO](#schema-pagecnesestabilishmentresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageCnesEstabilishmentResponseDTO](#schema-pagecnesestabilishmentresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageCnesEstabilishmentResponseDTO](#schema-pagecnesestabilishmentresponsedto).

</details>
<details>
<summary><strong>GET /datasus/cnes/estabilishments/{id}</strong> — Consultar estabelecimento CNES por ID</summary>

Retorna um estabelecimento importado, não uma instituição cadastrada no Vitalya. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [CnesEstabilishmentResponseDTO](#schema-cnesestabilishmentresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [CnesEstabilishmentResponseDTO](#schema-cnesestabilishmentresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [CnesEstabilishmentResponseDTO](#schema-cnesestabilishmentresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /datasus/cnes/professionals</strong> — Consultar vínculos profissionais CNES</summary>

Busca parcial por nome ou CNS. Filtros exatos cnes e cbo são combinados com a busca. CBO não distingue maiúsculas. Cada registro é um vínculo: um CNS pode aparecer várias vezes. Ordenação por name e id. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. Limite: 1 a 100. |
| `search` | query | string | Não | Texto de busca; consulte os campos pesquisados na descrição desta operação. |
| `cnes` | query | string | Não | Filtro cnes. Filtro exato. |
| `cbo` | query | string | Não | Código da Classificação Brasileira de Ocupações, mantido como texto. Filtro exato, sem distinguir maiúsculas. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageCnesProfessionalResponseDTO](#schema-pagecnesprofessionalresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageCnesProfessionalResponseDTO](#schema-pagecnesprofessionalresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageCnesProfessionalResponseDTO](#schema-pagecnesprofessionalresponsedto).

</details>
<details>
<summary><strong>GET /datasus/cnes/professionals/{id}</strong> — Consultar vínculo CNES por ID</summary>

Retorna um vínculo profissional importado, não um profissional cadastrado no Vitalya. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [CnesProfessionalResponseDTO](#schema-cnesprofessionalresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [CnesProfessionalResponseDTO](#schema-cnesprofessionalresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [CnesProfessionalResponseDTO](#schema-cnesprofessionalresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>POST /datasus/import-cnes-estabilishments</strong> — Importar estabelecimentos CNES</summary>

Envie CSV UTF-8 separado por ponto e vírgula no campo multipart file. Valida antes de substituir o catálogo. Resposta confirma catalog e importedRecords após commit. Acesso: ADMIN.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `file` | query | string | Sim | Arquivo CSV enviado como multipart/form-data, no campo file. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [CnesImportResponseDTO](#schema-cnesimportresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [CnesImportResponseDTO](#schema-cnesimportresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [CnesImportResponseDTO](#schema-cnesimportresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **413:** Arquivo maior que o limite configurado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>POST /datasus/import-cnes-professionals</strong> — Importar vínculos profissionais CNES</summary>

Envie CSV UTF-8 separado por ponto e vírgula no campo multipart file. Valida os registros antes de substituir o catálogo. Preserva vínculos repetidos por CNS e CBO alfanumérico. Resposta confirma catalog e importedRecords após commit. Acesso: ADMIN.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `file` | query | string | Sim | Arquivo CSV enviado como multipart/form-data, no campo file. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [CnesImportResponseDTO](#schema-cnesimportresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [CnesImportResponseDTO](#schema-cnesimportresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [CnesImportResponseDTO](#schema-cnesimportresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **413:** Arquivo maior que o limite configurado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>POST /datasus/import-sigtap</strong> — Forçar importação SIGTAP</summary>

Endpoint temporário de teste. Baixa e importa a versão mais recente mesmo se já importada. Responde após o commit com competência e contagens. Acesso: ADMIN.


**Respostas:**

- **200:** Consulta/operação concluída. */*: [SigtapImportResponseDTO](#schema-sigtapimportresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [SigtapImportResponseDTO](#schema-sigtapimportresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [SigtapImportResponseDTO](#schema-sigtapimportresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /datasus/sigtap/procedures</strong> — Consultar procedimentos SIGTAP</summary>

Busca parcial por código ou nome, sem distinguir maiúsculas. Ordenação por code e id. O catálogo importado é distinto dos serviços cadastrados no Vitalya. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. Limite: 1 a 100. |
| `search` | query | string | Não | Texto de busca; consulte os campos pesquisados na descrição desta operação. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageSigtapProcedureResponseDTO](#schema-pagesigtapprocedureresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageSigtapProcedureResponseDTO](#schema-pagesigtapprocedureresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageSigtapProcedureResponseDTO](#schema-pagesigtapprocedureresponsedto).

</details>
<details>
<summary><strong>GET /datasus/sigtap/procedures/{id}</strong> — Consultar procedimento SIGTAP por ID</summary>

Retorna o procedimento importado e os códigos/nomes da hierarquia. O ID é interno do catálogo. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [SigtapProcedureResponseDTO](#schema-sigtapprocedureresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [SigtapProcedureResponseDTO](#schema-sigtapprocedureresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [SigtapProcedureResponseDTO](#schema-sigtapprocedureresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-5"></a>
### Cirurgias

<details>
<summary><strong>GET /surgeries</strong> — Listar cirurgias</summary>

Consulta cirurgias com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `date-time` | query | string | Não | Data e hora do registro cirúrgico. Formato AAAA-MM-DDTHH:mm:ss, sem offset. |
| `person-name` | query | string | Não | Nome da pessoa vinculada ao registro cirúrgico. |
| `surgery-risk` | query | string | Não | Informação textual sobre o risco cirúrgico. |
| `location` | query | string | Não | Local associado ao registro cirúrgico. |
| `conclusion` | query | string | Não | Conclusão registrada para a cirurgia. |
| `sus-easy` | query | string | Não | Referência textual ao SUS Fácil, conforme cadastro. |
| `sesap` | query | string | Não | Referência textual à SESAP, conforme cadastro. |
| `procedure-date` | query | string | Não | Data de realização do procedimento. Formato AAAA-MM-DD. |
| `anesthesic-risk` | query | string | Não | Informação textual sobre risco anestésico. |
| `observation` | query | string | Não | Observações do registro cirúrgico. |
| `surgery-type-id` | query | inteiro | Não | ID do tipo de cirurgia cadastrado. Consulte GET /surgery-types. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageSurgeryResponseDTO](#schema-pagesurgeryresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageSurgeryResponseDTO](#schema-pagesurgeryresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageSurgeryResponseDTO](#schema-pagesurgeryresponsedto).

</details>
<details>
<summary><strong>POST /surgeries</strong> — Cadastrar registro</summary>

Cria um registro de cirurgias. Acesso: ADMIN, MANAGER, EMPLOYEE.


**Corpo (application/json):** [SurgeryRequestDTO](#schema-surgeryrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /surgeries/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /surgeries/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /surgeries/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [SurgeryRequestDTO](#schema-surgeryrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-6"></a>
### Condições de saúde

<details>
<summary><strong>GET /conditions</strong> — Listar condições de saúde</summary>

Consulta condições de saúde com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `specification` | query | string | Não | Descrição da condição de saúde cadastrada. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageConditionResponseDTO](#schema-pageconditionresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageConditionResponseDTO](#schema-pageconditionresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageConditionResponseDTO](#schema-pageconditionresponsedto).

</details>
<details>
<summary><strong>POST /conditions</strong> — Cadastrar registro</summary>

Cria um registro de condições de saúde. Acesso: ADMIN, MANAGER, EMPLOYEE.


**Corpo (application/json):** [ConditionRequestDTO](#schema-conditionrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /conditions/get-all-simplified</strong> — Listar condições de saúde</summary>

Consulta condições de saúde com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageConditionSimplifiedResponseDTO](#schema-pageconditionsimplifiedresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageConditionSimplifiedResponseDTO](#schema-pageconditionsimplifiedresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageConditionSimplifiedResponseDTO](#schema-pageconditionsimplifiedresponsedto).

</details>
<details>
<summary><strong>DELETE /conditions/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /conditions/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /conditions/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [ConditionRequestDTO](#schema-conditionrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-7"></a>
### Convênios

<details>
<summary><strong>GET /agreements</strong> — Listar convênios</summary>

Consulta convênios com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 20. |
| `name` | query | string | Não | Nome de exibição do registro. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageAgreementResponseDTO](#schema-pageagreementresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageAgreementResponseDTO](#schema-pageagreementresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageAgreementResponseDTO](#schema-pageagreementresponsedto).

</details>
<details>
<summary><strong>POST /agreements</strong> — Cadastrar registro</summary>

Cria um registro de convênios. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [AgreementRequestDTO](#schema-agreementrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [AgreementResponseDTO](#schema-agreementresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [AgreementResponseDTO](#schema-agreementresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /agreements/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /agreements/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /agreements/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [AgreementRequestDTO](#schema-agreementrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-8"></a>
### Funcionários

<details>
<summary><strong>GET /employees</strong> — Listar funcionários</summary>

Consulta funcionários com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `name` | query | string | Não | Nome de exibição do registro. |
| `gender` | query | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. |
| `cpf` | query | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. |
| `birthday` | query | string | Não | Data de nascimento. Formato AAAA-MM-DD. |
| `email` | query | string | Não | Endereço de e-mail de contato. |
| `sex` | query | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. |
| `cell-phone` | query | string | Não | Telefone celular de contato. |
| `residential-phone` | query | string | Não | Telefone residencial. |
| `contact-phone` | query | string | Não | Telefone de contato alternativo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageEmployeeResponseDTO](#schema-pageemployeeresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageEmployeeResponseDTO](#schema-pageemployeeresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageEmployeeResponseDTO](#schema-pageemployeeresponsedto).

</details>
<details>
<summary><strong>POST /employees</strong> — Cadastrar registro</summary>

Cria um registro de funcionários. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [EmployeeRequestDTO](#schema-employeerequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /employees/get-all-simplified</strong> — Listar funcionários</summary>

Consulta funcionários com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 20. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageEmployeeSimplifiedResponseDTO](#schema-pageemployeesimplifiedresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageEmployeeSimplifiedResponseDTO](#schema-pageemployeesimplifiedresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageEmployeeSimplifiedResponseDTO](#schema-pageemployeesimplifiedresponsedto).

</details>
<details>
<summary><strong>DELETE /employees/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /employees/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /employees/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [EmployeeRequestDTO](#schema-employeerequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-9"></a>
### Instituições

<details>
<summary><strong>GET /instituition</strong> — Listar instituições</summary>

Consulta instituições com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `name` | query | string | Não | Nome de exibição do registro. |
| `cep` | query | string | Não | Código postal como texto; preserve zeros à esquerda. |
| `cityName` | query | string | Não | Nome do município. |
| `address` | query | string | Não | Endereço completo informado no cadastro. |
| `phone` | query | string | Não | Telefone da instituição. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageBasicResponsesInstituitionResponseDTO](#schema-pagebasicresponsesinstituitionresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageBasicResponsesInstituitionResponseDTO](#schema-pagebasicresponsesinstituitionresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageBasicResponsesInstituitionResponseDTO](#schema-pagebasicresponsesinstituitionresponsedto).

</details>
<details>
<summary><strong>POST /instituition</strong> — Cadastrar registro</summary>

Cria um registro de instituições. Acesso: ADMIN, MANAGER, EMPLOYEE.


**Corpo (application/json):** [InstituitionRequestDTO](#schema-instituitionrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /instituition/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /instituition/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /instituition/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [InstituitionRequestDTO](#schema-instituitionrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-10"></a>
### Pacientes

<details>
<summary><strong>GET /patients</strong> — Listar pacientes</summary>

Consulta pacientes com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `name` | query | string | Não | Nome de exibição do registro. |
| `gender` | query | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. |
| `cpf` | query | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. |
| `birthday` | query | string | Não | Data de nascimento. Formato AAAA-MM-DD. |
| `email` | query | string | Não | Endereço de e-mail de contato. |
| `cns` | query | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. |
| `mother-name` | query | string | Não | Nome da mãe do paciente. |
| `team-name` | query | string | Não | Nome da equipe de saúde. |
| `team-ine` | query | string | Não | Identificador Nacional de Equipe (INE), mantido como texto. |
| `micro-area` | query | string | Não | Identificação da microárea de acompanhamento. |
| `origin` | query | string | Não | Origem do cadastro ou encaminhamento. |
| `sex` | query | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. |
| `cell-phone` | query | string | Não | Telefone celular de contato. |
| `residential-phone` | query | string | Não | Telefone residencial. |
| `contact-phone` | query | string | Não | Telefone de contato alternativo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PagePatientResponseDTO](#schema-pagepatientresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PagePatientResponseDTO](#schema-pagepatientresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PagePatientResponseDTO](#schema-pagepatientresponsedto).

</details>
<details>
<summary><strong>POST /patients</strong> — Cadastrar registro</summary>

Cria um registro de pacientes. Acesso: ADMIN.


**Corpo (application/json):** [PatientRequestDTO](#schema-patientrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /patients/get-all-simplified</strong> — Listar pacientes</summary>

Consulta pacientes com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PagePatientSimplifiedResponseDTO](#schema-pagepatientsimplifiedresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PagePatientSimplifiedResponseDTO](#schema-pagepatientsimplifiedresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PagePatientSimplifiedResponseDTO](#schema-pagepatientsimplifiedresponsedto).

</details>
<details>
<summary><strong>GET /patients/info-patient/{id}</strong> — Consultar ficha do paciente</summary>

Retorna dados pessoais, vínculos, condições e atendimentos do paciente. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PatientInfoResponseDTO](#schema-patientinforesponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PatientInfoResponseDTO](#schema-patientinforesponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PatientInfoResponseDTO](#schema-patientinforesponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /patients/simplified-list</strong> — Consultar lista resumida de pacientes</summary>

Retorna uma lista sem paginação com identificação, nome, CPF e nome da mãe. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.


**Respostas:**

- **200:** Consulta/operação concluída. */*: Lista de [PatientSimplifiedInfoDTO](#schema-patientsimplifiedinfodto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: Lista de [PatientSimplifiedInfoDTO](#schema-patientsimplifiedinfodto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: Lista de [PatientSimplifiedInfoDTO](#schema-patientsimplifiedinfodto).

</details>
<details>
<summary><strong>POST /patients/sync-patients</strong> — Sincronizar pacientes do PEC</summary>

Recebe um array JSON de PersonBackupInfoDTO. Rota de integração; não recebe um objeto contendo uma lista. Acesso: API.


**Corpo (application/json):** Lista de [PersonBackupInfoDTO](#schema-personbackupinfodto). 

**Respostas:**

- **200:** Consulta/operação concluída. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PATCH /patients/update-login</strong> — Atualizar CPF e login do paciente</summary>

Envie patientId e cpf. A operação usa a identidade do usuário autenticado. Acesso: ADMIN, MANAGER, EMPLOYEE.


**Corpo (application/json):** [PatientCPFRequestDTO](#schema-patientcpfrequestdto). 

**Respostas:**

- **200:** Consulta/operação concluída. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /patients/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /patients/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PatientResponseDTO](#schema-patientresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PatientResponseDTO](#schema-patientresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PatientResponseDTO](#schema-patientresponsedto).
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /patients/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [PatientRequestDTO](#schema-patientrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-11"></a>
### Profissionais

<details>
<summary><strong>GET /professionals</strong> — Listar profissionais</summary>

Consulta profissionais com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `name` | query | string | Não | Nome de exibição do registro. |
| `gender` | query | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. |
| `cpf` | query | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. |
| `birthday` | query | string | Não | Data de nascimento. Formato AAAA-MM-DD. |
| `email` | query | string | Não | Endereço de e-mail de contato. |
| `sex` | query | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. |
| `cell-phone` | query | string | Não | Telefone celular de contato. |
| `residential-phone` | query | string | Não | Telefone residencial. |
| `contact-phone` | query | string | Não | Telefone de contato alternativo. |
| `cns` | query | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. |
| `cbo` | query | string | Não | Código da Classificação Brasileira de Ocupações, mantido como texto. |
| `vinculation` | query | string | Não | Descrição do vínculo do profissional. |
| `description` | query | string | Não | Descrição textual do registro. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageProfessionalResponseDTO](#schema-pageprofessionalresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageProfessionalResponseDTO](#schema-pageprofessionalresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageProfessionalResponseDTO](#schema-pageprofessionalresponsedto).

</details>
<details>
<summary><strong>POST /professionals</strong> — Cadastrar registro</summary>

Cria um registro de profissionais. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [ProfessionalRequestDTO](#schema-professionalrequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /professionals/get-all-simplified</strong> — Listar profissionais</summary>

Consulta profissionais com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 20. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageProfessionalSimplifiedResponseDTO](#schema-pageprofessionalsimplifiedresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageProfessionalSimplifiedResponseDTO](#schema-pageprofessionalsimplifiedresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageProfessionalSimplifiedResponseDTO](#schema-pageprofessionalsimplifiedresponsedto).

</details>
<details>
<summary><strong>DELETE /professionals/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /professionals/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /professionals/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [ProfessionalRequestDTO](#schema-professionalrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-12"></a>
### Senhas

<details>
<summary><strong>PUT /login</strong> — Alterar minha senha</summary>

Atualiza a senha da conta identificada pelo token de acesso. Acesso: ADMIN, MANAGER, EMPLOYEE, PATIENT.


**Corpo (application/json):** [LoginRequestDTO](#schema-loginrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>POST /login/forgot-password</strong> — Solicitar redefinição de senha</summary>

Envie email para iniciar a recuperação. Apesar do nome, esta rota está sob /login e a configuração atual exige autenticação HTTP. Exige token de acesso válido.


**Corpo (application/json):** [ForgotPasswordRequestDTO](#schema-forgotpasswordrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>POST /login/reset-password</strong> — Redefinir senha</summary>

Envie token e newPassword. A configuração HTTP atual exige autenticação também nesta rota sob /login. Exige token de acesso válido.


**Corpo (application/json):** [ResetPasswordRequestDTO](#schema-resetpasswordrequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-13"></a>
### Serviços e procedimentos

<details>
<summary><strong>GET /service-types</strong> — Listar serviços e procedimentos</summary>

Consulta serviços e procedimentos com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `code` | query | string | Não | Código oficial do item no catálogo, mantido como texto. |
| `name` | query | string | Não | Nome de exibição do registro. |
| `type` | query | string | Não | Tipo/classificação do registro. |
| `category-group-name` | query | string | Não | Nome do grupo de categorias do serviço. |
| `value` | query | decimal | Não | Valor monetário do serviço, como número decimal. No cadastro, deve ser maior que zero e ter até duas casas decimais. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageServiceTypeResponseDTO](#schema-pageservicetyperesponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageServiceTypeResponseDTO](#schema-pageservicetyperesponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageServiceTypeResponseDTO](#schema-pageservicetyperesponsedto).

</details>
<details>
<summary><strong>POST /service-types</strong> — Cadastrar registro</summary>

Cria um registro de serviços e procedimentos. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [ServiceTypeRequestDTO](#schema-servicetyperequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /service-types/delete-replace-with-new/{id}</strong> — Substituir serviço por novo cadastro</summary>

Recebe o ID do serviço antigo e o corpo de um novo serviço, substitui referências e remove o anterior. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [ServiceTypeRequestDTO](#schema-servicetyperequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /service-types/delete-replace-with/{id}</strong> — Substituir serviço por cadastro existente</summary>

Informe replaceWithId para transferir as referências e excluir o serviço antigo. Retorna a quantidade de referências substituídas. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |
| `replaceWithId` | query | inteiro | Sim | ID do serviço existente que receberá as referências do serviço removido. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: inteiro.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: inteiro.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: inteiro.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /service-types/get-all-simplified</strong> — Listar serviços e procedimentos</summary>

Consulta serviços e procedimentos com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageServiceTypeSimplifiedResponseDTO](#schema-pageservicetypesimplifiedresponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageServiceTypeSimplifiedResponseDTO](#schema-pageservicetypesimplifiedresponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageServiceTypeSimplifiedResponseDTO](#schema-pageservicetypesimplifiedresponsedto).

</details>
<details>
<summary><strong>POST /service-types/import</strong> — Importar serviços por arquivo</summary>

Envia arquivo no campo multipart file. A implementação lê CSV; apesar do nome interno do método, não envie planilha XLSX. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `file` | query | string | Sim | Arquivo CSV enviado como multipart/form-data, no campo file. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: string.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: string.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: string.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **413:** Arquivo maior que o limite configurado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /service-types/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /service-types/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /service-types/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [ServiceTypeRequestDTO](#schema-servicetyperequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="modulo-14"></a>
### Tipos de cirurgia

<details>
<summary><strong>GET /surgery-type</strong> — Listar tipos de cirurgia</summary>

Consulta tipos de cirurgia com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE, PATIENT.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `page-number` | query | inteiro | Não | Número da página, começando em 0. Padrão: 0. |
| `page-length` | query | inteiro | Não | Quantidade de registros por página. Padrão: 10. |
| `type` | query | string | Não | Tipo/classificação do registro. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: [PageSurgeryTypeResponseDTO](#schema-pagesurgerytyperesponsedto).
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: [PageSurgeryTypeResponseDTO](#schema-pagesurgerytyperesponsedto).
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: [PageSurgeryTypeResponseDTO](#schema-pagesurgerytyperesponsedto).

</details>
<details>
<summary><strong>POST /surgery-type</strong> — Cadastrar registro</summary>

Cria um registro de tipos de cirurgia. Acesso: ADMIN, MANAGER.


**Corpo (application/json):** [SurgeryTypeRequestDTO](#schema-surgerytyperequestdto). 

**Respostas:**

- **201:** Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. sem esquema de corpo.
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>DELETE /surgery-type/{id}</strong> — Excluir registro</summary>

Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>GET /surgery-type/{id}</strong> — Consultar registro por ID</summary>

Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE, PATIENT.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Respostas:**

- **200:** Consulta/operação concluída. */*: objeto.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>
<details>
<summary><strong>PUT /surgery-type/{id}</strong> — Atualizar registro</summary>

Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.

| Parâmetro | Local | Tipo | Obrigatório | Significado |
|---|---|---|---|---|
| `id` | path | inteiro | Sim | Identificador interno do registro no Vitalya; não é o código do catálogo externo. |

**Corpo (application/json):** [SurgeryTypeRequestDTO](#schema-surgerytyperequestdto). 

**Respostas:**

- **204:** Operação concluída, sem corpo. sem esquema de corpo.
- **400:** Parâmetro ou regra de negócio inválida. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **401:** Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo. */*: objeto.
- **403:** Usuário sem permissão. A camada de segurança pode responder sem corpo. */*: objeto.
- **404:** Registro não encontrado. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **409:** Conflito de cadastro ou integridade. */*: [ErrorResponseDTO](#schema-errorresponsedto).
- **422:** Falha de validação; consulte errors com fieldName e message. */*: [ErrorResponseDTO](#schema-errorresponsedto).

</details>

<a id="campos"></a>
## Campos enviados e recebidos

Obrigatório indica presença exigida no esquema. Aceitar null não define o efeito da atualização: consulte a regra da operação. As estruturas abaixo são extraídas do OpenAPI validado.

<a id="schema-agentrequestdto"></a>
<details>
<summary><strong>AgentRequestDTO</strong></summary>

Dados enviados pelo frontend. AgentRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro. Mínimo de caracteres: `0`. Máximo de caracteres: `255`. |
| `birthday` | string | Sim | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `contactPhone` | string | Não | Telefone de contato alternativo. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `cpf` | string | Sim | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. Mínimo de caracteres: `0`. Máximo de caracteres: `14`. |
| `cpfNormalized` | string | Não | CPF normalizado calculado pelo backend; não envie este campo. Somente leitura. |
| `email` | string | Não | Endereço de e-mail de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `320`. Exemplo: `usuario@example.com`. |
| `gender` | string | Sim | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `50`. |
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `residentialPhone` | string | Não | Telefone residencial. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `sex` | string | Sim | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |

</details>

<a id="schema-agentresponsedto"></a>
<details>
<summary><strong>AgentResponseDTO</strong></summary>

Dados retornados pela API. AgentResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato.  |
| `contactPhone` | string | Não | Telefone de contato alternativo.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `gender` | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `residentialPhone` | string | Não | Telefone residencial.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |

</details>

<a id="schema-agentsimplifiedresponsedto"></a>
<details>
<summary><strong>AgentSimplifiedResponseDTO</strong></summary>

Dados retornados pela API. AgentSimplifiedResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-agreementrequestdto"></a>
<details>
<summary><strong>AgreementRequestDTO</strong></summary>

Dados enviados pelo frontend. AgreementRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |

</details>

<a id="schema-agreementresponsedto"></a>
<details>
<summary><strong>AgreementResponseDTO</strong></summary>

Dados retornados pela API. AgreementResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-appointmentgetbyidpatientinforesponsedto"></a>
<details>
<summary><strong>AppointmentGetByIdPatientInfoResponseDTO</strong></summary>

Dados retornados pela API. PatientInfoResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-appointmentgetbyidresponsedto"></a>
<details>
<summary><strong>AppointmentGetByIdResponseDTO</strong></summary>

Dados retornados pela API. AppointmentGetByIdResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `agreements` | string | Não | Nome ou rótulo do convênio retornado pelo atendimento.  |
| `authorInstituition` | [InstituitionInfoResponseDTO](#schema-instituitioninforesponsedto) | Não | Instituição autora do registro.  |
| `careModality` | string | Não | Modalidade assistencial: Ambulatorial para atendimento ambulatorial; Hospitalar para atendimento hospitalar. Valores: Ambulatorial, Hospitalar. Exemplo: `Ambulatorial`. Opções: `Ambulatorial`, `Hospitalar`. |
| `categoryGroup` | [CategoryGroupInfoResponseDTO](#schema-categorygroupinforesponsedto) | Não | Grupo de categorias do serviço.  |
| `clinicalReasonCode` | string | Não | Código textual do motivo clínico, entre 1 e 20 caracteres. Selecione exatamente o code retornado em /datasus/cid10 ou /datasus/ciap2. Código novo deve existir no catálogo selecionado. Mínimo de caracteres: `1`. Máximo de caracteres: `20`. |
| `clinicalReasonSystem` | string | Não | Catálogo do motivo clínico: CID-10 ou CIAP-2. Deve ser informado junto com clinicalReasonCode. Valores: CID-10, CIAP-2. Exemplo: `CID-10`. Opções: `CID-10`, `CIAP-2`. |
| `createdAt` | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `employee` | [EmployeeInfoResponseDTO](#schema-employeeinforesponsedto) | Não | Funcionário responsável pelo atendimento.  |
| `executingCbo` | string | Não | Código CBO da ocupação executante. No atendimento, aceita exatamente seis dígitos, como texto; difere do catálogo CNES que pode conter CBO alfanumérico. Formato: `^[0-9]{6}$`. Exemplo: `225125`. |
| `executingInstituition` | [InstituitionInfoResponseDTO](#schema-instituitioninforesponsedto) | Não | Instituição executante do atendimento.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `instituitionInfoResponseDTO` | [InstituitionInfoResponseDTO](#schema-instituitioninforesponsedto) | Não | Instituição do vínculo legado do atendimento.  |
| `isReturn` | boolean | Não | Indica se o atendimento é um retorno. false quando omitido em DTO com boolean primitivo. Exemplo: `False`. |
| `notes` | string | Não | Observações do atendimento; até 1000 caracteres.  |
| `patient` | [AppointmentGetByIdPatientInfoResponseDTO](#schema-appointmentgetbyidpatientinforesponsedto) | Não | Dados resumidos do paciente.  |
| `priority` | string | Não | Prioridade do atendimento. Valores: Eletivo, Prioritário, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Prioritário`, `Urgente`. |
| `regulatingInstituition` | [InstituitionInfoResponseDTO](#schema-instituitioninforesponsedto) | Não | Instituição reguladora do atendimento.  |
| `requestCharacter` | string | Não | Caráter da solicitação: Eletivo ou Urgente. É independente da prioridade do atendimento. Valores: Eletivo, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Urgente`. |
| `requestedAt` | string | Não | Data efetiva da solicitação assistencial; não é a data de criação do cadastro. Não pode estar no futuro. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `requestingInstituition` | [InstituitionInfoResponseDTO](#schema-instituitioninforesponsedto) | Não | Instituição solicitante do atendimento.  |
| `requestingProfessional` | [ProfessionalInfoResponseDTO](#schema-professionalinforesponsedto) | Não | Dados resumidos do profissional solicitante.  |
| `responsibleProfessional` | [ProfessionalInfoResponseDTO](#schema-professionalinforesponsedto) | Não | Dados resumidos do profissional responsável.  |
| `scheduledAt` | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `schedulingForecast` | string | Não | Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `serviceType` | [ServiceTypeInfoResponseDTO](#schema-servicetypeinforesponsedto) | Não | Classificação do serviço: Exame ou Especialidade.  |
| `status` | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |

</details>

<a id="schema-appointmentrequestdto"></a>
<details>
<summary><strong>AppointmentRequestDTO</strong></summary>

Dados enviados pelo frontend. AppointmentRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `agreementId` | inteiro | Não | ID do convênio cadastrado. Consulte GET /agreements. null ou -1 não atribui um novo convênio.  |
| `authorInstituitionId` | inteiro | Não | ID da instituição autora: unidade responsável pela autoria do registro. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.  |
| `careModality` | string | Não | Modalidade assistencial: Ambulatorial para atendimento ambulatorial; Hospitalar para atendimento hospitalar. Na atualização, null/omissão preserva o valor. Valores: Ambulatorial, Hospitalar. Exemplo: `Ambulatorial`. Opções: `Ambulatorial`, `Hospitalar`. |
| `clinicalReasonCode` | string | Não | Código textual do motivo clínico, entre 1 e 20 caracteres. Selecione exatamente o code retornado em /datasus/cid10 ou /datasus/ciap2. Código novo deve existir no catálogo selecionado. Mínimo de caracteres: `1`. Máximo de caracteres: `20`. |
| `clinicalReasonSystem` | string | Não | Catálogo do motivo clínico: CID-10 ou CIAP-2. Deve ser informado junto com clinicalReasonCode. Valores: CID-10, CIAP-2. Exemplo: `CID-10`. Opções: `CID-10`, `CIAP-2`. |
| `createdAt` | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `employeeId` | inteiro | Sim | ID do funcionário responsável pelo atendimento. Consulte GET /employees.  |
| `executingCbo` | string | Não | Código CBO da ocupação executante. No atendimento, aceita exatamente seis dígitos, como texto; difere do catálogo CNES que pode conter CBO alfanumérico. Formato: `^[0-9]{6}$`. Exemplo: `225125`. |
| `executingInstituitionId` | inteiro | Não | ID da instituição executante: unidade que realiza o atendimento. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.  |
| `instituitionId` | inteiro | Não | ID da instituição do vínculo legado. Consulte GET /instituition. Mantido por compatibilidade; não preenche automaticamente os quatro papéis.  |
| `notes` | string | Não | Observações do atendimento; até 1000 caracteres. Mínimo de caracteres: `0`. Máximo de caracteres: `1000`. |
| `patientId` | inteiro | Sim | ID do paciente no Vitalya. Consulte GET /patients.  |
| `priority` | string | Sim | Prioridade do atendimento. Valores: Eletivo, Prioritário, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Prioritário`, `Urgente`. |
| `regulatingInstituitionId` | inteiro | Não | ID da instituição reguladora: unidade que regula a solicitação. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.  |
| `requestCharacter` | string | Não | Caráter da solicitação: Eletivo ou Urgente. É independente da prioridade do atendimento. Na atualização, null/omissão preserva o valor. Valores: Eletivo, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Urgente`. |
| `requestedAt` | string | Não | Data efetiva da solicitação assistencial; não é a data de criação do cadastro. Não pode estar no futuro. Na atualização, null/omissão preserva o valor. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `requestingInstituitionId` | inteiro | Não | ID da instituição solicitante no Vitalya: unidade que solicita o atendimento. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.  |
| `requestingProfessionalId` | inteiro | Sim | ID do profissional solicitante no Vitalya. Consulte GET /professionals. Necessário no cadastro e na atualização do atendimento.  |
| `responsibleProfessionalId` | inteiro | Não | ID do profissional responsável no Vitalya. Consulte GET /professionals. null ou -1 não atribui um novo vínculo.  |
| `return` | boolean | Não | Indica se o atendimento é um retorno. No DTO de classe, a chave JSON é return; false quando omitida.  |
| `scheduledAt` | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `schedulingForecast` | string | Não | Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `serviceTypeId` | inteiro | Sim | ID do serviço/procedimento cadastrado no Vitalya. Consulte GET /service-types; não é o ID do catálogo SIGTAP.  |
| `status` | string | Sim | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Mínimo de caracteres: `0`. Máximo de caracteres: `30`. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |
| `statusChangeReason` | string | Não | Justificativa opcional para a mudança de status; até 1000 caracteres. Registrada no histórico quando houver mudança. Mínimo de caracteres: `0`. Máximo de caracteres: `1000`. |

</details>

<a id="schema-appointmentresponsedto"></a>
<details>
<summary><strong>AppointmentResponseDTO</strong></summary>

Dados retornados pela API. AppointmentResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `agreementName` | string | Não | Nome do convênio cadastrado.  |
| `authorInstituitionId` | inteiro | Não | ID da instituição autora: unidade responsável pela autoria do registro. Consulte GET /instituition.  |
| `authorInstituitionName` | string | Não | Nome da instituição autora.  |
| `careModality` | string | Não | Modalidade assistencial: Ambulatorial para atendimento ambulatorial; Hospitalar para atendimento hospitalar. Valores: Ambulatorial, Hospitalar. Exemplo: `Ambulatorial`. Opções: `Ambulatorial`, `Hospitalar`. |
| `categoryGroupName` | string | Não | Nome do grupo de categorias do serviço.  |
| `clinicalReasonCode` | string | Não | Código textual do motivo clínico, entre 1 e 20 caracteres. Selecione exatamente o code retornado em /datasus/cid10 ou /datasus/ciap2. Código novo deve existir no catálogo selecionado. Mínimo de caracteres: `1`. Máximo de caracteres: `20`. |
| `clinicalReasonSystem` | string | Não | Catálogo do motivo clínico: CID-10 ou CIAP-2. Deve ser informado junto com clinicalReasonCode. Valores: CID-10, CIAP-2. Exemplo: `CID-10`. Opções: `CID-10`, `CIAP-2`. |
| `createdAt` | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `employeeName` | string | Não | Nome do funcionário responsável.  |
| `executingCbo` | string | Não | Código CBO da ocupação executante. No atendimento, aceita exatamente seis dígitos, como texto; difere do catálogo CNES que pode conter CBO alfanumérico. Formato: `^[0-9]{6}$`. Exemplo: `225125`. |
| `executingInstituitionId` | inteiro | Não | ID da instituição executante: unidade que realiza o atendimento. Consulte GET /instituition.  |
| `executingInstituitionName` | string | Não | Nome da instituição executante.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `instituitionName` | string | Não | Nome da instituição do vínculo legado.  |
| `notes` | string | Não | Observações do atendimento; até 1000 caracteres.  |
| `patientName` | string | Não | Nome do paciente.  |
| `priority` | string | Não | Prioridade do atendimento. Valores: Eletivo, Prioritário, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Prioritário`, `Urgente`. |
| `regulatingInstituitionId` | inteiro | Não | ID da instituição reguladora: unidade que regula a solicitação. Consulte GET /instituition.  |
| `regulatingInstituitionName` | string | Não | Nome da instituição reguladora.  |
| `requestCharacter` | string | Não | Caráter da solicitação: Eletivo ou Urgente. É independente da prioridade do atendimento. Valores: Eletivo, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Urgente`. |
| `requestedAt` | string | Não | Data efetiva da solicitação assistencial; não é a data de criação do cadastro. Não pode estar no futuro. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `requestingInstituitionId` | inteiro | Não | ID da instituição solicitante no Vitalya: unidade que solicita o atendimento. Consulte GET /instituition.  |
| `requestingInstituitionName` | string | Não | Nome da instituição solicitante.  |
| `requestingProfessionalName` | string | Não | Nome do profissional solicitante.  |
| `responsibleProfessionalName` | string | Não | Nome do profissional responsável.  |
| `return` | boolean | Não | Indica se o atendimento é um retorno. No DTO de classe, a chave JSON é return; false quando omitida.  |
| `scheduledAt` | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `schedulingForecast` | string | Não | Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `serviceType` | string | Não | Classificação do serviço: Exame ou Especialidade. Valores: Exame, Especialidade. Exemplo: `Exame`. Opções: `Exame`, `Especialidade`. |
| `serviceTypeName` | string | Não | Nome do serviço/procedimento cadastrado.  |
| `status` | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |

</details>

<a id="schema-appointmentstatushistoryresponsedto"></a>
<details>
<summary><strong>AppointmentStatusHistoryResponseDTO</strong></summary>

Dados retornados pela API. AppointmentStatusHistoryResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `changedById` | inteiro | Não | ID da pessoa autora da alteração; null em mudanças automáticas.  |
| `changedByName` | string | Não | Nome do autor da alteração; Sistema para mudanças automáticas.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `newStatus` | string | Não | Status após a alteração. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |
| `occurredAt` | string | Não | Data e hora em que a alteração ocorreu, atribuídas pelo backend. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `previousStatus` | string | Não | Status anterior à alteração; pode ser null no primeiro registro. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |
| `reason` | string | Não | Justificativa registrada para a alteração de status; pode ser null.  |
| `recordedAt` | string | Não | Data e hora de gravação do evento no histórico, atribuídas pelo backend. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |

</details>

<a id="schema-authrequestdto"></a>
<details>
<summary><strong>AuthRequestDTO</strong></summary>

Dados enviados pelo frontend. AuthRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `login` | string | Sim | Identificação de acesso do usuário. Mínimo de caracteres: `0`. Máximo de caracteres: `100`. |
| `password` | string | Sim | Senha de acesso; envie somente na requisição, nunca em logs. Mínimo de caracteres: `6`. Máximo de caracteres: `72`. Somente entrada. |

</details>

<a id="schema-authresponsedto"></a>
<details>
<summary><strong>AuthResponseDTO</strong></summary>

Dados retornados pela API. AuthResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `access_token` | string | Não | Token JWT de acesso. Envie em Authorization: Bearer <access_token>.  |
| `login` | string | Não | Identificação de acesso do usuário.  |
| `refresh_token` | string | Não | Token de renovação. Envie no campo refreshToken de POST /auth/refresh ou /auth/logout.  |
| `roles` | Lista de [Role](#schema-role) | Não | Papéis de acesso atribuídos ao usuário. Possibilidades: API, ADMIN, MANAGER, EMPLOYEE, PATIENT.  |

</details>

<a id="schema-authorizationforminstituitionresponsedto"></a>
<details>
<summary><strong>AuthorizationFormInstituitionResponseDTO</strong></summary>

Dados retornados pela API. InstituitionResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `cep` | string | Não | Código postal como texto; preserve zeros à esquerda.  |
| `cityName` | string | Não | Nome do município.  |
| `linkLogo` | string | Não | URL do logotipo da instituição.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `phone` | string | Não | Telefone da instituição.  |

</details>

<a id="schema-authorizationformresponsedto"></a>
<details>
<summary><strong>AuthorizationFormResponseDTO</strong></summary>

Dados retornados pela API. AuthorizationFormResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `authorizationIndentifyingResponseDTO` | [AuthorizationIndentifyingResponseDTO](#schema-authorizationindentifyingresponsedto) | Não | Identificação da autorização e destino.  |
| `description` | string | Não | Descrição textual do registro.  |
| `emitionDate` | string | Não | Data de emissão da autorização; grafia legada mantida. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `employeeName` | string | Não | Nome do funcionário responsável.  |
| `patientDataResponseDTO` | [PatientDataResponseDTO](#schema-patientdataresponsedto) | Não | Dados do paciente para a autorização.  |
| `proceduringDataResponseDTO` | [ProceduringDataResponseDTO](#schema-proceduringdataresponsedto) | Não | Dados do procedimento para a autorização.  |

</details>

<a id="schema-authorizationindentifyingresponsedto"></a>
<details>
<summary><strong>AuthorizationIndentifyingResponseDTO</strong></summary>

Dados retornados pela API. AuthorizationIndentifyingResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `appointmentScheduledDateTime` | string | Não | Data e hora agendadas para o atendimento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `destiny` | string | Não | Destino do encaminhamento.  |
| `instituitionResponseDTO` | [AuthorizationFormInstituitionResponseDTO](#schema-authorizationforminstituitionresponsedto) | Não | Instituição usada na autorização.  |
| `origin` | string | Não | Origem do cadastro ou encaminhamento.  |

</details>

<a id="schema-basicresponsesinstituitionresponsedto"></a>
<details>
<summary><strong>BasicResponsesInstituitionResponseDTO</strong></summary>

Dados retornados pela API. InstituitionResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `cep` | string | Não | Código postal como texto; preserve zeros à esquerda.  |
| `cityName` | string | Não | Nome do município.  |
| `cnes` | string | Não | Código CNES opcional da instituição; quando informado, deve conter sete dígitos. Exemplo: `0000123`. |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `linkLogo` | string | Não | URL do logotipo da instituição.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `phone` | string | Não | Telefone da instituição.  |

</details>

<a id="schema-categorygroupinforesponsedto"></a>
<details>
<summary><strong>CategoryGroupInfoResponseDTO</strong></summary>

Dados retornados pela API. CategoryGroupInfoResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-categorygrouprequestdto"></a>
<details>
<summary><strong>CategoryGroupRequestDTO</strong></summary>

Dados enviados pelo frontend. CategoryGroupRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |

</details>

<a id="schema-categorygroupresponsedto"></a>
<details>
<summary><strong>CategoryGroupResponseDTO</strong></summary>

Dados retornados pela API. CategoryGroupResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-categorygroupsimplifiedresponsedto"></a>
<details>
<summary><strong>CategoryGroupSimplifiedResponseDTO</strong></summary>

Dados retornados pela API. CategoryGroupSimplifiedResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-ciap2responsedto"></a>
<details>
<summary><strong>Ciap2ResponseDTO</strong></summary>

Dados retornados pela API. Ciap2ResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `code` | string | Não | Código oficial do item no catálogo, mantido como texto.  |
| `defaultFilterInPec` | boolean | Não | Indicador de filtro padrão na fonte PEC; pode ser null e não comprova elegibilidade na RNDS. Exemplo: `False`. |
| `description` | string | Não | Descrição textual do registro.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `pecId` | inteiro | Não | Identificador interno do registro no PEC; não é o código clínico.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |

</details>

<a id="schema-cid10responsedto"></a>
<details>
<summary><strong>Cid10ResponseDTO</strong></summary>

Dados retornados pela API. Cid10ResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `activeInPec` | boolean | Não | Indica se o código estava ativo na fonte PEC importada; a consulta também retorna inativos. Exemplo: `False`. |
| `code` | string | Não | Código oficial do item no catálogo, mantido como texto.  |
| `description` | string | Não | Descrição textual do registro.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `pecId` | inteiro | Não | Identificador interno do registro no PEC; não é o código clínico.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |

</details>

<a id="schema-cnesestabilishmentresponsedto"></a>
<details>
<summary><strong>CnesEstabilishmentResponseDTO</strong></summary>

Dados retornados pela API. CnesEstabilishmentResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `cep` | string | Não | Código postal como texto; preserve zeros à esquerda.  |
| `cnesCode` | string | Não | Código CNES do estabelecimento com sete dígitos; preserve zeros à esquerda. Exemplo: `0000123`. |
| `cnpj` | string | Não | CNPJ do estabelecimento como texto, preservando zeros à esquerda.  |
| `cnpjMaintainer` | string | Não | CNPJ da mantenedora do estabelecimento, como texto.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `legalName` | string | Não | Razão social do estabelecimento.  |
| `phone` | string | Não | Telefone da instituição.  |
| `tradeName` | string | Não | Nome fantasia do estabelecimento.  |
| `unitCode` | string | Não | Código da unidade na base CNES.  |

</details>

<a id="schema-cnesimportresponsedto"></a>
<details>
<summary><strong>CnesImportResponseDTO</strong></summary>

Dados retornados pela API. CnesImportResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `catalog` | string | Não | Catálogo substituído na importação. Valores: ESTABLISHMENTS, PROFESSIONALS. Exemplo: `ESTABLISHMENTS`. Opções: `ESTABLISHMENTS`, `PROFESSIONALS`. |
| `importedRecords` | inteiro | Não | Quantidade de registros importados, confirmada após o commit.  |

</details>

<a id="schema-cnesprofessionalresponsedto"></a>
<details>
<summary><strong>CnesProfessionalResponseDTO</strong></summary>

Dados retornados pela API. CnesProfessionalResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `bondDetails` | string | Não | Detalhamento do vínculo profissional.  |
| `cbo` | string | Não | Código da Classificação Brasileira de Ocupações, mantido como texto. Pode conter letras, por exemplo 2231F9. Um CNS pode ter vários vínculos.  |
| `cboDescription` | string | Não | Descrição da ocupação CBO.  |
| `cityName` | string | Não | Nome do município.  |
| `cnes` | string | Não | Código CNES do estabelecimento com sete dígitos, como texto. Exemplo: `0000123`. |
| `cnpj` | string | Não | CNPJ do estabelecimento como texto, preservando zeros à esquerda.  |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Na importação CNES, exige 15 dígitos. Exemplo: `000000000000001`. |
| `employerBond` | string | Não | Descrição do vínculo com o empregador.  |
| `estabilishmentBond` | string | Não | Descrição do vínculo com o estabelecimento.  |
| `estabilishmentName` | string | Não | Nome do estabelecimento do vínculo profissional; grafia legada mantida.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `legalNatureCode` | string | Não | Código da natureza jurídica do estabelecimento.  |
| `legalNatureDescription` | string | Não | Descrição da natureza jurídica.  |
| `management` | string | Não | Gestão informada na origem; M/E/F são convertidos para Municipal/Estadual/Federal. Outros valores são preservados; não é enum fechado.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `preceptor` | boolean | Não | Indica se o vínculo é de preceptor. Exemplo: `False`. |
| `resident` | boolean | Não | Indica se o vínculo é de residente. Exemplo: `False`. |
| `sus` | boolean | Não | Indica se o vínculo atende ao SUS. Exemplo: `False`. |

</details>

<a id="schema-conditionrequestdto"></a>
<details>
<summary><strong>ConditionRequestDTO</strong></summary>

Dados enviados pelo frontend. ConditionRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `specification` | string | Sim | Descrição da condição de saúde cadastrada. Mínimo de caracteres: `0`. Máximo de caracteres: `1000`. |

</details>

<a id="schema-conditionresponsedto"></a>
<details>
<summary><strong>ConditionResponseDTO</strong></summary>

Dados retornados pela API. ConditionResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `specification` | string | Não | Descrição da condição de saúde cadastrada.  |

</details>

<a id="schema-conditionsimplifiedresponsedto"></a>
<details>
<summary><strong>ConditionSimplifiedResponseDTO</strong></summary>

Dados retornados pela API. ConditionSimplifiedResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `specification` | string | Não | Descrição da condição de saúde cadastrada.  |

</details>

<a id="schema-employeeinforesponsedto"></a>
<details>
<summary><strong>EmployeeInfoResponseDTO</strong></summary>

Dados retornados pela API. EmployeeInfoResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-employeerequestdto"></a>
<details>
<summary><strong>EmployeeRequestDTO</strong></summary>

Dados enviados pelo frontend. EmployeeRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `active` | boolean | Não | Indica se o funcionário está ativo. Exemplo: `False`. |
| `address` | string | Não | Endereço completo informado no cadastro. Mínimo de caracteres: `0`. Máximo de caracteres: `255`. |
| `birthday` | string | Sim | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `contactPhone` | string | Não | Telefone de contato alternativo. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `cpf` | string | Sim | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. Mínimo de caracteres: `0`. Máximo de caracteres: `14`. |
| `cpfNormalized` | string | Não | CPF normalizado calculado pelo backend; não envie este campo. Somente leitura. |
| `email` | string | Sim | Endereço de e-mail de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `320`. Exemplo: `usuario@example.com`. |
| `gender` | string | Sim | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `50`. |
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `residentialPhone` | string | Não | Telefone residencial. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `roles` | Lista de string | Sim | Papéis de acesso atribuídos ao usuário. Possibilidades: API, ADMIN, MANAGER, EMPLOYEE, PATIENT. Opções: `API`, `ADMIN`, `MANAGER`, `EMPLOYEE`, `PATIENT`. |
| `sex` | string | Sim | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |

</details>

<a id="schema-employeeresponsedto"></a>
<details>
<summary><strong>EmployeeResponseDTO</strong></summary>

Dados retornados pela API. EmployeeResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `active` | boolean | Não | Indica se o funcionário está ativo. Exemplo: `False`. |
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato.  |
| `contactPhone` | string | Não | Telefone de contato alternativo.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `gender` | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `residentialPhone` | string | Não | Telefone residencial.  |
| `roles` | Lista de string | Não | Papéis de acesso atribuídos ao usuário. Possibilidades: API, ADMIN, MANAGER, EMPLOYEE, PATIENT. Opções: `API`, `ADMIN`, `MANAGER`, `EMPLOYEE`, `PATIENT`. |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |

</details>

<a id="schema-employeesimplifiedresponsedto"></a>
<details>
<summary><strong>EmployeeSimplifiedResponseDTO</strong></summary>

Dados retornados pela API. EmployeeSimplifiedResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-errorresponsedto"></a>
<details>
<summary><strong>ErrorResponseDTO</strong></summary>

Dados retornados pela API. ErrorResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `errors` | Lista de [FieldErrorResponseDTO](#schema-fielderrorresponsedto) | Não | Erros específicos por campo; lista vazia quando não houver detalhamento.  |
| `message` | string | Não | Mensagem legível que explica o resultado ou erro.  |
| `status` | inteiro | Não | Código HTTP do erro, como 400, 401, 403, 404, 409, 413, 422 ou 500.  |

</details>

<a id="schema-fielderrorresponsedto"></a>
<details>
<summary><strong>FieldErrorResponseDTO</strong></summary>

Dados retornados pela API. FieldErrorResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `fieldName` | string | Não | Nome do campo que falhou na validação.  |
| `message` | string | Não | Mensagem legível que explica o resultado ou erro.  |

</details>

<a id="schema-forgotpasswordrequestdto"></a>
<details>
<summary><strong>ForgotPasswordRequestDTO</strong></summary>

Dados enviados pelo frontend. ForgotPasswordRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `email` | string | Sim | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |

</details>

<a id="schema-instituitioninforesponsedto"></a>
<details>
<summary><strong>InstituitionInfoResponseDTO</strong></summary>

Dados retornados pela API. InstituitionInfoResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-instituitionrequestdto"></a>
<details>
<summary><strong>InstituitionRequestDTO</strong></summary>

Dados enviados pelo frontend. InstituitionRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Sim | Endereço completo informado no cadastro. Mínimo de caracteres: `0`. Máximo de caracteres: `255`. |
| `cep` | string | Sim | Código postal como texto; preserve zeros à esquerda. Mínimo de caracteres: `0`. Máximo de caracteres: `8`. |
| `cityName` | string | Não | Nome do município. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `cnes` | string | Não | Código CNES opcional da instituição; quando informado, deve conter sete dígitos. Exemplo: `0000123`. |
| `linkLogo` | string | Não | URL do logotipo da instituição. Mínimo de caracteres: `0`. Máximo de caracteres: `2048`. |
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `phone` | string | Não | Telefone da instituição. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |

</details>

<a id="schema-loginrequestdto"></a>
<details>
<summary><strong>LoginRequestDTO</strong></summary>

Dados enviados pelo frontend. LoginRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `password` | string | Sim | Senha de acesso; envie somente na requisição, nunca em logs. Mínimo de caracteres: `6`. Máximo de caracteres: `72`. Somente entrada. |

</details>

<a id="schema-newcommonstatusdto"></a>
<details>
<summary><strong>NewCommonStatusDTO</strong></summary>

Dados enviados pelo frontend. NewCommonStatusDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `appointmentsId` | Lista de inteiro | Sim | Lista dos IDs de atendimentos a atualizar em lote.  |
| `status` | string | Sim | Situação atual do atendimento. Pré agendado não é aceito nesta rota; use a rota de pré-agendamento. Valores: Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Mínimo de caracteres: `0`. Máximo de caracteres: `30`. Exemplo: `Completo`. Opções: `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |
| `statusChangeReason` | string | Não | Justificativa opcional para a mudança de status; até 1000 caracteres. Registrada no histórico quando houver mudança. Mínimo de caracteres: `0`. Máximo de caracteres: `1000`. |

</details>

<a id="schema-pageagentresponsedto"></a>
<details>
<summary><strong>PageAgentResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [AgentResponseDTO](#schema-agentresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageagentsimplifiedresponsedto"></a>
<details>
<summary><strong>PageAgentSimplifiedResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [AgentSimplifiedResponseDTO](#schema-agentsimplifiedresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageagreementresponsedto"></a>
<details>
<summary><strong>PageAgreementResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [AgreementResponseDTO](#schema-agreementresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageappointmentresponsedto"></a>
<details>
<summary><strong>PageAppointmentResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [AppointmentResponseDTO](#schema-appointmentresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageappointmentstatushistoryresponsedto"></a>
<details>
<summary><strong>PageAppointmentStatusHistoryResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [AppointmentStatusHistoryResponseDTO](#schema-appointmentstatushistoryresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagebasicresponsesinstituitionresponsedto"></a>
<details>
<summary><strong>PageBasicResponsesInstituitionResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [BasicResponsesInstituitionResponseDTO](#schema-basicresponsesinstituitionresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagecategorygroupresponsedto"></a>
<details>
<summary><strong>PageCategoryGroupResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [CategoryGroupResponseDTO](#schema-categorygroupresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagecategorygroupsimplifiedresponsedto"></a>
<details>
<summary><strong>PageCategoryGroupSimplifiedResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [CategoryGroupSimplifiedResponseDTO](#schema-categorygroupsimplifiedresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageciap2responsedto"></a>
<details>
<summary><strong>PageCiap2ResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [Ciap2ResponseDTO](#schema-ciap2responsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagecid10responsedto"></a>
<details>
<summary><strong>PageCid10ResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [Cid10ResponseDTO](#schema-cid10responsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagecnesestabilishmentresponsedto"></a>
<details>
<summary><strong>PageCnesEstabilishmentResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [CnesEstabilishmentResponseDTO](#schema-cnesestabilishmentresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagecnesprofessionalresponsedto"></a>
<details>
<summary><strong>PageCnesProfessionalResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [CnesProfessionalResponseDTO](#schema-cnesprofessionalresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageconditionresponsedto"></a>
<details>
<summary><strong>PageConditionResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [ConditionResponseDTO](#schema-conditionresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageconditionsimplifiedresponsedto"></a>
<details>
<summary><strong>PageConditionSimplifiedResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [ConditionSimplifiedResponseDTO](#schema-conditionsimplifiedresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageemployeeresponsedto"></a>
<details>
<summary><strong>PageEmployeeResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [EmployeeResponseDTO](#schema-employeeresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageemployeesimplifiedresponsedto"></a>
<details>
<summary><strong>PageEmployeeSimplifiedResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [EmployeeSimplifiedResponseDTO](#schema-employeesimplifiedresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagepatientappointmentresponsedto"></a>
<details>
<summary><strong>PagePatientAppointmentResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [PatientAppointmentResponseDTO](#schema-patientappointmentresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagepatientreportreponsedto"></a>
<details>
<summary><strong>PagePatientReportReponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [PatientReportReponseDTO](#schema-patientreportreponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagepatientresponsedto"></a>
<details>
<summary><strong>PagePatientResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [PatientResponseDTO](#schema-patientresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagepatientsimplifiedresponsedto"></a>
<details>
<summary><strong>PagePatientSimplifiedResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [PatientSimplifiedResponseDTO](#schema-patientsimplifiedresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageprofessionalresponsedto"></a>
<details>
<summary><strong>PageProfessionalResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [ProfessionalResponseDTO](#schema-professionalresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageprofessionalsimplifiedresponsedto"></a>
<details>
<summary><strong>PageProfessionalSimplifiedResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [ProfessionalSimplifiedResponseDTO](#schema-professionalsimplifiedresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageservicetyperesponsedto"></a>
<details>
<summary><strong>PageServiceTypeResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [ServiceTypeResponseDTO](#schema-servicetyperesponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageservicetypesimplifiedresponsedto"></a>
<details>
<summary><strong>PageServiceTypeSimplifiedResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [ServiceTypeSimplifiedResponseDTO](#schema-servicetypesimplifiedresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagesigtapprocedureresponsedto"></a>
<details>
<summary><strong>PageSigtapProcedureResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [SigtapProcedureResponseDTO](#schema-sigtapprocedureresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagesurgeryresponsedto"></a>
<details>
<summary><strong>PageSurgeryResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [SurgeryResponseDTO](#schema-surgeryresponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pagesurgerytyperesponsedto"></a>
<details>
<summary><strong>PageSurgeryTypeResponseDTO</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `content` | Lista de [SurgeryTypeResponseDTO](#schema-surgerytyperesponsedto) | Não | Registros da página atual; pode ser uma lista vazia.  |
| `empty` | boolean | Não | Indica se não há elementos.  |
| `first` | boolean | Não | Indica se esta é a primeira página.  |
| `last` | boolean | Não | Indica se esta é a última página.  |
| `number` | inteiro | Não | Número da página atual, começando em 0.  |
| `numberOfElements` | inteiro | Não | Quantidade de registros nesta página.  |
| `pageable` | [PageableObject](#schema-pageableobject) | Não | Metadados da paginação aplicada.  |
| `size` | inteiro | Não | Tamanho de página solicitado.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `totalElements` | inteiro | Não | Quantidade total de registros que correspondem aos filtros.  |
| `totalPages` | inteiro | Não | Quantidade total de páginas.  |

</details>

<a id="schema-pageableobject"></a>
<details>
<summary><strong>PageableObject</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `offset` | inteiro | Não | Quantidade de registros anteriores à página atual.  |
| `pageNumber` | inteiro | Não | Número da página, começando em 0.  |
| `pageSize` | inteiro | Não | Quantidade de registros solicitada por página.  |
| `paged` | boolean | Não | Indica que a consulta usa paginação.  |
| `sort` | [SortObject](#schema-sortobject) | Não | Metadados da ordenação aplicada.  |
| `unpaged` | boolean | Não | Indica que a consulta não usa paginação.  |

</details>

<a id="schema-patientappointmentresponsedto"></a>
<details>
<summary><strong>PatientAppointmentResponseDTO</strong></summary>

Dados retornados pela API. PatientAppointmentResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `agreements` | string | Não | Nome ou rótulo do convênio retornado pelo atendimento.  |
| `cep` | string | Não | Código postal como texto; preserve zeros à esquerda.  |
| `cityName` | string | Não | Nome do município.  |
| `linkLogo` | string | Não | URL do logotipo da instituição.  |
| `name` | string | Não | Nome da instituição associada ao atendimento.  |
| `notes` | string | Não | Observações do atendimento; até 1000 caracteres.  |
| `phone` | string | Não | Telefone da instituição.  |
| `priorit` | string | Não | Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Prioritário`, `Urgente`. |
| `requestingProfessionalName` | string | Não | Nome do profissional solicitante.  |
| `responsibleProfessionalName` | string | Não | Nome do profissional responsável.  |
| `return` | boolean | Não | Indica se o atendimento é um retorno. No DTO de classe, a chave JSON é return; false quando omitida.  |
| `scheduledAt` | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `schedulingForecast` | string | Não | Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `serviceTypeName` | string | Não | Nome do serviço/procedimento cadastrado.  |
| `status` | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |
| `value` | decimal | Não | Valor monetário do serviço, como número decimal. No cadastro, deve ser maior que zero e ter até duas casas decimais. Exemplo: `100.0`. |

</details>

<a id="schema-patientcpfrequestdto"></a>
<details>
<summary><strong>PatientCPFRequestDTO</strong></summary>

Dados enviados pelo frontend. PatientCPFRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `cpf` | string | Sim | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. Mínimo de caracteres: `0`. Máximo de caracteres: `14`. |
| `cpfNormalized` | string | Não | CPF normalizado calculado pelo backend; não envie este campo. Somente leitura. |
| `patientId` | inteiro | Sim | ID do paciente no Vitalya. Consulte GET /patients.  |

</details>

<a id="schema-patientdataresponsedto"></a>
<details>
<summary><strong>PatientDataResponseDTO</strong></summary>

Dados retornados pela API. PatientDataResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `Sex` | string | Não | Sexo informado no cadastro do paciente; a chave JSON tem inicial maiúscula.  |
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `age` | inteiro | Não | Idade do paciente em anos.  |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `esusCard` | string | Não | Identificação do cartão e-SUS usada na autorização.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-patientinfoagentresponsedto"></a>
<details>
<summary><strong>PatientInfoAgentResponseDTO</strong></summary>

Dados retornados pela API. PatientInfoAgentResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-patientinfoappointmentresponsedto"></a>
<details>
<summary><strong>PatientInfoAppointmentResponseDTO</strong></summary>

Dados retornados pela API. PatientInfoAppointmentResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `createdAt` | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `employeeName` | string | Não | Nome do funcionário responsável.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `notes` | string | Não | Observações do atendimento; até 1000 caracteres.  |
| `patientName` | string | Não | Nome do paciente.  |
| `priorit` | string | Não | Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Prioritário`, `Urgente`. |
| `requestingProfessionalName` | string | Não | Nome do profissional solicitante.  |
| `responsibleProfessionalName` | string | Não | Nome do profissional responsável.  |
| `scheduledAt` | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `serviceTypeName` | string | Não | Nome do serviço/procedimento cadastrado.  |
| `status` | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |

</details>

<a id="schema-patientinforesponsedto"></a>
<details>
<summary><strong>PatientInfoResponseDTO</strong></summary>

Dados retornados pela API. PatientInfoResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `agent` | [PatientInfoAgentResponseDTO](#schema-patientinfoagentresponsedto) | Não | Dados resumidos do agente de saúde vinculado.  |
| `appointments` | Lista de [PatientInfoAppointmentResponseDTO](#schema-patientinfoappointmentresponsedto) | Não | Atendimentos vinculados ao paciente.  |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato.  |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Exemplo: `000000000000001`. |
| `conditions` | Lista de [ConditionResponseDTO](#schema-conditionresponsedto) | Não | Condições associadas ao paciente.  |
| `contactPhone` | string | Não | Telefone de contato alternativo.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `gender` | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `microArea` | string | Não | Identificação da microárea de acompanhamento.  |
| `motherName` | string | Não | Nome da mãe do paciente.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `origin` | string | Não | Origem do cadastro ou encaminhamento.  |
| `residentialPhone` | string | Não | Telefone residencial.  |
| `responsible` | [PatientInfoResponsibleResponseDTO](#schema-patientinforesponsibleresponsedto) | Não | Dados resumidos do responsável pelo paciente.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |
| `teamINE` | string | Não | Identificador Nacional de Equipe (INE), mantido como texto.  |
| `teamName` | string | Não | Nome da equipe de saúde.  |

</details>

<a id="schema-patientinforesponsibleresponsedto"></a>
<details>
<summary><strong>PatientInfoResponsibleResponseDTO</strong></summary>

Dados retornados pela API. PatientInfoResponsibleResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-patientreportreponsedto"></a>
<details>
<summary><strong>PatientReportReponseDTO</strong></summary>

Dados retornados pela API. PatientReportReponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `createdAt` | string | Não | Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `motherName` | string | Não | Nome da mãe do paciente.  |
| `patientName` | string | Não | Nome do paciente.  |
| `priorit` | string | Não | Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente. Exemplo: `Eletivo`. Opções: `Eletivo`, `Prioritário`, `Urgente`. |
| `requestingProfessionalName` | string | Não | Nome do profissional solicitante.  |
| `responsibleProfessionalName` | string | Não | Nome do profissional responsável.  |
| `scheduledAt` | string | Não | Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `serviceName` | string | Não | Nome do serviço/procedimento.  |
| `serviceType` | string | Não | Classificação do serviço: Exame ou Especialidade. Valores: Exame, Especialidade. Exemplo: `Exame`. Opções: `Exame`, `Especialidade`. |
| `status` | string | Não | Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado. Exemplo: `Pré agendado`. Opções: `Pré agendado`, `Completo`, `Cancelado`, `Falta justificada`, `Falta não justificada`, `Paciente faltou`, `Agendado`, `Sem agendamento`, `Atrasado`. |

</details>

<a id="schema-patientrequestdto"></a>
<details>
<summary><strong>PatientRequestDTO</strong></summary>

Dados enviados pelo frontend. PatientRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro. Mínimo de caracteres: `0`. Máximo de caracteres: `255`. |
| `agentId` | inteiro | Não | ID do agente de saúde cadastrado no Vitalya. Consulte GET /agents.  |
| `birthday` | string | Sim | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. Exemplo: `000000000000001`. |
| `conditionsId` | Lista de inteiro | Não | IDs das condições cadastradas no Vitalya. Consulte GET /conditions.  |
| `contactPhone` | string | Não | Telefone de contato alternativo. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. Mínimo de caracteres: `0`. Máximo de caracteres: `14`. |
| `cpfNormalized` | string | Não | CPF normalizado calculado pelo backend; não envie este campo. Somente leitura. |
| `email` | string | Não | Endereço de e-mail de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `320`. Exemplo: `usuario@example.com`. |
| `gender` | string | Sim | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `50`. |
| `microArea` | string | Não | Identificação da microárea de acompanhamento. Mínimo de caracteres: `0`. Máximo de caracteres: `50`. |
| `motherName` | string | Não | Nome da mãe do paciente. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `residentialPhone` | string | Não | Telefone residencial. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `responsibleId` | inteiro | Não | ID do paciente responsável vinculado ao cadastro. Consulte GET /patients.  |
| `sex` | string | Sim | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `teamINE` | string | Não | Identificador Nacional de Equipe (INE), mantido como texto. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `teamName` | string | Não | Nome da equipe de saúde. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |

</details>

<a id="schema-patientresponsedto"></a>
<details>
<summary><strong>PatientResponseDTO</strong></summary>

Dados retornados pela API. PatientResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `agentId` | inteiro | Não | ID do agente de saúde cadastrado no Vitalya. Consulte GET /agents.  |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato.  |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Exemplo: `000000000000001`. |
| `conditionsId` | Lista de inteiro | Não | IDs das condições cadastradas no Vitalya. Consulte GET /conditions.  |
| `contactPhone` | string | Não | Telefone de contato alternativo.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `gender` | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `microArea` | string | Não | Identificação da microárea de acompanhamento.  |
| `motherName` | string | Não | Nome da mãe do paciente.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `origin` | string | Não | Origem do cadastro ou encaminhamento.  |
| `residentialPhone` | string | Não | Telefone residencial.  |
| `responsibleId` | inteiro | Não | ID do paciente responsável vinculado ao cadastro. Consulte GET /patients.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |
| `teamINE` | string | Não | Identificador Nacional de Equipe (INE), mantido como texto.  |
| `teamName` | string | Não | Nome da equipe de saúde.  |

</details>

<a id="schema-patientsimplifiedinfodto"></a>
<details>
<summary><strong>PatientSimplifiedInfoDTO</strong></summary>

Dados retornados pela API. PatientSimplifiedInfoDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `motherName` | string | Não | Nome da mãe do paciente.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-patientsimplifiedresponsedto"></a>
<details>
<summary><strong>PatientSimplifiedResponseDTO</strong></summary>

Dados retornados pela API. PatientSimplifiedResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-personbackupinfodto"></a>
<details>
<summary><strong>PersonBackupInfoDTO</strong></summary>

Dados enviados pelo frontend. PersonBackupInfoDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `birthDate` | string | Não | Data de nascimento recebida do PEC. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellphone` | string | Não | Telefone celular recebido do PEC.  |
| `citizenSeqId` | string | Não | Identificador sequencial do cidadão no PEC, como texto.  |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Exemplo: `000000000000001`. |
| `contactPhone` | string | Não | Telefone de contato alternativo.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `gender` | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.  |
| `lastUpdate` | string | Não | Data e hora da última atualização na origem PEC. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `microArea` | string | Não | Identificação da microárea de acompanhamento.  |
| `motherName` | string | Não | Nome da mãe do paciente.  |
| `patientName` | string | Não | Nome do paciente.  |
| `residentialPhone` | string | Não | Telefone residencial.  |
| `sexualOrientation` | string | Não | Campo recebido do PEC que a implementação atual copia para sex; verifique a semântica na origem.  |
| `uniqueCitizenId` | string | Não | Identificador único do cidadão na origem PEC.  |

</details>

<a id="schema-preschedulingrequestdto"></a>
<details>
<summary><strong>PreSchedulingRequestDTO</strong></summary>

Dados enviados pelo frontend. PreSchedulingRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `appointmentsId` | Lista de inteiro | Sim | Lista dos IDs de atendimentos a atualizar em lote.  |
| `statusChangeReason` | string | Não | Justificativa opcional para a mudança de status; até 1000 caracteres. Registrada no histórico quando houver mudança. Mínimo de caracteres: `0`. Máximo de caracteres: `1000`. |

</details>

<a id="schema-proceduringdataresponsedto"></a>
<details>
<summary><strong>ProceduringDataResponseDTO</strong></summary>

Dados retornados pela API. ProceduringDataResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `categoryName` | string | Não | Nome da categoria do serviço.  |
| `serviceType` | string | Não | Classificação do serviço: Exame ou Especialidade. Valores: Exame, Especialidade. Exemplo: `Exame`. Opções: `Exame`, `Especialidade`. |
| `serviceTypeCode` | string | Não | Código do procedimento usado na autorização.  |
| `serviceTypeName` | string | Não | Nome do serviço/procedimento cadastrado.  |

</details>

<a id="schema-professionalinforesponsedto"></a>
<details>
<summary><strong>ProfessionalInfoResponseDTO</strong></summary>

Dados retornados pela API. ProfessionalInfoResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-professionalrequestdto"></a>
<details>
<summary><strong>ProfessionalRequestDTO</strong></summary>

Dados enviados pelo frontend. ProfessionalRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro. Mínimo de caracteres: `0`. Máximo de caracteres: `255`. |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cbo` | string | Sim | Código da Classificação Brasileira de Ocupações, mantido como texto. Mínimo de caracteres: `0`. Máximo de caracteres: `10`. |
| `cellPhone` | string | Não | Telefone celular de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. Exemplo: `000000000000001`. |
| `contactPhone` | string | Não | Telefone de contato alternativo. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend. Mínimo de caracteres: `0`. Máximo de caracteres: `14`. |
| `cpfNormalized` | string | Não | CPF normalizado calculado pelo backend; não envie este campo. Somente leitura. |
| `description` | string | Não | Descrição textual do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `255`. |
| `email` | string | Não | Endereço de e-mail de contato. Mínimo de caracteres: `0`. Máximo de caracteres: `320`. Exemplo: `usuario@example.com`. |
| `gender` | string | Sim | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `50`. |
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `residentialPhone` | string | Não | Telefone residencial. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `sex` | string | Sim | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. |
| `vinculation` | string | Não | Descrição do vínculo do profissional. Mínimo de caracteres: `0`. Máximo de caracteres: `50`. |

</details>

<a id="schema-professionalresponsedto"></a>
<details>
<summary><strong>ProfessionalResponseDTO</strong></summary>

Dados retornados pela API. ProfessionalResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cbo` | string | Não | Código da Classificação Brasileira de Ocupações, mantido como texto.  |
| `cellPhone` | string | Não | Telefone celular de contato.  |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Exemplo: `000000000000001`. |
| `contactPhone` | string | Não | Telefone de contato alternativo.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `description` | string | Não | Descrição textual do registro.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `gender` | string | Não | Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `residentialPhone` | string | Não | Telefone residencial.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |
| `vinculation` | string | Não | Descrição do vínculo do profissional.  |

</details>

<a id="schema-professionalsimplifiedresponsedto"></a>
<details>
<summary><strong>ProfessionalSimplifiedResponseDTO</strong></summary>

Dados retornados pela API. ProfessionalSimplifiedResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-profileresponsedto"></a>
<details>
<summary><strong>ProfileResponseDTO</strong></summary>

Dados retornados pela API. ProfileResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `address` | string | Não | Endereço completo informado no cadastro.  |
| `birthday` | string | Não | Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `cellPhone` | string | Não | Telefone celular de contato.  |
| `cns` | string | Não | Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Exemplo: `000000000000001`. |
| `contactPhone` | string | Não | Telefone de contato alternativo.  |
| `cpf` | string | Não | CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.  |
| `email` | string | Não | Endereço de e-mail de contato. Exemplo: `usuario@example.com`. |
| `microArea` | string | Não | Identificação da microárea de acompanhamento.  |
| `motherName` | string | Não | Nome da mãe do paciente.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `origin` | string | Não | Origem do cadastro ou encaminhamento.  |
| `residentialPhone` | string | Não | Telefone residencial.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |
| `teamINE` | string | Não | Identificador Nacional de Equipe (INE), mantido como texto.  |
| `teamName` | string | Não | Nome da equipe de saúde.  |

</details>

<a id="schema-reportappointmentgraphresponsedto"></a>
<details>
<summary><strong>ReportAppointmentGraphResponseDTO</strong></summary>

Dados retornados pela API. ReportAppointmentGraphResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `total` | inteiro | Não | Quantidade total de registros no agrupamento.  |
| `totalCanceled` | inteiro | Não | Quantidade de atendimentos cancelados.  |
| `totalCompleted` | inteiro | Não | Quantidade de atendimentos completos.  |
| `totalExcusedAbsense` | inteiro | Não | Quantidade de atendimentos com falta justificada.  |
| `totalNoShow` | inteiro | Não | Quantidade de atendimentos com paciente faltante.  |
| `totalOverdue` | inteiro | Não | Quantidade de atendimentos atrasados.  |
| `totalPendingScheduling` | inteiro | Não | Quantidade de atendimentos sem agendamento.  |
| `totalPreScheduled` | inteiro | Não | Quantidade de atendimentos pré-agendados.  |
| `totalScheduled` | inteiro | Não | Quantidade de atendimentos agendados.  |
| `totalUnjustifiededAbsense` | inteiro | Não | Quantidade de atendimentos com falta não justificada.  |

</details>

<a id="schema-resetpasswordrequestdto"></a>
<details>
<summary><strong>ResetPasswordRequestDTO</strong></summary>

Dados enviados pelo frontend. ResetPasswordRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `newPassword` | string | Sim | Nova senha para concluir a redefinição. Somente entrada. |
| `token` | string | Sim | Token recebido no fluxo de redefinição de senha.  |

</details>

<a id="schema-role"></a>
<details>
<summary><strong>Role</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | ID interno do papel de acesso.  |
| `role` | string | Não | Papel de acesso: API (integração), ADMIN (administrador), MANAGER (gestor), EMPLOYEE (funcionário), PATIENT (paciente). Opções: `API`, `ADMIN`, `MANAGER`, `EMPLOYEE`, `PATIENT`. |

</details>

<a id="schema-servicetypeinforesponsedto"></a>
<details>
<summary><strong>ServiceTypeInfoResponseDTO</strong></summary>

Dados retornados pela API. ServiceTypeInfoResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `type` | string | Não | Tipo/classificação do registro. Valores: Exame, Especialidade. Exemplo: `Exame`. Opções: `Exame`, `Especialidade`. |

</details>

<a id="schema-servicetyperequestdto"></a>
<details>
<summary><strong>ServiceTypeRequestDTO</strong></summary>

Dados enviados pelo frontend. ServiceTypeRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `categoryGroupId` | inteiro | Não | ID do grupo de categorias. Consulte GET /category-groups.  |
| `name` | string | Sim | Nome de exibição do registro. Mínimo de caracteres: `0`. Máximo de caracteres: `150`. |
| `sigtapCode` | string | Não | Código SIGTAP do procedimento, como texto. Consulte /datasus/sigtap/procedures; não envie o ID interno do catálogo. Mínimo de caracteres: `0`. Máximo de caracteres: `20`. Exemplo: `0301010072`. |
| `type` | string | Sim | Tipo/classificação do registro. Valores: Exame, Especialidade. Mínimo de caracteres: `0`. Máximo de caracteres: `50`. Exemplo: `Exame`. Opções: `Exame`, `Especialidade`. |
| `value` | decimal | Sim | Valor monetário do serviço, como número decimal. No cadastro, deve ser maior que zero e ter até duas casas decimais. Mínimo: `0.0`. Exemplo: `100.0`. |

</details>

<a id="schema-servicetyperesponsedto"></a>
<details>
<summary><strong>ServiceTypeResponseDTO</strong></summary>

Dados retornados pela API. ServiceTypeResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `categoryGroupId` | inteiro | Não | ID do grupo de categorias. Consulte GET /category-groups.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `sigtapCode` | string | Não | Código SIGTAP do procedimento, como texto. Consulte /datasus/sigtap/procedures; não envie o ID interno do catálogo. Exemplo: `0301010072`. |
| `type` | string | Não | Tipo/classificação do registro. Valores: Exame, Especialidade. Exemplo: `Exame`. Opções: `Exame`, `Especialidade`. |
| `value` | decimal | Não | Valor monetário do serviço, como número decimal. No cadastro, deve ser maior que zero e ter até duas casas decimais. Exemplo: `100.0`. |

</details>

<a id="schema-servicetypesimplifiedresponsedto"></a>
<details>
<summary><strong>ServiceTypeSimplifiedResponseDTO</strong></summary>

Dados retornados pela API. ServiceTypeSimplifiedResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `name` | string | Não | Nome de exibição do registro.  |

</details>

<a id="schema-sigtapimportresponsedto"></a>
<details>
<summary><strong>SigtapImportResponseDTO</strong></summary>

Dados retornados pela API. SigtapImportResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `competence` | string | Não | Competência da base, no formato AAAAMM. Formato: `^[0-9]{6}$`. Exemplo: `202607`. |
| `fileName` | string | Não | Nome do arquivo de origem importado.  |
| `groups` | inteiro | Não | Quantidade de grupos importados.  |
| `imported` | boolean | Não | Indica que a importação SIGTAP foi concluída. Exemplo: `False`. |
| `organizations` | inteiro | Não | Quantidade de formas de organização importadas.  |
| `procedures` | inteiro | Não | Quantidade de procedimentos importados.  |
| `subGroups` | inteiro | Não | Quantidade de subgrupos importados.  |

</details>

<a id="schema-sigtapprocedureresponsedto"></a>
<details>
<summary><strong>SigtapProcedureResponseDTO</strong></summary>

Dados retornados pela API. SigtapProcedureResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `code` | string | Não | Código oficial do item no catálogo, mantido como texto.  |
| `competence` | string | Não | Competência da base, no formato AAAAMM. Formato: `^[0-9]{6}$`. Exemplo: `202607`. |
| `complexity` | string | Não | Código de complexidade conforme a fonte SIGTAP.  |
| `groupCode` | string | Não | Código do grupo SIGTAP.  |
| `groupName` | string | Não | Nome do grupo SIGTAP.  |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `maximumAge` | inteiro | Não | Idade máxima aplicável conforme a fonte SIGTAP.  |
| `minimumAge` | inteiro | Não | Idade mínima aplicável conforme a fonte SIGTAP.  |
| `name` | string | Não | Nome de exibição do registro.  |
| `organizationCode` | string | Não | Código da forma de organização SIGTAP.  |
| `organizationName` | string | Não | Nome da forma de organização SIGTAP.  |
| `sex` | string | Não | Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.  |
| `subGroupCode` | string | Não | Código do subgrupo SIGTAP.  |
| `subGroupName` | string | Não | Nome do subgrupo SIGTAP.  |

</details>

<a id="schema-sortobject"></a>
<details>
<summary><strong>SortObject</strong></summary>



| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `empty` | boolean | Não | Indica se não há elementos.  |
| `sorted` | boolean | Não | Indica se há ordenação aplicada.  |
| `unsorted` | boolean | Não | Indica se não há ordenação aplicada.  |

</details>

<a id="schema-surgeryrequestdto"></a>
<details>
<summary><strong>SurgeryRequestDTO</strong></summary>

Dados enviados pelo frontend. SurgeryRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `anesthesicRisk` | string | Não | Informação textual sobre risco anestésico.  |
| `conclusion` | string | Não | Conclusão registrada para a cirurgia.  |
| `dateTime` | string | Não | Data e hora do registro cirúrgico. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `location` | string | Não | Local associado ao registro cirúrgico.  |
| `observation` | string | Não | Observações do registro cirúrgico.  |
| `personName` | string | Não | Nome da pessoa vinculada ao registro cirúrgico.  |
| `procedureDate` | string | Não | Data de realização do procedimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `sesap` | string | Não | Referência textual à SESAP, conforme cadastro.  |
| `surgeryRisk` | string | Não | Informação textual sobre o risco cirúrgico.  |
| `surgeryTypeId` | inteiro | Não | ID do tipo de cirurgia cadastrado. Consulte GET /surgery-type.  |
| `susEasy` | string | Não | Referência textual ao SUS Fácil, conforme cadastro.  |

</details>

<a id="schema-surgeryresponsedto"></a>
<details>
<summary><strong>SurgeryResponseDTO</strong></summary>

Dados retornados pela API. SurgeryResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `anesthesicRisk` | string | Não | Informação textual sobre risco anestésico.  |
| `conclusion` | string | Não | Conclusão registrada para a cirurgia.  |
| `dateTime` | string | Não | Data e hora do registro cirúrgico. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00. Exemplo: `2026-11-08T09:30:00`. |
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `location` | string | Não | Local associado ao registro cirúrgico.  |
| `observation` | string | Não | Observações do registro cirúrgico.  |
| `personName` | string | Não | Nome da pessoa vinculada ao registro cirúrgico.  |
| `procedureDate` | string | Não | Data de realização do procedimento. Formato AAAA-MM-DD, por exemplo 2026-11-08. Exemplo: `1990-05-20`. |
| `sesap` | string | Não | Referência textual à SESAP, conforme cadastro.  |
| `surgeryRisk` | string | Não | Informação textual sobre o risco cirúrgico.  |
| `surgeryTypeId` | inteiro | Não | ID do tipo de cirurgia cadastrado. Consulte GET /surgery-type.  |
| `susEasy` | string | Não | Referência textual ao SUS Fácil, conforme cadastro.  |

</details>

<a id="schema-surgerytyperequestdto"></a>
<details>
<summary><strong>SurgeryTypeRequestDTO</strong></summary>

Dados enviados pelo frontend. SurgeryTypeRequestDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `type` | string | Sim | Descrição livre do tipo de cirurgia; não é enum fechado.  |

</details>

<a id="schema-surgerytyperesponsedto"></a>
<details>
<summary><strong>SurgeryTypeResponseDTO</strong></summary>

Dados retornados pela API. SurgeryTypeResponseDTO.

| Campo JSON | Tipo | Obrigatório | Descrição e regras |
|---|---|---|---|
| `id` | inteiro | Não | Identificador interno do registro no Vitalya; não é o código do catálogo externo.  |
| `type` | string | Não | Descrição livre do tipo de cirurgia; não é enum fechado.  |

</details>

## Manutenção

Atualize as interfaces de documentação e os DTOs ao mudar o contrato. OpenApiConfiguration complementa metadados de paginação, papéis e aliases BindParam. OpenApiDocumentationTest valida /v3/api-docs e grava target/openapi-frontend.json para inspeção. Revise este guia quando a API mudar.
