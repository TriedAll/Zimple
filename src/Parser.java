import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    protected static ArrayList<String> variableNames = new ArrayList<>();
    protected static ArrayList<Object> variableValues = new ArrayList<>();

    public static void parseStatement(Token[] tokens) {
        Token[] args = new Token[tokens.length - 1];
        System.arraycopy(tokens, 1, args, 0, args.length);
        switch (tokens[0].getValue()) {
            case "exit" -> exit(args);
            case "out" -> out(args);
            case "outln" -> outln(args);
            case "var" -> var(args);
            case "if" -> isTrue(args);
            case "while" -> whileTrue(args);
            case "call" -> call(args);
            case "err" -> err(args);
        }
    }

    public static void exit(Token[] args) {
        if (args.length == 1 && args[0].getType().equals("int")) {
            System.exit((Integer) args[0].toType());
        } else {
            Error.send(3);
        }
    }

    public static void out(Token[] args) {
        for (Token arg : args) {
            System.out.print(arg.toType());
        }
    }

    public static void outln(Token[] args) {
        out(args);
        System.out.print('\n');
    }

    public static void var(Token[] args) {
        if (args[0].getType().equals("none") && args[1].getValue().equals("=")) {
            if (variableNames.contains(args[0].getValue())) {
                variableValues.set(variableNames.indexOf(args[0].getValue()), args[2].toType());
            } else {
                variableNames.add(args[0].getValue());
                variableValues.add(args[2].toType());
            }
        }
    }

    public static void isTrue(Token[] args) {
        Token[] params = new Token[args.length - 1];
        System.arraycopy(args, 1, params, 0, params.length);
        ArrayList<ArrayList<Token>> tokenArray = new ArrayList<>();
        tokenArray.add(new ArrayList<>());
        int level = 0;
        for (Token param : params) {
            if (param.getValue().equals("~")) {
                level += 1;
                tokenArray.add(new ArrayList<>());
            } else {
                tokenArray.get(level).add(param);
            }
        }
        if (args[0].toType().equals(true)) {
            tokenArray.forEach(tokens -> parseStatement(tokens.toArray(new Token[0])));
        }
    }

    public static void whileTrue (Token[] args) {
        Token[] params = new Token[args.length - 1];
        System.arraycopy(args, 1, params, 0, params.length);
        ArrayList<ArrayList<Token>> tokenArray = new ArrayList<>();
        tokenArray.add(new ArrayList<>());
        int level = 0;
        for (Token param : params) {
            if (param.getValue().equals("#")) {
                level += 1;
                tokenArray.add(new ArrayList<>());
            } else {
                tokenArray.get(level).add(param);
            }
        }
        while (args[0].toType() == Boolean.TRUE) {
            tokenArray.forEach(tokens -> parseStatement(tokens.toArray(new Token[0])));
        }
    }

    public static void call(Token[] args) {
        File file = new File(args[0].getValue());
        if (file.exists()) {
            if (file.toString().substring(file.toString().indexOf(".") + 1).equals("zimp")) {
                Lexer.lexFile(file);
            } else {
                try {
                    String[] params = new String[args.length];
                    for (int i = 0; i < args.length; i++) {
                        params[i] = args[i].getValue();
                    }
                    new ProcessBuilder(params).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void err(Token[] args) {
        if (args.length > 1) {
            Error.send(3);
        }
        Error.send((Integer) args[0].toType());
    }
}
