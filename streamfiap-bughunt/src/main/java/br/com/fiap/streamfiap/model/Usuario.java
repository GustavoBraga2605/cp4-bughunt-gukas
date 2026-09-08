package br.com.fiap.streamfiap.model;

import br.com.fiap.streamfiap.exception.ClassificacaoIndicativaException;
import br.com.fiap.streamfiap.exception.ConteudoIndisponivelException;
import br.com.fiap.streamfiap.exception.CreditosInsuficientesException;
import jakarta.persistence.*;


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private int idade;
    private double creditos;
    public Usuario() {
    }

    public Usuario(String nome, int idade, double creditos) {
        this.nome = nome;
        this.idade = idade;
        this.creditos = creditos;
    }

    public boolean temCreditosSuficientes(double preco) {
        // comparaçao invertida (preco >= this.creditos),
        // o que aprovava alugueis sem saldo e deixava creditos negativos
        return preco <= this.creditos;
    }

    public void debitarCreditos(double valor) {
        // comentario dizia "adiciona o valor" mas o codigo ta subtraindo
        this.creditos = this.creditos - valor;
    }

    public Usuario alugar(Conteudo c) throws ClassificacaoIndicativaException {
        if (!c.isDisponivel()) {
            throw new ConteudoIndisponivelException(c.getTitulo() + " nao esta disponivel para aluguel");
        }
        if (this.idade < c.getClassificacaoEtaria()) {
            throw new ClassificacaoIndicativaException("Usuário de " + this.idade
                    + " anos não pode assistir a " + c.getTitulo()
                    + " (classificação " + c.getClassificacaoEtaria() + " anos)");
        }

        double p = c.calcularPrecoAluguel();

        if (!temCreditosSuficientes(p)) {
            throw new CreditosInsuficientesException("Créditos insuficientes para alugar " + c.getTitulo());
        }

        debitarCreditos(p);
        c.setDisponivel(false);

        return this;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getCreditos() { return creditos; }
    public void setCreditos(double creditos) { this.creditos = creditos; }
}
