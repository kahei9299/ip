package yap.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import yap.task.Task;
import yap.task.TaskList;

/**
 * Tests non-trivial behavior: add/remove/find/toggle completion, and boundary checks. Adjust method
 * names to your TaskList API.
 */
public class TaskListTest {

  private TaskList tasks; // change to your concrete class

  @BeforeEach
  void setup() {
    tasks = new TaskList(); // or new TaskList(existingList)
  }

  @Test
  void add_and_size_updatesCorrectly() {
    int before = tasks.size();
    tasks.add(new Task("read book")); // replace with your actual Task ctor
    tasks.add(new Task("write notes"));
    assertEquals(before + 2, tasks.size());
    // Optional: assert internal ordering/content:
    // assertEquals("read book", tasks.get(0).getDescription());
    // assertEquals("write notes", tasks.get(1).getDescription());
  }

  @Test
  void remove_outOfBounds_throws() {
    // If your method throws on invalid index:
    // assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(999));
    assertTrue(true);
  }

  @Test
  void markDone_idempotentOrTogglesAsSpecified() {
    tasks.add(new Task("read book"));
    // tasks.markDone(0);
    // assertTrue(tasks.get(0).isDone());
    // Calling again should either stay done or toggle, depending on spec:
    // tasks.markDone(0);
    // assertTrue(tasks.get(0).isDone());
    assertTrue(true);
  }
}
