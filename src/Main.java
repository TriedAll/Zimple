import java.io.File;

public class Main {

    private static final String VERSION = "0.0.1";
    private static final String PROGRAM_NAME = "Zimple";

    public static void main(String[] args) {
        if (args.length > 1) Error.send(3);
        if (args.length == 0) {
            System.out.println(PROGRAM_NAME + " " + VERSION);
            System.out.print("""

                    Zimple is an interpreted programming language by Zakaria Choudhury.
                    It works with '.zimp' files.

                    Uses:
                    Zimple <[File]>\truns a file
                    Zimple <>\t\tdisplays this wonderful message you are reading
                    """);
            return;
        }
        File zimpFile = new File(args[0]);
        if (!zimpFile.exists()) Error.send(4);
        if (!zimpFile.toString().contains(".") || zimpFile.toString().length() - zimpFile.toString().lastIndexOf('.') != 5 || !zimpFile.toString().endsWith("zimp")) Error.send(5);
        Lexer.lexFile(zimpFile);
    }
}