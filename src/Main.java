import lexico.AnalisadorLexico;

public class Main {

	public static void main(String[] args) {
		String caminhoArquivo;
		try {
			caminhoArquivo = args[0];
		} catch (IndexOutOfBoundsException e) {
			System.out.println("ERRO: Informe o caminho do arquivo.");
			return;
		}

		try {
			AnalisadorLexico analisadorLexico = new AnalisadorLexico(caminhoArquivo);
			analisadorLexico.analisar();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
