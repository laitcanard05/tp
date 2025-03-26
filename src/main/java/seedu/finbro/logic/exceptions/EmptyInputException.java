package seedu.finbro.logic.exceptions;

public class EmptyInputException extends Exception {
    public EmptyInputException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: Empty Input\n" +
                "This field cannot be empty.\n");
    }
}
