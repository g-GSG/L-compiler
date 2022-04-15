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
   private static int linhas = 0;
   private static int index = 0;
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
      int i = 0;

      while (i < file.length()) {
         char c = file.charAt(i);
         if (searchAlfabeto(c, alfabeto_caracteres) == false && isLetter(c) == false && Character.isDigit(c) == false && c != '\n') {
            error = true;
            System.out.print((linhas) + "\ncaractere invalido.");
            i = file.length();
         } else {
            switch (estado) {
            case 0:
              // System.out.println("Case 0 c: " + c);
               // Constante Hexadecimal ou Identificador
               if (searchAlfabeto(c, letras_hexa)) {
                  lex += c;
                  i++;
                  estado = 11;
               // Identificador
               } else if (c == '_' || isLetter(c)) {
                  lex += c;
                  i++;
                  simbolo.setToken("id");
                  estado = 10;
               // Numero real ou numero inteiro ou constante hexadecimal
               } else if (Character.isDigit(c)) {
                  //System.out.println("Case 0 digit case c: " + c);
                  lex += c;
                  i++;
                  estado = 21;
                  // Comparadores de grandeza (maior,menor,igual, maiorigual,menorigual) ou
               // atribuicao
               } else if (c == '=' || c == '<' || c == '>') {
                  lex += c;
                  i++;
                  estado = 4;
                  //System.out.println(c + " compilado");
               // Diferente
               } else if (c == '!') {
                  lex += c;
                  i++;
                  estado = 5;
                  //System.out.println(c + " compilado");

               // Divisao ou quociente da divisão de 2 inteiros
               } else if (c == '/') {
                  lex += c;
                  i++;
                  estado = 6;
               // Caracter
               } else if (c == '\'') {
                  lex += c;
                  i++;
                  estado = 7;
                  //System.out.println(c + " compilado");

               // String
               } else if (c == '"') {
                  lex += c;
                  i++;
                  estado = 9;
               } else if (c == ',' || c == '-' || c == '+' || c == '*' || c == ';' || c == '%' || c == '(' || c == ')' || c == '[' || c == ']') {
                  lex += c;
                  simbolo.setToken(lex);
                  i++;
                  estado  = 30;
               // Ponto flutuante
               } else if (c == '.') {
                  lex += c;
                  i++;
                  estado = 19;
               // Comentario
               } else if (c == '{') {
                  lex += c;
                  i++;
                  estado = 1;
                  //System.out.println(c + " compilado");
               } else if (c == ' ') {
                  estado = 0;
                  i++;
               } else if (c == '\n' || c == '\r') {
                  estado = 0;
                  linhas++;
                  i++;
                  //System.out.println("quebra de linha");

               } else {
                  error = true;
                  if(lex.length() == 0){
                     lex += c;
                  }
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
               }
            break;

            case 1:
               if (c == '*') {
                  estado = 2;
                  i++;
                  lex += c;
                  //System.out.println(c + " compilado");
               } else if (i == file.length()) {
                  error = true;
                  System.out.print((linhas) + "\nfim de arquivo nao esperado.");
               } else {
                  error = true;
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
               }
            break;

            case 2:
               /* aparentemente eh coisa com comentario que nao eh fechado, 
               vamo ter que pensar nas possibilidades de alguem errar a sintaxe de um comentario */
               //System.out.println("case 2 c:" + c);
               if (c == '*') {
                  estado = 3;
                  i++; 
                  lex += c;
                  //System.out.println(c + " compilado");
               } else if (isLetter(c) || Character.isDigit(c) || searchAlfabeto(c, alfabeto_caracteres) == true || c == ' ' || c == '\n' || c == '\r') {
                  i++;
                  if (c == '\n' || c == '\r') linhas++;
                  lex += c;
                  estado = 2;
                 // System.out.println(c + " compilado");
                  System.out.println("lexema: " + lex);
               } else {
                  //System.out.println(c + "teste");
                  error = true;
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
               }

               if (i >= file.length()){
                  error = true;
                  System.out.print((linhas) + "\nfim de arquivo nao esperado.");
                  i = file.length();
               }
            break;

            case 3:
               if (c == '*') {
                  estado = 3;
                  i++;
                  lex += c;
                  //System.out.println(c + " compilado");
               } else if (c != '*' && c != '}') {
                  estado = 2;
                  i++;
                  lex += c;
                  if (c == '\n' || c == '\r') linhas++;
                  //System.out.println(c + " compilado");
               } else if (c == '}') {
                  estado = 0;
                  i++;
                  lex = "";
                  //System.out.println(c + " compilado");
               } else {
                  error = true;
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
               }
               if (i == file.length()){
                  error = true;
                  System.out.print((linhas) + "\nfim de arquivo nao esperado.");
               }
            break;

            // >= <= ==
            case 4:
               //System.out.println("case 4 c: " + c);
               if (c == '=') {
                  lex += c;
                  simbolo.setToken(lex);
                  i++;
                  //System.out.println(c + " compilado");
                  estado = 30;
               } else {
                  simbolo.setToken(lex);
                  estado = 30;
               }
            break;

               // !=
            case 5:
               if (c == '=') {
                  lex += c;
                  simbolo.setToken(lex);
                  i++;
                  estado = 30;
                  //System.out.println(c + " compilado");
               } else if (c != '=') {
                  error = true;
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
                  lex = "";
               }
            break;

            // / ou //
            case 6:
               if (c == '/') {
                  lex += c;
                  i++;
                  simbolo.setToken(lex);
                  estado = 30;
               } else {
                  simbolo.setToken(lex);
                  estado = 30;
               }
            break;

         //Char
            case 7:

            if(i >= file.length()){
               error = true;
               System.out.print((linhas) + "\nfim de arquivo nao esperado.");
            }else if ((searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c)  || isLetter(c)) && c != ' ') {
               lex += c;
               i++;
               estado = 8;
               //System.out.println(c + " compilado");
            } else {
               error = true;
               System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
               i = file.length();
               lex = "";
            }
         break;

         case 8:
            if(i >= file.length()){
               error = true;
               System.out.print((linhas) + "\nfim de arquivo nao esperado.");
            }else if (c == '\'') {
               lex += c;
               simbolo.setToken("char");
               i++;
               estado = 30;
               //System.out.println(c + " compilado");
            } else {
               error = true;
               System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
               i = file.length();
               lex = "";
            }
         break;
            
               // string
            case 9:
               if (c != '"' && (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c)  || isLetter(c))) {
                  lex += c;
                  i++;
                  estado = 9;
               } else if(c == '"') {
                  lex += c;
                  simbolo.setToken("string");
                  i++;
                  estado = 30;
               }else{
                  error = true;               
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
                  lex = "";
               }
            break;

            // identificador
            case 10:
               if ((c == '_' || isLetter(c) || Character.isDigit(c))) {
                  lex += c;
                  i++;
                  estado = 10;
               } else if (c != '_' && isLetter(c) == false && Character.isDigit(c) == false){
                  estado = 30;
               }
            break;

            case 11:
               if (searchAlfabeto(c, letras_hexa) || Character.isDigit(c)){
                  lex += c;
                  i++;
                  estado = 16;
               } else if(isLetter(c) || c == '_') {
                  lex += c;
                  i++;
                  estado = 10;
               } else{
                  estado = 30;
               }
            break;

            case 16:
               if(c == 'h'){
                  lex += c;
                  i++;
                  estado = 18;
               } else if ((c == '_' || isLetter(c) || Character.isDigit(c))){
                  lex += c;
                  i++;
                  estado = 10;
               }else{
                  estado = 30;
               }
            break;

            case 18:
               if ((c == '_' || isLetter(c) || Character.isDigit(c))){
                  lex += c;
                  i++;
                  estado = 10;
               }else{
                  estado = 30;
               }
            break;

            case 19:
               if(Character.isDigit(c)){
                  lex += c;
                  i++;
                  estado = 20;
               }else{
                  error = true;
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
                  lex = "";
               }
            break;

            case 20:
               if(Character.isDigit(c)){
                  lex += c;
                  i++;
                  estado = 20;
               }else{
                  estado = 30;
                  
               }
            break;

            case 21:
               //System.out.println("Case 21 c: " + c);
               if(c == '.'){
                  lex += c;
                  i++;
                  estado = 20;
               }else if(Character.isDigit(c)){
                  lex += c;
                  i++;
                  estado = 22;
               }else if(searchAlfabeto(c, letras_hexa)){
                  lex += c;
                  i++;
                  estado = 23;
               }else{
                  estado = 30;
               }
            break;

            case 22:
               if(c == '.' || Character.isDigit(c)){
                  lex += c;
                  i++;
                  estado = 20;
               }else if(c == 'h'){
                  lex += c;
                  i++;
                  estado = 30;
               }else{
                  estado = 30;
               }
            break;

            case 23:
               if(c == 'h'){
                  lex += c;
                  i++;
                  estado = 30;
               }else{
                  error = true;
                  System.out.print((linhas) + "\nlexema nao identificado [" + lex + "].");
                  i = file.length();
                  lex = "";
               }
               break;
               
            case 30:
               estado = 0;
               //System.out.println(lex);
               lex = "";
               // return simbolo;
            break;
            }
      }}

      return simbolo;

   }

   /* ANALISADOR SINTÁTICO */

   public static void ct(String token_esperado) {
      if (token.getToken().equals(token_esperado)) {
         token = analisadorLexico();
         // triar um erro de fim de arquivo aqui
      } else if (token.getToken().equals("")) {
         // triar um erro de fim de arquivo aqui
      } else {
         System.out.println(linhas);
         System.out.println("token nao esperado [" + token.getLexema() + "].");
      }
   }

   /*
    * Gramática S-> {Declaração}* {Comandos}* EoF
    */
   public static void S() {
      try {
         while (token.getToken().equals("integer") || token.getToken().equals("const")
               || token.getToken().equals("char") || token.getToken().equals("real")
               || token.getToken().equals("string") || token.getToken().equals("boolean")) {
            Declaracao();
         }
         while (token.getToken().equals("id") || token.getToken().equals("while") || token.getToken().equals("if")
               || token.getToken().equals(";") || token.getToken().equals("readln")
               || token.getToken().equals("writeln") || token.getToken().equals("write")) {
            Comandos();
         }
      } catch (Exception e) {
      }
   }

   /*
    * Declaração -> Variaveis | Constantes
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
            // ct(const) -> valor constante
         }
         while (token.getToken().equals(",")) {
            ct(",");
            ct("id");
            if (token.getToken().equals("=")) {
               ct("=");
               if (token.getToken().equals("-")) {
                  ct("-");
               }
               // ct(const) -> valor constante
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
         // ct(valor_constante)
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

   // Comando de atribuição
   // id = Exp ; | id “[“Exp”]” = Exp ;
   public static void atribuicao() {
      ct("id");
      if (token.getToken().equals("=")) {
         ct("=");
         // EXP, não sei oque fazer com ele a não ser chorar
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
   // readln “(“ id “)”;
   public static void leitura() {
      ct("readln");
      ct("(");
      ct("id");
      ct(")");
   }

   // Comando de impressao
   // (write | writeln) “(“ Exp {,Exp}* “)”;
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
         Exp_soma(); // exp soma 2
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
         Exp_mult(); // exp mult 2
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
   // id[“[“ Exp “]”] |constante | not Fator1 | “(“ Exp “)” | integer “(“ Exp “)” |
   // real “(“ Exp “)”
   public static void Fator() {
      // tem de retornar um simbolo, não sei se é no semantico ou agora no sintatico
      if (token.getToken().equals("id")) {
         ct("id");
         if (token.getToken().equals("[")) {
            ct("[");
            Exp();
            ct("]");
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
            // ct(valor_constante) ??
         }
      }
   }

   public static void main(String[] args) {

      // popular a tabela de símbolos com palavras reservadas e símbolos
      for (String palavra : alfabeto_simbolos) {
         tabela.put(palavra.toLowerCase(), new Simbolo(palavra, palavra, "", "", 0, 0, ""));
         index++;
      }

      for (String palavra : alfabeto_reservadas) {
         tabela.put(palavra.toLowerCase(), new Simbolo(palavra, palavra, "", "", 0, 0, ""));
         index++;
      }

      // inicia o analisador lexico
      S();

      if (error == false) System.out.print((linhasArquivo.size()) + " linhas compiladas.");
   }
}
