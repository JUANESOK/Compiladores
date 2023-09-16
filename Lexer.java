import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Lexer {
    private static HashMap<String, Token> keywords = new HashMap<>();
    private String input;
    private int position = 0;

    static {
        keywords.put("for", Token.FOR);
        keywords.put("function", Token.FUNCTION);
        keywords.put("let", Token.LET);
        keywords.put("if", Token.IF);
        keywords.put("else", Token.ELSE);
        keywords.put("while", Token.WHILE);
        keywords.put("true", Token.TRUE);
        keywords.put("false", Token.FALSE);
    }

    public Lexer(String input) {
        this.input = input;
    }

    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char currentChar = input.charAt(position);

            if (Character.isWhitespace(currentChar)) {
                position++;
                continue;
            }

            if (Character.isDigit(currentChar)) {
                tokens.add(readNumber());
            } else if (Character.isLetter(currentChar) || currentChar == '_') {
                tokens.add(readIdentifier());
            } else {
                Token token = matchSingleCharacterToken(currentChar);
                if (token != null) {
                    tokens.add(token);
                    position++;
                } else {
                    tokens.add(Token.ILLEGAL);
                    position++;
                }
            }
        }

        tokens.add(Token.EOF);
        return tokens;
    }

    private Token readNumber() {
        StringBuilder value = new StringBuilder();
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            value.append(input.charAt(position));
            position++;
        }
        return Token.INT;
    }

    private Token readIdentifier() {
        StringBuilder identifier = new StringBuilder();
        while (position < input.length() && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
            identifier.append(input.charAt(position));
            position++;
        }

        Token keywordToken = keywords.get(identifier.toString());
        if (keywordToken != null) {
            return keywordToken;
        } else {
            return Token.IDENT;
        }
    }

    private Token matchSingleCharacterToken(char currentChar) {
        switch (currentChar) {
            case '=':
                if (peek() == '=') {
                    position++;
                    return Token.EQ;
                }
                return Token.ASSIGN;
            case ',':
                return Token.COMMA;
            case '/':
                return Token.DIVISION;
            case '!':
                if (peek() == '=') {
                    position++;
                    return Token.NEQ;
                }
                return Token.NEGATION;
            case '<':
                if (peek() == '=') {
                    position++;
                    return Token.LTE;
                }
                return Token.LT;
            case '>':
                if (peek() == '=') {
                    position++;
                    return Token.GTE;
                }
                return Token.GT;
            case '+':
                return Token.PLUS;
            case '-':
                return Token.MINUS;
            case '*':
                return Token.MULTIPLICATION;
            case '(':
                return Token.LPAREN;
            case ')':
                return Token.RPAREN;
            case '{':
                return Token.LBRACE;
            case '}':
                return Token.RBRACE;
            case ';':
                return Token.SEMICOLON;
            case '&':
                if (peek() == '&') {
                    position++;
                    return Token.AND;
                }
                return Token.ILLEGAL;
            case '|':
                if (peek() == '|') {
                    position++;
                    return Token.OR;
                }
                return Token.ILLEGAL;
            default:
                return null;
        }
    }
//
    private char peek() {
        if (position + 1 < input.length()) {
            return input.charAt(position + 1);
        } else {
            return '\0';
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cadena a analizar: ");
        String input = scanner.nextLine();
        scanner.close();

        Lexer lexer = new Lexer(input);
        ArrayList<Token> tokens = lexer.tokenize();

        System.out.println("Tokens generados:");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}

enum Token {
    ASSIGN,
    COMMA,
    DIVISION,
    EQ,
    EOF,
    FOR,
    FUNCTION,
    GT,
    GTE,
    IDENT,
    ILLEGAL,
    INT,
    LBRACE,
    LET,
    LPAREN,
    LT,
    LTE,
    MINUS,
    MULTIPLICATION,
    NEGATION,
    NEQ,
    PLUS,
    RBRACE,
    RPAREN,
    SEMICOLON,
    // Nuevos tokens para operaciones booleanas y lógicas
    AND,
    OR,
    IF,
    ELSE,
    WHILE,
    TRUE,
    FALSE,
}
