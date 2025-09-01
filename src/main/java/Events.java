public class Events extends Task {

    private String date;
    private String start;
    private String end;

    public Events(String name, String date, String start, String end){
        super(name);
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public String getDate() {
        return this.date;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }
    @Override
    public String toString() {
        return String.format("[E]" + super.toString() + " (from: %s %s to: %s)", date,start,end );
    }
}
