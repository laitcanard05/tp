package seedu.finbro.logic.exceptions;

public class IndexExceedLimitException extends Exception {
    public IndexExceedLimitException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: index exceeds limit\n" +
                "Please input an index within the appropriate range.\n");
    }
}
