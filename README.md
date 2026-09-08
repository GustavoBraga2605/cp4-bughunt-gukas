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

| # | Sintoma observado (o que fizemos/vimos) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | Documentário alugado cobrando R$ 9,90 em vez de ser gratuito | `Documentario.java`: não sobrescrevia `calcularPrecoAluguel()`, herdava o valor fixo de `Conteudo` | Adicionado `@Override public double calcularPrecoAluguel() { return 0.0; }` | Herança e polimorfismo |
| bug02 | Série sempre alugava por R$ 9,90 em vez do preço por temporada (4,90 × temporadas) | `Serie.java`: `calcularPrecoAluguel(double desconto)` tinha assinatura diferente da classe mãe — era sobrecarga, não sobrescrita, então nunca era chamado | Removido o parâmetro e adicionado `@Override`, método passou a sobrescrever corretamente | Sobrescrita (override) vs sobrecarga (overload) |
| bug03 | Ao cadastrar série, título/categoria/duração/classificação ficavam nulos/zerados; projeto não compilava | `Serie.java`, construtor: não chamava `super(...)` com todos os parâmetros (faltava `disponivel`); `ConteudoController.cadastrarSerie` não repassava esse valor | Construtor de `Serie` passou a chamar `super(titulo, categoria, duracaoMinutos, classificacaoEtaria, disponivel)`; `cadastrarSerie` ajustado para passar `serie.isDisponivel()` | Herança e construtores |
| bug04 | Filme com promoção ficava mais caro em vez de mais barato | `Filme.java`, `aplicarPromocao` (~linha 17): multiplicava por `1.2` (aumento de 20%) em vez de `0.8` (desconto de 20%) | Corrigido o multiplicador para `preco * 0.8` | Interfaces (Promocionavel) e regras de negócio |
| bug05 | Aluguel era aprovado mesmo com saldo menor que o preço; usuário ficava com créditos negativos | `Usuario.java`, `temCreditosSuficientes` (~linha 27): comparação invertida (`preco >= creditos`) | Invertido para `preco <= this.creditos` | Regras de negócio no model |
| bug06 | Usuário cadastrado retorna com `nome` nulo na resposta da API | `Usuario.java`, construtor (~linha 22): `nome = nome;` atribui o parâmetro a ele mesmo | Trocado para `this.nome = nome;` | Escopo de variáveis e uso de `this` |
| bug07 | `id` do usuário cadastrado vinha `null`, sem ser gerado pelo banco | `Usuario.java`, campo `id` (~linha 13-14): só tinha `@Id`, sem `@GeneratedValue` | Adicionado `@GeneratedValue(strategy = GenerationType.IDENTITY)` | JPA / mapeamento de entidades |
| bug08 | Buscar um conteúdo inexistente retornava 200 vazio, como se fosse sucesso | `ConteudoController.buscarPorId`: catch vazio engolia a exceção e retornava `null` | Removido o try/catch; agora usa `.orElseThrow(() -> new ConteudoNaoEncontradoException(...))`, retornando 404 com mensagem | Tratamento de exceções |
| bug09 | Filtro por categoria não retornava nenhum resultado (ou resultados errados) | `ConteudoController.listarPorCategoria`: comparava Strings com `==` em vez de `.equals()` | Trocado para `categoria.equals(c.getCategoria())` | Comparação de objetos vs referências em Java |
| bug10 | Tentar alugar conteúdo com classificação indicativa incompatível estourava erro 500 genérico, sem mensagem útil | `ClassificacaoIndicativaException` é checked, mas `GlobalExceptionHandler` não tinha `@ExceptionHandler` pra ela | Adicionado handler retornando 403 com a mensagem da exceção | Exceções checked vs unchecked |
| bug11 | Conteúdo com `duracaoMinutos <= 0` era cadastrado normalmente | Nenhuma validação de duração em `Conteudo`/subclasses | Criada `DuracaoInvalidaException`; validação adicionada no construtor de `Conteudo`; handler registrado no `GlobalExceptionHandler` (400) | Validação de regras de negócio no domínio |
| bug12 | Conteúdo indisponível era alugado normalmente (e ainda debitava crédito) | `Usuario.alugar`: nunca verificava `c.isDisponivel()` | Adicionada checagem no início do método, lançando `ConteudoIndisponivelException` | Regras de negócio no model |

## Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que mudamos |
|---|---|---|---|
| clean01 | `Conteudo.java`, atributo `duracaoMinutos` | Encapsulamento: campo era `public`, acessível direto de fora da classe | Alterado para `private`, acesso via getter/setter já existentes (e ajustados 3 acessos diretos no `ConteudoController` para usar `.getDuracaoMinutos()`) |
| clean02 | `Usuario.debitarCreditos` | Comentário enganoso: dizia "adiciona o valor aos créditos" enquanto o código subtraía | Comentário corrigido para refletir o que o código realmente faz |
| clean03 | `ConteudoController.java` | Código morto: método `calcularDescontoAntigo` nunca era chamado, além de dois blocos de código comentado duplicados sobre "regra de cupons" | Removidos o método e os blocos comentados |
| clean04 | `Serie.java`, método de preço | Faltava `@Override` no método que deveria sobrescrever `calcularPrecoAluguel` — a anotação teria acusado o erro de assinatura em tempo de compilação | Adicionado `@Override` (corrigido junto com o bug02) |
| clean05 | `Usuario.alugar` | Variável de nome sem significado: `double p` | Renomeada para `double preco`, com os usos atualizados |
| clean06 | `Usuario.alugar()` — bloco do recibo | `System.out.println` para "imprimir recibo" direto no model, misturando regra de negócio com apresentação (viola Responsabilidade Única) | Bloco removido do model |

---

## Perguntas de reflexão

### 1. Injeção de dependência (Aula 13)

_Se uma classe `Pedido` cria sozinha sua instância de `PagamentoService` com `new PagamentoService()`, por que isso é considerado um "acoplamento forte"? O que exatamente muda no design (e nos testes) quando essa dependência passa a ser recebida via construtor?_

### 2. JDBC vs Spring Data JPA (Aulas 12 e 13)

_Ambos no fim das contas "conversam com o banco de dados" — então por que o JPA existe? Pense no que você precisa escrever manualmente em JDBC (conexão, statement, mapeamento de ResultSet) e o que o JPA abstrai. Essa abstração é sempre vantagem, ou existe cenário em que o controle manual do JDBC compensa?_

### 3. Exceções checked vs unchecked (Aula 11)

_Por que o compilador *obriga* você a tratar uma `IOException` mas não obriga a tratar uma `NullPointerException`? O que essa diferença revela sobre a intenção do Java quanto a "erros que você pode prever e recuperar" versus "erros que indicam bug no código"?_

### 4. Sobrescrita vs sobrecarga (Aula 7)

_Se você tem `calcular(int a, int b)` e cria `calcular(double a, double b)` na mesma classe, isso é sobrecarga. Mas se uma subclasse reimplementa `calcular(int a, int b)` com o mesmo assinatura, isso é sobrescrita. Por que uma acontece em tempo de compilação e a outra em tempo de execução? Qual das duas está realmente ligada ao conceito de polimorfismo?_

### 5. Onde blindar o objeto? (Aulas 3, 4 e 13)

_Encapsulamento não é só "colocar `private` nos atributos e gerar getters/setters". Se um `Conta` tem `saldo` privado mas tem um `setSaldo(double valor)` público sem nenhuma validação, o objeto está realmente protegido? Onde deveria morar a regra "saldo não pode ficar negativo" — no setter, no método de saque, ou em ambos?_

### 6. Abstração e interface (Aulas 8 e 9)

_Uma `interface` em Java não implementa nada (ou quase nada, com os `default methods`). Então qual é o ganho real de programar contra uma interface (`List<String> lista = new ArrayList<>();`) em vez de contra a implementação concreta? O que isso te permite fazer amanhã que a implementação direta não permitiria?_

---

## Respostas pós-reflexão

### 1. Reduz acoplamento: a classe não decide como suas dependências são criadas, apenas as recebe (via construtor, idealmente). Isso facilita trocar implementações e testar com mocks, sem alterar o código da classe que depende.
### 2. JDBC é baixo nível: você escreve SQL, abre/fecha conexão e mapeia `ResultSet` manualmente. JPA abstrai isso com ORM (mapeia objeto ↔ tabela) e gera as queries por você. Ganha-se produtividade; perde-se controle fino sobre o SQL exato executado.
### 3. Checked (`IOException`, `SQLException`) representam falhas previsíveis e recuperáveis — o compilador exige tratamento. Unchecked (`RuntimeException` e subclasses) indicam erros de programação (bug) — não são forçadas porque, em teoria, não deveriam acontecer se o código estiver correto.
### 4. Sobrecarga (overload): mesmo nome, assinaturas diferentes, resolvida em tempo de compilação. Sobrescrita (override): mesma assinatura em subclasse, resolvida em tempo de execução (polimorfismo). Só a sobrescrita está ligada a polimorfismo.
### 5. Não basta `private` + getter/setter. A validação de regra de negócio (ex: saldo não pode ser negativo) deve estar dentro dos métodos que alteram o estado (setter ou método específico como `sacar()`), garantindo que o objeto nunca fique em estado inválido, independente de quem o chama.
### 6. Programar contra a interface (`List` em vez de `ArrayList`) desacopla o código da implementação concreta, permitindo trocar a implementação (`ArrayList` → `LinkedList`) sem alterar quem usa a lista.

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
