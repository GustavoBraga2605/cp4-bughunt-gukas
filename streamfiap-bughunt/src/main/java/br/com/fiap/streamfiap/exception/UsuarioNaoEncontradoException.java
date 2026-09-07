package br.com.fiap.streamfiap.exception;

// NOVO: excecao de dominio para usuario inexistente, espelhando
public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}