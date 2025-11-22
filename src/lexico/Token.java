package lexico;

import enums.Terminal;

public class Token {

	private final Terminal terminal;

	public Token(Terminal terminal) {
		this.terminal = terminal;
	}

	public Terminal getTerminal() {
		return this.terminal;
	}

	@Override
	public String toString() {
		return this.terminal.name();
	}
}
