import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Lexer {

    public static void lexFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            char[] statement = new char[0];
            int b;
            boolean inComment = false;
            boolean inBlock = false;
            boolean justInComment = false;
            while ((b = fileInputStream.read()) != -1) {
                if (justInComment && b == 96) {
                    inBlock = !inBlock;
                }
                justInComment = false;
                if ((b == 13 || b == 10)) {
                    if (!inBlock) {
                        inComment = false;
                        if (statement.length > 0) {
                            lexStatement(new String(statement));
                        }
                    }
                    statement = new char[0];
                } else if (!inComment) {
                    if (b == 96) {
                        inComment = true;
                        justInComment = true;
                    } else {
                        char[] oldStatement = statement;
                        statement = new char[statement.length + 1];
                        System.arraycopy(oldStatement, 0 , statement, 0, oldStatement.length);
                        statement[statement.length - 1] = (char) b;
                    }
                }
                if (b == 96 && inBlock) {
                    justInComment = true;
                }
            }
            if (statement.length > 0) {
                lexStatement(new String(statement));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void lexStatement(String statement) {
        if (statement.endsWith(" ")) Error.send(6);
        Token[] tokens = new Token[1];
        boolean inString = false;
        String value = "";
        String type = "none";
        for (int i = 0; i < statement.length(); i++) {
            if (statement.charAt(i) == ' ' && !inString) {
                if (tokens.length == 1) {
                    type = "command";
                }
                tokens[tokens.length - 1] = new Token(value, type);
                Token[] oldTokens = tokens;
                tokens = new Token[tokens.length + 1];
                System.arraycopy(oldTokens, 0, tokens, 0, oldTokens.length);
                value = "";
                type = "";
            } else if (statement.charAt(i) == '\"') {
                inString = !inString;
                type = "string";
                value += '"';
            } else if (Character.isDigit(statement.charAt(i)) && !type.equals("string")) {
                type = "int";
                value += statement.charAt(i);
            } else {
                value += statement.charAt(i);
            }
            if (type.equals("int") && !Character.isDigit(statement.charAt(i))) {
                type = "none";
            }
        }
        if (tokens[tokens.length - 1] == null) {
            tokens[tokens.length - 1] = new Token(value, "");
        }
        Parser.parseStatement(tokens);
    }
}
