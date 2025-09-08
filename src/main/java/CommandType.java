public enum CommandType {
  SHOW,
  HELP,
  EXIT,
  ADD,
  DONE,
  COMPLETE,
  DELETE;

  public static CommandType fromString(String command) {
    switch (command.toLowerCase()) {
      case "show":
        return SHOW;
      case "help":
        return HELP;
      case "exit":
        return EXIT;
      case "add":
        return ADD;
      case "done":
        return DONE;
      case "complete":
        return COMPLETE;
      case "delete":
        return DELETE;
      default:
        throw new IllegalArgumentException("Unknown command");
    }
  }
}
