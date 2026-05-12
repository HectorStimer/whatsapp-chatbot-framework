# 🤖 WhatsApp AI Chatbot Framework

Framework de chatbot com IA para WhatsApp, construído com Java e Spring Boot. Totalmente configurável via System Prompt — funciona como qualquer personagem ou assistente sem alterar o código.

---

## 🧱 Arquitetura

```
WhatsApp ──► Evolution API ──► Cloudflare Tunnel ──► Spring Boot (8081)
                                                           │
                                              ┌────────────┼────────────┐
                                           Groq AI      Redis       PostgreSQL
                                          (Llama 3)   (contexto)     (logs)
```

**Fluxo de uma mensagem:**
1. Mensagem chega no grupo do WhatsApp
2. Evolution API dispara um webhook via Cloudflare Tunnel para o Spring Boot
3. `MessageService` decide se deve responder (trigger word ou chance aleatória)
4. `AiService` recupera o contexto do Redis e chama a Groq API
5. Resposta gerada é salva no PostgreSQL e enviada de volta via Evolution API

---

## 🛠️ Stack

| Componente | Tecnologia | Custo |
|---|---|---|
| Backend | Spring Boot 4.0.6 + Java 21 | Gratuito |
| WhatsApp Gateway | Evolution API v2.2.3 | Gratuito |
| IA | Groq Cloud + Llama 3 | Gratuito |
| Cache / Contexto | Redis 7 | Gratuito |
| Banco de dados | PostgreSQL 16 | Gratuito |
| Tunnel | Cloudflare Tunnel | Gratuito |
| Containers | Docker + Docker Compose | Gratuito |
| Migrations | Flyway | Gratuito |
| Documentação | Springdoc / Swagger | Gratuito |
| HTTP Clients | OpenFeign | Gratuito |
| Testes | JUnit 5 + Testcontainers | Gratuito |

**Custo total: R$ 0,00**

---

## 📁 Estrutura do projeto

```
src/
├── main/
│   ├── java/com/hectorstimer/chatbotwhatsapp/
│   │   ├── ChatbotWhatsappApplication.java
│   │   ├── controller/
│   │   │   └── WebhookController.java
│   │   ├── service/
│   │   │   ├── MessageService.java
│   │   │   ├── AiService.java
│   │   │   └── WhatsAppService.java
│   │   ├── client/
│   │   │   ├── GroqClient.java
│   │   │   └── EvolutionClient.java
│   │   ├── dto/
│   │   │   ├── WebhookPayloadDTO.java
│   │   │   ├── GroqRequestDTO.java
│   │   │   └── SendMessageDTO.java
│   │   ├── model/
│   │   │   └── MessageLog.java
│   │   ├── repository/
│   │   │   └── MessageLogRepository.java
│   │   └── config/
│   │       └── FeignConfig.java
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           └── V1__create_message_log.sql
└── test/
    └── java/com/hectorstimer/chatbotwhatsapp/
        ├── controller/
        │   └── WebhookControllerTest.java
        └── repository/
            └── MessageLogRepositoryTest.java
```

---

## 📦 Pré-requisitos

- Java 21
- Maven
- Docker Desktop
- [cloudflared](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/downloads)
- Conta no [Groq Cloud](https://console.groq.com) (gratuito)
- Chip/número de WhatsApp secundário

---

## 🚀 Como rodar

### 1. Clonar o repositório

```bash
git clone https://github.com/HectorStimer/whatsapp-ai-chatbot.git
cd whatsapp-ai-chatbot
```

### 2. Configurar variáveis de ambiente

```bash
cp .env.example .env
```

Preencha o `.env`:

```env
GROQ_API_KEY=sua_chave_groq
EVOLUTION_API_KEY=sua_chave_evolution
EVOLUTION_BASE_URL=http://localhost:8080
EVOLUTION_INSTANCE=chatbot
```

### 3. Subir a infraestrutura

```bash
docker-compose up -d
```

Aguarda subir e verifica em `http://localhost:8080`.

### 4. Expor o localhost

```bash
cloudflared tunnel --url http://localhost:8081
```

Anota a URL gerada (ex: `https://xxxx.trycloudflare.com`).

### 5. Criar instância do WhatsApp

```http
POST http://localhost:8080/instance/create
apikey: sua-chave

{
  "instanceName": "chatbot",
  "integration": "WHATSAPP-BAILEYS",
  "qrcode": true
}
```

### 6. Conectar o WhatsApp via pairing code

```http
POST http://localhost:8080/instance/pairing-code/chatbot
apikey: sua-chave

{
  "phoneNumber": "5511999999999"
}
```

### 7. Configurar o webhook

```http
POST http://localhost:8080/webhook/set/chatbot
apikey: sua-chave

{
  "url": "https://sua-url.trycloudflare.com/webhook",
  "events": ["MESSAGES_UPSERT"]
}
```

### 8. Rodar o Spring Boot

```bash
mvn spring-boot:run
```

Swagger disponível em: `http://localhost:8081/swagger-ui.html`

---

## ⚙️ Configurando o personagem

Todo o comportamento do bot é definido pelo System Prompt em `AiService.java`:

```java
private static final String SYSTEM_PROMPT = """
    Você é [NOME DO PERSONAGEM].
    Sua personalidade: ...
    """;
```

Trigger words e chance de resposta aleatória são configuráveis no `application.yml`:

```yaml
bot:
  trigger-words: tung, cajado, sahur
  random-response-chance: 15
```

---

## 🧪 Rodando os testes

```bash
mvn test
```

Os testes usam Testcontainers — o Docker precisa estar rodando.

---

## 🔀 Estratégia de repositórios

Este repositório é o **framework genérico** (portfólio público). Para criar uma instância com personalidade específica, clone este repositório e altere apenas o System Prompt e as configurações.

---

## 👤 Autor

**Hector Stimer** — [github.com/HectorStimer](https://github.com/HectorStimer)
