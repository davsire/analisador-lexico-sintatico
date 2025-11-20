package lexico;

public class Numero extends Token {

	private final int valor;

	public Numero(int valor) {
		super(Tag.NUM);
		this.valor = valor;
	}

	public int getValor() {
		return this.valor;
	}
}
