package yap.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** A scheduled event with a date, start time, and end time. */
public class Events extends Task {
  private static  DateTimeFormatter DATE_OUT = DateTimeFormatter.ofPattern("MMM dd yyyy");
  private static  DateTimeFormatter TIME_IN =
      DateTimeFormatter.ofPattern("HHmm"); // e.g., 1800
  private static  DateTimeFormatter TIME_OUT =
      DateTimeFormatter.ofPattern("h:mma"); // e.g., 6:00PM

  private LocalDate date;
  private LocalTime start;
  private LocalTime end;

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
    assert dateStr != null && startStr != null && endStr != null : "Event fields must be present";
    this.date = LocalDate.parse(dateStr); // yyyy-MM-dd
    this.start = LocalTime.parse(startStr, TIME_IN); // HHmm
    this.end = LocalTime.parse(endStr, TIME_IN); // HHmm
    assert !end.isBefore(start) : "Event end must not be before start";
  }

  public void setDate(LocalDate date) { this.date = date; }
  public void setDate(String iso) {
    this.date = LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE);
  }
  public void setStart(String hhmm) {
    this.start = LocalTime.parse(hhmm, DateTimeFormatter.ofPattern("HHmm"));
  }
  public void setEnd(String hhmm) {
    this.end = LocalTime.parse(hhmm, DateTimeFormatter.ofPattern("HHmm"));
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
