import java.util.*;
import java.io.*;

class Simbolo {
  private String lexema = "";
  private String tipo = "";
  private String classe = "";
  private int endereco = -1;
  private int tamanho = 0;
  private String valor = "";
  private String token; // ver se esse token vai continuar com o byte ou string

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
  private static String[] alfabeto_simbolos = { "_", ".", ",", ";", ":", "(", ")", "[", "]", "+", "-", "'",
      "\"", "@", "&", "%", "!", "?", ">", "<", "=", "*", "/" };
  private static String[] alfabeto_reservadas = { "const", "integer", "char", "while", "if", "real", "else", "and",
      "or", "not", "begin", "end", "readln", "string", "write", "writeln", "TRUE", "FALSE", "boolean", "==", "!=", ">=",
      "<=", "//" };

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

  public static Simbolo analisadorLexico() {
    Simbolo simbolo = new Simbolo();
    int estado = 0;
    String tmp = "";

    for (int i = 0; i < file.length(); i++) {
      char c = file.charAt(i);
      if (estado == 0) {
        if (c == '_' || Character.isLetter(c)) {
          tmp += c;
          simbolo.setToken("id");
          System.out.println(c + " compilado");
        }
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
