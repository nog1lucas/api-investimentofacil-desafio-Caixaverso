# API SmartInvest - [Desafio CaixaVerso]

💰 **Simulador de Investimentos** desenvolvido em **Java 21 + Quarkus**

## ⚡ Execução Rápida

### 🔹 Opção 1 — Docker Compose (Recomendado)
Pré-requisitos: Docker instalado
```bash
docker-compose up -d --build 
```
---
### 🔹 Opção 2 — Build e Execução Manual (Modo Desenvolvimento)
Pré-requisitos:
- Java 21+ instalado
- Maven Wrapper (./mvnw) ou Maven 3.9+ instalado
  Maven Wrapper (./mvnw) ou Maven 3.9+ instalado
```bash
# Build
./mvnw clean package

# Execução em modo desenvolvimento
./mvnw quarkus:dev
```
---
## Links de acesso

- **API** → http://localhost:8080
- **Swagger UI** → http://localhost:8080/q/swagger-ui
---

## 🔑 Autenticação JWT

Este projeto utiliza **JWT (JSON Web Token)** para proteger os endpoints da API.

### ⚙️ Política de Segurança

- Apenas endpoints com o prefixo `/api/*` exigem autenticação via token JWT.

### 🔐 Obtenção de Token JWT
> ⚠️ O token gerado **não possui expiração**, facilitando a testabilidade pelo avalidador.


Para testar os endpoints protegidos, utilize o seguinte endpoint:

```
GET http://localhost:8008/jwt/
```

O retorno já inclui o prefixo `Bearer`, pronto para uso no header de autenticação:

```
Authorization: Bearer <seu_token_aqui>
```

---

---

## Diferenciais Implementados

### 🔒 Rate Limiting

- Definido após testes de carga com JMeter para obter a taxa ideal de requests por segundo, protegendo a aplicação sem limitar demais o uso.
- **Limites**: 200 req/s, 12.000 req/min, 17.280.000 req/hora.
- Bloqueio temporário inteligente para abusos.

### 📁 Arquivo .env

- O projeto utiliza arquivo `.env` para configuração de variáveis de ambiente
---

- ### Testes unitários
---
### 🧠 Cache
- Cache de produtos
---
## 🔄 Processamento assíncrono
- Persistência das métricas no SqlServer local em segundo plano.
---

### 📊 Endpoints Extras

- Parâmetro opcional na busca paginada para valores referentes ao sistema SAC ou PRICE.
- Parâmetro opcional de data no endpoint de telemetria.
---

## ⚙️ Funcionalidades Obrigatórias (Core)

### 🗄️ Banco de Dados

- Pool otimizado (min: 2, max: 20 conexões).
- **Database**:
    - SQL Server (produção).

### ✅ Validação personalizada

- Bean Validation com mensagens customizadas.
- DTOs tipados → validação + serialização automática.
- Exception Handling centralizado com respostas detalhadas.

### 🩺 Observabilidade e Resiliência

- Health Checks em todos os controllers.
- Transações com rollback automático.
- OpenTelemetry → rastreamento distribuído.


### 🛠️ Desenvolvimento Amigável

- Scripts SQL de dados de teste → permite desenvolver offline.
- Uso do arquivo .env para variáveis de ambiente (mais seguro e fácil troca de variável)
- Properties por ambiente (prod e dev).
- Swagger/OpenAPI completo com exemplos práticos.