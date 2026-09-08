# Checkpoint 4 — Bug Hunt StreamFIAP

## Sobre o projeto

O **StreamFIAP** é uma API REST de aluguel de conteúdo audiovisual (filmes, séries e
documentários), construída com Spring Boot e Spring Data JPA. O sistema permite
cadastrar conteúdos, consultá-los por categoria e realizar aluguéis, aplicando regras
de negócio como classificação indicativa, débito de créditos e preços promocionais.

## O problema

O código foi entregue com uma série de defeitos introduzidos propositalmente: bugs de
lógica que não quebram a compilação (e por isso passam despercebidos), violações de
encapsulamento, tratamento de exceções que engole erros, e trechos que ferem boas
práticas de Clean Code.

O desafio é **caçar, diagnosticar e corrigir** esses problemas — documentando não só o
que foi mudado, mas a causa raiz de cada um e o conceito da disciplina envolvido.

## Como estamos resolvendo

- Leitura dirigida das camadas `model`, `controller` e `exception`, procurando
  divergências entre o comportamento esperado e o implementado.
- Um commit por correção, com mensagem no padrão `fix: bugNN — descrição`, para que o
  histórico do Git conte a investigação passo a passo.
- Registro de cada achado na tabela abaixo, ligando o sintoma observado à linha
  responsável e ao conteúdo de aula correspondente.

---

## Identificação

**Grupo:** ___

| Integrante | RM | Turma |
|---|---|---|
| Gustavo Braga | 562247 | 2CCPO |
| Lucas Mendes | 563667 | 2CCPO |
| Kaio Correa | 563443 | 2CCPO |


## Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | Usuário cadastrado retorna com `nome` nulo na resposta da API | `Usuario.java`, construtor (~linha 21): `nome = nome;` atribui o parâmetro a ele mesmo | Trocado para `this.nome = nome;` | Escopo de variáveis e uso de `this` |
| bug02 | Aluguel é aprovado mesmo com saldo menor que o preço; usuário fica com créditos negativos | `Usuario.java`, `temCreditosSuficientes` (~linha 26): comparação invertida (`preco >= creditos`) | Invertido para `preco <= this.creditos` | Regras de negócio no model |
| bug03 | Campo `duracaoMinutos` pode ser alterado direto de fora da classe, sem passar pelo setter | `Conteudo.java` (~linha 16): atributo declarado `public` | Alterado para `private`, acesso via getter/setter já existentes | Encapsulamento |
| bug04 | | | | |
| bug05 | | | | |
| bug06 | | | | |
| bug07 | | | | |
| bug08 | | | | |
| bug09 | | | | |
| bug10 | | | | |
| bug11 | | | | |
| bug12 | | | | |

## Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que mudamos |
|---|---|---|---|
| clean01 | `Usuario.alugar()` — bloco do recibo | Uso de `System.out.println` para log de aplicação: sem nível de severidade, sem timestamp, sem controle por ambiente | Substituído por SLF4J (`Logger` + `logger.info` com placeholders `{}`) |
| clean02 | | | |
| clean03 | | | |
| clean04 | | | |
| clean05 | | | |
| clean06 | | | |

---

## Perguntas de reflexão

### 1. Injeção de dependência (Aula 13)

_(a preencher)_

### 2. JDBC vs Spring Data JPA (Aulas 12 e 13)

_(a preencher)_

### 3. Exceções checked vs unchecked (Aula 11)

_(a preencher)_

### 4. Sobrescrita vs sobrecarga (Aula 7)

_(a preencher)_

### 5. Onde blindar o objeto? (Aulas 3, 4 e 13)

_(a preencher)_

### 6. Abstração e interface (Aulas 8 e 9)

_(a preencher)_

---

## Como executar

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Principais endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/conteudos` | Lista todos os conteúdos |
| GET | `/api/conteudos/{id}` | Busca conteúdo por ID |
| GET | `/api/conteudos/categoria/{categoria}` | Filtra por categoria |
| GET | `/api/conteudos/{id}/preco-promocional` | Preço com promoção aplicada |
| POST | `/api/conteudos/filme` | Cadastra um filme |
| POST | `/api/conteudos/serie` | Cadastra uma série |
| POST | `/api/conteudos/documentario` | Cadastra um documentário |
| POST | `/api/alugueis?usuarioId=&conteudoId=` | Realiza um aluguel |

## Stack

Java · Spring Boot · Spring Data JPA · Maven
