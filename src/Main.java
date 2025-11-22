import lexico.AnalisadorLexico;
import lexico.Token;
import sintatico.AnalisadorSintatico;

import java.util.List;

public class Main {

	public static void main(String[] args) {
		String caminhoArquivo;
		try {
			caminhoArquivo = args[0];
		} catch (IndexOutOfBoundsException e) {
			System.out.println("ERRO: Informe o caminho do arquivo.");
			return;
		}

		List<Token> tokens;
		try {
			AnalisadorLexico analisadorLexico = new AnalisadorLexico(caminhoArquivo);
			tokens = analisadorLexico.analisar();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}

		try {
			AnalisadorSintatico analisadorSintatico = new AnalisadorSintatico(tokens);
			analisadorSintatico.analisar();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}

		System.out.println("SUCESSO: O programa escrito é válido com a linguagem LSI-2025-2.");
	}
}
