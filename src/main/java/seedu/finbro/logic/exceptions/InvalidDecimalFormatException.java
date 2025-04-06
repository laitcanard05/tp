package seedu.finbro.logic.exceptions;


public class InvalidDecimalFormatException extends Exception {

    public InvalidDecimalFormatException() {
        super();
    }

    public static void handle() {
        System.out.println("INVALID INPUT: Decimal input uses comma.\n" +
                "Please use Decimal Points \".\" instead.\n");
    }
}
