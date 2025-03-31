package seedu.finbro.logic.exceptions;

public class ExceedCharCountException extends Exception {

    public ExceedCharCountException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: Input exceeds char count of 120\n"
                + "Please input a shorter string.");
    }
}
