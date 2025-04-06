package seedu.finbro.logic.exceptions;

public class MissingDecimalsException extends Exception {

    public MissingDecimalsException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: Decimal point detected but no digits proceeding decimal point.\n"
                + "If you entered a Decimal Point \".\" please enter up to 2 decimal places\n");
    }
}
