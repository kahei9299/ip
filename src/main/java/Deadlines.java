public class Deadlines extends Task {

    private String deadline;

    public Deadlines(String name, String deadline) {
        super(name);
        this.deadline = deadline;
    }

    public String getDeadline() {
        return this.deadline;
    }
    @Override
    public String toString() {
        return String.format("[D]" + super.toString() + " (by: %s)",deadline);
    }
}
