# L-Compiler

## Compilador da linguagem L desenvolvido na disciplina de Compiladores da PUC Minas.

O compilador consiste no analizador léxico, sintático, semântico e na tradução e geração de código das instruções para Assembly de códigos na linguagem de programação L.

## Instalação

Basta clonar esse repositório utilizando o seguinte comando:

```sh
git clone https://github.com/g-GSG/L-compiler.git
cd L-compiler
```

## Execução do compilador

O compilador requer o [Java](https://www.oracle.com/java/technologies/downloads/) para rodar.

Instale o jdk e inicie o compilador.

```sh
javac Main.java
```

Para compilar um arquivo e gerar o código .asm

```sh
java Main < pub.in
```

Para montar o arquivo .asm gerado pelo compilador

```sh
nasm arquivoSaida.asm -g -w-zeroing -f elf64 -o arquivoSaida.o
```

Linkedição e execução:

```sh
ld arquivoSaida.o -o arquivoSaida
./arquivoSaida
```

### Observação

O código executável gerado pelo compilador somente funcionará para sistemas Unix e MacOsX pois as chamadas de sistema para Windows são alteradas a cada atualização, sendo mais díficil gerar o Assembly para cada versão.
