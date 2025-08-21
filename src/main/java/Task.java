public class Task {

    private String name;
    private Boolean status;

    public Task(String name){
        this.name = name;
        this.status = false;
    }

    public String getName() {
        return this.name;
    }
    public String getStatusIcon() {
        return status ? "X" : " ";
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.name);
    }
}
