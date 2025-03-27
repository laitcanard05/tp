package seedu.finbro.logic.exceptions;

public class DecimalPointException extends RuntimeException {

    public DecimalPointException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: Exceed 2 Decimal Points\n"
              + "Please keep your input to 2 decimal places or less.");
    }
}
