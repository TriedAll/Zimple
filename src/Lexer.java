import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class Lexer {

    public static void lexFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            char[] statement = new char[0];
            int lastChar = -1;
            int b;
            boolean inComment = false;
            boolean inBlock = false;
            while ((b = fileInputStream.read()) != -1) {
                if (lastChar == 96 && b == 96) {
                    inBlock = !inBlock;
                }
                if ((b == 13 || b == 10)) {
                    inComment = false;
                    if (!inBlock) {
                        if (statement.length > 0) {
                            lexStatement(new String(statement));
                        }
                    }
                    statement = new char[0];
                } else if (!inComment) {
                    if (b == 96) {
                        inComment = true;
                    } else {
                        char[] oldStatement = statement;
                        statement = new char[statement.length + 1];
                        System.arraycopy(oldStatement, 0 , statement, 0, oldStatement.length);
                        statement[statement.length - 1] = (char) b;
                    }
                }
                lastChar = (char) b;
            }
            if (statement.length > 0) {
                lexStatement(new String(statement));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void lexStatement(String statement) {
        if (statement.endsWith(" ")) Error.send(5);
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
                type = "none";
            } else {
                value += statement.charAt(i);
                if (statement.charAt(i) == '\"') {
                    inString = !inString;
                    type = "string";
                } else if (Character.isDigit(statement.charAt(i)) && type.equals("none") && isNumeric(value)) {
                    type = "int";
                } else if (value.matches("true|false")) {
                    type = "bool";
                }
            }
        }
        if (tokens[tokens.length - 1] == null) {
            tokens[tokens.length - 1] = new Token(value, type);
        }
        Parser.parseStatement(tokens);
    }
    public static boolean isNumeric(String str) {
        ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }
}
