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

   // linguagem
   private static String[] alfabeto_simbolos = { "_", ".", ",", ";", ":", "(", ")", "[", "]", "+", "-", "'", "\"", "@",
         "&", "%", "!", "?", ">", "<", "=", "*", "/" };
   private static String[] alfabeto_reservadas = { "const", "integer", "char", "while", "if", "real", "else", "and",
         "or", "not", "begin", "end", "readln", "string", "write", "writeln", "TRUE", "FALSE", "boolean", "==", "!=",
         ">=", "<=", "//" };
   private static String[] alfabeto_caracteres = { " ", "_", ".", ",", ";", ":", "(", ")", "[", "]", "{", "}", "+", "-",
         "\"", "'", "/", "\\", "@", "&", "%", "!", "?", ">", "<", "=" };

   public static HashMap<String, Simbolo> tabela = new HashMap<>();

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
      boolean comentario = false;

      while (i < file.length()) {
         char c = file.charAt(i);
         switch (estado) {
         // identificador
         case 0:
         System.out.println(c + " case 0");
            if (c == '_' || Character.isLetter(c)) {
               lex += c;
               simbolo.setToken("id");
               System.out.println(c + " compilado");
               i++;
            } else if (c == '=' || c == '<' || c == '>') {
               lex += c;
               i++;
               estado = 3;
            } else if (c == '!') {
               lex += c;
               i++;
               estado = 4;
            } else if (c == '/') {
               lex += c;
               i++;
               estado = 5;
            } else if (c == '\'') {
               lex += c;
               i++;
               estado = 6;
            } else if (c == '"') {
               lex += c;
               i++;
               estado = 8;
               System.out.println(c + " compilado, estado: " + estado);
            } else if (c == '\n' || c == ' ') {
               estado = 0;
               i++;
            } else if (c == '{'){
               estado = 1;
               i++;
               System.out.println(c + " compilado");
            }
            break;

         case 1:
         System.out.println(c + " case 2");
            if (c == '*') {
               estado = 2;
               comentario = true;
               i++;
               System.out.println(c + " compilado else if");
               c = lerChar(i);
            } else if ((Character.isLetter(c) || searchAlfabeto(c, alfabeto_simbolos) == true || c == ' ')
                  && comentario) {
               i++;
               estado = 1;
               System.out.println(c + " compilado");
            } else if (i == file.length()) {
               i = file.length();
               System.out.println(c + " compilado");
            } else {
               System.out.println("ERRO: token não esperado");
               i = file.length();
            }
            break;

         case 2:
         System.out.println(c + " case 3");
            if (c != '}') {
               estado = 1; // se não encontrar o } significa que o * fazia parte do comentario, volta e
                           // continuar a ler
            } else if (c == '}') {
               estado = 0; // aqui termina de ler o comentario e volta pro estadio inicial
               lex = "";
               i++;
            }
            break;

            // >= <= ==
         case 3:
         System.out.println(c + " case 3");
            if (c == '=') {
               lex += c;
            } else {
               // devolve
            }
            break;

            // !=
         case 4:
         System.out.println(c + " case 4");
            if (c == '=') {
               lex += c;
            } else if (c != '=') {
               System.out.println("ERRO: Token inesperado ou faltante");
               i = file.length();
            }
            break;

         case 5:
         System.out.println(c + " case 5");
            if (c == '/') {
               lex += c;
            } else {
               // devolve
            }
            break;
            // char
         case 6:
         System.out.println(c + " case 6");
            if (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c)) {
               lex += c;
               i++;
               estado = 7;
            } else {
               System.out.println("ERRO: Caractere invalido");
               i = file.length();
            }
            break;

         case 7:
         System.out.println(c + " case 7");
            if (c == '\'') {
               lex += c;
               i++;
               // estado final
            } else {
               System.out.println("ERRO: caractere não esperado");
               i = file.length();
            }
            break;

            // string
         case 8:
         System.out.println(c + " case 8");
            System.out.println(c + "char, estado: " + estado);
            if (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c)) {
               lex += c;
               i++;
               estado = 9;
               System.out.println(c + " compilado");
            } else {
               System.out.println("ERRO: Caractere invalido");
               i = file.length();
            }
            break;

         case 9:
         System.out.println(c + " case 9");
            if (c == '"') {
               lex += c;
               i++;
               System.out.println(c + " compilado");
               // estado final
            } else if (searchAlfabeto(c, alfabeto_caracteres) || Character.isDigit(c)) {
               lex += c;
               i++;
               estado = 9;
               System.out.println(c + " compilado");
            } else {
               System.out.println("ERRO: caractere não esperado");
               i = file.length();
            }
            break;
            
         }
      }

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

      System.out.print(linhas + " linhas compiladas.");
   }
}
