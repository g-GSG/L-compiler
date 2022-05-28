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
   private String endereco = "";
   private int tamanho = 0;
   private String valor = "";
   private String token;

   public Simbolo() {
      this.lexema = "";
      this.tipo = "";
      this.classe = "";
      this.endereco = "";
      this.tamanho = 0;
      this.valor = "";
      this.token = "";
   }

   public Simbolo(String token, String lexema, String tipo, String classe, String endereco, int tamanho, String valor) {
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

   public String getEndereco() {
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

   public void setEndereco(String endereco) {
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
   private static boolean error = false;
   private static int numLinhasArquivo;
   private static int endAtual;
   private static int rotulo = 0;
   private static int temp = 0;
   private static File arquivoSaida;
   private static String declarations = "section .data \nM: \nresb 10000h";
   private static String code = "section .text \nglobal _start\n_start:\n";


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
   private static String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

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

//Criação do arquivo para escrita dos comandos em assembly
   public static void createFile(){
      try {
         arquivoSaida = new File("arquivoSaida.asm");
         if (!arquivoSaida.createNewFile()) {
            new FileOutputStream(arquivoSaida).close();
         } 
       } catch (IOException e) {
       }
   }

   public static void writeDeclaration(String conteudo){
      declarations+= conteudo;
   }

   public static void writeCode(String conteudo){
      code += conteudo;
   }

   public static Simbolo searchTabela(String lexema) {
      return tabela.get(lexema); // retorna null se nao estiver presente
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

   public static boolean isCaractere(String str) {
      if (str.length() != 3) return false;

      if(str.charAt(0) == '\'' 
      && (str.charAt(1) >= 0 && str.charAt(1) <= 255) 
      && str.charAt(2) == '\'')
         return true;

      return false;
   }

   public static boolean isHexa(String str) {
      if (str.length() == 3) {
            if (searchAlfabeto(str.charAt(0), hex)) {
               if (searchAlfabeto(str.charAt(1), hex)) {
                  if (str.charAt(2) == 'h') {
                     return true;
                  }
               }
            }
         }
      return false;
   }

   public static String isNumero(String str) {
      if(str.contains(".")){
         try {
            Double.parseDouble(str);
            return "real";
         } catch (Exception e) {
            return "";
         }
      }else{
         try {
            Integer.parseInt(str);
            return "integer";
         } catch (NumberFormatException nfe) {
            return "";
         }
      }
   }

   public static void addTabela(Simbolo token) {
      tabela.put(token.getLexema(),token);
   }

   public static void removeTabela(Simbolo token) {
      tabela.remove(token.getLexema());
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

   /* ANALISADOR SINTATICO E SEMANTICO*/

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

   public static int NovoRotulo() {
      int aux = rotulo;
      rotulo += 1;
      return aux;
   }

   public static int NovoTemp(int bytes) {
      int aux = temp;
      temp += bytes;
      return aux;
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

   public static Simbolo addEndereco(Simbolo simbolo, int bytes, String instrucao){
      simbolo.setEndereco(endAtual + "h");
      endAtual+= bytes;
      writeDeclaration(instrucao+"\n");
      return simbolo;
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
    * VARIAVEIS {(int | char | boolean | real | string) id1[ = [-] constante] {,id2[ = [-] constante ] }* ;}
    */
    public static void Variaveis() {
      while (token.getToken().equals("integer") || token.getToken().equals("char") || token.getToken().equals("real")
            || token.getToken().equals("string") || token.getToken().equals("boolean")) {
         if (token.getToken().equals("integer")) {
            ct("integer");
            Simbolo id1;
            Simbolo id2;
            Simbolo exp;
            id1 = searchTabela(token.getLexema()); //Procura de o id ja foi declarado anteriormente
            if (id1 == null) {
               id1 = token;
               id1.setClasse("var");  //Iniciacao da classe e tipo do token, alem de fazer a traducao para os comandos de assembly
               id1.setTipo("integer");
               id1 = addEndereco(id1, 4, "resd 1");
               ct("id");
               addTabela(id1);

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")) {
                     ct("-");
                  }

                  exp = Exp();

                  if (exp.getTipo().equals("integer")) {
                     id1.setValor(exp.getLexema()); 
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            } else {
               System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
               System.exit(0);
            }

            while (token.getToken().equals(",")) {
               ct(",");
               id2 = searchTabela(token.getLexema());

               if (id2 == null) {
                  id2 = token;
                  id2.setClasse("var");
                  id2.setTipo("integer");
                  id2 = addEndereco(id2, 4, "resd 1");
                  ct("id");
                  addTabela(id2);
               } else {
                  System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
                  System.exit(0);
               }

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")) {
                     ct("-");
                  }
                  
                  exp = Exp();

                  if (exp.getTipo().equals("integer")) {
                     id2.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            }
            ct(";");
         } else if (token.getToken().equals("char")) {
            ct("char");
            Simbolo id1;
            Simbolo id2;
            Simbolo exp;
            id1 = searchTabela(token.getLexema());

            if (id1 == null) {
               id1 = token;
               id1.setClasse("var");
               id1.setTipo("char");
               id1 = addEndereco(id1, 1, "resb 1");
               ct("id");

               addTabela(id1);

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")){
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
                  
                  exp = Exp();

                  if (exp.getTipo().equals("char")) {
                     id1.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            } else {
               System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
               System.exit(0);
            }

            while (token.getToken().equals(",")) {
               ct(",");
               id2 = searchTabela(token.getLexema());

               if (id2 == null) {
                  id2 = token;
                  id2.setClasse("var");
                  id2.setTipo("char");
                  id2 = addEndereco(id2, 1, "resb 1");
                  ct("id");
                  addTabela(id2);
               } else {
                  System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
                  System.exit(0);
               }

               if (token.getToken().equals("=")) {
                  ct("=");
              
                  if (token.getToken().equals("-")){
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  } 

                  exp = Exp();

                  if (exp.getTipo().equals("char")) {
                     id2.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            }
            ct(";");
         } else if (token.getToken().equals("boolean")) {
            ct("boolean");
            Simbolo id1;
            Simbolo id2;
            Simbolo exp;
            id1 = searchTabela(token.getLexema());

            if (id1 == null) {
               id1 = token;
               id1.setClasse("var");
               id1.setTipo("boolean");
               id1 = addEndereco(id1, 4, "resd 1");
               ct("id");
               addTabela(id1);

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")){
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
                  exp = Exp();
                  if (exp.getTipo().equals("boolean")) {
                     id1.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            } else {
               System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
               System.exit(0);
            }

            while (token.getToken().equals(",")) {
               ct(",");
               id2 = searchTabela(token.getLexema());

               if (id2 == null) {
                  id2 = token;
                  id2.setClasse("var");
                  id2.setTipo("boolean");
                  id2 = addEndereco(id2, 4, "resd 1");
                  ct("id");
                  addTabela(id2);
               } else {
                  System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
                  System.exit(0);
               }

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")){
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
                  exp = Exp();

                  if (exp.getTipo().equals("boolean")) {
                     id2.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            }
            ct(";");
         } else if (token.getToken().equals("real")) {
            ct("real");
            Simbolo id1;
            Simbolo id2;
            Simbolo exp;

            id1 = searchTabela(token.getLexema());
            
            if (id1 == null) {
               id1 = token;
               id1.setClasse("var");
               id1.setTipo("real");
               id1 = addEndereco(id1, 4, "resd 1");
               ct("id");

               addTabela(id1);

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")) {
                     ct("-");
                  }

                  exp = Exp();

                  if (exp.getTipo().equals("real") || exp.getTipo().equals("integer")) {
                     id1.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            } else {
               System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
               System.exit(0);
            }

            while (token.getToken().equals(",")) {
               ct(",");
               id2 = searchTabela(token.getLexema());

               if (id2 == null) {
                  id2 = token;
                  id2.setClasse("var");
                  id2.setTipo("real");
                  id2 = addEndereco(id2, 4, "resd 1");
                  ct("id");
                  addTabela(id2);
               } else {
                  System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
                  System.exit(0);
               }

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")) {
                     ct("-");
                  }
                  exp = Exp(); 

                  if ((exp.getTipo().equals("real") || exp.getTipo().equals("integer"))) {
                     id2.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            }
            ct(";");
         } else if (token.getToken().equals("string")) {
            Simbolo id1;
            Simbolo id2;
            Simbolo exp;
            ct("string"); 
            id1 = searchTabela(token.getLexema()); 

            if (id1 == null) {
               id1 = token;
               id1.setClasse("var");
               id1.setTipo("string");
               id1 = addEndereco(id1, 100, "resb 100h");
               ct("id");
               addTabela(id1);

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")){
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
                  exp = Exp();

                  if (exp.getTipo().equals("string")) {
                     id1.setValor(exp.getLexema());
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
            } else {
               System.out.println(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
               System.exit(0);
            }

            while (token.getToken().equals(",")) {
               ct(",");
               id2 = searchTabela(token.getLexema());

               if (id2 == null) {
                  id2 = token;
                  id2.setClasse("var");
                  id2.setTipo("string");
                  id2 = addEndereco(id2, 100, "resb 100h");
                  ct("id");
                  addTabela(id2);
               } else {
                  System.out.print(linhas + "\nidentificador ja declarado [" + token.getLexema() + "].");
                  System.exit(0);
               }

               if (token.getToken().equals("=")) {
                  ct("=");
                  if (token.getToken().equals("-")){
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               }
               exp = Exp(); 

               if (exp.getTipo().equals("string")) {
                  id2.setValor(exp.getLexema());
               } else {
                  System.out.print(linhas + "\ntipos incompativeis.");
                  System.exit(0);
               }
            }
            ct(";");
         }
      }

   }
   
   
   /*
    * CONSTANTES {const id = [-] constante;}*
    */
   public static void Constantes() {
      Simbolo id;
      Simbolo exp;
      int sinal = 0;
      String saida;

      while (token.getToken().equals("const")) {
         ct("const");

         if (token.getToken().equals("id")) {
            id = searchTabela(token.getLexema());
            saida = token.getLexema();

            if (id == null) {
               id = token;
               id.setClasse("const");
               ct("id");
               ct("=");

               if (token.getToken().equals("-")) {
                  ct("-");
                  sinal = 1;
               }
               
               exp = Exp();

               if(sinal == 1 && !(exp.getTipo().equals("integer") || 
               exp.getTipo().equals("real"))){
                  System.out.print(linhas + "\ntipos incompativeis.");
                  System.exit(0);
               }

               if (exp.getToken().equals("const")) {
                  
                  if (exp.getTipo().equals("real") || exp.getTipo() == "integer" || exp.getTipo() == "boolean"){
                     id.setTipo(exp.getTipo());
                     id.setValor(exp.getValor());
                     id.setEndereco(exp.getEndereco());
                     id.setTamanho(4);
                     addTabela(id);
                  } else if ( exp.getTipo() == "char"  || exp.getTipo() == "string"){
                     id.setTipo(exp.getTipo());
                     id.setTipo(exp.getTipo());
                     id.setValor(exp.getValor());
                     id.setTamanho(exp.getValor().length());
                     id.setEndereco(exp.getEndereco());
                     addTabela(id);
                  } else {
                     System.out.print(linhas + "\ntipos incompativeis.");
                     System.exit(0);
                  }
               } 
            } else {
               System.out.println(linhas);
               System.out.print("identificador ja declarado [" + saida + "].");
               System.exit(0);
            }
         }
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
      Simbolo id;
      Simbolo aux = token;

      id = searchTabela(aux.getLexema());
      ct("id");
      
      if(id == null){
         System.out.println(linhas);
         System.out.print("identificador nao declarado [" + aux.getLexema() + "].");
         System.exit(0);
      }else if (id.getClasse().equals("var")) {
         if (token.getToken().equals("[")) {
            if(id.getTipo().equals("string")){
               ct("[");
               // testar os tipos e tamanhos
               Simbolo indice = Exp();
               temp = 0;
               if (!indice.getTipo().equals("integer")){
                  System.out.println(linhas);
                  System.out.print("tipos incompativeis.");
                  System.exit(0);
               }
               ct("]");

               ct("=");

               Simbolo exp = Exp(); 
               temp = 0;
               if (!exp.getTipo().equals("char")) {
                  System.out.println(linhas);
                  System.out.print("tipos incompativeis.");
                  System.exit(0);
               } else {
           
                  int nt = NovoTemp(4); // novo temp pra id.end
                  id.setEndereco(Integer.toString(nt));
                  String mov = ("mov rax, [qword M+" + Integer.parseInt(aux.getEndereco()) + "]\n"); // exp.end ->
                                                                                                           // reg 64
                                                                                                           // bits, reg1
                  String mov2 = ("mov rbx, [qword M+" + (id.getEndereco()) + "]\n");
                  String add = ("add rax,rbx\n"); // id.end + reg1
                  String mov3 = ("mov rcx, rbx\n");// carregar reg1 p/ reg2
                  String mov4 = ("mov [qword M+" + nt + "], rcx\n"); // reg2 p/ o novo temp
                  writeCode(mov + mov2 + add + mov3 + mov4); // acesso a posição no vetor
                  String mov5 = ("mov rax, [qword M+" + exp.getEndereco() + "]\n"); // pega o valor a ser atribuido a posicao
                  String mov6 = ("mov [qword M+" + nt + "], rax"); // colocar o valor na posicao (em novo temporario)
                  writeCode(mov5 + mov6);
               }
            } else {
               System.out.println(linhas);
               System.out.print("tipos incompativeis.");
               System.exit(0);
            }
         } else {
            ct("=");
            Simbolo aux2 = Exp();
            temp = 0;

            if ((id.getTipo().equals("integer") && id.getTipo().equals(aux2.getTipo()))
                  || id.getTipo().equals("char") && id.getTipo().equals(aux2.getTipo())
                  || id.getTipo().equals("boolean") && id.getTipo().equals(aux2.getTipo())
                  || id.getTipo().equals("real") && id.getTipo().equals(aux2.getTipo())
                  || id.getTipo().equals("string") && id.getTipo().equals(aux2.getTipo())
                  || id.getTipo().equals("real") && aux2.getTipo().equals("integer")) {
               id.setValor(aux2.getValor()); 
               if (!id.getTipo().equals("string")) {
                  // mover caractere a carectere
               } else {
                  String mov = ("mov eax, [qword M+" + aux2.getEndereco() + "]\n");
                  String mov2 = ("mov [qword M+" + id.getEndereco() + "], eax\n");
                  writeCode(mov + mov2);
               }
            } else {
               System.out.println(linhas);
               System.out.print("tipos incompativeis.");
               System.exit(0);
            }
         }
      } else if (id.getClasse().equals("const")) {
         System.out.println(linhas);
         System.out.print("classe de identificador incompativel [" + id.getLexema() + "].");

         System.exit(0);
      }
      ct(";");
      // atualiza na tabela
      removeTabela(searchTabela(aux.getLexema()));
      addTabela(id);
   }

   // Comando de repeticao
   // while Exp (Comandos | Lista_Comandos)
   public static void repeticao() {
      ct("while");
      Simbolo exp = Exp();
      temp = 0;
      if (!exp.getTipo().equals("boolean")){
         System.out.println(linhas);
         System.out.print("tipos incompativeis.");
         System.exit(0);
      }
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
      Simbolo exp = Exp();
      temp = 0;

      if(!exp.getTipo().equals("boolean")){
         System.out.print(linhas + "\ntipos incompativeis.");
         System.exit(0);
      }

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
      String saida = token.getLexema();
      Simbolo id = searchTabela(saida);
      ct("id");

      if(id == null){
         System.out.print(linhas + "\nidentificador nao declarado [" + saida + "].");
         System.exit(0);
      } else if(id.getClasse() == "const"){
         System.out.print(linhas + "\nclasse de identificador incompativel [" + saida + "].");
         System.exit(0);
      } else if(id.getTipo() == "boolean"){
         System.out.print(linhas + "\ntipos incompativeis.");
         System.exit(0);
      }
      // !!

      ct(")");
      ct(";");
   }

   // Comando de impressao
   // (write | writeln) "(" Exp {,Exp}* ")";
   public static void escrita() {
      boolean enterFinal = false;
      if (token.getToken().equals("write")) {
         ct("write");
      } else {
         ct("writeln");
         enterFinal = true;
      }
      ct("(");
      Simbolo exp = Exp();
      temp = 0;
      if (exp.getTipo().equals("boolean")) {
         System.out.println(linhas);
         System.out.print("tipos incompativeis.");
         System.exit(0);
      }
      if (enterFinal) exp.setLexema(exp.getLexema() + "\n");
      while (token.getToken().equals(",")) {
         ct(",");
         Simbolo exp2 = Exp();
         temp = 0;
         if (exp2.getTipo().equals("boolean")) {
            System.out.println(linhas);
            System.out.print("tipos incompativeis.");
            System.exit(0);
         }
         if (enterFinal) exp2.setLexema(exp2.getLexema() + "\n");
      }
      
      ct(")");
      ct(";");

      if (exp.getTipo().equals("integer")) {
         int novoTemp = NovoTemp(4);
         writeCode("mov eax, [qword M+" + exp.getEndereco() + "]\n");
         writeCode("mov rsi, [qword M+" + novoTemp + "]\n" );
         writeCode("mov rcx, 0 \n");
         writeCode("mov rdi, 0 \n");
         writeCode("cmp eax, 0 \n");
         writeCode("jge Rot0 \n");
         writeCode("mov bl, \'-\' \n");
         writeCode("mov [rsi], bl \n");
         writeCode("add rsi, 1 \n");
         writeCode("add rdi, 1 \n");
         writeCode("neg eax \n");
         writeCode("Rot0: \n");
         writeCode("mov ebx, 10 \n");
         writeCode("Rot1: \n");
         writeCode("add rcx, 1 \n");
         writeCode("cdq \n");
         writeCode("idiv ebx \n");
         writeCode("push dx \n");
         writeCode("cmp eax, 0 \n");
         writeCode("jne Rot1 \n");
         writeCode("add rdi,rcx \n");
         writeCode("Rot2: \n");
         writeCode("pop dx \n");
         writeCode("add dl, \'0\' \n");
         writeCode("mov [rsi], dl \n");
         writeCode("add rsi, 1 \n");
         writeCode("sub rcx, 1 \n");
         writeCode("cmp rcx, 0 \n");
         writeCode("jne Rot2 \n");
         // funcao de saida
      } else if (exp.getTipo().equals("real")) {
         int novoTemp = NovoTemp(4);
         writeCode("movss xmm0, [qword M+" + exp.getEndereco() + "]\n");
         writeCode("mov rsi, [qword M+" + novoTemp + "]\n");
         writeCode("mov rcx, 0 \n");
         writeCode("mov rdi, 6 \n");
         writeCode("mov rbx, 10 \n");
         writeCode("cvtsi2ss xmm2, rbx \n");
         writeCode("subss xmm1, xmm1 \n");
         writeCode("comiss xmm0, xmm1 \n");
         writeCode("jae Rot0 \n");
         writeCode("mov dl, \'-\' \n");
         writeCode("mov [rsi], dl \n");
         writeCode("mov rdx, -1 \n");
         writeCode("cvtsi2ss xmm1, rdx \n");
         writeCode("mulss xmm0, xmm1 \n");
         writeCode("add rsi, 1 \n");
         writeCode("Rot0: \n");
         writeCode("roundss xmm1, xmm0, 0b0011 \n");
         writeCode("subss xmm0, xmm1 \n");
         writeCode("cvtss2si rax, xmm1 \n");
         writeCode("Rot1: \n");
         writeCode("add rcx, 1 \n");
         writeCode("cdq \n");
         writeCode("idiv ebx \n");
         writeCode("push dx \n");
         writeCode("cmp eax, 0 \n");
         writeCode("jne Rot1 \n");
         writeCode("sub rdi, rcx \n");
         writeCode("Rot2: \n");
         writeCode("pop dx \n");
         writeCode("add dl, \'0\' \n");
         writeCode("mov [rsi], dl \n");
         writeCode("add rsi, 1 \n");
         writeCode("sub rcx, 1 \n");
         writeCode("cmp rcx, 0 \n");
         writeCode("jne Rot2 \n");
         writeCode("mov dl, \'.\' \n");
         writeCode("mov [rsi], dl \n");
         writeCode("add rsi, 1 \n");
         writeCode("Rot3: \n");
         writeCode("cmp rdi, 0 \n");
         writeCode("jle Rot4 \n");
         writeCode("mulss xmm0,xmm2 \n");
         writeCode("roundss xmm1,xmm0,0b0011 \n");
         writeCode("subss xmm0,xmm1 \n");
         writeCode("cvtss2si rdx, xmm1 \n");
         writeCode("add dl, \'0\' \n");
         writeCode("mov [rsi], dl \n");
         writeCode("add rsi, 1 \n");
         writeCode("sub rdi, 1 \n");
         writeCode("jmp Rot3 \n");
         writeCode("Rot4: \n");
         writeCode("mov dl, 0 \n");
         writeCode("mov [rsi], dl \n");
         writeCode("mov rdx, rsi \n");
         writeCode("mov rbx, [qword M+" + novoTemp + "\n");
         writeCode("sub rdx, rbx \n");
         writeCode("mov rsi, [qword M+" + novoTemp + "\n");
      } else {
         writeCode("mov rsi, [qword M+ " + exp.getEndereco() + "]\n" +
               "mov rdx, " + exp.getTamanho() +" \n " +
               "mov rax, 1 \n" +
               "mov rdi, 1 \n" +
               "syscall\n");
      }

   }

   public static boolean ValidarSeString(Simbolo exp1, Simbolo exp2) {
      return (exp1.getTipo().equals("char") && exp1.getTamanho() >= 0 && exp2.getTamanho() >= 0);
   }

   static boolean ValidarTiposOperadorExpInt(Simbolo exp1, Simbolo exp2) {
        return (!ValidarSeString(exp1, exp2) && !exp1.getTipo().equals("boolean") && !exp2.getTipo().equals("boolean"))
                && exp1.getTipo().equals(exp2.getTipo());
    }

   // EXP
   // Exp_Soma1 [(== | != | < | > | <= | >=) Exp_Soma2]
   public static Simbolo Exp() {
      Simbolo exp1;
      Simbolo exp2;
      Simbolo resultado = new Simbolo();
      int operador = -1;

      exp1 = Exp_soma(); // exp soma1
      if (token.getToken().equals("==") || token.getToken().equals("!=") || token.getToken().equals("<")
            || token.getToken().equals(">") || token.getToken().equals("<=") || token.getToken().equals(">=")) {
         if (token.getToken().equals("==")) {
                ct("==");
                operador = 0;
            } else if (token.getToken().equals("!=")) {
                ct("!=");
                operador = 1;
            } else if (token.getToken().equals("<")) {
               ct("<");
               operador = 2;
           } else if (token.getToken().equals(">")) {
                ct(">");
                operador = 3;
            } else if (token.getToken().equals("<=")) {
                ct("<=");
                operador = 4;
            } else if (token.getToken().equals(">=")) {
                ct(">=");
                operador = 5;
            }

            exp2 = Exp_soma();
            boolean inteiroReal = (exp1.getTipo().equals("integer") || exp1.getTipo().equals("real")) && (exp2.getTipo().equals("real") || exp2.getTipo().equals("integer"));
            boolean chars = exp1.getTipo().equals("char") && exp2.getTipo().equals("char");
            boolean strings = exp1.getTipo().equals("string") && exp2.getTipo().equals("string");

            if (operador == 0 && (inteiroReal || chars || strings)) {
               resultado.setTipo("boolean");
            } else if (operador == 1 && (inteiroReal || chars)) {
               resultado.setTipo("boolean");
            } else if (operador == 2 && (inteiroReal || chars)) {
               resultado.setTipo("boolean");
            } else if (operador == 3 && (inteiroReal || chars)) {
               resultado.setTipo("boolean");
            } else if (operador == 4 && (inteiroReal || chars)) {
               resultado.setTipo("boolean");
            } else if (operador == 5 && (inteiroReal || chars)) {
               resultado.setTipo("boolean");
            } else {
                System.out.println(linhas);
                System.out.print("tipos incompativeis.");
                System.exit(0);
            }
      } else {
         return exp1;
      }
      return resultado;
   }

   // EXP SOMA
   // [+|-] Exp_Mult1 {(+ | - | or) Exp_Mult2 }
   public static Simbolo Exp_soma() {
      Simbolo exp_mult1;
      Simbolo exp_mult2;
      int operador = -1;
      int sinal = 0;
      
      if (token.getToken().equals("+")) {
         ct("+");
         sinal = 1;
      } else if (token.getToken().equals("-")) {
         ct("-");
         sinal = 2;
      }
      exp_mult1 = Exp_mult(); // exp mult1

      if(exp_mult1.getTipo().equals("boolean") && sinal != 0){
         System.out.println(linhas);
         System.out.print("tipos incompativeis.");
         System.exit(0);
      }

      if(sinal == 2) {
         writeCode("mov eax, [qword M+" + exp_mult1.getEndereco()+"]");
         writeCode("neg eax");
         writeCode("mov [qword M+" + exp_mult1.getEndereco()+ "], eax");
      }

      if (token.getToken().equals("+") || token.getToken().equals("-") || token.getToken().equals("or")) {
         if (token.getToken().equals("+")) {
            ct("+");
            operador = 0;
         } else if (token.getToken().equals("-")) {
            ct("-");
            operador = 1;
         } else if (token.getToken().equals("or")) {
            ct("or");
            operador = 2;
         }

         exp_mult2 = Exp_mult();

         boolean inteiros = exp_mult1.getTipo().equals("integer") && exp_mult2.getTipo().equals("integer");
         boolean inteiroReal = (exp_mult1.getTipo().equals("integer") && exp_mult2.getTipo().equals("real")) || (exp_mult1.getTipo().equals("real") && exp_mult2.getTipo().equals("integer"));
         boolean reais = exp_mult1.getTipo().equals("real") && exp_mult2.getTipo().equals("real");

         if ((operador == 0 || operador == 1) && inteiros) {
            exp_mult2.setTipo("integer");
            writeCode("mov eax, [qword M+" + exp_mult1.getEndereco()+"]");
            writeCode("mov ebx, [qword M+" + exp_mult2.getEndereco()+"]");
            if (operador == 0) writeCode("add eax ebx");
            if (operador == 1) writeCode("sub eax ebx");
            writeCode("mov [qword M+" + exp_mult2.getEndereco()+ "], eax");
         } else if ((operador == 0 || operador == 1) && (inteiroReal || reais)) {
            exp_mult2.setTipo("real");
            writeCode("mov eax, [qword M+" + exp_mult1.getEndereco()+"]");
            writeCode("mov ebx, [qword M+" + exp_mult2.getEndereco()+"]");
            if (operador == 0) writeCode("addss eax ebx");
            if (operador == 1) writeCode("subss eax ebx");
            writeCode("mov [qword M+" + exp_mult2.getEndereco()+ "], eax");
         } else if (operador == 2 && exp_mult1.getTipo().equals("boolean") && exp_mult2.getTipo().equals("boolean")) {
            exp_mult2.setTipo("boolean");
            writeCode("mov eax, [qword M+" + exp_mult1.getEndereco()+"]");
            writeCode("mov ebx, [qword M+" + exp_mult2.getEndereco()+"]");
            writeCode("cmp eax, 0");
            writeCode("cmp ebx, 0");
            writeCode("mov eax, [qword M+" + exp_mult2.getEndereco()+"]");
            writeCode("cmp eax, 1");
            writeCode("cmp ebx, 0");
            writeCode("mov eax, [qword M+" + exp_mult2.getEndereco()+"]");
            writeCode("cmp eax, 1");
            writeCode("cmp ebx, 1");
            writeCode("mov eax, [qword M+" + exp_mult2.getEndereco()+"]");
            writeCode("cmp eax, 0");
            writeCode("cmp ebx, 1");
            writeCode("mov ebx, [qword M+" + exp_mult2.getEndereco()+"]");
            
            writeCode("mov [qword M+" + exp_mult2.getEndereco()+ "], eax");
         } else {
               System.out.println(linhas);
               System.out.print("tipos incompativeis.");
               System.exit(0);
         }
      } else {
         return exp_mult1;
      }
      return exp_mult2;
   }

   // EXP MULT
   // Fator1{ (* | / | and | // | %) Fator2 }
   public static Simbolo Exp_mult() {
      Simbolo Exp_Multi = new Simbolo();
      Simbolo fator1 = Fator();
      Simbolo fator2;
      int operador = -1;

      if (token.getToken().equals("*") || token.getToken().equals("/") || token.getToken().equals("and")
            || token.getToken().equals("//") || token.getToken().equals("%")) {
         if (token.getToken().equals("*")) {
            ct("*");
            operador = 0;
         } else if (token.getToken().equals("/")) {
            ct("/");
            operador = 1;
         } else if (token.getToken().equals("and")) {
            ct("and");
            operador = 2;
         } else if (token.getToken().equals("%")) {
            ct("%");
            operador = 3;
         } else if (token.getToken().equals("//")) {
            ct("//");
            operador = 4;
         }

         fator2 = Fator(); // fator2
         boolean inteiros = fator1.getTipo().equals("integer") && fator2.getTipo().equals("integer");
         boolean inteiroReal = ((fator1.getTipo().equals("integer") && fator2.getTipo().equals("real")) || (fator1.getTipo().equals("real") && fator2.getTipo().equals("integer")));
         boolean reais = fator1.getTipo().equals("real") && fator2.getTipo().equals("real");
         if (operador == 0 && inteiros) {
            Exp_Multi.setTipo("integer");
            // multiplicacao inteiros
         } else if (operador == 1 && (reais || inteiroReal)) {
            Exp_Multi.setTipo("real");
            // divisão entre reais ou inteiro e real
         } else if (operador == 2 && fator1.getTipo().equals("boolean") && fator2.getTipo().equals("boolean")) {
            Exp_Multi.setTipo("boolean");
            // and
         } else if (operador == 3 && inteiros) {
            Exp_Multi.setTipo("integer");
            // mod entre inteiros
         } else if (operador == 4 && inteiros) {
            Exp_Multi.setTipo("integer");
            // divisao de inteiross
         } else if (operador == 0 && (reais || inteiroReal)) {
            Exp_Multi.setTipo("real");
            // multiplicacao entre reais ou inteiro e real
         } else {
            System.out.println(linhas);
            System.out.print("tipos incompativeis.");
            System.exit(0);
         }
      } else {
         Exp_Multi = fator1;
         return Exp_Multi;
      }

      return Exp_Multi;
   }

   // FATOR
   // id["[" Exp "]"] | constante | not Fator1 | "(" Exp ")" | integer "(" Exp ")"
   // |
   // real "(" Exp ")"
   public static Simbolo Fator() {
      Simbolo resultado = new Simbolo();
      Simbolo aux;
      if (token.getToken().equals("id")) {
         resultado = searchTabela(token.getLexema());
         String saida = token.getLexema();

         if (!(resultado == null)) { // Verifica se o id foi declarado
            ct("id");
            
            if (token.getToken().equals("[")) {
               ct("[");

               if(!(resultado.getTipo().equals("string"))){
                  System.out.println(linhas);
                  System.out.print("tipos incompativeis.");
                  System.exit(0);
               }

               aux = Exp();

               if(!(aux.getTipo().equals("integer"))){
                  System.out.println(linhas);
                  System.out.print("tipos incompativeis.");
                  System.exit(0);
               }else{
                  resultado.setTipo("char");
               }

               ct("]");
               int nt = NovoTemp(4); // novo temp pra f.end
               resultado.setEndereco(Integer.toString(nt));
               String mov = ("mov rax, [qword M+" + (Integer.parseInt(aux.getEndereco())* 4)+"]\n"); // exp.end -> reg 64 bits, reg1 // reg1 * n de bytes q o tipo do vetor ocupa
               String mov2 = ("mov rbx, [qword M+" + (resultado.getEndereco()) + "]\n");
               String add = ("add rax,rbx\n"); // id.end + reg1
               String mov3 = ("mov rcx, rbx\n");// carregar reg1 p/ reg2
               String mov4 = ("mov [qword M+" + nt + "], rcx\n"); // reg2 p/ o novo temp
               writeCode(mov + mov2 + add + mov3 + mov4);
            }

         } else {
            System.out.println(linhas);
            System.out.print("identificador nao declarado [" + saida + "].");
            System.exit(0);
         }
      } else if (token.getToken().equals("not")) {
         ct("not");

         int novoTemp = NovoTemp(4);

         Simbolo fator = Fator();
         Simbolo id = searchTabela(fator.getLexema());

         if (fator.getToken().equals("id")) {
            if (!id.getClasse().equals("")) {
               if (id.getTipo().equals("boolean")) {
                  resultado.setTipo("boolean");
                  resultado.setEndereco(Integer.toString(novoTemp)); 
                  String mov = "mov eax, [qword M+" + id.getEndereco() + "]\n";
                  String neg = "neg eax\n";
                  String add = "add eax,1\n";
                  String mov1 = "mov[qword M+" + resultado.getEndereco() + "], eax\n";
                  writeCode(mov + neg + add + mov1);
               } else {
                  System.out.println(linhas);
                  System.out.print("tipos incompativeis.");
                  System.exit(0);
               }
            } else {
               System.out.println(linhas);
               System.out.print("identificador nao declarado [" + fator.getLexema() + "].");
               System.exit(0);
            }
         } else if (fator.getToken().equals("const")) {
            if (id.getTipo().equals("boolean")) {
               resultado.setTipo("boolean");
               resultado.setEndereco(Integer.toString(novoTemp)); 
                  String mov = "mov eax, [qword M+" + id.getEndereco() + "]\n";;
                  String neg = "neg eax\n";
                  String add = "add eax,1\n";
                  String mov1 = "mov[qword M+" + resultado.getEndereco() + "], eax\n";
                  writeCode(mov + "\n" + neg + add + mov1); 
            } else {
               System.out.println(linhas);
               System.out.print("tipos incompativeis.");
               System.exit(0);
            }
         } else {
            System.out.println(linhas);
            System.out.print("classe de identificador incompativel [" + fator.getLexema() + "].");
            System.exit(0);
         }
      } else if (token.getToken().equals("(")) {
         ct("(");
         resultado = Exp();
         ct(")");
      } else if (token.getToken().equals("integer")) {
         ct("integer");
         ct("(");
         Simbolo exp = Exp();
         
         if(!(exp.getTipo().equals("integer")) && !(exp.getTipo().equals("real")) ){
            System.out.println(linhas);
            System.out.print("tipos incompativeis.");
            System.exit(0);
         }else{
            resultado.setTipo("integer");
         }
         ct(")");

      } else if (token.getToken().equals("real")) {
         ct("real");
         ct("(");
         Simbolo exp = Exp();         
         if(!(exp.getTipo().equals("real")) && !(exp.getTipo().equals("integer"))) {
            System.out.println(linhas);
            System.out.print("tipos incompativeis.");
            System.exit(0);
         } else {
            resultado.setTipo("real");
         }
         ct(")");
      } else {
         if (isNumero(token.getLexema()).equals("integer")) {
            token.setTipo("integer");
            token.setValor(token.getLexema());
            token.setEndereco(Integer.toString(NovoTemp(4)));
            String mov = "mov eax," + token.getValor() + "\n";
            String mov1 = "mov [qword M +" + token.getEndereco() + "],eax\n";
            writeCode(mov+mov1);
         } else if(isNumero(token.getLexema()).equals("real")){
            token.setTipo("real");
            token.setValor(token.getLexema());
            token.setTamanho(4);
            token = addEndereco(token, 4, ("dd " + token.getValor()));
         } else if (isCaractere(token.getLexema())) {
            token.setTipo("char");
            token.setValor(token.getLexema());
            String auxChar = token.getValor();
            auxChar = auxChar.replaceAll("\'", "");
            token.setValor(auxChar);
            token.setEndereco(Integer.toString(NovoTemp(4)));
            String mov = "mov eax, '" + token.getValor() + "'\n";
            String mov1 = "mov [qword M +" + token.getEndereco() + "],eax\n";
            writeCode(mov+mov1);
         } else if (isHexa(token.getLexema())) {
            token.setTipo("char");
            token.setValor(token.getLexema());
            String auxChar = token.getValor();
            auxChar = auxChar.replaceAll("\'", "");
            token.setValor(auxChar);
            token.setEndereco(Integer.toString(NovoTemp(4)));
            String mov = "mov eax," + token.getValor() + "\n";
            String mov1 = "mov [qword M +" + token.getEndereco() + "],eax\n";
            writeCode(mov+mov1);
         } else if (token.getLexema().equals("TRUE") || token.getLexema().equals("FALSE")) {
            token.setTipo("boolean");
            token.setValor(token.getLexema());
            String auxBoolean = token.getValor();
            if(auxBoolean ==  "TRUE"){
               token.setValor("1");
            }else{
               token.setValor("0");
            }
            token.setEndereco(Integer.toString(NovoTemp(4)));
            String mov = "mov eax," + token.getValor() + "\n";
            String mov1 = "mov [qword M +" + token.getEndereco() + "],eax\n";
            writeCode(mov+mov1);
         } else if (token.getLexema().length() > 1) {
            token.setTipo("string");
            token.setValor(token.getLexema());
            String auxString = token.getValor();
            auxString = auxString.replaceAll("\"", "");
            token.setValor(auxString);
            token.setTamanho(auxString.length()+1);
            token = addEndereco(token, token.getTamanho(), ("db \"" + token.getValor() + "\",0"));
         } else {
            System.out.println(linhas);
            System.out.print("tipos incompativeis.");
            System.exit(0);
         }

   
         aux = token;
         ct("const");

         return aux;
      }
      return resultado; 
   }

   public static void main(String[] args) throws IOException{
      // popular a tabela de simbolos com palavras reservadas e simbolos
      for (String palavra : alfabeto_simbolos) {
         tabela.put(palavra, new Simbolo(palavra, palavra, "", "", "", 0, ""));
      }

      for (String palavra : alfabeto_reservadas) {
         tabela.put(palavra, new Simbolo(palavra, palavra, "", "", "", 0, ""));
      }

      String finalProgram = "mov rax, 60 \n mov rdi, 0 \nsyscall";

      createFile();
      // chamada inicial ao analisador sintatico e semantico
      S();
      try{
      FileWriter output = new FileWriter(arquivoSaida.getAbsolutePath());
      output.write(declarations + code);
      output.close();
   
      // retorno do programa compilado com sucesso
      if (error == false)
         System.out.print((linhasArquivo.size() + 1) + " linhas compiladas.");
         output.write(declarations + code + finalProgram);
      }catch (IOException e){
      }
   }
}