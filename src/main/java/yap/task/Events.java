package yap.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** A scheduled event with a date, start time, and end time. */
public class Events extends Task {
  private static final DateTimeFormatter DATE_OUT = DateTimeFormatter.ofPattern("MMM dd yyyy");
  private static final DateTimeFormatter TIME_IN =
      DateTimeFormatter.ofPattern("HHmm"); // e.g., 1800
  private static final DateTimeFormatter TIME_OUT =
      DateTimeFormatter.ofPattern("h:mma"); // e.g., 6:00PM

  private final LocalDate date;
  private final LocalTime start;
  private final LocalTime end;

  /**
   * Creates an event from a name, ISO date, and HHmm start/end times.
   *
   * @param name event name
   * @param dateStr date in ISO format, e.g., "2019-12-02"
   * @param startStr start time in HHmm, e.g., "1800"
   * @param endStr end time in HHmm, e.g., "2000"
   * @throws java.time.format.DateTimeParseException if any input is malformed
   */
  public Events(String name, String dateStr, String startStr, String endStr) {
    super(name);
    this.date = LocalDate.parse(dateStr); // yyyy-MM-dd
    this.start = LocalTime.parse(startStr, TIME_IN); // HHmm
    this.end = LocalTime.parse(endStr, TIME_IN); // HHmm
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalTime getStart() {
    return start;
  }

  public LocalTime getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return String.format(
        "[E]%s (from: %s %s to: %s)",
        super.toString(), date.format(DATE_OUT), start.format(TIME_OUT), end.format(TIME_OUT));
  }
}
