# Checkpoint 4 — Bug Hunt StreamFIAP

## Identificação

**Grupo:** GUKAS

| Integrante | RM | Turma |
|---|---|---|
| Gustavo Braga | 562247 | 2CCPO |
| Lucas Mendes | 563667 | 2CCPO |
| Kaio Correa | 563443 | 2CCPO |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | 12 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |

---

## Parte 1 — Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | Documentário cobrava R$ 9,90 em vez de ser gratuito | `Documentario.java`: não sobrescrevia `calcularPrecoAluguel()` | Adicionado `@Override` retornando `0.0` | Herança e polimorfismo |
| bug02 | Série sempre alugava por R$ 9,90 em vez do preço por temporada | `Serie.java`: método de preço tinha assinatura diferente da classe mãe (sobrecarga, não sobrescrita) | Corrigida a assinatura e adicionado `@Override` | Sobrescrita vs sobrecarga |
| bug03 | Série cadastrada com dados nulos; projeto não compilava | `Serie.java`: construtor não chamava `super(...)` corretamente | Construtor ajustado para repassar todos os parâmetros ao `super` | Herança e construtores |
| bug04 | Filme com promoção ficava mais caro em vez de mais barato | `Filme.aplicarPromocao`: multiplicava por `1.2` em vez de `0.8` | Corrigido o multiplicador | Interfaces e regras de negócio |
| bug05 | Aluguel aprovado mesmo sem saldo suficiente | `Usuario.temCreditosSuficientes`: lógica invertida | Comparação corrigida | Regras de negócio no model |
| bug06 | Nome do usuário salvo como nulo | `Usuario.java`, construtor: `nome = nome;` | Trocado para `this.nome = nome;` | Uso de `this` |
| bug07 | `id` do usuário não era gerado | `Usuario.java`: faltava `@GeneratedValue` | Anotação adicionada | Mapeamento JPA |
| bug08 | Buscar conteúdo inexistente retornava sucesso vazio | `ConteudoController`: catch vazio engolia o erro | Substituído por exceção tratada (404) | Tratamento de exceções |
| bug09 | Filtro por categoria não funcionava | `listarPorCategoria`: comparava Strings com `==` | Trocado para `.equals()` | Comparação de objetos |
| bug10 | Erro de classificação indicativa estourava 500 genérico | Exceção sem handler no `GlobalExceptionHandler` | Handler adicionado (403) | Exceções checked vs unchecked |
| bug11 | Conteúdo com duração inválida era aceito | Faltava validação de `duracaoMinutos` | Validação adicionada no construtor | Validação de regras de negócio |
| bug12 | Conteúdo indisponível era alugado normalmente | `Usuario.alugar` não checava disponibilidade | Checagem adicionada | Regras de negócio no model |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | `Conteudo.java` | Campo `duracaoMinutos` era `public` | Alterado para `private`, com getter/setter |
| clean02 | `Usuario.debitarCreditos` | Comentário não batia com o código | Comentário corrigido |
| clean03 | `ConteudoController.java` | Código morto (método e comentários não usados) | Removido |
| clean04 | `Serie.java` | Faltava `@Override` no método de preço | Anotação adicionada |
| clean05 | `Usuario.alugar` | Variável sem significado (`p`) | Renomeada para `preco` |
| clean06 | `Usuario.alugar` | `System.out.println` misturando negócio com apresentação | Bloco removido |

---

## Parte 3 — Perguntas de reflexão

### 1. Injeção de dependência (Aula 13)
Os controllers recebem os repositories via `@Autowired` (ex.: `ConteudoController`
usa `ConteudoRepository`). Explique por que o Spring precisa gerenciar esses objetos
em vez de criarmos com `new ConteudoRepository()`. O que exatamente o Spring faz ao
injetar um bean, e por que isso não funcionaria com um `new` comum?

_Reduz acoplamento: a classe não decide como suas dependências são criadas, apenas as recebe (via construtor, idealmente). Isso facilita trocar implementações e testar com mocks, sem alterar o código da classe que depende._

### 2. JDBC vs Spring Data JPA (Aulas 12 e 13)
Na Aula 12 escrevemos um `ProdutoDAO` na mão com `Connection`, `PreparedStatement` e
`ResultSet`. Aqui o `ConteudoRepository` tem 2 linhas e faz CRUD completo. Compare as
duas abordagens: o que o Spring Data JPA automatiza, o que o JDBC/DAO ainda resolve
melhor, e como o `findByCategoria` consegue funcionar sem implementação.

_JDBC é baixo nível: você escreve SQL, abre/fecha conexão e mapeia `ResultSet` manualmente. JPA abstrai isso com ORM (mapeia objeto ↔ tabela) e gera as queries por você. Ganha-se produtividade; perde-se controle fino sobre o SQL exato executado._

### 3. Exceções checked vs unchecked (Aula 11)
A `ClassificacaoIndicativaException` estourava como um erro genérico do servidor,
sem mensagem útil para o cliente. Explique a diferença entre `extends Exception` e
`extends RuntimeException` no contexto desse bug, e como você fez a mensagem da
regra (classificação indicativa) chegar de forma clara ao cliente da API.

_Checked (`IOException`, `SQLException`) representam falhas previsíveis e recuperáveis — o compilador exige tratamento. Unchecked (`RuntimeException` e subclasses) indicam erros de programação (bug), não são forçadas porque, em teoria, não deveriam acontecer se o código estiver correto._

### 4. Sobrescrita vs sobrecarga (Aula 7)
Um dos bugs compilava sem nenhum erro: o método da `Serie` parecia sobrescrever
`calcularPrecoAluguel`, mas na verdade sobrecarregava. Explique a diferença entre
override e overload nesse caso e por que a anotação `@Override` teria impedido o bug.

_Sobrecarga (overload): mesmo nome, assinaturas diferentes, resolvida em tempo de compilação. Sobrescrita (override): mesma assinatura em subclasse, resolvida em tempo de execução (polimorfismo). Só a sobrescrita está ligada a polimorfismo._

### 5. Onde blindar o objeto? (Aulas 3, 4 e 13)
Vimos bugs de dados inválidos aceitos (duração negativa, créditos negativos, campos
nulos). Em quais lugares (construtor, setter, método do model) cada tipo de validação
deve ficar? Justifique usando os bugs que você encontrou e explique por que validar só
em um lugar não foi suficiente.

_Não basta `private` + getter/setter. A validação de regra de negócio (ex: saldo não pode ser negativo) deve estar dentro dos métodos que alteram o estado (setter ou método específico como `sacar()`), garantindo que o objeto nunca fique em estado inválido, independente de quem o chama._

### 6. Abstração e interface (Aulas 8 e 9)
`Conteudo` é abstrata e `Promocionavel` é uma interface. Explique a diferença de
propósito entre as duas nesse projeto e o que mudaria no código se o Documentário
passasse a ter promoções — quais classes/linhas seriam tocadas e quais ficariam
intactas? O que isso diz sobre o design do sistema?

_Programar contra a interface (`List` em vez de `ArrayList`) desacopla o código da implementação concreta, permitindo trocar a implementação (`ArrayList` → `LinkedList`) sem alterar quem usa a lista._

---

## Parte 4 - Como executar

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
