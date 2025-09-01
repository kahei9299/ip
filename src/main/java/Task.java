public class Task {

    private String name;
    private Boolean isdone;

    public Task(String name){
        this.name = name;
        this.isdone = false;
    }

    public String getName() {
        return this.name;
    }
    public String getStatusIcon() {
        return isdone ? "X" : " ";
    }

    public void setStatus(Boolean isdone) {
        this.isdone = isdone;
    }

    public Boolean getStatus() {
        return isdone;
    }

    public boolean isDone() {
        return Boolean.TRUE.equals(isdone);
    }

    public void markDone() {
        this.isdone = true;
    }
    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.name);
    }
}
