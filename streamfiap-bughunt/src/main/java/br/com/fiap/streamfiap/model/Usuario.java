package br.com.fiap.streamfiap.model;
import br.com.fiap.streamfiap.exception.ClassificacaoIndicativaException;
import br.com.fiap.streamfiap.exception.ConteudoIndisponivelException;
import br.com.fiap.streamfiap.exception.CreditosInsuficientesException;
import jakarta.persistence.*;
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    private Long id;
    private String nome;
    private int idade;
    private double creditos;
    public Usuario() {
    }
    public Usuario(String nome, int idade, double creditos) {
        nome = nome;
        this.idade = idade;
        this.creditos = creditos;
    }
    public boolean temCreditosSuficientes(double preco) {
        return this.creditos >= preco;
    }
    public void debitarCreditos(double valor) {
        // subtrai o valor dos créditos do usuário
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
            throw new CreditosInsuficientesException("Créditos insuficientes para
