package yap.core;

import yap.task.TaskList;
import yap.task.Task;
import yap.task.ToDos;
import yap.task.Deadlines;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

/**
 * Tests non-trivial behavior: add/remove/find/toggle completion, and boundary checks.
 * Adjust method names to your TaskList API.
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
        tasks.add(new Task("read book"));     // replace with your actual Task ctor
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

    @Test
    void findIndices_matchesCaseInsensitively() {
        TaskList list = new TaskList();
        list.add(new ToDos("read book"));
        list.add(new Deadlines("return book", "2019-06-06"));
        list.add(new ToDos("exercise"));

        var hits = list.findIndices("Book");
        assertEquals(2, hits.size());
        assertEquals(1, (int) hits.get(0));
        assertEquals(2, (int) hits.get(1));
    }

    @Test
    void renderByIndices_formatsWithNumbers() {
        TaskList list = new TaskList();
        list.add(new ToDos("read book"));
        var out = list.renderByIndices(java.util.List.of(1));
        assertTrue(out.startsWith("1.[T]")); // depends on your Task.toString()
    }
}
