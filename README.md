# Analisador léxico + Analisador sintático preditivo

Programa que simula um analisador léxico e um analisador sintático preditivo para uma linguagem fictícia (LSI-2025-2).

## Compilação

Para compilar a aplicação, utilize o seguinte comando na raiz do projeto:

```bash
make all
```

## Como Executar

Execute o programa da seguinte forma, também na raiz do projeto:

```bash
java -cp bin Main <caminho_arquivo>
```

- `<caminho_arquivo>`: Caminho para o arquivo com o programa escrito na linguagem especificada.

### Exemplo:

```bash
java -cp bin Main test/sintatico_correto.lsi
```

## Observações

- Aplicação desenvolvida para sistemas baseados em Linux.
- Feita com Java 21 (javac 21.0.6)