import java.util.Scanner;

public class Token {

    private String value;
    private final String type;

    Token(String value, String type) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            type = "string";
            value = value.substring(1, value.length() - 1);
        }
        if (type.equals("") && value.matches("true|false")) {
            type = "bool";
        } else if (type.equals("string")) {
            value = value.replace("\\n", "\n").replace("\\\\","\\").replace("\\t", "\t").replace("\\r", "\r");
        }
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
        return toType(type, value);
    }

    public static Object toType(String type,String value) {

        if (type.equals("") && (value.equals("true") || value.equals("false"))) {
         return Boolean.parseBoolean(value);
        }

        if (value.equals("in")) {
            return new Scanner(System.in).nextLine();
        }

        if (!type.equals("string")) {
            if (value.contains(">")) {
                return (Double) toType("", value.split(">")[0]) > (Double) toType("", value.split(">")[1]);
            }
            if (value.contains("<")) {
                return (Double) toType("", value.split("<")[0]) < (Double) toType("", value.split("<")[1]);
            }
            if (value.contains("==")) {
                return toType("", value.split("==")[0]).equals(toType("", value.split("==")[1]));
            }
            if (value.contains("&&")) {
                return (Boolean)toType("", value.split("&&")[0]) && (Boolean) (toType("", value.split("&&")[1]));
            }
            if (value.contains("||")) {
                return (Boolean)toType("", value.split("\\|\\|")[0]) || (Boolean) (toType("", value.split("\\|\\|")[1]));
            }
            if (value.contains(">=")) {
                return (Double)toType("", value.split(">=")[0]) >= (Double)toType("", value.split(">=")[1]);
            }
            if (value.contains("<=")) {
                return (Double) toType("", value.split("<=")[0]) <= (Double) toType("", value.split("<=")[1]);
            }
            if (value.contains("+")) {
                return (Double) toType("", value.split("\\+")[0]) + (Double) toType("", value.split("\\+")[1]);
            }
            if (value.contains("*")) {
                return (Double) toType("", value.split("\\*")[0]) * (Double) toType("", value.split("\\*")[1]);
            }
            if (value.contains("/")) {
                return (Double) toType("", value.split("/")[0]) / (Double) toType("", value.split("/")[1]);
            }
            if (value.contains("-")) {
                return (Double) toType("", value.split("-")[0]) - (Double) toType("", value.split("-")[1]);
            }
            if (value.contains("^")) {
                return Math.pow((Double) toType("", value.split("\\^")[0]),(Double) toType("", value.split("\\^")[1]));
            }
        }

        if (type.equals("")) {
            if (Parser.variableNames.contains(value)) {
                return Parser.variableValues.get(Parser.variableNames.indexOf(value));
            }
        }

        if (value.matches("\\d*") && !type.equals("string")) type = "int";

        return switch (type) {
            case "int" -> Double.parseDouble(value);
            case "string" -> value;
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

    public void setValue(String value) {
        this.value = value;
    }
}
