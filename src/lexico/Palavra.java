package lexico;

public class Palavra extends Token {

	private final String palavra;

	public Palavra(Tag tag, String palavra) {
		super(tag);
		this.palavra = palavra;
	}

	public String getPalavra() {
		return this.palavra;
	}
}
