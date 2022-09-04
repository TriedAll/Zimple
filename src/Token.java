import java.util.Scanner;

public class Token {

    private final String value;
    private final String type;

    Token(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Object toType() {
        return toType(value, type);
    }

    public static Object toType(String value, String type) {

        if (value.startsWith("\"") && value.endsWith("\"")) {
            type = "string";
        }

        if (type.equals("none")) {
            if (Parser.variableNames.contains(value)) {
                return Parser.variableValues.get(Parser.variableNames.indexOf(value));
            }
        }

        if (value.equals("in")) {
            return new Scanner(System.in).nextLine();
        }
        boolean shouldSplit = false;
        String splitOperator = null;

        String[] boolOperators = new String[]{"&&", "\\|\\|"};
        String[] intOperators = new String[]{"+", "-", "*", "/", ">", "<", "<=", ">=", "^"};

        if (type.equals("none")) {
            for (String operator : boolOperators) {
                if (value.contains(operator)) {
                    shouldSplit = true;
                    splitOperator = operator;
                    break;
                }
            }
            for (String operator : intOperators) {
                if (value.contains(operator)) {
                    shouldSplit = true;
                    splitOperator = operator;
                }
            }
        }
         if (value.contains("==") || value.contains("!=")) {
            shouldSplit = true;
            if (value.contains("==")) {
                splitOperator = "==";
            } else {
                splitOperator = "!=";
            }
        }

        if (shouldSplit) {
            if (splitOperator.equals("+") || splitOperator.equals("*") || splitOperator.equals("^")) {
                splitOperator = "\\" + splitOperator;
            }
            Object firstHalf = toType(value.split(splitOperator)[0], "none");
            Object secondHalf = toType(value.split(splitOperator)[1], "none");

            assert firstHalf != null;
            if (firstHalf.getClass() == java.lang.Integer.class) {
                firstHalf = ((Integer) firstHalf).doubleValue();
            }
            assert secondHalf != null;
            if (secondHalf.getClass() == java.lang.Integer.class) {
                secondHalf = ((Integer) secondHalf).doubleValue();
            }

            if (splitOperator.equals("==")) {
                return firstHalf.equals(secondHalf);
            } else if (splitOperator.equals("!=")) {
                return !firstHalf.equals(secondHalf);
            }

            Object returnValue;

            switch (splitOperator) {
                case "&&" -> {
                    assert firstHalf instanceof Boolean;
                    assert secondHalf instanceof Boolean;
                    returnValue = (Boolean) firstHalf && (Boolean) secondHalf;
                }
                case "\\|\\|" -> {
                    assert firstHalf instanceof Boolean;
                    assert secondHalf instanceof Boolean;
                    returnValue = (Boolean) firstHalf || (Boolean) secondHalf;
                }
                case "\\+" -> {
                    if (firstHalf instanceof String) {
                        returnValue = (String) firstHalf + secondHalf;
                    } else if (firstHalf instanceof Double && secondHalf instanceof Double) {
                        returnValue = (Double) firstHalf + (Double) secondHalf;
                    } else {
                        return null;
                    }
                }
                case "-" -> returnValue = (Double) firstHalf - (Double) secondHalf;
                case "\\*" -> returnValue = (Double) firstHalf * (Double) secondHalf;
                case "/" -> returnValue = (Double) firstHalf / (Double) secondHalf;
                case "\\^" -> returnValue = Math.pow((Double) firstHalf, (Double) secondHalf);
                case "<" -> returnValue = (Double) firstHalf < (Double) secondHalf;
                case ">" -> returnValue = (Double) firstHalf > (Double) secondHalf;
                case "<=" -> returnValue = (Double) firstHalf <= (Double) secondHalf;
                case ">=" -> returnValue = (Double) firstHalf >= (Double) secondHalf;
                default -> returnValue = null;

            }

            if (returnValue instanceof Double && (returnValue.toString().endsWith(".0") || !returnValue.toString().contains("."))) {
                return ((Double) returnValue).intValue();
            }

            return returnValue;
        }

        if (value.matches("\\d*")) type = "int";

        if (type.equals("int") && (value.endsWith(".0") || !value.contains("."))) return Integer.parseInt(value);
        if (value.matches("true|false")) return Boolean.parseBoolean(value);

        return switch (type) {
            case "int" -> Double.parseDouble(value);
            case "string" -> value.substring(1, value.length() - 1);
            case "bool" -> Boolean.parseBoolean(value);
            default -> null;
        };
    }

    @Override
    public String toString() {
        if (!type.equals("none")) {
            return type+":"+value;
        } else {
            return value;
        }
    }
}
