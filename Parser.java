import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Integer> tokens;
    private int currentTokenIndex = 0;

    public Parser(List<Integer> tokens) {
        this.tokens = tokens;
    }

    public Integer parse() {
        return parseExpression();
    }

    private Integer parseExpression() {
        return parseLogicalOr();
    }

    private Integer parseLogicalOr() {
        Integer left = parseLogicalAnd();
        while (match(Token.OR.ordinal())) {
            int operator = previous();
            Integer right = parseLogicalAnd();
            left = operator == Token.OR.ordinal() ? left | right : left; // Real functionality for logical OR
        }
        return left;
    }

    private Integer parseLogicalAnd() {
        Integer left = parseEquality();
        while (match(Token.AND.ordinal())) {
            int operator = previous();
            Integer right = parseEquality();
            left = operator == Token.AND.ordinal() ? left & right : left; // Real functionality for logical AND
        }
        return left;
    }

    private Integer parseEquality() {
        Integer left = parseComparison();
        while (match(Token.EQ.ordinal(), Token.NEQ.ordinal())) {
            int operator = previous();
            Integer right = parseComparison();
            if (operator == Token.EQ.ordinal()) {
                left = left.equals(right) ? 1 : 0; // Real functionality for equality (1 for true, 0 for false)
            } else if (operator == Token.NEQ.ordinal()) {
                left = !left.equals(right) ? 1 : 0; // Real functionality for inequality (1 for true, 0 for false)
            }
        }
        return left;
    }

    private Integer parseComparison() {
        Integer left = parseTerm();
        while (match(Token.LT.ordinal(), Token.LTE.ordinal(), Token.GT.ordinal(), Token.GTE.ordinal())) {
            int operator = previous();
            Integer right = parseTerm();
            if (operator == Token.LT.ordinal()) {
                left = left < right ? 1 : 0; // Real functionality for less than (1 for true, 0 for false)
            } else if (operator == Token.LTE.ordinal()) {
                left = left <= right ? 1 : 0; // Real functionality for less than or equal (1 for true, 0 for false)
            } else if (operator == Token.GT.ordinal()) {
                left = left > right ? 1 : 0; // Real functionality for greater than (1 for true, 0 for false)
            } else if (operator == Token.GTE.ordinal()) {
                left = left >= right ? 1 : 0; // Real functionality for greater than or equal (1 for true, 0 for false)
            }
        }
        return left;
    }

    private Integer parseTerm() {
        Integer left = parseFactor();
        while (match(Token.PLUS.ordinal(), Token.MINUS.ordinal())) {
            int operator = previous();
            Integer right = parseFactor();
            if (operator == Token.PLUS.ordinal()) {
                left += right; // Real functionality for addition
            } else if (operator == Token.MINUS.ordinal()) {
                left -= right; // Real functionality for subtraction
            }
        }
        return left;
    }

    private Integer parseFactor() {
        Integer left = parsePrimary();
        while (match(Token.MULTIPLICATION.ordinal(), Token.DIVISION.ordinal())) {
            int operator = previous();
            Integer right = parsePrimary();
            if (operator == Token.MULTIPLICATION.ordinal()) {
                left *= right; // Real functionality for multiplication
            } else if (operator == Token.DIVISION.ordinal()) {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                left /= right; // Real functionality for division
            }
        }
        return left;
    }

    private Integer parsePrimary() {
        if (match(Token.INT.ordinal())) {
            return previous(); // Return the integer value
        } else if (match(Token.TRUE.ordinal())) {
            return 1; // True represented as 1
        } else if (match(Token.FALSE.ordinal())) {
            return 0; // False represented as 0
        } else if (match(Token.LPAREN.ordinal())) {
            Integer expression = parseExpression();
            consume(Token.RPAREN.ordinal(), "Expected ')' after expression");
            return expression;
        } else {
            // Handle variables and identifiers here (not implemented in this example)
            throw new RuntimeException("Variable or identifier handling not implemented");
        }
    }

    private boolean match(int... expectedTokens) {
        for (int expectedToken : expectedTokens) {
            if (check(expectedToken)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private int advance() {
        if (!isAtEnd()) {
            currentTokenIndex++;
        }
        return previous();
    }

    private boolean check(int expectedToken) {
        if (isAtEnd()) {
            return false;
        }
        return peek() == expectedToken;
    }

    private boolean isAtEnd() {
        return currentTokenIndex >= tokens.size();
    }

    private int peek() {
        return isAtEnd() ? Token.EOF.ordinal() : tokens.get(currentTokenIndex);
    }

    private int previous() {
        return tokens.get(currentTokenIndex - 1);
    }

    private void consume(int expectedToken, String errorMessage) {
        if (check(expectedToken)) {
            advance();
        } else {
            throw new RuntimeException(errorMessage);
        }
    }
}
