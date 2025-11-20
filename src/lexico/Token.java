package lexico;

public class Token {

	private final Tag tag;

	public Token(Tag tag) {
		this.tag = tag;
	}

	public Tag getTag() {
		return this.tag;
	}

	@Override
	public String toString() {
		return this.tag.name();
	}
}
