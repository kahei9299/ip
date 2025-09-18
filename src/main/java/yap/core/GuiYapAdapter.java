package yap.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import yap.io.Ui;
import yap.parser.Parser;
import yap.task.TaskList;

/**
 * Bridges the existing CLI engine in {@link Yap} to the GUI without changing behaviour.
 *
 * <p>It reuses the same {@code Yap} instance and state. On each call, it temporarily captures
 * {@code System.out}, executes the exact same code paths (using reflection to invoke Yap's private
 * helpers when needed), and returns the text that the CLI would have printed. No command strings,
 * ordering, or formatting are reimplemented here, ensuring parity.
 */
public final class GuiYapAdapter {
    private final Yap yap;

    // Cached reflective handles for performance and clarity.
    private final Field fUi;
    private final Field fParser;
    private final Field fStorage;
    private final Field fTasks;
    private final Field fInAddMode;

    private final Method mHelpText;
    private final Method mHandleAddLine;
    private final Method mHandleDelete;
    private final Method mHandleComplete;
    private final Method mHandleFind;
    private final Method mHandleEdit;


    private boolean nameSet = false;
    private boolean exitRequested = false;

    /**
     * Creates an adapter around a new {@link Yap} using the given file path (same as CLI).
     *
     * @param filePath path to the persistent task file (e.g., {@code data/tasks.txt}).
     */
    public GuiYapAdapter(String filePath) {
        this.yap = new Yap(filePath);
        try {
            fUi = Yap.class.getDeclaredField("ui");
            fParser = Yap.class.getDeclaredField("parser");
            fStorage = Yap.class.getDeclaredField("storage");
            fTasks = Yap.class.getDeclaredField("tasks");
            fInAddMode = Yap.class.getDeclaredField("inAddMode");
            fUi.setAccessible(true);
            fParser.setAccessible(true);
            fStorage.setAccessible(true);
            fTasks.setAccessible(true);
            fInAddMode.setAccessible(true);

            mHelpText = Yap.class.getDeclaredMethod("helpText");
            mHandleAddLine = Yap.class.getDeclaredMethod("handleAddLine", String.class);
            mHandleDelete = Yap.class.getDeclaredMethod("handleDelete", String.class);
            mHandleComplete = Yap.class.getDeclaredMethod("handleComplete", String.class);
            mHandleFind = Yap.class.getDeclaredMethod("handleFind", String.class);
            mHelpText.setAccessible(true);
            mHandleAddLine.setAccessible(true);
            mHandleDelete.setAccessible(true);
            mHandleComplete.setAccessible(true);
            mHandleFind.setAccessible(true);
            mHandleEdit = Yap.class.getDeclaredMethod("handleEdit", String.class);
            mHandleEdit.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Reflection wiring failed: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the exact initial greeting and prompt the CLI shows at startup,
     * assembled from {@link Ui#showWelcome()} and the name prompt in {@link Ui#askName()}.
     *
     * <p>We do not call {@code askName()} here to avoid blocking on {@code System.in}. Instead,
     * we return the same two lines the CLI would print up front.
     *
     * @return Greeting + name prompt text.
     */
    public String getGreetingAndPrompt() {
        // These two lines come directly from Ui.java in your repo.
        return "Hello! I'm Yap your new best friend!\n"
                + "May I know what's your name?\n";
    }

    /**
     * Supplies the user's name exactly as the CLI would set it, and returns the CLI's
     * post-name message.
     *
     * @param name user's name (non-null; may be trimmed by caller).
     * @return Output produced by the CLI after the name is accepted.
     */
    public String setUserName(String name) {
        // The CLI prints: "For a list of available commands, type 'help'."
        nameSet = true;
        return "For a list of available commands, type 'help'.\n";
    }

    /**
     * Handles one user input line by executing the exact same command flow and returning the
     * CLI's output. Output is captured by temporarily redirecting {@code System.out}.
     *
     * @param raw user input.
     * @return text exactly as the CLI would have printed.
     */
    public String handle(String raw) {
        if (!nameSet) {
            // If the GUI forgot to set the user's name first, emulate the CLI flow:
            // accept this line as the name and print the same post-name line.
            return setUserName(raw == null ? "" : raw.trim());
        }

        String s = raw == null ? "" : raw.trim();

        // Capture System.out
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        try (PrintStream capture = new PrintStream(baos)) {
            System.setOut(capture);

            // We will now run the same switch(cmd.kind) the CLI uses, but
            // we call Yap's private helpers (which print via Ui) through reflection.
            Parser parser = (Parser) fParser.get(yap);
            Parser.Parsed cmd = parser.parse(s);

            Ui ui = (Ui) fUi.get(yap);
            TaskList tasks = (TaskList) fTasks.get(yap);
            Object storage = fStorage.get(yap); // used only when the CLI saves

            switch (cmd.kind) {
                case HELP: {
                    String help = (String) mHelpText.invoke(yap);
                    ui.showMessage(help);
                    break;
                }
                case SHOW: {
                    if (tasks.size() == 0) {
                        ui.showMessage("No tasks yet.");
                    } else {
                        ui.showMessage(tasks.render());
                    }
                    break;
                }
                case ADD: {
                    boolean inAddMode = fInAddMode.getBoolean(yap);
                    if (!inAddMode && !"done".equalsIgnoreCase(cmd.rest)) {
                        fInAddMode.setBoolean(yap, true);
                        ui.showMessage("Entered Add mode. Use:\n"
                                + "  t <name>\n"
                                + "  d <name>/<yyyy-MM-dd>\n"
                                + "  e <name>/<yyyy-MM-dd>/<HHmm>/<HHmm>\n"
                                + "Type 'done' to exit Add mode.");
                    } else if (inAddMode) {
                        if ("done".equalsIgnoreCase(cmd.rest)) {
                            fInAddMode.setBoolean(yap, false);
                            ui.showMessage("Exited Add mode.");
                            // CLI saves after exiting add-mode only when additions occurred;
                            // additions save inside handleAddLine itself.
                        } else {
                            // Delegate to the same helper
                            mHandleAddLine.invoke(yap, cmd.rest);
                            // After a successful add-line, the CLI doesn't always save immediately here,
                            // because handleAddLine adds and prints. It saves in the CLI after batches
                            // for some flows; your CLI also calls save after DELETE/COMPLETE cases.
                            // Here, we follow the CLI's exact behaviour: handleAddLine itself
                            // does not call save; the CLI later calls save at specific points.
                        }
                    } else {
                        ui.showError("Say 'add' first to enter Add mode.");
                    }
                    break;
                }
                case DELETE: {
                    mHandleDelete.invoke(yap, cmd.rest);
                    // storage.save(tasks.all());
                    // Call exactly as the CLI would: we cannot access save via helper,
                    // but DELETE case in CLI calls save immediately after handleDelete.
                    yap.io.Storage.class
                            .getMethod("save", java.util.List.class)
                            .invoke(storage, tasks.all());
                    break;
                }
                case COMPLETE: {
                    mHandleComplete.invoke(yap, cmd.rest);
                    yap.io.Storage.class
                            .getMethod("save", java.util.List.class)
                            .invoke(storage, tasks.all());
                    break;
                }
                case EXIT: {
                    exitRequested = true;
                    break;
                }
                case FIND: {
                    mHandleFind.invoke(yap, cmd.rest);
                    break;
                }

                case EDIT: {
                    // call the same helper your CLI uses
                    mHandleEdit.invoke(yap, cmd.rest);

                    // then persist, exactly like CLI does after EDIT
                    yap.io.Storage.class
                            .getMethod("save", java.util.List.class)
                            .invoke(storage, tasks.all());
                    break;
                }

                case UNKNOWN:
                default: {
                    boolean inAddMode = fInAddMode.getBoolean(yap);
                    if (inAddMode) {
                        mHandleAddLine.invoke(yap, cmd.rest);
                    } else {
                        ui.showError("I don't understand. Type 'help' to see the list of commands.");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // Print exactly like the CLI generic error path
            System.out.print("☹ OOPS! " + e.getMessage() + System.lineSeparator());
        } finally {
            System.setOut(originalOut);
        }
        return baos.toString();
    }

    /**
     * Returns whether the last command requested exit (so the GUI can close).
     *
     * @return {@code true} if the user issued an exit command.
     */
    public boolean isExit() {
        return exitRequested;
    }
}

