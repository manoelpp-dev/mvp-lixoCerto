# LixoCerto: MVP do back-end

API em **Java 21 + Spring Boot** que ajuda a descartar resíduos corretamente em Canoas/RS:

- **Por foto:** a IA (Gemini) identifica o resíduo entre os lixos cadastrados e devolve a instrução de descarte.
- **Por texto:** o próprio sistema compara a descrição digitada com as palavras-chave dos lixos cadastrados (sem IA).
- **Ecopontos:** cadastro com a descrição de cada ponto, que falta no site da Prefeitura.

O site oficial da Prefeitura (rotas, escalas, coleta seletiva) fica como link no front-end.

## Requisitos

- Java 21
- Docker Desktop (no Windows, com WSL 2). **Não precisa instalar o Postgres**: ele roda em container.
- Uma chave do Gemini (https://aistudio.google.com/apikey), só para a identificação por foto

## Como rodar

**1. Banco de dados** (na pasta onde está o `docker-compose.yml`; com o Docker Desktop aberto)

```powershell
docker compose up -d
docker ps
```

Deve aparecer o container `lixocerto-db` com status `Up`.

**2. Chave do Gemini** (nunca coloque a chave no código nem no git)

- IntelliJ: *Run > Edit Configurations > Environment variables* e adicione `GEMINI_API_KEY=sua-chave`.
- PowerShell: `$env:GEMINI_API_KEY="sua-chave"`
- Linux/Mac: `export GEMINI_API_KEY=sua-chave`

Sem a chave a API sobe normalmente e a busca por texto funciona; só a rota de foto falha (502, "Falha na IA").

**3. API**

```powershell
.\mvnw spring-boot:run
```

Sobe em http://localhost:8080. Na primeira execução, a **carga inicial** cria 4 categorias, 10 instruções, 10 lixos e os 5 ecopontos de Canoas do `ecopontos.csv`.

Para desligar: `docker compose down` (os dados ficam salvos). Para **zerar o banco**: `docker compose down -v`.

## Endpoints

| Método | Rota | O que faz |
|---|---|---|
| GET | `/lixos/busca?q=garrafa pet` | Busca por texto (até 5 resultados com pontuação; lista vazia = nada encontrado) |
| POST | `/lixos/identificar-foto` | `multipart/form-data` com o campo `foto` (até 5 MB) |
| GET | `/lixos?categoriaId=1&umido=false` | Lista lixos; os filtros são opcionais (selects de apoio) |
| GET | `/categorias` | Categorias: Orgânico, Reciclável, Perigoso e Rejeito (opções do select) |
| GET | `/pontos-descarte?bairro=Niterói` | Ecopontos ativos; o filtro de bairro é opcional. `&incluirInativos=true` mostra também os desativados |
| GET/POST/PUT/DELETE | `/lixos`, `/categorias`, `/instrucoes`, `/pontos-descarte` | CRUD completo |

Exemplos prontos em [`docs/requests.http`](docs/requests.http) (IntelliJ) e roteiro em [`docs/DEMONSTRACAO.md`](docs/DEMONSTRACAO.md).

Erros seguem o padrão do Spring (`status`, `error`, `message`): 400 dado inválido, 404 não encontrado (inclui resíduo não identificado na foto), 409 conflito, 502 falha na IA (inclui chave não configurada).

## Modelo de dados

![Diagrama](docs/diagrama.png)

- `categoria_lixo.tipo_de_lixo` é o **grupo** (Orgânico, Reciclável, Perigoso, Rejeito) e `descricao` explica o grupo. Cada `lixo` (Papel e papelão, Plástico, Vidro...) pertence a uma categoria.
- `ponto_descarte.status` é `ATIVO` ou `INATIVO`; pontos inativos ficam fora da listagem padrão.
- Cada `lixo` tem **uma** instrução de descarte (relação 1 para 1, com FK única).
- `palavras_chave` alimenta a busca por texto; `umido` alimenta o select seco/úmido. Esses dois campos foram acrescentados ao catálogo original do grupo.
- A lista de palavras-chave está em [`docs/palavras-chave.md`](docs/palavras-chave.md).

## Ecopontos

Os ecopontos ficam em `src/main/resources/ecopontos.csv` (colunas `nome;descricao;cidade;rua;num;cep;bairro;latitude;longitude;status`). Ele é carregado na inicialização **se a tabela estiver vazia**. Para acrescentar pontos, edite o CSV e faça `docker compose down -v` + suba de novo, ou use `POST /api/pontos-descarte`.

Atenção: **latitude e longitude de Canoas são negativas** (por exemplo `-29.9178` e `-51.1839`). Sem o sinal, o ponto aparece no mapa do outro lado do mundo.

## Alterando as models

As tabelas são geradas pelo JPA (`spring.jpa.hibernate.ddl-auto=update`). Ele **adiciona** tabelas e colunas, mas **não remove nem renomeia** nada, e falha ao adicionar coluna `NOT NULL` em tabela que já tem dados. Se mudarem uma model e o banco ficar inconsistente: `docker compose down -v` e subam de novo (a carga inicial recria tudo). Avisem o grupo quando isso for necessário, porque cada um tem o seu banco local.


## Estrutura

```
controllers/   rotas HTTP (finas: recebem, chamam o service, devolvem)
services/      lógica (busca por texto, identificação por foto, CRUDs e validações)
repositories/  acesso ao banco (Spring Data JPA)
model/         entidades (tabelas)
dto/           formatos de entrada e saída da API
util/          BuscaTexto: normalização e pontuação da busca por texto
config/        CORS e carga inicial (DataSeeder)
```

## Pontos de atenção

- O CRUD não tem autenticação: qualquer pessoa com acesso à URL pode criar, alterar e apagar. Para publicar, deixem apenas os GET abertos.
- A foto e a descrição digitada **não são guardadas**; existem só durante a requisição.
- A carga inicial vem dos antigos `insert_*.sql` do grupo, agora em Java (`DataSeeder`). Não usem os SQLs com `INSERT` de id fixo: eles dependem da ordem das colunas e não avançam a sequência do id, o que quebra o `POST` depois.
- O CORS libera `localhost:3000` e `localhost:5173`. Para outras origens, defina `CORS_ORIGINS` (separadas por vírgula).
