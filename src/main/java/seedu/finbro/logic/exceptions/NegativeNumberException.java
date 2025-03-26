package seedu.finbro.logic.exceptions;

import seedu.finbro.ui.Ui;

public class NegativeNumberException extends Exception {
    public NegativeNumberException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: Negative Number Input\n" +
                "Please input a non-negative number.");
    }
}
