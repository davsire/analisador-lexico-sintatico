package sintatico;

import enums.NaoTerminal;
import enums.Terminal;
import lexico.Token;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class AnalisadorSintatico {

	private static final List<Enum> PRODUCAO_VAZIA = List.of();

	private final Map<NaoTerminal, Map<Terminal, List<Enum>>> tabelaReconhecimento = new HashMap<>();
	private final List<Terminal> input;
	private final Stack<Enum> pilha = new Stack<>();

	public AnalisadorSintatico(List<Token> tokens) {
		this.input = tokens.stream().map(Token::getTerminal).collect(Collectors.toList());
		this.input.add(Terminal.$);
		this.pilha.push(Terminal.$);
		this.pilha.push(NaoTerminal.MAIN);
		this.inicializarTabelaReconhecimento();
	}

	public void analisar() throws Exception {
		Iterator<Terminal> iteradorToken = this.input.iterator();
		Terminal tokenAtual = iteradorToken.next();
		Enum simboloAtual = this.pilha.peek();

		while (!simboloAtual.equals(Terminal.$)) {
			if (simboloAtual.equals(tokenAtual)) {
				tokenAtual = iteradorToken.next();
				this.pilha.pop();
			} else if (simboloAtual instanceof Terminal) {
				throw new Exception(String.format("ERRO: Erro sintático - token esperado: %s, token recebido: %s", simboloAtual.name(), tokenAtual.name()));
			} else {
				List<Enum> producao = this.obterEntradaTabela((NaoTerminal) simboloAtual, tokenAtual);
				this.pilha.pop();
				producao.reversed().forEach(this.pilha::push);
			}
			simboloAtual = this.pilha.peek();
		}

		if (!tokenAtual.equals(Terminal.$)) {
			throw new Exception("ERRO: Erro sintático - input não reconhecido corretamente");
		}
	}

	public void adicionarEntradaTabela(NaoTerminal naoTerminal, Terminal terminal, List<Enum> producao) {
		if (!this.tabelaReconhecimento.containsKey(naoTerminal)) {
			this.tabelaReconhecimento.put(naoTerminal, new HashMap<>());
		}
		this.tabelaReconhecimento.get(naoTerminal).put(terminal, producao);
	}

	public List<Enum> obterEntradaTabela(NaoTerminal naoTerminal, Terminal terminal) throws Exception {
		if (!this.tabelaReconhecimento.get(naoTerminal).containsKey(terminal)) {
			throw new Exception(String.format("ERRO: Erro sintático - sem produção válida para o não-terminal %s com o token %s", naoTerminal.name(), terminal.name()));
		}
		return this.tabelaReconhecimento.get(naoTerminal).get(terminal);
	}

	public void inicializarTabelaReconhecimento() {
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.$, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.DEF, List.of(NaoTerminal.FLIST));
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.ID, List.of(NaoTerminal.STMT));
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.CHAVE_E, List.of(NaoTerminal.STMT));
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.INT, List.of(NaoTerminal.STMT));
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.PONTO_VIRGULA, List.of(NaoTerminal.STMT));
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.PRINT, List.of(NaoTerminal.STMT));
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.RETURN, List.of(NaoTerminal.STMT));
		this.adicionarEntradaTabela(NaoTerminal.MAIN, Terminal.IF, List.of(NaoTerminal.STMT));
		this.adicionarEntradaTabela(NaoTerminal.FLIST, Terminal.DEF, List.of(NaoTerminal.FDEF, NaoTerminal.FLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.FLIST_I, Terminal.$, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.FLIST_I, Terminal.DEF, List.of(NaoTerminal.FLIST));
		this.adicionarEntradaTabela(NaoTerminal.FDEF, Terminal.DEF, List.of(Terminal.DEF, Terminal.ID, Terminal.PARENTESE_E, NaoTerminal.PARLIST, Terminal.PARENTESE_D, Terminal.CHAVE_E, NaoTerminal.STMTLIST, Terminal.CHAVE_D));
		this.adicionarEntradaTabela(NaoTerminal.PARLIST, Terminal.PARENTESE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.PARLIST, Terminal.INT, List.of(Terminal.INT, Terminal.ID, NaoTerminal.PARLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.PARLIST_I, Terminal.PARENTESE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.PARLIST_I, Terminal.VIRGULA, List.of(Terminal.VIRGULA, NaoTerminal.PARLIST));
		this.adicionarEntradaTabela(NaoTerminal.VARLIST, Terminal.ID, List.of(Terminal.ID, NaoTerminal.VARLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.VARLIST_I, Terminal.VIRGULA, List.of(Terminal.VIRGULA, NaoTerminal.VARLIST));
		this.adicionarEntradaTabela(NaoTerminal.VARLIST_I, Terminal.PONTO_VIRGULA, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.STMT, Terminal.ID, List.of(NaoTerminal.ATRIBST, Terminal.PONTO_VIRGULA));
		this.adicionarEntradaTabela(NaoTerminal.STMT, Terminal.CHAVE_E, List.of(Terminal.CHAVE_E, NaoTerminal.STMTLIST, Terminal.CHAVE_D));
		this.adicionarEntradaTabela(NaoTerminal.STMT, Terminal.INT, List.of(Terminal.INT, NaoTerminal.VARLIST, Terminal.PONTO_VIRGULA));
		this.adicionarEntradaTabela(NaoTerminal.STMT, Terminal.PONTO_VIRGULA, List.of(Terminal.PONTO_VIRGULA));
		this.adicionarEntradaTabela(NaoTerminal.STMT, Terminal.PRINT, List.of(NaoTerminal.PRINTST, Terminal.PONTO_VIRGULA));
		this.adicionarEntradaTabela(NaoTerminal.STMT, Terminal.RETURN, List.of(NaoTerminal.RETURNST, Terminal.PONTO_VIRGULA));
		this.adicionarEntradaTabela(NaoTerminal.STMT, Terminal.IF, List.of(NaoTerminal.IFSTMT));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST, Terminal.ID, List.of(Terminal.ID, Terminal.ATRIBUICAO, NaoTerminal.ATRIBST_I));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_I, Terminal.ID, List.of(Terminal.ID, NaoTerminal.ATRIBST_II));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_I, Terminal.PARENTESE_E, List.of(Terminal.PARENTESE_E, NaoTerminal.NUMEXPR, Terminal.PARENTESE_D, NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_I, Terminal.NUM, List.of(Terminal.NUM, NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.PARENTESE_E, List.of(NaoTerminal.FCALL));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.PONTO_VIRGULA, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.MENOR, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.MENOR_IGUAL, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.MAIOR, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.MAIOR_IGUAL, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.IGUAL, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.DIFERENTE, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.SOMA, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.SUBT, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.MULT, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.ATRIBST_II, Terminal.DIV, List.of(NaoTerminal.SEMIEXPR));
		this.adicionarEntradaTabela(NaoTerminal.FCALL, Terminal.PARENTESE_E, List.of(Terminal.PARENTESE_E, NaoTerminal.PARLISTCALL, Terminal.PARENTESE_D));
		this.adicionarEntradaTabela(NaoTerminal.PARLISTCALL, Terminal.ID, List.of(Terminal.ID, NaoTerminal.PARLISTCALL_I));
		this.adicionarEntradaTabela(NaoTerminal.PARLISTCALL, Terminal.PARENTESE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.PARLISTCALL_I, Terminal.PARENTESE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.PARLISTCALL_I, Terminal.VIRGULA, List.of(Terminal.VIRGULA, NaoTerminal.PARLISTCALL));
		this.adicionarEntradaTabela(NaoTerminal.PRINTST, Terminal.PRINT, List.of(Terminal.PRINT, NaoTerminal.EXPR));
		this.adicionarEntradaTabela(NaoTerminal.RETURNST, Terminal.RETURN, List.of(Terminal.RETURN, NaoTerminal.RETURNST_I));
		this.adicionarEntradaTabela(NaoTerminal.RETURNST_I, Terminal.ID, List.of(Terminal.ID));
		this.adicionarEntradaTabela(NaoTerminal.RETURNST_I, Terminal.PONTO_VIRGULA, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT, Terminal.IF, List.of(Terminal.IF, Terminal.PARENTESE_E, NaoTerminal.EXPR, Terminal.PARENTESE_D, Terminal.CHAVE_E, NaoTerminal.STMT, Terminal.CHAVE_D, NaoTerminal.IFSTMT_I));
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.$, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.ID, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.CHAVE_E, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.CHAVE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.INT, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.PONTO_VIRGULA, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.PRINT, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.RETURN, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.IF, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.IFSTMT_I, Terminal.ELSE, List.of(Terminal.ELSE, Terminal.CHAVE_E, NaoTerminal.STMT, Terminal.CHAVE_D));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST, Terminal.ID, List.of(NaoTerminal.STMT, NaoTerminal.STMTLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST, Terminal.CHAVE_E, List.of(NaoTerminal.STMT, NaoTerminal.STMTLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST, Terminal.INT, List.of(NaoTerminal.STMT, NaoTerminal.STMTLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST, Terminal.PONTO_VIRGULA, List.of(NaoTerminal.STMT, NaoTerminal.STMTLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST, Terminal.PRINT, List.of(NaoTerminal.STMT, NaoTerminal.STMTLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST, Terminal.RETURN, List.of(NaoTerminal.STMT, NaoTerminal.STMTLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST, Terminal.IF, List.of(NaoTerminal.STMT, NaoTerminal.STMTLIST_I));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.ID, List.of(NaoTerminal.STMTLIST));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.CHAVE_E, List.of(NaoTerminal.STMTLIST));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.CHAVE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.INT, List.of(NaoTerminal.STMTLIST));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.PONTO_VIRGULA, List.of(NaoTerminal.STMTLIST));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.PRINT, List.of(NaoTerminal.STMTLIST));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.RETURN, List.of(NaoTerminal.STMTLIST));
		this.adicionarEntradaTabela(NaoTerminal.STMTLIST_I, Terminal.IF, List.of(NaoTerminal.STMTLIST));
		this.adicionarEntradaTabela(NaoTerminal.EXPR, Terminal.ID, List.of(NaoTerminal.NUMEXPR, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.EXPR, Terminal.PARENTESE_D, List.of(NaoTerminal.NUMEXPR, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.EXPR, Terminal.NUM, List.of(NaoTerminal.NUMEXPR, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.PONTO_VIRGULA, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.MENOR, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.MENOR_IGUAL, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.MAIOR, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.MAIOR_IGUAL, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.IGUAL, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.DIFERENTE, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.SOMA, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.SUBT, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.MULT, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.SEMIEXPR, Terminal.DIV, List.of(NaoTerminal.TERM_I, NaoTerminal.NUMEXPR_I, NaoTerminal.EXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.PARENTESE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.PONTO_VIRGULA, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.MENOR, List.of(Terminal.MENOR, NaoTerminal.NUMEXPR));
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.MENOR_IGUAL, List.of(Terminal.MENOR_IGUAL, NaoTerminal.NUMEXPR));
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.MAIOR, List.of(Terminal.MAIOR, NaoTerminal.NUMEXPR));
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.MAIOR_IGUAL, List.of(Terminal.MAIOR_IGUAL, NaoTerminal.NUMEXPR));
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.IGUAL, List.of(Terminal.IGUAL, NaoTerminal.NUMEXPR));
		this.adicionarEntradaTabela(NaoTerminal.EXPR_I, Terminal.DIFERENTE, List.of(Terminal.DIFERENTE, NaoTerminal.NUMEXPR));
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR, Terminal.ID, List.of(NaoTerminal.TERM, NaoTerminal.NUMEXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR, Terminal.PARENTESE_E, List.of(NaoTerminal.TERM, NaoTerminal.NUMEXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR, Terminal.NUM, List.of(NaoTerminal.TERM, NaoTerminal.NUMEXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.PARENTESE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.PONTO_VIRGULA, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.MENOR, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.MENOR_IGUAL, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.MAIOR, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.MAIOR_IGUAL, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.IGUAL, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.DIFERENTE, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.SOMA, List.of(NaoTerminal.NUMEXPR_II, NaoTerminal.NUMEXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_I, Terminal.SUBT, List.of(NaoTerminal.NUMEXPR_II, NaoTerminal.NUMEXPR_I));
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_II, Terminal.SOMA, List.of(Terminal.SOMA, NaoTerminal.TERM));
		this.adicionarEntradaTabela(NaoTerminal.NUMEXPR_II, Terminal.SUBT, List.of(Terminal.SUBT, NaoTerminal.TERM));
		this.adicionarEntradaTabela(NaoTerminal.TERM, Terminal.ID, List.of(NaoTerminal.FACTOR, NaoTerminal.TERM_I));
		this.adicionarEntradaTabela(NaoTerminal.TERM, Terminal.PARENTESE_E, List.of(NaoTerminal.FACTOR, NaoTerminal.TERM_I));
		this.adicionarEntradaTabela(NaoTerminal.TERM, Terminal.NUM, List.of(NaoTerminal.FACTOR, NaoTerminal.TERM_I));
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.PARENTESE_D, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.PONTO_VIRGULA, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.MENOR, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.MENOR_IGUAL, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.MAIOR, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.MAIOR_IGUAL, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.IGUAL, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.DIFERENTE, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.SOMA, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.SUBT, AnalisadorSintatico.PRODUCAO_VAZIA);
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.MULT, List.of(NaoTerminal.TERM_II, NaoTerminal.TERM_I));
		this.adicionarEntradaTabela(NaoTerminal.TERM_I, Terminal.DIV, List.of(NaoTerminal.TERM_II, NaoTerminal.TERM_I));
		this.adicionarEntradaTabela(NaoTerminal.TERM_II, Terminal.MULT, List.of(Terminal.MULT, NaoTerminal.FACTOR));
		this.adicionarEntradaTabela(NaoTerminal.TERM_II, Terminal.DIV, List.of(Terminal.DIV, NaoTerminal.FACTOR));
		this.adicionarEntradaTabela(NaoTerminal.FACTOR, Terminal.ID, List.of(Terminal.ID));
		this.adicionarEntradaTabela(NaoTerminal.FACTOR, Terminal.PARENTESE_E, List.of(Terminal.PARENTESE_E, NaoTerminal.NUMEXPR, Terminal.PARENTESE_D));
		this.adicionarEntradaTabela(NaoTerminal.FACTOR, Terminal.NUM, List.of(Terminal.NUM));
	}
}
