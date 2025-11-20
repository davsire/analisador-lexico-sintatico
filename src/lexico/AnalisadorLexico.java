package lexico;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalisadorLexico {

	private static final int CARACTER_FIM_ARQUIVO = -1;
	private static final List<Character> INICIO_RELOP = List.of('<', '>', '!', '=');

	private final BufferedReader reader;
	private final Map<String, Token> tabelaSimbolos = new HashMap<>();
	private final List<Token> tokens = new ArrayList<>();
	private int caracterAtual;
	private int numeroLinha = 1;
	private int numeroColuna = 1;

	public AnalisadorLexico(String caminhoArquivo) throws Exception {
		try {
			this.reader = new BufferedReader(new FileReader(caminhoArquivo));
			this.caracterAtual = this.reader.read();
			this.inicializarTabelaSimbolos();
		} catch (FileNotFoundException e) {
			throw new Exception("ERRO: Arquivo não encontrado!");
		} catch (IOException e) {
			throw new Exception("ERRO: Erro ao ler o arquivo: " + e.getMessage());
		}
	}

	public List<Token> analisar() throws Exception {
		while (!this.isFimArquivo()) {
			this.iterarCaracteresBrancos();

			if (AnalisadorLexico.INICIO_RELOP.contains(this.getCaracterAtual())) this.analisarTokenRelop();
			else if (Character.isDigit(this.getCaracterAtual())) this.analisarTokenNumerico();
			else if (Character.isLetter(this.getCaracterAtual())) this.analisarTokenPalavra();
			else if (!this.isFimArquivo()) this.analisarTokenOutro();
		}

		this.fecharArquivo();
		this.imprimirResultado();
		return this.tokens;
	}

	private void iterarCaracteresBrancos() {
		while (true) {
			if (this.getCaracterAtual() == ' ' || this.getCaracterAtual() == '\t') {
				this.numeroColuna++;
			} else if (this.getCaracterAtual() == '\n') {
				this.numeroLinha++;
				this.numeroColuna = 1;
			} else {
				break;
			}
			this.avancarArquivo();
		}
	}

	private void analisarTokenNumerico() {
		int valor = 0;
		do {
			valor = valor * 10 + Character.getNumericValue(this.getCaracterAtual());
			this.avancarArquivo();
			this.numeroColuna++;
		} while (Character.isDigit(this.getCaracterAtual()));

		this.tokens.add(new Numero(valor));
	}

	private void analisarTokenPalavra() {
		StringBuilder palavraBuilder = new StringBuilder();
		do {
			palavraBuilder.append(this.getCaracterAtual());
			this.avancarArquivo();
			this.numeroColuna++;
		} while (Character.isLetter(this.getCaracterAtual()) || Character.isDigit(this.getCaracterAtual()));

		String palavra = palavraBuilder.toString();
		if (this.tabelaSimbolos.containsKey(palavra)) {
			this.tokens.add(this.tabelaSimbolos.get(palavra));
		} else {
			Token tokenIdentificador = new Palavra(Tag.ID, palavra);
			this.tabelaSimbolos.put(palavra, tokenIdentificador);
			this.tokens.add(tokenIdentificador);
		}
	}

	private void analisarTokenRelop() throws Exception {
		int estadoAtual = 0;
		boolean aceita = false;

		while (!aceita) {
			switch (estadoAtual) {
				case 0 -> {
					switch (this.getCaracterAtual()) {
						case '<' -> estadoAtual = 1;
						case '>' -> estadoAtual = 2;
						case '!' -> estadoAtual = 3;
						case '=' -> estadoAtual = 4;
					}
					this.avancarArquivo();
					this.numeroColuna++;
				}
				case 1 -> {
					if (this.getCaracterAtual() == '=') {
						this.tokens.add(new Token(Tag.MENOR_IGUAL));
						aceita = true;
						this.avancarArquivo();
						this.numeroColuna++;
					} else {
						this.tokens.add(new Token(Tag.MENOR));
						aceita = true;
					}
				}
				case 2 -> {
					if (this.getCaracterAtual() == '=') {
						this.tokens.add(new Token(Tag.MAIOR_IGUAL));
						aceita = true;
						this.avancarArquivo();
						this.numeroColuna++;
					} else {
						this.tokens.add(new Token(Tag.MAIOR));
						aceita = true;
					}
				}
				case 3 -> {
					if (this.getCaracterAtual() == '=') {
						this.tokens.add(new Token(Tag.DIFERENTE));
						aceita = true;
						this.avancarArquivo();
						this.numeroColuna++;
					} else {
						throw new Exception(String.format("ERRO: Erro léxico na linha %d, coluna %d", this.numeroLinha, this.numeroColuna - 1));
					}
				}
				case 4 -> {
					if (this.getCaracterAtual() == '=') {
						this.tokens.add(new Token(Tag.IGUAL));
						aceita = true;
						this.avancarArquivo();
						this.numeroColuna++;
					} else {
						this.tokens.add(new Token(Tag.ATRIBUICAO));
						aceita = true;
					}
				}
			}
		}
	}

	private void analisarTokenOutro() throws Exception {
		boolean caracterDesconhecido = false;

		switch (this.getCaracterAtual()) {
			case '+' -> this.tokens.add(new Token(Tag.SOMA));
			case '-' -> this.tokens.add(new Token(Tag.SUBT));
			case '*' -> this.tokens.add(new Token(Tag.MULT));
			case '/' -> this.tokens.add(new Token(Tag.DIV));
			case '(' -> this.tokens.add(new Token(Tag.PARENTESE_E));
			case ')' -> this.tokens.add(new Token(Tag.PARENTESE_D));
			case '{' -> this.tokens.add(new Token(Tag.CHAVE_E));
			case '}' -> this.tokens.add(new Token(Tag.CHAVE_D));
			case ',' -> this.tokens.add(new Token(Tag.VIRGULA));
			case ';' -> this.tokens.add(new Token(Tag.PONTO_VIRGULA));
			default -> caracterDesconhecido = true;
		}

		if (caracterDesconhecido) {
			throw new Exception(String.format("ERRO: Erro léxico na linha %d, coluna %d", this.numeroLinha, this.numeroColuna));
		}

		this.avancarArquivo();
		this.numeroColuna++;
	}

	private void inicializarTabelaSimbolos() {
		this.tabelaSimbolos.put("int", new Palavra(Tag.INT, "int"));
		this.tabelaSimbolos.put("if", new Palavra(Tag.IF, "if"));
		this.tabelaSimbolos.put("else", new Palavra(Tag.ELSE, "else"));
		this.tabelaSimbolos.put("def", new Palavra(Tag.DEF, "def"));
		this.tabelaSimbolos.put("print", new Palavra(Tag.PRINT, "print"));
		this.tabelaSimbolos.put("return", new Palavra(Tag.RETURN, "return"));
	}

	private void imprimirResultado() {
		System.out.println("--- TOKENS ---");
		System.out.printf("[%s]%n%n", this.tokens.stream().map(Token::toString).collect(Collectors.joining(", ")));
		System.out.println("--- TABELA DE SÍMBOLOS ---");
		this.tabelaSimbolos.forEach((chave, valor) -> System.out.println(chave + ": " + valor));
		System.out.println();
	}

	private void avancarArquivo() {
		try {
			this.caracterAtual = this.reader.read();
		} catch (IOException e) {
			System.out.println("ERRO: Erro ao ler o arquivo: " + e.getMessage());
		}
	}

	private char getCaracterAtual() {
		return (char) this.caracterAtual;
	}

	private boolean isFimArquivo() {
		return this.caracterAtual == AnalisadorLexico.CARACTER_FIM_ARQUIVO;
	}

	public void fecharArquivo() {
		try {
			if (this.reader != null) {
				this.reader.close();
			}
		} catch (IOException e) {
			System.out.println("ERRO: Erro ao fechar o arquivo: " + e.getMessage());
		}
	}
}
