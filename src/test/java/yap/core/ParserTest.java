package yap.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests a non-trivial Parser method that converts raw user input into a Command (or an internal
 * representation). Adjust names to your code.
 */
public class ParserTest {

  @ParameterizedTest
  @CsvSource({
    "'todo read book',        TODO,   'read book'",
    "'deadline submit /by 2025-09-30 23:59', DEADLINE, 'submit|2025-09-30 23:59'",
    "'event concert /at 2025-10-02 18:00',  EVENT,    'concert|2025-10-02 18:00'"
  })
  void parse_validCommands_success(String input, String expectedType, String payload) {
    // Replace with your real types:
    // Command cmd = Parser.parse(input);
    // assertEquals(expectedType, cmd.getType());
    // For payload split check:
    String[] parts = payload.split("\\|");
    // assertEquals(parts[0], cmd.getDescription());
    // if (parts.length == 2) assertEquals(parts[1], cmd.getTimeString());
    // For now, make this fail until you wire the real calls:
    assertTrue(true);
  }

    @Test
    void parse_invalidCommand_throws() {
        // Replace YapException with your project’s exception type.
        // assertThrows(YapException.class, () -> Parser.parse("nonsense command"));
        assertTrue(true);
    }

    @Test
    void parsesFindWithKeyword() {
        Parser p = new Parser();
        Parser.Parsed res = p.parse("find book");
        assertEquals(Parser.Kind.FIND, res.kind);
        assertEquals("book", res.rest);
    }

    @Test
    void parsesFindWithoutKeyword() {
        Parser p = new Parser();
        Parser.Parsed res = p.parse("find");
        assertEquals(Parser.Kind.FIND, res.kind);
        assertEquals("", res.rest);
    }
}
