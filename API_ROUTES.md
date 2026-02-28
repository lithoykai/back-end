# Documentação das Rotas da API

Esta documentação lista todos os endpoints expostos pelo backend, organizados por controladores.

## 1. Autenticação (AuthController)
**Base Path:** `/api/auth`

| Método | Rota | Descrição | Corpo da Requisição (Body) | Retorno |
|---|---|---|---|---|
| `POST` | `/login` | Autentica um usuário e retorna o token JWT. | `LoginDTO` (email, password) | `TokenDTO` (token) |
| `POST` | `/register` | Registra um novo usuário. | `RegisterDTO` | `200 OK` |

---

## 2. Propriedades (PropertyController)
**Base Path:** `/api/property`

| Método | Rota | Descrição | Parâmetros / Corpo | Retorno |
|---|---|---|---|---|
| `GET` | `/` | Lista propriedades com paginação e filtros. | **Query Params:** `name`, `type`, `minPrice`, `maxPrice`, `minBedrooms`, `page`, `size`, `sort` | `Page<PropertyDTO>` |
| `GET` | `/{id}` | Busca os detalhes de uma propriedade específica. | **Path Variable:** `id` | `PropertyDTO` |
| `GET` | `/getUserProperties` | Lista todas as propriedades cadastradas pelo corretor logado. | - | `List<PropertyDTO>` |
| `POST` | `/` | Cria uma nova propriedade. | **Body:** `PropertyCreateDTO` | `PropertyDTO` (201 Created) |
| `PUT` | `/{id}` | Atualiza uma propriedade existente. | **Path Variable:** `id`<br>**Body:** `PropertyUpdateDTO` | `PropertyDTO` (201 Created) |
| `DELETE` | `/{id}` | Deleta uma propriedade. | **Path Variable:** `id` | `204 No Content` |
| `PATCH` | `/status/{id}` | Altera o status (ativo/inativo) de uma propriedade. | **Path Variable:** `id` | `PropertyDTO` |

---

## 3. Usuário (UserController)
**Base Path:** `/api/user`

| Método | Rota | Descrição | Parâmetros / Corpo | Retorno |
|---|---|---|---|---|
| `GET` | `/` | Retorna os dados do usuário autenticado (getMe). | - | `UserDTO` |
| `PUT` | `/update` | Atualiza os dados do usuário. | **Body:** `UserUpdateDTO` | `UserDTO` (201 Created) |
| `POST` | `/create` | Cria um novo usuário (Requer role: `ADMIN`). | **Body:** `UserCreateDTO` | `UserDTO` (201 Created) |
| `GET` | `/favorites` | Lista as propriedades favoritadas pelo usuário logado. | - | `List<PropertyDTO>` |
| `POST` | `/favorites/{propertyId}` | Adiciona uma propriedade aos favoritos do usuário. | **Path Variable:** `propertyId` | `204 No Content` |
| `DELETE` | `/favorites/{propertyId}`| Remove uma propriedade dos favoritos do usuário. | **Path Variable:** `propertyId` | `204 No Content` |

---

## 4. Health Check (Healthz)
**Base Path:** `/healthz`

| Método | Rota | Descrição | Corpo da Requisição | Retorno |
|---|---|---|---|---|
| `GET` | `/` | Endpoint de verificação de saúde da aplicação. | - | `{"status": "UP"}` |
