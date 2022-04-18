/*
Compiladores/2022.1

Grupo:
@Giulia Chiucchi
@Gustavo Gomes
@Stephanie Silva

*/

import java.util.*;
import java.io.*;

/*Criacao da classe para a representacao de um simbolo da linguagem*/
class Simbolo {
   private String lexema = "";
   private String tipo = "";
   private String classe = "";
   private int endereco = -1;
   private int tamanho = 0;
   private String valor = "";
   private String token;

   public Simbolo() {
      this.lexema = "";
      this.tipo = "";
      this.classe = "";
      this.endereco = -1;
      this.tamanho = 0;
      this.valor = "";
      this.token = "";
   }

   public Simbolo(String token, String lexema, String tipo, String classe, int endereco, int tamanho, String valor) {
      this.lexema = lexema;
      this.token = token;
      this.tipo = tipo;
      this.classe = classe;
      this.endereco = endereco;
      this.tamanho = tamanho;
      this.valor = valor;
   }

   public String getToken() {
      return this.token;
   }

   public String getLexema() {
      return this.lexema;
   }

   public String getTipo() {
      return this.tipo;
   }

   public String getClasse() {
      return this.classe;
   }

   public int getTamanho() {
      return this.tamanho;
   }

   public String getValor() {
      return this.valor;
   }

   public int getEndereco() {
      return this.endereco;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public void setLexema(String lexema) {
      this.lexema = lexema;
   }

   public void setTipo(String tipo) {
      this.tipo = tipo;
   }

   public void setClasse(String classe) {
      this.classe = classe;
   }

   public void setTamanho(int tamanho) {
      this.tamanho = tamanho;
   }

   public void setValor(String valor) {
      this.valor = valor;
   }

   public void setEndereco(int endereco) {
      this.endereco = endereco;
   }
}

public class Main {
   private static ArrayList<String> linhasArquivo;
   private static Simbolo token = new Simbolo();
   private static String[] file = readFile();
   static BufferedReader arquivo; // pode tirar, nao?
   private static int linhas = 1;
   private static int index = 0;
   private static int indexTabela = 0;
   private static boolean error = false;
   private static int numLinhasArquivo;

   // Declaracao dos caracteres que fazem parte dos tokens da linguagem
   private static String[] alfabeto_simbolos = { "_", ".", ",", ";", "(", ")", "[", "]", "+", "-", "'", "\"",
         "%", "!", ">", "<", "=", "*", "/" };
   // Declaracao dos caracteres reservados da linguagem que nao podem ser
   // declaradas ou atribuidas durante o programa, exceto o TRUE e FALSE
   private static String[] alfabeto_reservadas = { "const", "integer", "char", "while", "if", "real", "else", "and",
         "or", "not", "begin", "end", "readln", "string", "write", "writeln", "TRUE", "FALSE", "boolean", "==", "!=",
         ">=", "<=", "//" };
   // Declaracao dos caracteres permitidos na linguagem que podem ser usados em
   // comentarios, strings e char
   private static String[] alfabeto_caracteres = { " ", "_", ".", ",", ";", ":", "(", ")", "[", "]", "{", "}", "+", "-",
         "\"", "'", "/", "\\", "@", "&", "%", "!", "?", ">", "<", "=", "*" };
   // Declaracao das letras permitidas nas constantes hexadecimais
   private static String[] letras_hexa = { "A", "B", "C", "D", "E", "F" };

   public static HashMap<String, Simbolo> tabela = new HashMap<>();

   // Letras permitidas na linguagem
   public static boolean isLetter(char c) {
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
         return true;
      else
         return false;
   }

   public static String[] readFile() {
      String result = "";
      int auxLinha = 1;

      try {
         linhasArquivo = new ArrayList<String>();
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

         for (int t = br.read(); t != -1; t = br.read()) {

            if ((char) t == '\n') {
               result = result + (char) t;
               auxLinha += 1; // Variavel para ter controle de quantas linhas tem no arquivo, mesmo as nulas
               linhasArquivo.add(result);
            } else if ((char) t == '\r') {
            } else {
               char aux = (char) t;
               result = result + aux;
            }
         }

         if (result.charAt(result.length() - 1) != '\n') {// Formatacao para leitura correta de todos os tokens,
                                                          // sinaliza que o fim do arquivo foi atingido
            result = result + '\n';
         }

         br.close();

      } catch (Exception e) {
         e.printStackTrace();
      }

      String results[] = new String[2]; // Formatacao do retorno da funcao, que retorna o arquivo lido e a quantidade de
                                        // linhas
      results[0] = result;
      results[1] = Integer.toString(auxLinha);

      return results;
   }

   public static Simbolo searchTabela(String tok) {
      return tabela.get(tok.toLowerCase()); // retorna null se nao estiver presente
   }

   public static boolean searchAlfabeto(char tok, String[] alfabeto) {
      boolean result = false;
      for (int i = 0; i < alfabeto.length; i++) {
         if (alfabeto[i].charAt(0) == tok) {
            result = true;
         }
      }
      return result;
   }

   public static char lerChar(int i) {
      return file[0].charAt(i);
   }

   /* ANALISADOR LEXICO */
   public static Simbolo analisadorLexico() {
      Simbolo simbolo = new Simbolo();
      int estado = 0;
      String lex = "";
      numLinhasArquivo = Integer.parseInt(file[1]);

      // System.out.println(file);

      // Enquanto for um estado valido, o analisador lexico ira procurar por um token
      while (estado != -1) {

         // Se for tentando ler um caracter depois do fim de arquivo, dispara um erro
         if (index >= file[0].length() && error == false) {
            error = true;
            estado = -1;
            if (numLinhasArquivo < linhas) { // Formatacao da linha, quando o erro de fim de arquivo aparece na linha
                                             // antes do EOF
               linhas--;
            }
            System.out.print((linhas) + "\nfim de arquivo nao esperado.");
            break;
         }

         if (index >= file[0].length()) {
            estado = -1;
            break;
         }

         char c = file[0].charAt(index);
         // Se o caracter lido nao pertencer a linguagem, dispara um erro
         if (searchAlfabeto(c, alfabeto_caracteres) == false && isLetter(c) == false && Character.isDigit(c) == false
               && c != '\n' && c != '\r') {
            error = true;
            System.out.print((linhas) + "\ncaractere invalido.");
            index = file[0].length();
         } else {
            switch (estado) {
               case 0:
                  // Constante Hexadecimal ou Identificador
                  if (searchAlfabeto(c, letras_hexa)) {
                     lex += c;
                     index++;
                     estado = 11;
                     // Identificador ou Palavra reservada
                  } else if (c == '_' || isLetter(c)) {
                     lex += c;
                     index++;
                     simbolo.setToken("id");
                     estado = 10;
                     // Numero real ou numero inteiro ou constante hexadecimal
                  } else if (Character.isDigit(c)) {
                     lex += c;
                     index++;
                     estado = 21;
                     // Comparadores de grandeza (maior,menor,igual, maiorigual,menorigual) ou
                     // atribuicao
                  } else if (c == '=' || c == '<' || c == '>') {
                     lex += c;
                     index++;
                     estado = 4;
                     // Diferente
                  } else if (c == '!') {
                     lex += c;
                     index++;
                     estado = 5;
                     // Divisao ou quociente da divisao de 2 inteiros
                  } else if (c == '/') {
                     lex += c;
                     index++;
                     estado = 6;
                     // Caracter
                  } else if (c == '\'') {
                     lex += c;
                     index++;
                     estado = 7;
                     // String
                  } else if (c == '"') {
                     lex += c;
                     index++;
                     estado = 9;
                     // Operadores
                  } else if (c == ',' || c == '-' || c == '+' || c == '*' || c == ';' || c == '%' || c == '('
                        || c == ')' || c == '[' || c == ']') {
                     lex += c;
                     simbolo.setToken(lex);
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                     // Ponto flutuante
                  } else if (c == '.') {
                     lex += c;
                     index++;
                     estado = 19;
                     // Comentario
                  } else if (c == '{') {
                     lex += c;
                     index++;
                     estado = 1;
                     // Delimitador de token
                  } else if (c == ' ') {
                     estado = 0;
                     index++;
                     // Delimitador de token
                  } else if (c == '\n' || c == '\r') {
                     estado = 0;
                     linhas++;
                     index++;
                     if (index >= file[0].length()) {
                        estado = -1;
                     }
                     // Caracter nao pertencente a nenhum token
                  } else {
                     if (lex.length() == 0) {
                        lex += c;
                     }
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                     error = true;
                     estado = -1;
                  }
                  break;

               // Comentario: casos de 1 a 3
               case 1:
                  if (c == '*') {
                     estado = 2;
                     index++;
                     lex += c;
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                     estado = -1;
                     break;
                  }
                  break;

               case 2:
                  if (c == '*') {
                     estado = 3;
                     index++;
                     lex += c;
                  } else if (isLetter(c) || Character.isDigit(c) || searchAlfabeto(c, alfabeto_caracteres) == true
                        || c == ' ' || c == '\n' || c == '\r') { // Enquanto o comentario nao achar um * e ler um
                                                                 // caracter ou delimitador valido na linguagem
                     index++;
                     if (c == '\n' || c == '\r')
                        linhas++;
                     lex += c;
                     estado = 2;
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                     estado = -1;
                  }
                  break;

               case 3:
                  if (c == '*') { // Pode-se ler 0 ou mais * dentro de um comentario
                     estado = 3;
                     index++;
                     lex += c;
                  } else if (c != '*' && c != '}') { // Volta para o estado 2 pois o comentario ainda nao foi finalizado
                     estado = 2;
                     index++;
                     lex += c;
                     if (c == '\n' || c == '\r')
                        linhas++;
                  } else if (c == '}') { // Fechamento do comentario
                     estado = 0;
                     index++;
                     lex = "";
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                  }
                  break;

               // >= <= == > < =
               case 4:
                  if (c == '=') { // Adiciona o = aos simbolos <,> ou =
                     lex += c;
                     simbolo.setToken(lex);
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                  } else { // Leu apenas os simbolos >, < ou =
                     simbolo.setToken(lex);
                     simbolo.setLexema(lex);
                     estado = 30;
                  }
                  break;

               // !=
               case 5:
                  if (c == '=') {
                     lex += c;
                     simbolo.setToken(lex);
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                  } else if (c != '=') { // O caracter ! deve ser obrigatoriamente seguido por um '=', se isso nao
                                         // ocorrer dispara um erro
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                     lex = "";
                     estado = -1;
                  }
                  break;

               // / ou //
               case 6:
                  if (c == '/') {
                     lex += c;
                     index++;
                     simbolo.setToken(lex);
                     simbolo.setLexema(lex);
                     estado = 30;
                  } else {
                     simbolo.setToken(lex);
                     simbolo.setLexema(lex);
                     estado = 30;
                  }
                  break;

               // Char
               case 7:
                  if ((searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c) || isLetter(c)) // Char so pode
                                                                                                      // ser composto de
                                                                                                      // letras, digitos
                                                                                                      // ou caracteres
                                                                                                      // validos na
                                                                                                      // linguagem
                        && c != ' ') {
                     lex += c;
                     index++;
                     estado = 8;
                  } else {
                     error = true;
                     if ((c == '\n' || c == '\r') && (index + 1) == file[0].length()) { // Se exister uma quebra de
                                                                                        // linha
                        // logo antes do arquivo acabar
                        // dispara-se um erro
                        System.out.print((linhas) + "\nfim de arquivo nao esperado.");
                        index = file[0].length();
                        lex = "";
                        estado = -1;
                     } else {
                        System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                        index = file[0].length();
                        lex = "";
                        estado = -1;
                     }
                  }
                  break;

               case 8:
                  if (c == '\'') { // Fechamento do char
                     lex += c;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                  } else {
                     error = true;
                     if ((c == '\n' || c == '\r') && (index + 1) == file[0].length()) {// Se exister uma quebra de linha
                        // logo antes do arquivo acabar
                        // dispara-se um erro
                        System.out.print((linhas) + "\nfim de arquivo nao esperado.");
                        index = file[0].length();
                        lex = "";
                        estado = -1;
                     } else {
                        System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                        index = file[0].length();
                        lex = "";
                        estado = -1;
                     }
                  }
                  break;

               // string
               case 9:
                  if (c != '"'
                        && (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c) || isLetter(c))) { // String
                                                                                                              // so pode
                                                                                                              // ser
                                                                                                              // composto
                                                                                                              // de
                                                                                                              // letras,
                                                                                                              // digitos
                                                                                                              // ou
                                                                                                              // caracteres
                                                                                                              // validos
                                                                                                              // na
                                                                                                              // linguagem,
                                                                                                              // exceto
                                                                                                              // aspas
                     lex += c;
                     index++;
                     estado = 9;
                  } else if (c == '"') { // Fechamento da string
                     lex += c;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                  } else {
                     error = true;
                     estado = -1;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                     lex = "";
                  }
                  break;

               // identificador ou palavra reservada
               case 10:
                  if ((c == '_' || isLetter(c) || Character.isDigit(c))) {
                     lex += c;
                     index++;
                     estado = 10;
                  } else if (c != '_' && isLetter(c) == false && Character.isDigit(c) == false) { // Leitura de um
                                                                                                  // caracter nao mais
                                                                                                  // pertencente ao ID
                                                                                                  // ou palavra
                                                                                                  // reservada
                     if (searchTabela(lex) == null) { // Verifica se é uma palavra reservada, se retornar null na busca
                                                      // da tabela de simbolos é um ID
                        simbolo.setToken("id");
                        simbolo.setLexema(lex);
                     } else {
                        if (lex.equals("TRUE") || lex.equals("FALSE")) { // das palavras reservadas, o TRUE e ELSE sao
                                                                         // constantes
                           simbolo.setToken("const");
                           simbolo.setLexema(lex);
                        } else {
                           simbolo = searchTabela(lex);
                           simbolo.setLexema(lex);
                        }
                     }
                     estado = 30;
                  }
                  break;

               // Constante hexadecimal ou identificador, casos 11, 16, 18
               case 11:
                  if (searchAlfabeto(c, letras_hexa) || Character.isDigit(c)) { // Verifica se é uma das letras
                                                                                // hexadecimal ou um numero para decidir
                                                                                // se é um ID ou nao
                     lex += c;
                     index++;
                     estado = 16;
                  } else if (isLetter(c) || c == '_') { // Verificou que os caracteres lidos podem fazer parte de um ID
                                                        // e nao mais uma const hexadecimal
                     lex += c;
                     index++;
                     estado = 10;
                  } else { // Verificou que foi encontrado um ID
                     estado = 30;
                     simbolo.setToken("id");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 16:
                  if (c == 'h') { // Possivel const hexadecimal encontrada, vai para o estado 18 para confirmar se
                                  // é um ID ou uma const
                     lex += c;
                     index++;
                     estado = 18;
                  } else if ((c == '_' || isLetter(c) || Character.isDigit(c))) {// Verificou que os caracteres lidos
                                                                                 // podem fazer parte de um ID e nao
                                                                                 // mais uma const hexadecimal
                     lex += c;
                     index++;
                     estado = 10;
                  } else { // Verificou que foi encontrado um ID
                     estado = 30;
                     simbolo.setToken("id");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 18:
                  if ((c == '_' || isLetter(c) || Character.isDigit(c))) { // Verificacao que os caracteres sendo lidos
                                                                           // podem fazer parte de um ID
                     lex += c;
                     index++;
                     estado = 10;
                  } else { // Confirmacao que é uma const hexadecimal
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 19:// Verificacao de const hexadecimal ou numero, casos 19,20,21,22,23,24
                  if (Character.isDigit(c)) {// Numero iniciado com ponto, se nao achar um numero depois do '.'
                                             // dispara-se um erro
                     lex += c;
                     index++;
                     estado = 20;
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                     lex = "";
                     estado = -1;
                  }
                  break;

               case 20:
                  if (Character.isDigit(c)) { // Leitura de um numero interio ou real
                     lex += c;
                     index++;
                     estado = 20;
                  } else {
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 21:
                  if (c == '.') { // Verificacao se é um numero real, inteiro ou const hexadecimal
                     lex += c;
                     index++;
                     estado = 20;
                  } else if (Character.isDigit(c)) {
                     lex += c;
                     index++;
                     estado = 22;
                  } else if (searchAlfabeto(c, letras_hexa)) {
                     lex += c;
                     index++;
                     estado = 23;
                  } else { // Encontrou um numero
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 22:
                  if (c == '.') {// Verificacao se é um numero real ou inteiro
                     lex += c;
                     index++;
                     estado = 20;
                  } else if (Character.isDigit(c)) {
                     lex += c;
                     index++;
                     estado = 24;
                  } else if (c == 'h') {// Encontrou uma constante hexadecimal comecada por digito
                     lex += c;
                     index++;
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  } else {
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 23:
                  if (c == 'h') {// Encontrou uma constante hexadecimal composta do por 1 digito e uma letra
                                 // hexadecimal
                     lex += c;
                     index++;
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file[0].length();
                     lex = "";
                     estado = -1;
                  }
                  break;

               case 24:
                  if (c == '.') {// Verificacao se é um numero real ou inteiro
                     lex += c;
                     index++;
                     estado = 20;
                  } else if (Character.isDigit(c)) {
                     lex += c;
                     index++;
                     estado = 24;
                  } else {
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 30: // Estado final de aceitacao de um token, devolve o lexema lido para o
                        // analisador sintatico
                  lex = "";
                  estado = -1;
                  break;
            }
         }
      }

      return simbolo;

   }

   /* ANALISADOR SINTATICO */

   // Metodo do CasaToken para identificar se o token lido corresponde as regras da
   // gramatica
   public static void ct(String token_esperado) {
      if (token.getToken().equals(token_esperado)) {
         token = analisadorLexico();
      } else if (token.getToken().equals("") && error == false) {
         if (numLinhasArquivo < linhas) { // Formatacao da linha, quando o erro de fim de arquivo aparece na linha antes
                                          // do EOF
            linhas--;
         }
         System.out.print((linhas) + "\nfim de arquivo nao esperado.");
         error = true;
         System.exit(0);
      } else {
         if (error == false) {
            error = true;
            System.out.println(linhas);
            System.out.print("token nao esperado [" + token.getToken() + "].");
            System.exit(0);
         }
      }
   }

   /*
    * Gramatica S-> {Declaracao}* {Comandos}* EoF
    */
   public static void S() {
      token = analisadorLexico();
      try {
         // Enquanto for uma declaracao ou comando
         while (token.getToken().equals("integer") || token.getToken().equals("const")
               || token.getToken().equals("char") || token.getToken().equals("real")
               || token.getToken().equals("string") || token.getToken().equals("boolean") || token.getToken()
                     .equals("id")
               || token.getToken().equals("while") || token.getToken().equals("if")
               || token.getToken().equals(";") || token.getToken().equals("readln")
               || token.getToken().equals("writeln") || token.getToken().equals("write")) {

            // Identifica os tokens que sao declaracoes
            while (token.getToken().equals("integer") || token.getToken().equals("const")
                  || token.getToken().equals("char") || token.getToken().equals("real")
                  || token.getToken().equals("string") || token.getToken().equals("boolean")) {
               Declaracao();
            }

            // Identifica os tokens que sao comandos
            while (token.getToken().equals("id") || token.getToken().equals("while") || token.getToken().equals("if")
                  || token.getToken().equals(";") || token.getToken().equals("readln")
                  || token.getToken().equals("writeln") || token.getToken().equals("write")) {
               Comandos();
            }

            // Se for um token que nao inicia nem declaracao ou comando dispara-se um erro
            while (token.getToken().equals("end") || token.getToken().equals("begin")) {
               error = true;
               System.out.println(linhas);
               System.out.print("token nao esperado [" + token.getToken() + "].");
               System.exit(0);
            }
         }
      } catch (Exception e) {
      }
   }

   /*
    * Declaracao -> Variaveis | Constantes
    */
   public static void Declaracao() {
      if (token.getToken().equals("integer") || token.getToken().equals("char") || token.getToken().equals("real")
            || token.getToken().equals("string") || token.getToken().equals("boolean")) {
         Variaveis();
      } else if (token.getToken().equals("const")) {
         Constantes();
      }
   }

   /*
    * VARIAVEIS {(int | char | boolean | real | string) id1[ = [-] constante]
    * {,id2[ = [-] constante ] }* ;}
    */
   public static void Variaveis() {
      while (token.getToken().equals("integer") || token.getToken().equals("char") || token.getToken().equals("real")
            || token.getToken().equals("string") || token.getToken().equals("boolean")) {
         ct(token.getToken());
         ct("id");
         if (token.getToken().equals("=")) {
            ct("=");
            if (token.getToken().equals("-")) {
               ct("-");
            }
            ct("const"); // -> valor constante
         }
         while (token.getToken().equals(",")) {
            ct(",");
            ct("id");
            if (token.getToken().equals("=")) {
               ct("=");
               if (token.getToken().equals("-")) {
                  ct("-");
               }
               ct("const"); // -> valor constante
            }
         }
         ct(";");

      }
   }

   /*
    * CONSTANTES {const id = [-] constante;}*
    */
   public static void Constantes() {
      while (token.getToken().equals("const")) {
         ct("const");
         ct("id");
         ct("=");
         if (token.getToken().equals("-")) {
            ct("-");
         }
         ct("const");
         ct(";");
      }
   }

   /*
    * COMANDOS Comandos disponiveis: atribuicao, repeticao, condicional, nulo,
    * leitura, escrita
    */
   public static void Comandos() {
      if (token.getToken().equals("id")) {
         atribuicao();
      } else if (token.getToken().equals("while")) {
         repeticao();
      } else if (token.getToken().equals("if")) {
         condicional();
      } else if (token.getToken().equals(";")) {
         ct(";");
      } else if (token.getToken().equals("readln")) {
         leitura();
      } else if (token.getToken().equals("writeln") || token.getToken().equals("write")) {
         escrita();
      }
   }

   // Comando de atribuicao
   // id = Exp ; | id "["Exp"]" = Exp ;
   public static void atribuicao() {
      ct("id");
      if (token.getToken().equals("=")) {
         ct("=");
         Exp();
      } else if (token.getToken().equals("[")) {
         ct("[");
         Exp();
         ct("]");
         ct("=");
         Exp();
      }
      ct(";");
   }

   // Comando de repeticao
   // while Exp (Comandos | Lista_Comandos)
   public static void repeticao() {
      ct("while");
      Exp();
      if (token.getToken().equals("begin")) {
         ct("begin");
         while (token.getToken().equals("id") || token.getToken().equals("while") || token.getToken().equals("if")
               || token.getToken().equals(";") || token.getToken().equals("readln")
               || token.getToken().equals("writeln") || token.getToken().equals("write")) {
            Comandos();
         }
         ct("end");
      } else {
         Comandos();
      }
   }

   // Comando condicional
   // if Exp (Comandos | Lista_Comandos) [else (Comandos | Lista_Comandos)]
   public static void condicional() {
      ct("if");
      Exp();
      if (token.getToken().equals("begin")) {
         ct("begin");
         while (token.getToken().equals("id") || token.getToken().equals("while") || token.getToken().equals("if")
               || token.getToken().equals(";") || token.getToken().equals("readln")
               || token.getToken().equals("writeln") || token.getToken().equals("write")) {
            Comandos();
         }
         ct("end");
      } else {
         Comandos();
      }
      if (token.getToken().equals("else")) {
         ct("else");
         if (token.getToken().equals("begin")) {
            ct("begin");
            while (token.getToken().equals("id") || token.getToken().equals("while") || token.getToken().equals("if")
                  || token.getToken().equals(";") || token.getToken().equals("readln")
                  || token.getToken().equals("writeln") || token.getToken().equals("write")) {
               Comandos();
            }
            ct("end");
         } else {
            Comandos();
         }
      }
   }

   // Comando de leitura
   // readln "(" id ")";
   public static void leitura() {
      ct("readln");
      ct("(");
      ct("id");
      ct(")");
      ct(";");
   }

   // Comando de impressao
   // (write | writeln) "(" Exp {,Exp}* ")";
   public static void escrita() {
      if (token.getToken().equals("write")) {
         ct("write");
      } else {
         ct("writeln");
      }
      ct("(");
      Exp();
      while (token.getToken().equals(",")) {
         ct(",");
         Exp();
      }
      ct(")");
      ct(";");
   }

   // EXP
   // Exp_Soma1 [(== | != | < | > | <= | >=) Exp_Soma2]
   public static void Exp() {
      Exp_soma(); // exp soma1
      if (token.getToken().equals("==") || token.getToken().equals("!=") || token.getToken().equals("<")
            || token.getToken().equals(">") || token.getToken().equals("<=") || token.getToken().equals(">=")) {
         ct(token.getToken());
         Exp_soma(); // exp soma2
      }
   }

   // EXP SOMA
   // [+|-] Exp_Mult1 {(+ | - | or) Exp_Mult2 }
   public static void Exp_soma() {
      if (token.getToken().equals("+")) {
         ct("+");
      } else if (token.getToken().equals("-")) {
         ct("-");
      }
      Exp_mult(); // exp mult1
      while (token.getToken().equals("+") || token.getToken().equals("-") || token.getToken().equals("or")) {
         ct(token.getToken());
         Exp_mult(); // exp mult2
      }
   }

   // EXP MULT
   // Fator1{ (* | / | and | // | %) Fator2 }
   public static void Exp_mult() {
      Fator(); // fator 1
      while (token.getToken().equals("*") || token.getToken().equals("/") || token.getToken().equals("and")
            || token.getToken().equals("//") || token.getToken().equals("%")) {
         ct(token.getToken());
         Fator(); // fator2
      }
   }

   // FATOR
   // id["[" Exp "]"] | constante | not Fator1 | "(" Exp ")" | integer "(" Exp ")"
   // |
   // real "(" Exp ")"
   public static void Fator() {
      if (token.getToken().equals("id")) {
         ct("id");
         if (token.getToken().equals("[")) {
            ct("[");
            Exp();
            ct("]");
         }
      } else if (token.getToken().equals("not")) {
         ct("not");
         Fator();
      } else if (token.getToken().equals("(")) {
         ct("(");
         Exp();
         ct(")");
      } else if (token.getToken().equals("integer")) {
         ct("integer");
         ct("(");
         Exp();
         ct(")");
      } else if (token.getToken().equals("real")) {
         ct("real");
         ct("(");
         Exp();
         ct(")");
      } else {
         ct("const");
      }
   }

   public static void main(String[] args) {
      // popular a tabela de simbolos com palavras reservadas e simbolos
      for (String palavra : alfabeto_simbolos) {
         tabela.put(palavra.toLowerCase(), new Simbolo(palavra, palavra, "", "", 0, 0, ""));
         indexTabela++;
      }

      for (String palavra : alfabeto_reservadas) {
         tabela.put(palavra.toLowerCase(), new Simbolo(palavra, palavra, "", "", 0, 0, ""));
         indexTabela++;
      }

      // chamada inicial ao analisador sintatico
      S();

      // retorno do programa compilado com sucesso
      if (error == false)
         System.out.print((linhasArquivo.size() + 1) + " linhas compiladas.");
   }
}
