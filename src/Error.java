import java.util.ArrayList;

public class Error {


    private static final ArrayList<String> errors = new ArrayList<>();

    static {
        errors.add("Execution complete.");
        errors.add("Something went wrong.");
        errors.add("Unrecognized error.");
        errors.add("Invalid or inaccessible file.");
        errors.add("Incorrect File Extension");
    }

    public static void send(int code) {
        if (errors.size() >= code) {
            System.err.println(errors.get(code));
            System.exit(code);
        } else {
            send(2);
        }
    }
}