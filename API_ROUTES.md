# Documentação das Rotas da API

Esta documentação lista todos os endpoints expostos pelo backend, organizados por controladores. Ela detalha os formatos esperados no corpo da requisição (JSON) e os retornos das rotas, incluindo restrições e tipos de dados.

---

## 1. Autenticação (AuthController)
**Base Path:** `/api/auth`

### `POST /login`
Autentica um usuário e retorna o token JWT.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### `POST /register`
Registra um novo usuário.

**Request Body (`RegisterDTO`):**
```json
{
  "name": "João Silva", // min 3, max 100 caracteres, não vazio
  "email": "joao@example.com", // formato de e-mail válido, não vazio
  "password": "senhaSegura123" // min 6 caracteres, não vazio
}
```

**Response (`200 OK`)**

---

## 2. Propriedades (PropertyController)
**Base Path:** `/api/property`

### `GET /`
Lista propriedades com paginação e filtros opcionais.

**Query Params (Opcionais):** `name` (String), `type` (Enum: RESIDENCIAL, COMERCIAL, etc), `minPrice` (Double), `maxPrice` (Double), `minBedrooms` (Integer), `page` (Integer, default 0), `size` (Integer, default 10), `sort` (String, default "id").

**Response (`200 OK`):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Apartamento no Centro",
      "description": "Lindo apartamento com 3 quartos.",
      "type": "RESIDENCIAL",
      "value": 350000.00,
      "area": 85,
      "bedrooms": 3,
      "address": "Rua das Flores, 123",
      "city": "São Paulo",
      "state": "SP",
      "active": true,
      "brokerId": 2,
      "brokerName": "Corretor Silva",
      "imageUrls": "https://img1.com,https://img2.com"
    }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### `GET /{id}`
Busca os detalhes de uma propriedade específica.

**Path Variable:** `id` (Long)

**Response (`200 OK`):** *Mesmo objeto da lista acima*

### `GET /getUserProperties`
Lista todas as propriedades cadastradas pelo corretor logado. Requer Autenticação JWT.

**Response (`200 OK`):** *Lista do mesmo objeto de propriedade*

### `POST /`
Cria uma nova propriedade. Requer Autenticação JWT.

**Request Body (`PropertyCreateDTO`):**
```json
{
  "name": "Casa de Praia", // min 10, max 100 caracteres, não nulo/vazio
  "description": "Excelente vista para o mar.", // não vazio
  "type": "RESIDENCIAL", // não nulo (Enum)
  "value": 500000.00, // não nulo, positivo
  "area": 120, // não nulo, positivo
  "bedrooms": 4, // não nulo, positivo
  "address": "Av. Beira Mar, 100", // não vazio
  "city": "Rio de Janeiro", // não vazio
  "state": "RJ", // não vazio
  "imageUrls": "https://img.com/1.jpg" // não vazio
}
```

**Response (`201 Created`):** *Objeto da propriedade com ID gerado, igual ao GET*

### `PUT /{id}`
Atualiza uma propriedade existente. Requer Autenticação JWT.

**Path Variable:** `id` (Long)

**Request Body (`PropertyUpdateDTO`):** (Todos os campos são opcionais)
```json
{
  "name": "Casa de Praia Atualizada", // min 10, max 100 caracteres
  "description": "Nova descrição do imóvel.",
  "type": "RESIDENCIAL",
  "value": 480000.00, // positivo
  "area": 125, // positivo
  "bedrooms": 5, // positivo
  "address": "Av. Beira Mar, 102",
  "city": "Rio de Janeiro",
  "state": "RJ",
  "brokerId": 2
}
```

**Response (`201 Created`):** *Objeto da propriedade atualizado*

### `DELETE /{id}`
Deleta uma propriedade. Requer Autenticação JWT e privilégios ou o usuário ser dono da propriedade.

**Path Variable:** `id` (Long)
**Response:** `204 No Content`

### `PATCH /status/{id}`
Altera o status (ativo/inativo) de uma propriedade. Requer Autenticação JWT.

**Path Variable:** `id` (Long)
**Response (`200 OK`):** *Objeto da propriedade atualizado com a flag `active` alternada*

---

## 3. Usuário (UserController)
**Base Path:** `/api/user`

### `GET /`
Retorna os dados do usuário autenticado (getMe). Requer Autenticação JWT.

**Response (`200 OK`):**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "role": "USER"
}
```

### `PUT /update`
Atualiza os dados do usuário logado. Requer Autenticação JWT.

**Request Body (`UserUpdateDTO`):** (Campos opcionais)
```json
{
  "name": "João Santos Silva", // min 3, max 100 caracteres
  "password": "novaSenhaSec" // min 6 caracteres
}
```

**Response (`201 Created`):** *Objeto do usuário atualizado*

### `POST /create`
Cria um novo usuário com função específica. Requer Autenticação JWT e Permissão de `ADMIN` (`hasRole('ADMIN')`).

**Request Body (`UserCreateDTO`):**
```json
{
  "name": "Admin Sistema", // min 3, max 100 caracteres, não vazio
  "email": "admin@example.com", // formato de e-mail válido, não vazio
  "password": "senhaForteAdmin", // min 6 caracteres, não vazio
  "role": "ADMIN" // não nulo (Enum)
}
```

**Response (`201 Created`):** *Objeto do usuário recém-criado*

### `GET /favorites`
Lista as propriedades favoritadas pelo usuário logado. Requer Autenticação JWT.

**Response (`200 OK`):** *Lista de propriedades igual ao /api/property*

### `POST /favorites/{propertyId}`
Adiciona uma propriedade aos favoritos do usuário logado. Requer Autenticação JWT.

**Path Variable:** `propertyId` (Long)
**Response:** `204 No Content`

### `DELETE /favorites/{propertyId}`
Remove uma propriedade dos favoritos do usuário. Requer Autenticação JWT.

**Path Variable:** `propertyId` (Long)
**Response:** `204 No Content`

---

## 4. Health Check (Healthz)
**Base Path:** `/healthz`

### `GET /`
Endpoint de verificação de saúde da aplicação. Não requer autenticação.

**Response (`200 OK`):**
```json
{
  "status": "UP"
}
```
