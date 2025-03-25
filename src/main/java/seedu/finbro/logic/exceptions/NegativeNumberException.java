package seedu.finbro.logic.exceptions;

public class NegativeNumberException extends Exception {
    public NegativeNumberException() {
        super();
    }

    public static void handle() {
        System.err.println("INVALID INPUT - negative number\n" +
                "Please input a non-negative number.\n");
    }
}
