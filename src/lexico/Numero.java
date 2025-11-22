package lexico;

import enums.Terminal;

public class Numero extends Token {

	private final int valor;

	public Numero(int valor) {
		super(Terminal.NUM);
		this.valor = valor;
	}

	public int getValor() {
		return this.valor;
	}
}
