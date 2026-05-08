# 🤖 WhatsApp AI Chatbot Framework

Framework de chatbot com IA para WhatsApp, construído com Java/Spring Boot. Altamente configurável via System Prompt — funciona como qualquer personagem ou assistente sem mudar o código.

> **Origem do projeto:** nasceu como um bot do meme "Tung Tung Sahur" para um grupo de amigos, e foi abstraído para um framework genérico e reutilizável.

---

## 🧱 Arquitetura

```
WhatsApp ──► Evolution API ──► Cloudflare Tunnel ──► Spring Boot
                                                          │
                                                    ┌─────┴─────┐
                                                  Groq AI    PostgreSQL
                                                  (Llama 3)      │
                                                              Redis
                                                          (contexto)
```

**Fluxo de uma mensagem:**
1. Mensagem chega no grupo do WhatsApp
2. Evolution API dispara um webhook pro Spring Boot via Cloudflare Tunnel
3. Spring Boot decide se deve responder (trigger word, menção ou aleatoriedade)
4. Contexto das últimas mensagens é recuperado do Redis
5. Mensagem + contexto são enviados para a Groq API (Llama 3)
6. Resposta gerada é enviada de volta via Evolution API para o grupo

---

## 🛠️ Stack

| Componente | Tecnologia | Custo |
|---|---|---|
| Backend | Spring Boot 4.0.6 + Java 21 | Gratuito |
| WhatsApp Gateway | Evolution API v2.2.3 | Gratuito (open source) |
| IA | Groq Cloud + Llama 3 | Gratuito |
| Banco de dados | PostgreSQL 16 | Gratuito |
| Cache / Contexto | Redis 7 | Gratuito |
| Tunnel | Cloudflare Tunnel | Gratuito |
| Containers | Docker + Docker Compose | Gratuito |
| Migrations | Flyway | Gratuito |
| Documentação | Springdoc / Swagger | Gratuito |

**Custo total de infraestrutura: R$ 0,00**

---

## 📦 Pré-requisitos

- Docker Desktop instalado e rodando
- Java 21
- Maven
- Conta no [Groq Cloud](https://console.groq.com) (gratuito)
- Chip/número de WhatsApp secundário (recomendado)
- [cloudflared](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/downloads) instalado

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

Editar o `.env`:

```env
GROQ_API_KEY=sua_chave_aqui
EVOLUTION_API_KEY=sua_chave_aqui
EVOLUTION_BASE_URL=http://localhost:8080
EVOLUTION_INSTANCE=chatbot
```

### 3. Subir a infraestrutura

```bash
docker-compose up -d
```

Isso vai subir:
- PostgreSQL na porta `5432`
- Redis na porta `6379`
- Evolution API na porta `8080`

### 4. Expor o localhost via Cloudflare Tunnel

```bash
cloudflared tunnel --url http://localhost:8081
```

Anota a URL gerada (ex: `https://xxxx.trycloudflare.com`).

### 5. Criar instância do WhatsApp na Evolution API

```http
POST http://localhost:8080/instance/create
apikey: sua-chave

{
  "instanceName": "chatbot",
  "integration": "WHATSAPP-BAILEYS",
  "qrcode": true
}
```

### 6. Conectar o WhatsApp

Via pairing code (recomendado):

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

## 📁 Estrutura do projeto

```
src/
└── main/
    └── java/com/hectorstimer/chatbot/
        ├── controller/
        │   └── WebhookController.java      # recebe mensagens da Evolution API
        ├── service/
        │   ├── MessageService.java         # decide se/como responder
        │   ├── AiService.java              # integração com Groq
        │   └── WhatsAppService.java        # envia mensagens via Evolution API
        ├── client/
        │   ├── GroqClient.java             # Feign client para Groq
        │   └── EvolutionClient.java        # Feign client para Evolution API
        ├── repository/
        │   └── MessageLogRepository.java   # JPA repository
        ├── model/
        │   └── MessageLog.java             # entidade JPA
        └── config/
            ├── RedisConfig.java            # configuração do Redis
            └── FeignConfig.java            # configuração do OpenFeign
```

---

## ⚙️ Configurando o personagem

Todo o comportamento do bot é definido pelo System Prompt em `AiService.java`. Para criar uma nova "instância" com personalidade diferente, basta clonar o repositório e alterar o prompt:

```java
private static final String SYSTEM_PROMPT = """
    Você é [NOME DO PERSONAGEM].
    Sua personalidade: ...
    Regras: ...
    """;
```

---

## ✅ O que já foi feito

- [x] Definição da arquitetura completa
- [x] `pom.xml` com todas as dependências (Spring Boot 4.0.6)
- [x] `docker-compose.yml` com Evolution API + PostgreSQL + Redis
- [x] Evolution API rodando e acessível
- [x] Instância do WhatsApp criada na Evolution API
- [x] Cloudflare Tunnel configurado

---

## 🔜 O que falta fazer

- [ ] Conectar o WhatsApp (aguardando chip secundário)
- [ ] Configurar webhook na Evolution API apontando para o Cloudflare Tunnel
- [ ] Criar estrutura de pacotes no Spring Boot
- [ ] Implementar `WebhookController` — receber e mapear payload da Evolution API
- [ ] Implementar `MessageService` — lógica de trigger words e chance aleatória
- [ ] Implementar `AiService` — integração com Groq API via OpenFeign
- [ ] Implementar `WhatsAppService` — enviar resposta via Evolution API
- [ ] Configurar Redis para salvar contexto das últimas mensagens por grupo
- [ ] Criar entidade `MessageLog` e migration Flyway
- [ ] Testes com JUnit e Testcontainers
- [ ] Configurar GitHub Actions CI/CD

---

## 🔀 Estratégia de repositórios

Este repositório é o **framework genérico** (portfólio público).

A instância com personalidade do Tung Tung Sahur fica em repositório privado separado, que clona este e apenas altera o System Prompt e configurações.

---

## 👤 Autor

**Hector Stimer** — [github.com/HectorStimer](https://github.com/HectorStimer)
