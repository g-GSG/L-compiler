import java.util.*;
import java.io.*;

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
   private static String file = readFile();
   static BufferedReader arquivo;
   private static int linhas = 1;
   private static int index = 0;
   private static int index1 = 0;
   private static boolean error = false;

   // linguagem
   private static String[] alfabeto_simbolos = { "_", ".", ",", ";", ":", "(", ")", "[", "]", "+", "-", "'", "\"", "@",
         "&", "%", "!", "?", ">", "<", "=", "*", "/" };
   private static String[] alfabeto_reservadas = { "const", "integer", "char", "while", "if", "real", "else", "and",
         "or", "not", "begin", "end", "readln", "string", "write", "writeln", "TRUE", "FALSE", "boolean", "==", "!=",
         ">=", "<=", "//" };
   private static String[] alfabeto_caracteres = { " ", "_", ".", ",", ";", ":", "(", ")", "[", "]", "{", "}", "+", "-",
         "\"", "'", "/", "\\", "@", "&", "%", "!", "?", ">", "<", "=", "*" };
   private static String[] letras_hexa = { "A", "B", "C", "D", "E", "F" };

   public static HashMap<String, Simbolo> tabela = new HashMap<>();

   public static boolean isLetter(char c) {
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
         return true;
      else
         return false;
   }

   public static String readFile() {
      String result = "";
      String temp = "";

      try {
         linhasArquivo = new ArrayList<String>();
         Scanner sc = new Scanner(System.in);
         while (sc.hasNext()) {
            temp = sc.nextLine();
            linhasArquivo.add(temp);
            result = result + temp + "\n";
            linhas++;
         }

         // System.out.print(result);

         sc.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return result;
   }

   public static Simbolo searchTabela(String tok) {
      return tabela.get(tok.toLowerCase()); // retorna null se não estiver presente
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
      return file.charAt(i);
   }

   /* ANALISADOR LÉXICO */
   public static Simbolo analisadorLexico() {
      Simbolo simbolo = new Simbolo();
      int estado = 0;
      String lex = "";

      while (estado != -1) {

         if (index >= file.length() && error == false) {
            error = true;
            estado = -1;
            System.out.print((linhas) + "\nfim de arquivo nao esperado.");
            break;
         }

         char c = file.charAt(index);
         // System.out.println("Case 0 c: " + c + "|| index: " + index);
         if (searchAlfabeto(c, alfabeto_caracteres) == false && isLetter(c) == false && Character.isDigit(c) == false
               && c != '\n' && c != '\r') {
            error = true;
            System.out.print((linhas) + "\ncaractere invalido.");
            index = file.length();
         } else {
            switch (estado) {
               case 0:
                  // System.out.println("Case 0 c: " + c + "index: " + index);
                  // Constante Hexadecimal ou Identificador
                  if (searchAlfabeto(c, letras_hexa)) {
                     lex += c;
                     index++;
                     estado = 11;
                     // Identificador
                  } else if (c == '_' || isLetter(c)) {
                     lex += c;
                     index++;
                     simbolo.setToken("id");
                     estado = 10;
                     // Numero real ou numero inteiro ou constante hexadecimal
                  } else if (Character.isDigit(c)) {
                     // System.out.println("Case 0 digit case c: " + c);
                     lex += c;
                     index++;
                     estado = 21;
                     // Comparadores de grandeza (maior,menor,igual, maiorigual,menorigual) ou
                     // atribuicao
                  } else if (c == '=' || c == '<' || c == '>') {
                     lex += c;
                     index++;
                     estado = 4;
                     // System.out.println(c + " compilado");
                     // Diferente
                  } else if (c == '!') {
                     lex += c;
                     index++;
                     estado = 5;
                     // System.out.println(c + " compilado");

                     // Divisao ou quociente da divisão de 2 inteiros
                  } else if (c == '/') {
                     lex += c;
                     index++;
                     estado = 6;
                     // Caracter
                  } else if (c == '\'') {
                     lex += c;
                     index++;
                     estado = 7;
                     // System.out.println(c + " compilado");

                     // String
                  } else if (c == '"') {
                     lex += c;
                     index++;
                     estado = 9;
                  } else if (c == ',' || c == '-' || c == '+' || c == '*' || c == ';' || c == '%' || c == '('
                        || c == ')' || c == '[' || c == ']') {
                     lex += c;
                     simbolo.setToken(lex);
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                     // Ponto flutuante
                  } else if (c == '.') {
                     // System.out.println("ponto flutuante, c:" + c);
                     lex += c;
                     index++;
                     estado = 19;
                     // Comentario
                  } else if (c == '{') {
                     lex += c;
                     index++;
                     estado = 1;
                     // System.out.println(c + " compilado");
                  } else if (c == ' ') {
                     estado = 0;
                     index++;
                  } else if (c == '\n' || c == '\r') {
                     estado = 0;
                     linhas++;
                     index++;
                     if (index >= file.length()) {
                        estado = -1;
                     }
                     // System.out.println("quebra de linha");
                  } else {
                     if (lex.length() == 0) {
                        lex += c;
                     }

                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     error = true;
                     estado = -1;
                     break;
                  }
                  break;

               case 1:
                  if (c == '*') {
                     estado = 2;
                     index++;
                     lex += c;
                     // System.out.println(c + " compilado");
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     estado = -1;
                     break;
                  }
                  break;

               case 2:
                  /*
                   * aparentemente eh coisa com comentario que nao eh fechado,
                   * vamo ter que pensar nas possibilidades de alguem errar a sintaxe de um
                   * comentario
                   */
                  // System.out.println("case 2 c:" + c);
                  if (c == '*') {
                     estado = 3;
                     index++;
                     lex += c;
                     // System.out.println(c + " compilado");
                  } else if (isLetter(c) || Character.isDigit(c) || searchAlfabeto(c, alfabeto_caracteres) == true
                        || c == ' ' || c == '\n' || c == '\r') {
                     index++;
                     if (c == '\n' || c == '\r')
                        linhas++;
                     lex += c;
                     estado = 2;
                     // System.out.println(c + " compilado");
                     // System.out.println("lexema: " + lex);
                  } else {
                     // System.out.println(c + "teste");
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     estado = -1;

                  }
                  break;

               case 3:
                  if (c == '*') {
                     estado = 3;
                     index++;
                     lex += c;
                     // System.out.println(c + " compilado");
                  } else if (c != '*' && c != '}') {
                     estado = 2;
                     index++;
                     lex += c;
                     if (c == '\n' || c == '\r')
                        linhas++;
                     // System.out.println(c + " compilado");
                  } else if (c == '}') {
                     estado = 0;
                     index++;
                     lex = "";
                     // System.out.println(c + " compilado");
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                  }
                  break;

               // >= <= ==
               case 4:
                  // System.out.println("case 4 c: " + c);
                  if (c == '=') {
                     lex += c;
                     simbolo.setToken(lex);
                     index++;
                     // System.out.println(c + " compilado");
                     estado = 30;
                  } else {
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
                     // System.out.println(c + " compilado");
                  } else if (c != '=') {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
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

                  if ((searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c) || isLetter(c))
                        && c != ' ') {
                     lex += c;
                     index++;
                     estado = 8;
                     // System.out.println(c + " compilado");
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     lex = "";
                     estado = -1;
                  }
                  break;

               case 8:
                  if (c == '\'') {
                     lex += c;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                     // System.out.println(c + " compilado");
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     lex = "";
                     estado = -1;
                  }
                  break;

               // string
               case 9:
                  if (c != '"'
                        && (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c) || isLetter(c))) {
                     lex += c;
                     index++;
                     estado = 9;
                  } else if (c == '"') {
                     lex += c;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                     index++;
                     estado = 30;
                  } else {
                     error = true;
                     estado = -1;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     lex = "";
                  }
                  break;

               // identificador
               case 10:
                  if ((c == '_' || isLetter(c) || Character.isDigit(c))) {
                     lex += c;
                     index++;
                     estado = 10;
                  } else if (c != '_' && isLetter(c) == false && Character.isDigit(c) == false) {
                     // System.out.println(lex + " -> lex");
                     if (searchTabela(lex) == null) {
                        simbolo.setToken("id");
                        simbolo.setLexema(lex);
                     } else {
                        if (lex.equals("TRUE") || lex.equals("FALSE")) {
                           simbolo.setToken("const");
                           simbolo.setLexema(lex);
                        } else {
                           simbolo = searchTabela(lex);
                           simbolo.setLexema(lex);
                        }

                     }

                     // System.out.println(simbolo.getToken() + " -> simbolo");

                     estado = 30;
                  }
                  break;

               case 11:
                  if (searchAlfabeto(c, letras_hexa) || Character.isDigit(c)) {
                     lex += c;
                     index++;
                     estado = 16;
                  } else if (isLetter(c) || c == '_') {
                     lex += c;
                     index++;
                     estado = 10;
                  } else {
                     estado = 30;
                     simbolo.setToken("id");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 16:
                  if (c == 'h') {
                     lex += c;
                     index++;
                     estado = 18;
                  } else if ((c == '_' || isLetter(c) || Character.isDigit(c))) {
                     lex += c;
                     index++;
                     estado = 10;
                  } else {
                     estado = 30;
                     simbolo.setToken("id");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 18:
                  if ((c == '_' || isLetter(c) || Character.isDigit(c))) {
                     lex += c;
                     index++;
                     estado = 10;
                  } else {
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 19:
                  if (Character.isDigit(c)) {
                     lex += c;
                     index++;
                     estado = 20;
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     lex = "";
                     estado = -1;
                  }
                  break;

               case 20:
                  if (Character.isDigit(c)) {
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
                  // System.out.println("Case 21 c: " + c);
                  if (c == '.') {
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
                  } else {
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  }
                  break;

               case 22:
                  if (c == '.' || Character.isDigit(c)) {
                     lex += c;
                     index++;
                     estado = 20;
                  } else if (c == 'h') {
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
                  if (c == 'h') {
                     lex += c;
                     index++;
                     estado = 30;
                     simbolo.setToken("const");
                     simbolo.setLexema(lex);
                  } else {
                     error = true;
                     System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                     index = file.length();
                     lex = "";
                     estado = -1;
                  }
                  break;

               case 30:
                  // System.out.println("Token lido no estado final:" + lex);
                  lex = "";
                  estado = -1;
                  // System.out.println(lex);
                  break;
            }
         }
      }

      return simbolo;

   }

   /* ANALISADOR SINTATICO */

   public static void ct(String token_esperado) {
      // System.out.println("CASA TOKEN: " + token.getToken());

      // System.out.println(token.getToken().equals(token_esperado));
      if (token.getToken().equals(token_esperado)) {
         // System.out.println("antes do AL");
         token = analisadorLexico();
         /*
          * System.out.println("depois do AL");
          * System.out.println(token.getToken());
          * if (error == true) {
          * System.out.print((linhas) + "\nfim de arquivo nao esperado.");
          * }
          */
      } else if (token.getToken().equals("") && error == false) {
         // triar um erro de fim de arquivo aqui
         System.out.print((linhas) + "\nfim de arquivo nao esperado.");
         error = true;
         System.exit(0);
      } else {
         if (error == false) {
            error = true;
            System.out.println(linhas);
            System.out.println("token nao esperado [" + token.getLexema() + "].");
            System.exit(0);
         }
      }

      // System.out.println("saindo do ct");
   }

   /*
    * Gramatica S-> {Declaracao}* {Comandos}* EoF
    */
   public static void S() {
      token = analisadorLexico();
      // System.out.println("token lido:" + token.getToken());
      try {
         while (token.getToken().equals("integer") || token.getToken().equals("const")
               || token.getToken().equals("char") || token.getToken().equals("real")
               || token.getToken().equals("string") || token.getToken().equals("boolean") || token.getToken()
                     .equals("id")
               || token.getToken().equals("while") || token.getToken().equals("if")
               || token.getToken().equals(";") || token.getToken().equals("readln")
               || token.getToken().equals("writeln") || token.getToken().equals("write")) {
            while (token.getToken().equals("integer") || token.getToken().equals("const")
                  || token.getToken().equals("char") || token.getToken().equals("real")
                  || token.getToken().equals("string") || token.getToken().equals("boolean")) {
               // System.out.println("while declaracao");
               Declaracao();
            }
            while (token.getToken().equals("id") || token.getToken().equals("while") || token.getToken().equals("if")
                  || token.getToken().equals(";") || token.getToken().equals("readln")
                  || token.getToken().equals("writeln") || token.getToken().equals("write")) {
               // System.out.println("while comandos");
               Comandos();
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
         // System.out.println("Variaveis " + token.getToken());
         Variaveis();
      } else if (token.getToken().equals("const")) {
         Constantes();
         // System.out.println("Constantes");
      }
   }

   /*
    * VARIAVEIS {(int | char | boolean | real | string) id1[ = [-] constante]
    * {,id2[ = [-] constante ] }* ;}
    */
   public static void Variaveis() {
      while (token.getToken().equals("integer") || token.getToken().equals("char") || token.getToken().equals("real")
            || token.getToken().equals("string") || token.getToken().equals("boolean")) {
         // System.out.println("Variaveis " + token.getToken());
         ct(token.getToken());
         ct("id");
         if (token.getToken().equals("=")) {
            ct("=");
            if (token.getToken().equals("-")) {
               ct("-");
            }
            ct("const");
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
         // System.out.println("entrou atribuicao");
         atribuicao();
         // System.out.println("saiu da atribuicao 2");
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
         // System.out.println("saiu da expressao chamada pela atribuicao");
      } else if (token.getToken().equals("[")) {
         ct("[");
         Exp();
         ct("]");
         ct("=");
         Exp();
         // System.out.println("saiu da expressao chamada pela atribuicao");
      }
      // System.out.println("antes do ct ;");
      ct(";");
      // System.out.println("saiu da atribuicao");
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
         // System.out.println("esperando o end");
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
      // System.out.println("antes do Exp_soma");
      Exp_soma(); // exp soma1
      // System.out.println("depois do Exp_soma");
      if (token.getToken().equals("==") || token.getToken().equals("!=") || token.getToken().equals("<")
            || token.getToken().equals(">") || token.getToken().equals("<=") || token.getToken().equals(">=")) {
         ct(token.getToken());
         Exp_soma(); // exp soma 2
      }
      // System.out.println("depois do if no Exp_soma");
   }

   // EXP SOMA
   // [+|-] Exp_Mult1 {(+ | - | or) Exp_Mult2 }
   public static void Exp_soma() {
      if (token.getToken().equals("+")) {
         // System.out.println("entrou no +");
         ct("+");
      } else if (token.getToken().equals("-")) {
         ct("-");
      }
      // System.out.println("antes do Exp_mult");
      Exp_mult(); // exp mult1
      // System.out.println("depois do Exp_mult");
      while (token.getToken().equals("+") || token.getToken().equals("-") || token.getToken().equals("or")) {
         ct(token.getToken());
         Exp_mult(); // exp mult 2
      }
      // System.out.println("depois do while do Exp_soma");
   }

   // EXP MULT
   // Fator1{ (* | / | and | // | %) Fator2 }
   public static void Exp_mult() {
      // System.out.println("antes do Fator");
      Fator(); // fator 1
      // System.out.println("depois do Fator");
      while (token.getToken().equals("*") || token.getToken().equals("/") || token.getToken().equals("and")
            || token.getToken().equals("//") || token.getToken().equals("%")) {
         ct(token.getToken());
         Fator(); // fator2
      }
      // System.out.println("depois do while do Exp_mult");

   }

   // FATOR
   // id["[" Exp "]"] | constante | not Fator1 | "(" Exp ")" | integer "(" Exp ")"
   // |
   // real "(" Exp ")"
   public static void Fator() {
      // System.out.println("entrou no fator");
      // tem de retornar um simbolo, não sei se é no semantico ou agora no sintatico
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
         // System.out.println("entrou no constante do fator");
      }
   }

   public static void main(String[] args) {

      // popular a tabela de símbolos com palavras reservadas e simbolos
      for (String palavra : alfabeto_simbolos) {
         tabela.put(palavra.toLowerCase(), new Simbolo(palavra, palavra, "", "", 0, 0, ""));
         index1++;
      }

      for (String palavra : alfabeto_reservadas) {
         tabela.put(palavra.toLowerCase(), new Simbolo(palavra, palavra, "", "", 0, 0, ""));
         index1++;
      }

      // inicia o analisador lexico
      // token = analisadorLexico();

      S();

      if (error == false)
         System.out.print((linhasArquivo.size()) + " linhas compiladas.");
   }
}
