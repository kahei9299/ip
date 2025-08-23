public enum TaskType {

    TODO, DEADLINE, EVENT;

    public static TaskType fromChar(char c) throws InvalidTaskTypeException {
        switch (Character.toLowerCase(c)) {
            case 't': return TODO;
            case 'd': return DEADLINE;
            case 'e': return EVENT;
            default: throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
        }
    }
}
