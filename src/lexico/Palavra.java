package lexico;

import enums.Terminal;

public class Palavra extends Token {

	private final String palavra;

	public Palavra(Terminal terminal, String palavra) {
		super(terminal);
		this.palavra = palavra;
	}

	public String getPalavra() {
		return this.palavra;
	}
}
