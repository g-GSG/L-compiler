public class Simbolo {
  private String lexema = "";
  private byte token;

  public Simbolo() {
    this.lexema = "";
    this.token = -1;
  }

  public Simbolo(byte token, String lexema) {
    this.lexema = lexema;
    this.token = token;
  }

  public byte getToken() {
    return token;
  }

  public String getLexema() {
    return lexema;
  }
}