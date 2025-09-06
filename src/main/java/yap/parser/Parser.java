package yap.parser;

import java.util.Optional;

public class Parser {

    public enum Kind { LIST, SHOW, ADD, DELETE, COMPLETE, HELP, EXIT, UNKNOWN }

    public static class Parsed {
        public final Kind kind;
        public final String rest;    // whatever follows the command
        public Parsed(Kind kind, String rest) { this.kind = kind; this.rest = rest; }
    }

    public Parsed parse(String raw) {
        String s = Optional.ofNullable(raw).orElse("").trim();
        if (s.isEmpty()) return new Parsed(Kind.UNKNOWN, "");
        String lower = s.toLowerCase();

        if (lower.equals("list") || lower.equals("show")) {
            return new Parsed(Kind.SHOW, "");
        } else if (lower.startsWith("add")) {
            return new Parsed(Kind.ADD, s.substring(3).trim()); // supports your add-mode trigger
        } else if (lower.startsWith("delete")) {
            return new Parsed(Kind.DELETE, s.substring(6).trim());
        } else if (lower.startsWith("complete") || lower.startsWith("done ")) {
            String rest = lower.startsWith("done ") ? s.substring(4).trim() : s.substring(8).trim();
            return new Parsed(Kind.COMPLETE, rest);
        } else if (lower.equals("help")) {
            return new Parsed(Kind.HELP, "");
        } else if (lower.equals("exit") || lower.equals("quit")) {
            return new Parsed(Kind.EXIT, "");
        } else if (lower.equals("done")) { // end add-mode
            return new Parsed(Kind.ADD, "done");
        }
        return new Parsed(Kind.UNKNOWN, s);
    }
}
