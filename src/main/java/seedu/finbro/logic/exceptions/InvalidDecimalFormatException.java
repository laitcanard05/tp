package seedu.finbro.logic.exceptions;

import seedu.finbro.ui.Ui;

public class InvalidDecimalFormatException extends Exception {

    public InvalidDecimalFormatException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: Decimal input uses comma. Please use Decimal Points instead.");
    }
}
