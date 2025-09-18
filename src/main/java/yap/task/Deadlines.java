package yap.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** A task with a due date. */
public class Deadlines extends Task {
  private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("MMM dd yyyy");

  private final LocalDate by;

  /**
   * Creates a deadline from a name and an ISO date string (yyyy-MM-dd).
   *
   * @param name task name
   * @param byStr deadline date in ISO format, e.g., "2019-12-02"
   * @throws java.time.format.DateTimeParseException if byStr is not yyyy-MM-dd
   */
  public Deadlines(String name, String byStr) {
    super(name);
    assert byStr != null && !byStr.isBlank() : "Deadline date string must be present";
    this.by = LocalDate.parse(byStr); // expects yyyy-MM-dd
    assert by != null : "Parsed deadline date is null";
  }

  /** Returns the due date. */
  public LocalDate getBy() {
    return by;
  }

  @Override
  public String toString() {
    return String.format("[D]%s (by: %s)", super.toString(), by.format(OUT_FMT));
  }
}
