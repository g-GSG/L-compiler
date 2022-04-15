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
   private static String[] letras_hexa = { "A", "B", "C", "D", "E", "F"};

   public static HashMap<String, Simbolo> tabela = new HashMap<>();

   public static boolean isLetter(char c){
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) return true;
      else return false;
   }

   public static String readFile() {
      String result = "";
      String temp = "";

      try {
         linhasArquivo = new ArrayList<String>();
         Scanner sc = new Scanner(System.in);
         // sc = new Scanner(new File(file));
         while (sc.hasNext()) {
            temp = sc.nextLine();
            linhasArquivo.add(temp);
            result = result + temp + "\n";
            linhas++;
         }

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

   public static Simbolo analisadorLexico() {
      Simbolo simbolo = new Simbolo();
      int estado = 0;
      String lex = "";
      int i = 0;

      while (i < file.length()) {
         char c = file.charAt(i);
         if (searchAlfabeto(c, alfabeto_caracteres) == false && isLetter(c) == false && Character.isDigit(c) == false && c != '\n') {
            error = true;
            System.out.println((linhas + 1) + "\ncaractere invalido.");
            i = file.length();
         } else {
            switch (estado) {
            case 0:
               //System.out.println("Case 0 c: " + c);
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
                  //System.out.println("Fim de arquivo.");
                  i = file.length();
               }
            break;

            case 1:
               if (c == '*') {
                  estado = 2;
                  i++;
                  //System.out.println(c + " compilado");
               } else if (i == file.length()) {
                  i = file.length();
               } else {
                  System.out.println("ERRO: token nao esperado");
                  i = file.length();
               }
            break;

            case 2:
               /* aparentemente eh coisa com comentario que nao eh fechado, 
               vamo ter que pensar nas possibilidades de alguem errar a sintaxe de um comentario */
               //System.out.println("case 2 c: " + c);
               if (c == '*') {
                  estado = 3;
                  i++; 
                  //System.out.println(c + " compilado");
               } else if (isLetter(c) || Character.isDigit(c) || searchAlfabeto(c, alfabeto_simbolos) == true || c == ' ' || c == '\n' || c == '\r') {
                  i++;
                  estado = 2;
                  if (c == '\n' || c == '\r') linhas++;
                  if (i == file.length()){
                     error = true;
                     System.out.println((linhas + 1) + "\nfim de arquivo nao esperado.");
                  }
                  //System.out.println(c + " compilado");
               } else {
                  error = true;
                  System.out.println((linhas + 1) + "\nfim de arquivo nao esperado.");
                  i = file.length();
               }
            break;

            case 3:
               if (c == '*') {
                  estado = 3;
                  i++;
                  //System.out.println(c + " compilado");
               } else if (c != '*' && c != '}') {
                  estado = 2;
                  i++;
                  //System.out.println(c + " compilado");
               } else if (c == '}') {
                  estado = 0;
                  i++;
                  //System.out.println(c + " compilado");
               } else {
                  System.out.println("ERRO: token não esperado");
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
                  System.out.println("ERRO: Token inesperado ou faltante");
                  i = file.length();
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
               if (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c)  || Character.isLetter(c)) {
                  lex += c;
                  i++;
                  estado = 8;
                  //System.out.println(c + " compilado");
               } else {
                  System.out.println("ERRO: Caractere invalido");
                  i = file.length();
               }
            break;

            case 8:
               if (c == '\'') {
                  lex += c;
                  simbolo.setToken("char");
                  i++;
                  estado = 30;
                  //System.out.println(c + " compilado");
               } else {
                  System.out.println("ERRO: caractere não esperado");
                  i = file.length();
               }
            break;
            
               // string
            case 9:
               if (c != '"' && (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c)  || Character.isLetter(c))) {
                  lex += c;
                  i++;
                  estado = 9;
               } else if(c == '"') {
                  lex += c;
                  simbolo.setToken("string");
                  i++;
                  estado = 30;
               }else{
                  System.out.println("ERRO: Caractere invalido");
                  i = file.length();
               }
            break;

            // identificador
            case 10:
               if ((c == '_' || Character.isLetter(c) || Character.isDigit(c))) {
                  lex += c;
                  i++;
               } else if (c != '_' && Character.isLetter(c) == false && Character.isDigit(c) == false){
                  estado = 30;
               }
            break;

            case 11:
               if (searchAlfabeto(c, letras_hexa) || Character.isDigit(c)){
                  lex += c;
                  i++;
                  estado = 16;
               } else if(Character.isLetter(c) || c == '_') {
                  lex += c;
                  i++;
                  estado = 10;
               } else{
                  i++;
                  estado = 30;
               }
            break;

            case 16:
               if(c == 'h'){
                  lex += c;
                  i++;
                  estado = 18;
               } else if ((c == '_' || Character.isLetter(c) || Character.isDigit(c))){
                  lex += c;
                  i++;
                  estado = 10;
               }else{
                  estado = 30;
               }
            break;

            case 18:
               if ((c == '_' || Character.isLetter(c) || Character.isDigit(c))){
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
                  System.out.println("ERRO: Caractere invalido");
                  i = file.length();
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
                  System.out.println("ERRO: Caractere invalido");
                  i = file.length();
               }
               break;
               
            case 30:
               estado = 0;
               // System.out.println("estado final");
               // return simbolo;
            break;
            }
      }}

      return simbolo;

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
      token = analisadorLexico();

      if (error == false) System.out.print(linhas + " linhas compiladas.");
   }
}
