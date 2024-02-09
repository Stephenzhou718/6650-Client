package part2;

public enum RequestMethod {
  GET(1, "GET"),
  POST(2, "POST");

  private int value;
  private String name;

  RequestMethod(int value, String name) {
    this.value = value;
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }
}
