import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Token {
    public static final String ILLEGAL = "ILLEGAL";
    public static final String EOF = "EOF";
    public static final String IDENT = "IDENT";
    public static final String INT = "INT";
    public static final String ASSIGN = "=";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String BANG = "!";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";
    public static final String LT = "<";
    public static final String GT = ">";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String LBRACE = "{";
    public static final String RBRACE = "}";
    public static final String FUNCTION = "FUNCTION";
    public static final String LET = "LET";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String IF = "if";
    public static final String ELSE = "else";
    public static final String RETURN = "return";
    public static final String EQ = "==";
    public static final String NOT_EQ = "!=";
    public static final String STRING = "STRING";
    public static final String LBRACKET = "[";
    public static final String RBRACKET = "]";
    public static final String COLON = ":";

    private String type = "";
    private String literal = "";
    
    public Token() {
    }

    public Token(String type, String literal) {
        this.type = type;
        this.literal = literal;
    }
    
    public String getType() {
        return type;
    }
    
    public String getLiteral() {
        return literal;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setLiteral(String literal) {
        this.literal = literal;
    }
}

class Lexer {
    public static final Map<String, String> KEYWORDS;
    public static final String VALID_IDENTS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    public static final String VALID_NUMBERS = "0123456789";
    public static final String WHITESPACES = " \t\r\n";
    static {
        KEYWORDS = new HashMap<String, String>();
        KEYWORDS.put("fn", Token.FUNCTION);
        KEYWORDS.put("let", Token.LET);
        KEYWORDS.put("true", Token.TRUE);
        KEYWORDS.put("false", Token.FALSE);
        KEYWORDS.put("if", Token.IF);
        KEYWORDS.put("else", Token.ELSE);
        KEYWORDS.put("return", Token.RETURN);
    }
    
    private String input = "";
    private int position = 0;
    private int read = 0;
    private char ch = 0;

    public Lexer() {
    }
    
    public Lexer(String input, int position, int read, char ch) {
        this.input = input;
        this.position = position;
        this.read = read;
        this.ch = ch;
    }

    void readChar() {
        if (read >= input.length()) {
            ch = 0;
        } else {
            ch = input.charAt(read);
        }
        position = read;
        read += 1;
    }
    
    char peekChar() {
        if (read >= input.length()) {
            return 0;
        } else {
            return input.charAt(read);
        }
    }
    
    Token newToken(Token token, String type, char ch) {
        token.setType(type);
        token.setLiteral(String.valueOf(ch));
        return token;
    }
    
    Token newToken(Token token, String type, String ch) {
        token.setType(type);
        token.setLiteral(ch);
        return token;
    }

    Token nextToken() {
        Token t = new Token();
        
        this.skipWhitespace();
        
        if (ch == '=') {
            if (peekChar() == '=') {
                String c = String.valueOf(ch);
                readChar();
                t = newToken(t, Token.EQ, c + String.valueOf(ch));
            } else {
                t = newToken(t, Token.ASSIGN, ch);
            }
        } else if (ch == '+') {
            t = newToken(t, Token.PLUS, ch);
        } else if (ch == '-') {
            t = newToken(t, Token.MINUS, ch);          
        } else if (ch == '!') {
            if (peekChar() == '=') {
                String c = String.valueOf(ch);
                readChar();
                t = newToken(t, Token.NOT_EQ, c + String.valueOf(ch));
            } else {
                t = newToken(t, Token.BANG, ch);
            }
        } else if (ch == '/') {
            t = newToken(t, Token.SLASH, ch);     
        } else if (ch == '*') {
            t = newToken(t, Token.ASTERISK, ch);      
        } else if (ch == '<') {
            t = newToken(t, Token.LT, ch);      
        } else if (ch == '>') {
            t = newToken(t, Token.GT, ch);      
        } else if (ch == ';') {
            t = newToken(t, Token.SEMICOLON, ch);      
        } else if (ch == '(') {
            t = newToken(t, Token.LPAREN, ch);      
        } else if (ch == ')') {
            t = newToken(t, Token.RPAREN, ch);      
        } else if (ch == ',') {
            t = newToken(t, Token.COMMA, ch);      
        } else if (ch == '+') {
            t = newToken(t, Token.PLUS, ch);      
        } else if (ch == '{') {
            t = newToken(t, Token.LBRACE, ch);      
        } else if (ch == '}') {
            t = newToken(t, Token.RBRACE, ch);      
        } else if (ch == 0) {
            t.setLiteral("");
            t.setType(Token.EOF);
        } else if (ch == '"') {
            t.setLiteral(readString());
            t.setType(Token.STRING);
        } else if (ch == '[') {
            t = newToken(t, Token.LBRACKET, ch);      
        } else if (ch == ']') {
            t = newToken(t, Token.RBRACKET, ch);      
        } else if (ch == ':') {
            t = newToken(t, Token.COLON, ch);      
        } else {
            if (isLetter(ch)) {
                t.setLiteral(readIdent());
                t.setType(lookUpIdent(t.getLiteral()));
                return t;
            } else if (isDigit(ch)) {
                t.setLiteral(readNumber());
                t.setType(Token.INT);
                return t;
            } else {
                t = newToken(t, Token.ILLEGAL, ch);      
            }
        }
        readChar();
        return t;
    }
    
    String readIdent() {
        int pos = position;
        while (true) {
            if (ch == 0) {
                break;
            }
            boolean test = isLetter(ch);
            if (!test) {
                break;
            }
            readChar();
        }
        String ret = input.substring(pos, position);
        return ret;
    }
    
    String readNumber() {
        int pos = position;
        while (true) {
            if (ch == 0) {
                break;
            }
            boolean test = isDigit(ch);
            if (!test) {
                break;
            }
            readChar();
        }
        String ret = input.substring(pos, position);
        return ret;
    }
    
    String readString() {
        int pos = position + 1;
        while (true) {
            readChar();
            if (ch == '"' || ch == 0) {
                break;
            }
        }
        String ret = input.substring(pos, position);
        return ret;
    }
    
    String lookUpIdent(String s) {
        String ret = KEYWORDS.get(s);
        if (ret != null) {
            return ret;
        }
        return Token.IDENT;
    }
    
    boolean isLetter(char c) {
        return VALID_IDENTS.indexOf(c) > -1;
    }
    
    boolean isDigit(char c) {
        return VALID_NUMBERS.indexOf(c) > -1;
    }
    
    void skipWhitespace() {
        while (WHITESPACES.indexOf(ch) > -1) {
            readChar();
        }
    }
    
    void setInput(String input) {
        this.input = input;
    }
    
    public static Lexer newInstance(String s) {
        Lexer l = new Lexer();
        l.setInput(s);
        l.readChar();
        return l;
    }
}

class Node {
    protected Token token;
    
    public Node() {
    }
    
    public void setToken(Token token) {
        this.token = token;
    }
    
    public String tokenLiteral() {
        return token.getLiteral();
    }
    
    @Override
    public String toString() {
        return "";
    }
}

class Statement extends Node {
    public Statement() {
    }
    
    public void statementNode() {
    }    
}

class Expression extends Node {
    public Expression() {
    }
    
    public void expressionNode() {
    }    
}

class Identifier extends Expression {
    private String value = "";
    
    public Identifier(String value) {
        this.value = value;
        this.token = new Token();
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}

class LetStatement extends Statement {
    private Identifier name;
    private Expression value;
    
    public LetStatement() {
        this.token = new Token();
        this.name = new Identifier("");
        this.value = new Expression();
    }
    
    public Expression getValue() {
        return value;
    }
    
    public Identifier getName() {
        return name;
    }
    
    public void setName(Identifier name) {
        this.name = name;
    }
    
    public void setValue(Expression value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(tokenLiteral());
        ret.append(" ");
        ret.append(name);
        ret.append(" = ");
        //
        if (value != null) {
            ret.append(value);
        }
        //
        ret.append(";");
        return ret.toString();
    }
}

class ReturnStatement extends Statement {
    private Expression returnValue;

    public ReturnStatement() {
        this.token = new Token();
        this.returnValue = new Expression();
    }
    
    public Expression getReturnValue() {
        return returnValue;
    }
    
    public void setReturnValue(Expression returnValue) {
        this.returnValue = returnValue;
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(tokenLiteral());
        ret.append(" ");
        if (returnValue != null) {
            ret.append(returnValue);
        }
        ret.append(";");
        return ret.toString();
    }
    
}

class ExpressionStatement extends Statement {
    private Expression expression;

    public ExpressionStatement() {
        this.token = new Token();
        this.expression = new Expression();
    }
    
    public Expression getExpression() {
        return expression;
    }
    
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        if (expression != null) {
            return expression.toString();
        }
        return "";
    }
}

class BlockStatement extends Statement {
    private List<Statement> statements;

    public BlockStatement() {
        this.token = new Token();
        this.statements = new ArrayList<Statement>();
    }
    
    public List<Statement> getStatements() {
        return statements;
    }
    
    public boolean isEmpty() {
        return statements.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(String.format("%s{%s", Compilador.LINESEP, Compilador.LINESEP));
        //
        for(Statement s: statements) {
            ret.append(String.format("%s;%s", s.toString(), Compilador.LINESEP));
        }
        //
        ret.append(String.format("}%s", Compilador.LINESEP));
        return ret.toString();
    }
}

class IntegerLiteral extends Expression {
    private BigDecimal value;

    public IntegerLiteral(BigDecimal value) {
        this.token = new Token();
        this.value = value;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return token.getLiteral();
    }
    
}

class MonkeyStringLiteral extends Expression {
    private String value;

    public MonkeyStringLiteral() {
        this.token = new Token();
        this.value = "";    
    }
    
    public MonkeyStringLiteral(String value) {
        this.token = new Token();
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return token.getLiteral();
    }
}

class MonkeyFunctionLiteral extends Expression {
    private List<Identifier> parameters;
    private BlockStatement body;

    public MonkeyFunctionLiteral() {
        this.token = new Token();
        this.parameters = new ArrayList<Identifier>();
        this.body = new BlockStatement();
    }
    
    public List<Identifier> getParameters() {
        return parameters;
    }
    
    public BlockStatement getBody() {
        return body;
    }
    
    public void setParameters(List<Identifier> parameters) {
        this.parameters = parameters;
    }
    
    public void setBody(BlockStatement body) {
        this.body = body;
    }
    
    @Override
    public String toString() {
        List<String> params = new ArrayList<String>();
        for (Identifier p: parameters) {
            params.add(p.toString());
        }
        //
        StringBuilder ret = new StringBuilder();
        ret.append(tokenLiteral());
        ret.append("(");
        ret.append(CompiUtil.stringJoin(", ", params));
        ret.append(")");
        ret.append(body.toString());
        //
        return ret.toString();
    }
}

class MonkeyCallExpression extends Expression {
    private Expression function;
    private List<Expression> arguments;

    public MonkeyCallExpression() {
        this.token = new Token();
        this.function = new Expression();
        this.arguments = new ArrayList<Expression>();
    }
    
    public Expression getFunction() {
        return function;
    }
    
    public List<Expression> getArguments() {
        return arguments;
    }
    
    public void setFunction(Expression function) {
        this.function = function;
    }
    
    public void setArguments(List<Expression> arguments) {
        this.arguments = arguments;
    }
    
    @Override
    public String toString() {
        List<String> args = new ArrayList<String>();
        for (Expression a: arguments) {
            args.add(a.toString());
        }
        //
        StringBuilder ret = new StringBuilder();
        ret.append(function.toString());
        ret.append("(");
        ret.append(CompiUtil.stringJoin(", ", args));
        ret.append(")");
        //
        return ret.toString();
    }
}

class MonkeyBoolean extends Expression {
    private boolean value;

    public MonkeyBoolean(boolean value) {
        this.token = new Token();
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return token.getLiteral();
    }
}

class MonkeyPrefixExpression extends Expression {
    private String operator;
    private Expression right;

    public MonkeyPrefixExpression() {
        this.token = new Token();
        this.operator = "";
        this.right = new Expression();
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Expression getRight() {
        return right;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setRight(Expression right) {
        this.right = right;
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("(");
        ret.append(operator);
        ret.append(right.toString());
        ret.append(")");
        //
        return ret.toString();
    }
}

class MonkeyInfixExpression extends Expression {
    private Expression left;
    private String operator;
    private Expression right;

    public MonkeyInfixExpression() {
        this.token = new Token();
        this.left = new Expression();
        this.operator = "";
        this.right = new Expression();
    }
    
    public Expression getLeft() {
        return left;
    }
    
    public Expression getRight() {
        return right;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public void setLeft(Expression left) {
        this.left = left;
    }
    
    public void setRight(Expression right) {
        this.right = right;
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("(");
        ret.append(left.toString());
        ret.append(" ");
        ret.append(operator);
        ret.append(" ");
        ret.append(right.toString());
        ret.append(")");
        //
        return ret.toString();
    }
}

class MonkeyIfExpression extends Expression {
    private Expression condition;
    private BlockStatement consequence;
    private BlockStatement alternative;

    public MonkeyIfExpression() {
        this.token = new Token();
        this.condition = new Expression();
        this.consequence = new BlockStatement();
        this.alternative = new BlockStatement();
    }
    
    public void setCondition(Expression condition) {
        this.condition = condition;
    }
    
    public void setConsequence(BlockStatement consequence) {
        this.consequence = consequence;
    }
    
    public void setAlternative(BlockStatement alternative) {
        this.alternative = alternative;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public BlockStatement getConsequence() {
        return consequence;
    }
    
    public BlockStatement getAlternative() {
        return alternative;
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("if");
        ret.append(condition.toString());
        ret.append(" ");
        ret.append(consequence.toString());
        //
        if (!alternative.isEmpty()) {
            ret.append(" else ");
            ret.append(alternative.toString());
        }
        //
        return ret.toString();
    }
}

class MonkeyArrayLiteral extends Expression {
    private List<Expression> elements;

    public MonkeyArrayLiteral() {
        this.token = new Token();
        this.elements = new ArrayList<Expression>();
    }
    
    public List<Expression> getElements() {
        return elements;
    }
    
    public void setElements(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        List<String> elements = new ArrayList<String>();
        for (Expression e: this.elements) {
            elements.add(e.toString());
        }
        //
        StringBuilder ret = new StringBuilder();
        ret.append("[");
        ret.append(CompiUtil.stringJoin(", ", elements));
        ret.append("]");
        //
        return ret.toString();
    }
}

class MonkeyIndexExpression extends Expression {
    private Expression left;
    private Expression index;

    public MonkeyIndexExpression() {
        this.token = new Token();
        this.left = new Expression();
        this.index = new Expression();
    }
    
    public Expression getLeft() {
        return left;
    }
    
    public Expression getIndex() {
        return index;
    }
    
    public void setLeft(Expression left) {
        this.left = left;
    }
    
    public void setIndex(Expression index) {
        this.index = index;
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("(");
        ret.append(left.toString());
        ret.append("[");
        ret.append(index.toString());
        ret.append("])");
        //
        return ret.toString();
    }
}

class MonkeyHashLiteral extends Expression {
    private Map<Expression, Expression> pairs;

    public MonkeyHashLiteral() {
        this.token = new Token();
        this.pairs = new HashMap<Expression, Expression>();
    }   
    
    public void setPairs(Map<Expression, Expression> pairs) {
        this.pairs = pairs;
    }
    
    public Map<Expression, Expression> getPairs() {
        return pairs;
    }
    
    @Override
    public String toString() {
        List<String> pairs = new ArrayList<String>();
        for (Expression k: this.pairs.keySet()) {
            Expression v = this.pairs.get(k);
            pairs.add(String.format("%s:%s", k.toString(), v.toString()));
        }
        //
        StringBuilder ret = new StringBuilder();
        ret.append("{");
        ret.append(CompiUtil.stringJoin(", ", pairs));
        ret.append("}");
        //
        return ret.toString();
    }
}

class MonkeyParser {
    public static final int LOWEST = 1;
    public static final int EQUALS = 2;
    public static final int LESSGREATER = 3;
    public static final int SUM = 4;
    public static final int PRODUCT = 5;
    public static final int PREFIX = 6;
    public static final int CALL = 7;
    public static final int INDEX = 8;
    
    public static final Map<String, Integer> PRECEDENCES;
    static {
        PRECEDENCES = new HashMap<String, Integer>();
        PRECEDENCES.put(Token.LPAREN, CALL);
        PRECEDENCES.put(Token.EQ, EQUALS);
        PRECEDENCES.put(Token.NOT_EQ, EQUALS);
        PRECEDENCES.put(Token.LT, LESSGREATER);
        PRECEDENCES.put(Token.GT, LESSGREATER);
        PRECEDENCES.put(Token.PLUS, SUM);
        PRECEDENCES.put(Token.MINUS, SUM);
        PRECEDENCES.put(Token.SLASH, PRODUCT);
        PRECEDENCES.put(Token.ASTERISK, PRODUCT);
        PRECEDENCES.put(Token.LBRACKET, INDEX);
    }
    
    private Lexer lexer;
    private Token curToken;
    private Token peekToken;
    private List<String> errors;
    private Map<String, MonkeyParserPrefixCallable> prefixParseFns;
    private Map<String, MonkeyParserInfixCallable> infixParseFns;

    class ParseIdentifer implements MonkeyParserPrefixCallable {
        public Expression call() {
            Identifier ret = new Identifier("");
            ret.setToken(curToken);
            ret.setValue(curToken.getLiteral());
            return ret;
        }        
    }

    class ParseIntegerLiteral implements MonkeyParserPrefixCallable {
        public Expression call() {
            BigDecimal test;
            try {
                test = new BigDecimal(curToken.getLiteral());
            } catch (Exception e) {
                String msg = String.format("could not parse %s as integer", curToken.getLiteral());
                errors.add(msg);
                return null;
            }
            IntegerLiteral lit = new IntegerLiteral(test);
            lit.setToken(curToken);
            return lit;
        }        
    }
    
    class ParsePrefixExpression implements MonkeyParserPrefixCallable {
        public Expression call() {
            MonkeyPrefixExpression e = new MonkeyPrefixExpression();
            e.setToken(curToken);
            e.setOperator(curToken.getLiteral());
            //
            nextToken();
            e.setRight(parseExpression(PREFIX));
            //
            return e;            
        }        
    }
    
    class ParseBoolean implements MonkeyParserPrefixCallable {
        public Expression call() {
            MonkeyBoolean ret = new MonkeyBoolean(curTokenIs(Token.TRUE));
            ret.setToken(curToken);
            return ret;            
        }        
    }

    class ParseGroupedExpression implements MonkeyParserPrefixCallable {
        public Expression call() {
            nextToken();
            Expression e = parseExpression(LOWEST);
            //
            if (!expectPeek(Token.RPAREN)) {
                return null;
            }
            //
            return e;
        }        
    }

    class ParseIfExpression implements MonkeyParserPrefixCallable {
        public Expression call() {
            MonkeyIfExpression e = new MonkeyIfExpression();
            e.setToken(curToken);
            //
            if (!expectPeek(Token.LPAREN)) {
                return null;
            }
            //
            nextToken();
            e.setCondition(parseExpression(LOWEST));
            //
            if (!expectPeek(Token.RPAREN)) {
                return null;
            }
            //
            if (!expectPeek(Token.LBRACE)) {
                return null;
            }
            //
            e.setConsequence(parseBlockStatement());
            //
            if (peekTokenIs(Token.ELSE)) {
                nextToken();
                //
                if (!expectPeek(Token.LBRACE)) {
                    return null;
                }
                e.setAlternative(parseBlockStatement());
                
            }
            //
            return e;
        }        
    }
    
    class ParseFunctionLiteral implements MonkeyParserPrefixCallable {
        public Expression call() {
            MonkeyFunctionLiteral lit = new MonkeyFunctionLiteral();
            lit.setToken(curToken);
            //
            if (!expectPeek(Token.LPAREN)) {
                return null;
            }
            //
            lit.setParameters(parseFunctionParameters());
            //
            if (!expectPeek(Token.LBRACE)) {
                return null;
            }
            //
            lit.setBody(parseBlockStatement());
            //
            return lit;
        }
    }

    class ParseStringLiteral implements MonkeyParserPrefixCallable {
        public Expression call() {
            MonkeyStringLiteral lit = new MonkeyStringLiteral(curToken.getLiteral());
            lit.setToken(curToken);        
            return lit;
        }        
    }
    
    class ParseArrayLiteral implements MonkeyParserPrefixCallable {
        public Expression call() {
            MonkeyArrayLiteral array = new MonkeyArrayLiteral();
            array.setToken(curToken);
            array.setElements(parseExpressionList(Token.RBRACKET));
            return array;
        }        
    }
    
    class ParseHashLiteral implements MonkeyParserPrefixCallable {
        public Expression call() {
            MonkeyHashLiteral h = new MonkeyHashLiteral();
            h.setToken(curToken);
            //
            while (!peekTokenIs(Token.RBRACE)) {
                nextToken();
                Expression key = parseExpression(LOWEST);
                //
                if (!expectPeek(Token.COLON)) {
                    return null;
                }
                //
                nextToken();
                Expression value = parseExpression(LOWEST);
                //
                h.getPairs().put(key, value);
                //
                if (!peekTokenIs(Token.RBRACE) && !expectPeek(Token.COMMA)) {
                    return null;
                }
            }
            if (!expectPeek(Token.RBRACE)) {
                return null;
            }
            //
            return h;
        }        
    }

    class ParseInfixExpression implements MonkeyParserInfixCallable {
        public Expression call(Expression expression) {
            MonkeyInfixExpression e = new MonkeyInfixExpression();
            e.setToken(curToken);
            e.setOperator(curToken.getLiteral());
            e.setLeft(expression);
            //
            int precedence = curPrecedence();
            nextToken();
            //
            e.setRight(parseExpression(precedence));
            //
            return e;
        }
    }

    class ParseCallExpression implements MonkeyParserInfixCallable {
        public Expression call(Expression expression) {
            MonkeyCallExpression exp = new MonkeyCallExpression();
            exp.setToken(curToken);
            exp.setFunction(expression);
            exp.setArguments(parseExpressionList(Token.RPAREN));
            return exp;
        }
    }

    class ParseIndexExpression implements MonkeyParserInfixCallable {
        public Expression call(Expression expression) {
            MonkeyIndexExpression exp = new MonkeyIndexExpression();
            exp.setToken(curToken);
            exp.setLeft(expression);
            //
            nextToken();
            exp.setIndex(parseExpression(LOWEST));
            //
            if (!expectPeek(Token.RBRACKET)) {
                return null;
            }
            //
            return exp;
        }
    }
    
    public MonkeyParser() {
        this.lexer = Lexer.newInstance("");
        this.curToken = new Token();
        this.peekToken = new Token();
        this.errors = new ArrayList<String>();
        this.prefixParseFns = new HashMap<String, MonkeyParserPrefixCallable>();
        this.infixParseFns = new HashMap<String, MonkeyParserInfixCallable>();
        //
        registerPrefix(Token.IDENT, new ParseIdentifer());
        registerPrefix(Token.INT, new ParseIntegerLiteral());
        registerPrefix(Token.BANG, new ParsePrefixExpression());
        registerPrefix(Token.MINUS, new ParsePrefixExpression());
        registerPrefix(Token.TRUE, new ParseBoolean());
        registerPrefix(Token.FALSE, new ParseBoolean());
        registerPrefix(Token.LPAREN, new ParseGroupedExpression());
        registerPrefix(Token.IF, new ParseIfExpression());
        registerPrefix(Token.FUNCTION, new ParseFunctionLiteral());
        registerPrefix(Token.STRING, new ParseStringLiteral());
        registerPrefix(Token.LBRACKET, new ParseArrayLiteral());
        registerPrefix(Token.LBRACE, new ParseHashLiteral());
        //
        registerInfix(Token.PLUS, new ParseInfixExpression());
        registerInfix(Token.MINUS, new ParseInfixExpression());
        registerInfix(Token.SLASH, new ParseInfixExpression());
        registerInfix(Token.ASTERISK, new ParseInfixExpression());
        registerInfix(Token.EQ, new ParseInfixExpression());
        registerInfix(Token.NOT_EQ, new ParseInfixExpression());
        registerInfix(Token.LT, new ParseInfixExpression());
        registerInfix(Token.GT, new ParseInfixExpression());
        registerInfix(Token.LPAREN, new ParseCallExpression());
        registerInfix(Token.LBRACKET, new ParseIndexExpression());
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    MonkeyProgram parseProgram() {
        MonkeyProgram program = new MonkeyProgram();
        
        while (!curToken.getType().equals(Token.EOF)) {
            Statement s = parseStatement();
            if (s != null) {
                program.getStatements().add(s);
            }
            nextToken();
        }
        
        return program;
    }
    
    Statement parseStatement() {
        if (curToken.getType().equals(Token.LET)) {
            return parseLetStatement();
        } else if (curToken.getType().equals(Token.RETURN)) {
            return parseReturnStatement();
        } else {
            return parseExpressionStatement();
        }
    }
    
    LetStatement parseLetStatement() {
        LetStatement s = new LetStatement();
        s.setToken(curToken);
        if (!expectPeek(Token.IDENT)) {
            return null;
        }
        //
        Identifier ident = new Identifier(curToken.getLiteral());
        ident.setToken(curToken);
        s.setName(ident);
        if (!expectPeek(Token.ASSIGN)) {
            return null;
        }
        nextToken();
        s.setValue(parseExpression(LOWEST));
        if (peekTokenIs(Token.SEMICOLON)) {
            nextToken();
        }
        //
        return s;
    }
    
    ReturnStatement parseReturnStatement() {
        ReturnStatement s = new ReturnStatement();
        s.setToken(curToken);
        
        nextToken();
        s.setReturnValue(parseExpression(LOWEST));
        if (peekTokenIs(Token.SEMICOLON)) {
            nextToken();
        }
        //
        return s;
    }
    
    ExpressionStatement parseExpressionStatement() {
        ExpressionStatement s = new ExpressionStatement();
        s.setToken(curToken);
        s.setExpression(parseExpression(LOWEST));
        //
        if (peekTokenIs(Token.SEMICOLON)) {
            nextToken();
        }
        //
        return s;
    }
    
    BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement();
        block.setToken(curToken);
        //
        nextToken();
        while (!curTokenIs(Token.RBRACE) && !curTokenIs(Token.EOF)) {
            Statement s = parseStatement();
            if (s != null) {
                block.getStatements().add(s);
            }
            nextToken();
        }
        //
        return block;
    }
    
    Expression parseExpression(int precedence) {
        MonkeyParserPrefixCallable prefix = prefixParseFns.get(curToken.getType());
        if (prefix == null) {
            noPrefixParseFnError(curToken.getType());
            return null;
        }
        Expression leftExp = prefix.call();
        //
        while (!peekTokenIs(Token.SEMICOLON) && precedence < peekPrecedence()) {
            MonkeyParserInfixCallable infix = infixParseFns.get(peekToken.getType());
            if (infix == null) {
                return leftExp;
            }
            //
            nextToken();
            leftExp = infix.call(leftExp);
        }
        //
        return leftExp;       
    } 
    
    List<Identifier> parseFunctionParameters() {
        List<Identifier> identifiers = new ArrayList<Identifier>();
        //
        if (peekTokenIs(Token.RPAREN)) {
            nextToken();
            return identifiers;
        }
        //
        nextToken();
        Identifier ident = new Identifier("");
        ident.setToken(curToken);
        ident.setValue(curToken.getLiteral());
        identifiers.add(ident);
        //
        while (peekTokenIs(Token.COMMA)) {
            nextToken();
            nextToken();
            ident = new Identifier("");
            ident.setToken(curToken);
            ident.setValue(curToken.getLiteral());
            identifiers.add(ident);
        }
        //
        if (!expectPeek(Token.RPAREN)) {
            return null;
        }
        //
        return identifiers;
    }

    List<Expression> parseExpressionList(String end) {
        List<Expression> ret = new ArrayList<Expression>();
        //
        if (peekTokenIs(end)) {
            nextToken();
            return ret;
        }
        //
        nextToken();
        ret.add(parseExpression(LOWEST));
        //
        while (peekTokenIs(Token.COMMA)) {
            nextToken();
            nextToken();
            ret.add(parseExpression(LOWEST));
        }
        //
        if (!expectPeek(end)) {
            return null;
        }
        //
        return ret;
    }
    
    boolean curTokenIs(String t) {
        return curToken.getType().equals(t);
    }
    
    boolean peekTokenIs(String t) {
        return peekToken.getType().equals(t);
    }
    
    boolean expectPeek(String t) {
        if (peekTokenIs(t)) {
            nextToken();
            return true;
        } else {
            peekError(t);
            return false;
        }
    }
    
    void peekError(String t) {
        String m = String.format("expected next token to be %s, got %s instead", t, peekToken.getType());
        errors.add(m);
    }
    
    void registerPrefix(String tokenType, MonkeyParserPrefixCallable fn) {
        prefixParseFns.put(tokenType, fn);
    }
    
    void registerInfix(String tokenType, MonkeyParserInfixCallable fn) {
        infixParseFns.put(tokenType, fn);
    }

    void noPrefixParseFnError(String tokenType) {
        String m = String.format("no prefix parse function for %s found", tokenType);
        errors.add(m);
    }
    
    int peekPrecedence() {
        Integer p = PRECEDENCES.get(peekToken.getType());
        if (p != null) {
            return p;
        }
        //
        return LOWEST;
    }


    int curPrecedence() {
        Integer p = PRECEDENCES.get(curToken.getType());
        if (p != null) {
            return p;
        }
        //
        return LOWEST;
    }
    
    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }
    
    public static MonkeyParser newInstance(Lexer l) {
        MonkeyParser p = new MonkeyParser();
        p.setLexer(l);
        p.nextToken();
        p.nextToken();
        return p;
    }
}

interface MonkeyParserPrefixCallable {
    Expression call();
}

interface MonkeyParserInfixCallable {
    Expression call(Expression expression);
}

class MonkeyProgram extends Node {
    private List<Statement> statements;

    public MonkeyProgram() {
        statements = new ArrayList<Statement>();
    }
    
    List<Statement> getStatements() {
        return statements;
    }
    
    @Override
    public String tokenLiteral() {
        if (statements.size() > 0) {
            return statements.get(0).tokenLiteral();
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Statement s: statements) {
            ret.append(s.toString());
        }
        return ret.toString();
    }    
}

interface MonkeyHashable {
    MonkeyHashKey hashKey();
}

class MonkeyObject {
    public static final String INTEGER_OBJ = "INTEGER";
    public static final String BOOLEAN_OBJ = "BOOLEAN";
    public static final String NULL_OBJ = "NULL";
    public static final String RETURN_VALUE_OBJ = "RETURN_VALUE";
    public static final String ERROR_OBJ = "ERROR";
    public static final String FUNCTION_OBJ = "FUNCTION";
    public static final String STRING_OBJ = "STRING";
    public static final String BUILTIN_OBJ = "BUILTIN";
    public static final String ARRAY_OBJ = "ARRAY";
    public static final String HASH_OBJ = "HASH";
    
    protected String type;

    public MonkeyObject() {
        this.type = "";
    }
    
    public String getType() {
        return type;
    }
    
    public String inspect() {
        return "";
    }
    
    public String inspectValue() {
        return inspect();
    }
}

class MonkeyObjectInteger extends MonkeyObject implements MonkeyHashable {
    private BigDecimal value;

    public MonkeyObjectInteger() {
        type = INTEGER_OBJ;
    }

    public MonkeyObjectInteger(BigDecimal value) {
        this();
        this.value = value;
    }

    public MonkeyObjectInteger(int value) {
        this();
        this.value = BigDecimal.valueOf(value);
    }

    public BigDecimal getValue() {
        return value;
    }
    
    public int getIntegerValue() {
        return value.intValue();
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public MonkeyHashKey hashKey() {
        return new MonkeyHashKey(type, value.hashCode());
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }
}

class MonkeyObjectString extends MonkeyObject implements MonkeyHashable {
    private String value;

    public MonkeyObjectString() {
        type = STRING_OBJ;
    }

    public MonkeyObjectString(String value) {
        this();
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String inspect() {
        return String.format("\"%s\"", value);
    }

    @Override
    public String inspectValue() {
        return value;
    }

    public MonkeyHashKey hashKey() {
        return new MonkeyHashKey(type, value.hashCode());
    }
    
}

class MonkeyObjectBoolean extends MonkeyObject implements MonkeyHashable {
    private boolean value;

    public MonkeyObjectBoolean() {
        type = BOOLEAN_OBJ;
    }

    public MonkeyObjectBoolean(boolean value) {
        this();
        this.value = value;
    }
    
    @Override
    public String inspect() {
        return String.format("%s", value);
    }

    public MonkeyHashKey hashKey() {
        int hashValue;
        if (value) {
            hashValue = 1;
        } else {
            hashValue = 0;
        }
        return new MonkeyHashKey(getType(), hashValue);
    }
}

class MonkeyObjectNull extends MonkeyObject {
    public MonkeyObjectNull() {
        type = NULL_OBJ;
    }

    @Override
    public String inspect() {
        return "null";
    }
}

class MonkeyObjectReturnValue extends MonkeyObject {
    private MonkeyObject value;

    public MonkeyObjectReturnValue() {
        type = RETURN_VALUE_OBJ;
    }
    
    public MonkeyObject getValue () {
        return value;
    }
    
    public void setValue(MonkeyObject value) {
        this.value = value;
    }
    
    @Override
    public String inspect() {
        return value.inspect();
    }
}

class MonkeyObjectError extends MonkeyObject {
    private String value;
    private String message;

    public MonkeyObjectError() {
        value = "";
        message = "";
        type = ERROR_OBJ;
    }

    public MonkeyObjectError(String value, String message) {
        this();
        this.value = value;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String inspect() {
        return String.format("ERROR: %s", message);
    }
}

class MonkeyObjectFunction extends MonkeyObject {
    private List<Identifier> parameters;
    private BlockStatement body;
    private MonkeyEnvironment env;

    public MonkeyObjectFunction() {
        parameters = new ArrayList<Identifier>();
        body = new BlockStatement();
        env = MonkeyEnvironment.newInstance();
    }

    public List<Identifier> getParameters() {
        return parameters;
    }
    
    public BlockStatement getBody() {
        return body;
    }
    
    public MonkeyEnvironment getEnvironment() {
        return env;
    }
    
    public void setParameter(List<Identifier> parameters) {
        this.parameters = parameters;
    }
    
    public void setBody(BlockStatement body) {
        this.body = body;
    }
    
    public void setEnvironment(MonkeyEnvironment env) {
        this.env = env;
    }
    
    @Override
    public String getType() {
        return FUNCTION_OBJ;
    }

    @Override
    public String inspect() {
        List<String> params = new ArrayList<String>();
        for (Identifier p: parameters) {
            params.add(p.toString());
        }
        //
        StringBuilder ret = new StringBuilder();
        ret.append("fn");
        ret.append("(");
        ret.append(CompiUtil.stringJoin(", ", params));
        ret.append(")");
        ret.append(body.toString());
        //
        return ret.toString();
    }
}

class MonkeyObjectBuiltin extends MonkeyObject {
    private MonkeyBuiltinCallable fn;
    private String value;

    public MonkeyObjectBuiltin(MonkeyBuiltinCallable fn, String value) {
        this.fn = fn;
        this.value = value;
    }

    public MonkeyObjectBuiltin(MonkeyBuiltinCallable fn) {
        this.fn = fn;
        this.value = "";
    }

    public MonkeyBuiltinCallable getFn() {
        return fn;
    }
    
    @Override
    public String getType() {
        return BUILTIN_OBJ;
    }

    @Override
    public String inspect() {
        return "builtin function";
    }
}

class MonkeyObjectArray extends MonkeyObject {
    private List<MonkeyObject> elements;

    public MonkeyObjectArray() {
        elements = new ArrayList<MonkeyObject>();
    }

    public List<MonkeyObject> getElements() {
        return elements;
    }
    
    public void setElements(List<MonkeyObject> elements) {
        this.elements = elements;
    }
    
    @Override
    public String getType() {
        return ARRAY_OBJ;
    }

    @Override
    public String inspect() {
        List<String> list = new ArrayList<String>();
        for (MonkeyObject e: elements) {
            list.add(e.inspect());
        }
        //
        StringBuilder ret = new StringBuilder();
        ret.append("[");
        ret.append(CompiUtil.stringJoin(", ", list));
        ret.append("]");
        //
        return ret.toString();
    }
}

class MonkeyObjectHash extends MonkeyObject {
    private Map<MonkeyHashKey, MonkeyHashPair> pairs;

    public MonkeyObjectHash() {
        pairs = new HashMap<MonkeyHashKey, MonkeyHashPair>();
    }
    
    public Map<MonkeyHashKey, MonkeyHashPair> getPairs() {
        return pairs;
    }
    
    public void setPairs(Map<MonkeyHashKey, MonkeyHashPair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public String getType() {
        return HASH_OBJ;
    }

    @Override
    public String inspect() {
        List<String> list = new ArrayList<String>();
        for (MonkeyHashKey k: pairs.keySet()) {
            MonkeyHashPair v = pairs.get(k);
            String pair = String.format("%s: %s", v.getKey().inspect(), v.getValue().inspect());
            list.add(pair);
        }
        //
        StringBuilder ret = new StringBuilder();
        ret.append("{");
        ret.append(CompiUtil.stringJoin(", ", list));
        ret.append("}");
        //
        return ret.toString();
    }
}

class MonkeyHashKey {
    private String type;
    private int value;

    public MonkeyHashKey(String type, int value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MonkeyHashKey) {
            MonkeyHashKey other = (MonkeyHashKey) obj;
            if (other.type.equals(type) && other.value == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        String h = String.format("%s-%s", type, value);
        return h.hashCode();
    }
}
    
class MonkeyHashPair {
    private MonkeyObject key;
    private MonkeyObject value;

    public MonkeyHashPair() {
        key = new MonkeyObject();
        value = new MonkeyObject();
    }
    
    public MonkeyObject getKey() {
        return key;
    }
    
    public MonkeyObject getValue() {
        return value;
    }
    
    public void setKey(MonkeyObject key) {
        this.key = key;
    }
    
    public void setValue(MonkeyObject value) {
        this.value = value;
    }
}

class MonkeyEnvironment {
    private Map<String, MonkeyObject> store;
    private Map<String, MonkeyObject> outer;

    public MonkeyEnvironment() {
        store = new HashMap<String, MonkeyObject>();
        outer = new HashMap<String, MonkeyObject>();
    }
    
    public MonkeyEnvironment(HashMap<String, MonkeyObject> outer) {
        this();
        this.outer = outer;
    }
    
    public MonkeyObject get(String name) {
        MonkeyObject obj = store.get(name);
        if (obj == null && outer != null) {
            obj = outer.get(name);
        }
        return obj;
    }
    
    public MonkeyObject set(String name, MonkeyObject value) {
        store.put(name, value);
        return value;
    }
    
    public void debug() {
        for (String k: store.keySet()) {
            MonkeyObject v = store.get(k);
            if (v != null) {
                Compilador.output(String.format("%s: %s", k, v.inspect()));
            }
        }
    }
    
    public static MonkeyEnvironment newInstance() {
        MonkeyEnvironment e = new MonkeyEnvironment();
        return e;
    }
    
    public static MonkeyEnvironment newInstanceEnclosed(MonkeyEnvironment outer) {
        MonkeyEnvironment e = new MonkeyEnvironment(MonkeyEnvironment.toMap(outer));
        return e;
    }
    
    public static HashMap<String, MonkeyObject> toMap(MonkeyEnvironment env) {
        HashMap<String, MonkeyObject> ret = new HashMap<String, MonkeyObject>();
        for (String k: env.store.keySet()) {
            MonkeyObject v = env.store.get(k);
            ret.put(k, v);
        }
        return ret;
    }
}

interface MonkeyBuiltinCallable {
    MonkeyObject call(MonkeyEvaluator evaluator, List<MonkeyObject> args);
}

class MonkeyBuiltinFunctionLen implements MonkeyBuiltinCallable {
    public MonkeyObject call(MonkeyEvaluator evaluator, List<MonkeyObject> args) {
        if (args.size() != 1) {
            return evaluator.newError(
                    String.format("wrong number of arguments, got=%s, want=1",
                            args.size()));
        }
        
        MonkeyObject a = args.get(0);
        if (a instanceof MonkeyObjectString) {
            MonkeyObjectString s = (MonkeyObjectString) a;
            MonkeyObjectInteger o = new MonkeyObjectInteger(s.getValue().length());
            return o;
        } else if (a instanceof MonkeyObjectArray) {
            MonkeyObjectArray s = (MonkeyObjectArray) a;
            MonkeyObjectInteger o = new MonkeyObjectInteger(s.getElements().size());
            return o;
        } else {
            return evaluator.newError(
                            String.format("argument to \"len\" not supported, got %s", 
                                    a.getType()));
        }
    }
}

class MonkeyBuiltinFunctionFirst implements MonkeyBuiltinCallable {
    public MonkeyObject call(MonkeyEvaluator evaluator, List<MonkeyObject> args) {
        if (args.size() != 1) {
            return evaluator.newError(
                    String.format("wrong number of arguments, got=%s, want=1",
                            args.size()));
        }
        
        MonkeyObject a = args.get(0);
        if (!(a instanceof MonkeyObjectArray)) {
            return evaluator.newError(
                            String.format("argument to \"first\" must be ARRAY, got %s", 
                                    a.getType()));
        };
        MonkeyObjectArray s = (MonkeyObjectArray) a;
        if (s.getElements().size() > 0) {
            return s.getElements().get(0);
        }
        //
        return MonkeyEvaluator.NULL;
    }
}

class MonkeyBuiltinFunctionLast implements MonkeyBuiltinCallable {
    public MonkeyObject call(MonkeyEvaluator evaluator, List<MonkeyObject> args) {
        if (args.size() != 1) {
            return evaluator.newError(
                    String.format("wrong number of arguments, got=%s, want=1",
                            args.size()));
        }
        
        MonkeyObject a = args.get(0);
        if (!(a instanceof MonkeyObjectArray)) {
            return evaluator.newError(
                            String.format("argument to \"last\" must be ARRAY, got %s", 
                                    a.getType()));
        };
        MonkeyObjectArray s = (MonkeyObjectArray) a;
        int length = s.getElements().size();
        if (length > 0) {
            return s.getElements().get(length - 1);
        }
        //
        return MonkeyEvaluator.NULL;
    }
}

class MonkeyBuiltinFunctionRest implements MonkeyBuiltinCallable {
    public MonkeyObject call(MonkeyEvaluator evaluator, List<MonkeyObject> args) {
        if (args.size() != 1) {
            return evaluator.newError(
                    String.format("wrong number of arguments, got=%s, want=1",
                            args.size()));
        }
        
        MonkeyObject a = args.get(0);
        if (!(a instanceof MonkeyObjectArray)) {
            return evaluator.newError(
                            String.format("argument to \"rest\" must be ARRAY, got %s", 
                                    a.getType()));
        };
        MonkeyObjectArray s = (MonkeyObjectArray) a;
        List<MonkeyObject> elements = s.getElements();
        int length = elements.size();
        if (length > 0) {
            List<MonkeyObject> list = new ArrayList<MonkeyObject>();
            for (int i=1; i<length; i++) {
                list.add(elements.get(i));
            }
            MonkeyObjectArray o = new MonkeyObjectArray();
            o.setElements(list);
            //
            return o;
        }
        //
        return MonkeyEvaluator.NULL;
    }
}

class MonkeyBuiltinFunctionPush implements MonkeyBuiltinCallable {
    public MonkeyObject call(MonkeyEvaluator evaluator, List<MonkeyObject> args) {
        if (args.size() != 2) {
            return evaluator.newError(
                    String.format("wrong number of arguments, got=%s, want=2",
                            args.size()));
        }
        
        MonkeyObject a = args.get(0);
        if (!(a instanceof MonkeyObjectArray)) {
            return evaluator.newError(
                            String.format("argument to \"push\" must be ARRAY, got %s", 
                                    a.getType()));
        };
        
        MonkeyObjectArray s = (MonkeyObjectArray) a;
        List<MonkeyObject> elements = s.getElements();
        int length = elements.size();
        
        List<MonkeyObject> list = new ArrayList<MonkeyObject>();
        for (int i=0; i<length; i++) {
            list.add(elements.get(i));
        }
        try {
            list.add(args.get(1));
        } catch (Exception e) {
                return MonkeyEvaluator.NULL; 
        }
        
        MonkeyObjectArray o = new MonkeyObjectArray();
        o.setElements(list);
        //
        return o;
    }
}

class MonkeyBuiltinFunctionPuts implements MonkeyBuiltinCallable {
    public MonkeyObject call(MonkeyEvaluator evaluator, List<MonkeyObject> args) {
        for (MonkeyObject a: args) {
            Compilador.output(a.inspectValue(), evaluator.getOutput());
        }
        //
        return MonkeyEvaluator.NULL;        
    }
}

class MonkeyBuiltins {
    public static final Map<String, MonkeyObjectBuiltin> BUILTINS;
    static {
        BUILTINS = new HashMap<String, MonkeyObjectBuiltin>();
        BUILTINS.put("len", new MonkeyObjectBuiltin(new MonkeyBuiltinFunctionLen()));
        BUILTINS.put("first", new MonkeyObjectBuiltin(new MonkeyBuiltinFunctionFirst()));
        BUILTINS.put("last", new MonkeyObjectBuiltin(new MonkeyBuiltinFunctionLast()));
        BUILTINS.put("rest", new MonkeyObjectBuiltin(new MonkeyBuiltinFunctionRest()));
        BUILTINS.put("push", new MonkeyObjectBuiltin(new MonkeyBuiltinFunctionPush()));
        BUILTINS.put("puts", new MonkeyObjectBuiltin(new MonkeyBuiltinFunctionPuts()));
    }
    
    public static MonkeyObjectBuiltin get(String name) {
        return BUILTINS.get(name);
    }
}

class MonkeyEvaluator {
    public static MonkeyObjectNull NULL = new MonkeyObjectNull();
    public static MonkeyObjectBoolean TRUE = new MonkeyObjectBoolean(true);
    public static MonkeyObjectBoolean FALSE = new MonkeyObjectBoolean(false);

    private PrintStream output;

    public MonkeyEvaluator() {
        output = System.out;
    }
    
    public MonkeyEvaluator(PrintStream output) {
        this.output = output;
    }
    
    MonkeyObject eval(Object node, MonkeyEnvironment env) {
        if (node instanceof MonkeyProgram) {
            MonkeyProgram s = (MonkeyProgram) node;
            return evalProgram(s, env);
        } else if (node instanceof ExpressionStatement) {
            ExpressionStatement s = (ExpressionStatement) node;
            return eval(s.getExpression(), env);
        } else if (node instanceof IntegerLiteral) {
            IntegerLiteral s = (IntegerLiteral) node;
            MonkeyObjectInteger o = new MonkeyObjectInteger(s.getValue());
            return o;
        } else if (node instanceof MonkeyBoolean) {
            MonkeyBoolean s = (MonkeyBoolean) node;
            return getBoolean(s.getValue());
        } else if (node instanceof MonkeyPrefixExpression) {
            MonkeyPrefixExpression s = (MonkeyPrefixExpression) node;
            MonkeyObject right = eval(s.getRight(), env);
            if (isError(right)) {
                return right;
            }
            //
            return evalPrefixExpression(s.getOperator(), right);
        } else if (node instanceof MonkeyInfixExpression) {
            MonkeyInfixExpression s = (MonkeyInfixExpression) node;
            MonkeyObject left = eval(s.getLeft(), env);
            if (isError(left)) {
                return left;
            }
            //
            MonkeyObject right = eval(s.getRight(), env);
            if (isError(right)) {
                return right;
            }
            //
            return evalInfixExpression(s.getOperator(), left, right);
        } else if (node instanceof BlockStatement) {
            BlockStatement s = (BlockStatement) node;
            return evalBlockStatement(s, env);
        } else if (node instanceof MonkeyIfExpression) {
            MonkeyIfExpression s = (MonkeyIfExpression) node;
            return evalIfExpression(s, env);
        } else if (node instanceof ReturnStatement) {
            ReturnStatement s = (ReturnStatement) node;
            MonkeyObject val = eval(s.getReturnValue(), env);
            if (isError(val)) {
                return val;
            }
            //
            MonkeyObjectReturnValue o = new MonkeyObjectReturnValue();
            o.setValue(val);
            return o;
        } else if (node instanceof LetStatement) {
            LetStatement s = (LetStatement) node;
            MonkeyObject val = eval(s.getValue(), env);
            if (isError(val)) {
                return val;
            }
            //
            env.set(s.getName().getValue(), val);
        } else if (node instanceof Identifier) {
            Identifier s = (Identifier) node;
            return evalIdentifier(s, env);
        } else if (node instanceof MonkeyFunctionLiteral) {
            MonkeyFunctionLiteral s = (MonkeyFunctionLiteral) node;
            List<Identifier> params = s.getParameters();
            BlockStatement body = s.getBody();
            //
            MonkeyObjectFunction o = new MonkeyObjectFunction();
            o.setParameter(params);
            o.setBody(body);
            o.setEnvironment(env);
            return o;
        } else if (node instanceof MonkeyCallExpression) {
            MonkeyCallExpression s = (MonkeyCallExpression) node;
            MonkeyObject function = eval(s.getFunction(), env);
            if (isError(function)) {
                return function;
            }
            //
            List<MonkeyObject> args = evalExpressions(s.getArguments(), env);
            if (args.size() == 1 && isError(args.get(0))) {
                return args.get(0);
            }
            //
            return applyFunction(function, args);
        } else if (node instanceof MonkeyStringLiteral) {
            MonkeyStringLiteral s = (MonkeyStringLiteral) node;
            MonkeyObjectString o = new MonkeyObjectString(s.getValue());
            return o;
        } else if (node instanceof MonkeyArrayLiteral) {
            MonkeyArrayLiteral s = (MonkeyArrayLiteral) node;
            List<MonkeyObject> elements = evalExpressions(s.getElements(), env);
            if (elements.size() == 1 && isError(elements.get(0))) {
                return elements.get(0);
            }
            //
            MonkeyObjectArray o = new MonkeyObjectArray();
            o.setElements(elements);
            return o;
        } else if (node instanceof MonkeyIndexExpression) {
            MonkeyIndexExpression s = (MonkeyIndexExpression) node;
            MonkeyObject left = eval(s.getLeft(), env);
            if (isError(left)) {
                return left;
            }
            //
            MonkeyObject index = eval(s.getIndex(), env);
            if (isError(index)) {
                return index;
            }
            //
            return evalIndexExpression(left, index);
        } else if (node instanceof MonkeyHashLiteral) {
            MonkeyHashLiteral s = (MonkeyHashLiteral) node;
            return evalHashLiteral(s, env);
        }
        
        return null;
    }

    MonkeyObject evalProgram(MonkeyProgram program, MonkeyEnvironment env) {
        MonkeyObject ret = new MonkeyObject();
        for (Statement s: program.getStatements()) {
            ret = eval(s, env);
            //
            if (ret instanceof MonkeyObjectReturnValue) {
                MonkeyObjectReturnValue o = (MonkeyObjectReturnValue) ret;
                return o.getValue();
            } else if (ret instanceof MonkeyObjectError) {
                MonkeyObjectError o = (MonkeyObjectError) ret;
                return o;
            }
        }
        return ret;
    }
    
    MonkeyObject evalBlockStatement(BlockStatement block, MonkeyEnvironment env) {
        MonkeyObject ret = new MonkeyObject();
        for (Statement s: block.getStatements()) {
            ret = eval(s, env);
            //
            if (ret != null) {
                String type = ret.getType();
                if (type.equals(MonkeyObject.RETURN_VALUE_OBJ) || type.equals(MonkeyObject.ERROR_OBJ)) {
                    return ret;
                }
            }
        }
        return ret;
    }

    MonkeyObject getBoolean(boolean value) {
        if (value) {
            return TRUE;
        }
        //
        return FALSE;
    }

    MonkeyObject evalPrefixExpression(String operator, MonkeyObject right) {
        if (operator.equals("!")) {
            return evalBangOperatorExpression(right);
        } else if (operator.equals("-")) {
            return evalMinusPrefixOperatorExpression(right);
        }
        return newError(String.format("unknown operator: %s%s", operator, right.getType()));
    }

    MonkeyObject evalInfixExpression(String operator, MonkeyObject left, MonkeyObject right) {
        if (left.getType().equals(MonkeyObject.INTEGER_OBJ) && 
                right.getType().equals(MonkeyObject.INTEGER_OBJ)) {
            return evalIntegerInfixExpression(operator, (MonkeyObjectInteger)left, 
                    (MonkeyObjectInteger)right);
        } else if (left.getType().equals(MonkeyObject.STRING_OBJ) &&
                right.getType().equals(MonkeyObject.STRING_OBJ)) {
            return evalStringInfixExpression(operator, (MonkeyObjectString)left, 
                    (MonkeyObjectString)right);
        } else if (operator.equals("==")) {
            return getBoolean(left == right);
        } else if (operator.equals("!=")) {
            return getBoolean(left != right);
        } else if (!left.getType().equals(right.getType())) {
            return newError(String.format("type mismatch: %s %s %s", 
                    left.getType(), operator, right.getType()));
        }
        return newError(String.format("unknown operator: %s %s %s", 
                    left.getType(), operator, right.getType()));
    }
    
    MonkeyObject evalIntegerInfixExpression(String operator, 
            MonkeyObjectInteger left, MonkeyObjectInteger right) {
        BigDecimal leftVal = left.getValue();
        BigDecimal rightVal = right.getValue();
        //
        MonkeyObjectInteger o = new MonkeyObjectInteger();
        if (operator.equals("+")) {
            try {
                o.setValue(leftVal.add(rightVal));
                return o;
            } catch (Exception e) {
                return NULL; 
            }
        } else if (operator.equals("-")) {
            try {
                o.setValue(leftVal.subtract(rightVal));
                return o;
            } catch (Exception e) {
                return NULL; 
            }
        } else if (operator.equals("*")) {
            try {
                o.setValue(leftVal.multiply(rightVal));
                return o;
            } catch (Exception e) {
                return NULL; 
            }
        } else if (operator.equals("/")) {
            try {
                o.setValue(leftVal.divide(rightVal, RoundingMode.DOWN));
                return o;
            } catch (Exception e) {
                return NULL;
            }
        } else if (operator.equals("<")) {
            return getBoolean(leftVal.compareTo(rightVal) < 0);
        } else if (operator.equals(">")) {
            return getBoolean(leftVal.compareTo(rightVal) > 0);
        } else if (operator.equals("==")) {
            return getBoolean(leftVal.compareTo(rightVal) == 0);
        } else if (operator.equals("!=")) {
            return getBoolean(leftVal.compareTo(rightVal) != 0);
        }
        return newError(String.format("unknown operator: %s %s %s", left.getType(),
                operator, right.getType()));
    }

    MonkeyObject evalStringInfixExpression(String operator, 
            MonkeyObjectString left, MonkeyObjectString right) {
        String leftVal = left.getValue();
        String rightVal = right.getValue();
        //
        MonkeyObjectString o = new MonkeyObjectString();
        if (!operator.equals("+")) {
            return newError(String.format("unknown operator: %s %s %s", 
                    left.getType(), operator, right.getType()));
        }
        //
        o.setValue(leftVal + rightVal);
        return o;
    }
    
    MonkeyObject evalBangOperatorExpression(MonkeyObject right) {
        if (right == TRUE) {
            return FALSE;
        } else if (right == FALSE) {
            return TRUE;
        } else if (right == NULL) {
            return TRUE;
        }
        return FALSE;
    }

    MonkeyObject evalMinusPrefixOperatorExpression(MonkeyObject right) {
        if (!right.getType().equals(MonkeyObject.INTEGER_OBJ)) {
            return newError(String.format("unknown operator: -%s", right.getType()));
        }
        //
        MonkeyObjectInteger o = new MonkeyObjectInteger();
        BigDecimal val = ((MonkeyObjectInteger) right).getValue();
        o.setValue(val.multiply(BigDecimal.valueOf(-1)));
        return o;
    }

    MonkeyObject evalIfExpression(MonkeyIfExpression expression, MonkeyEnvironment env) {
        MonkeyObject condition = eval(expression.getCondition(), env);
        if (isError(condition)) {
            return condition;
        }
        //
        if (isTruthy(condition)) {
            return eval(expression.getConsequence(), env);
        } else if (!expression.getAlternative().isEmpty()) {
            return eval(expression.getAlternative(), env);
        } else {
            return NULL;
        }
    }
    
    MonkeyObject evalIdentifier(Identifier ident, MonkeyEnvironment env) {
        MonkeyObject val = env.get(ident.getValue());
        if (val != null) {
            return val;
        }
        //
        MonkeyObjectBuiltin builtin = MonkeyBuiltins.get(ident.getValue());
        if (builtin != null) {
            return builtin;
        }
        //
        return newError(String.format("identifier not found: %s", ident.getValue()));
    }
    
    List<MonkeyObject> evalExpressions(List<Expression> exp, MonkeyEnvironment env) {
        List<MonkeyObject> result = new ArrayList<MonkeyObject>();
        //
        for (Expression e: exp) {
            MonkeyObject evaluated = eval(e, env);
            if (isError(evaluated)) {
                result.add(evaluated);
                return result;
            }
            result.add(evaluated);
        }
        //
        return result;
    }
    
    MonkeyObject evalIndexExpression(MonkeyObject left, MonkeyObject index) {
        if (left.getType().equals(MonkeyObject.ARRAY_OBJ) &&
                index.getType().equals(MonkeyObject.INTEGER_OBJ)) {
            return evalArrayIndexExpression((MonkeyObjectArray)left, 
                    (MonkeyObjectInteger)index);
        } else if (left.getType().equals(MonkeyObject.HASH_OBJ)) {
            return evalHashIndexExpression((MonkeyObjectHash)left, index);
        }
        return newError(String.format("index operator not supported: %s", 
                left.getType()));
    }
    
    MonkeyObject evalArrayIndexExpression(MonkeyObjectArray array, 
            MonkeyObjectInteger index) {
        int idx = index.getIntegerValue();
        int max = array.getElements().size() - 1;
        //
        if (idx < 0 || idx > max) {
            return NULL;
        }
        //
        return array.getElements().get(idx);
    }
    
    MonkeyObject evalHashLiteral(MonkeyHashLiteral node, MonkeyEnvironment env) {
        Map<MonkeyHashKey, MonkeyHashPair> pairs = new HashMap<MonkeyHashKey, MonkeyHashPair>();
        //
        for (Expression k: node.getPairs().keySet()) {
            MonkeyObject key = eval(k, env);
            if (isError(key)) {
                return key;
            }
            //
            if (!(key instanceof MonkeyHashable)) {
                return newError(String.format("unusable as hash key: %s", 
                        key.getType()));
            }
            //
            Expression v = node.getPairs().get(k);
            MonkeyObject val = eval(v, env);
            if (isError(val)) {
                return val;
            }
            //
            MonkeyHashKey hashed = ((MonkeyHashable)key).hashKey();
            MonkeyHashPair p = new MonkeyHashPair();
            p.setKey(key);
            p.setValue(val);
            try {
                pairs.put(hashed, p);
            } catch (Exception e) {
                return NULL; 
            }
        }
        //
        MonkeyObjectHash o = new MonkeyObjectHash();
        o.setPairs(pairs);
        return o;
    }
    
    MonkeyObject evalHashIndexExpression(MonkeyObjectHash hashtable, MonkeyObject index) {
        if (!(index instanceof MonkeyHashable)) {
            return newError(String.format("unusable as hash key: %s", index.getType()));
        }
        //
        MonkeyHashable hashIndex = (MonkeyHashable)index;
        MonkeyHashPair pair = hashtable.getPairs().get(hashIndex.hashKey());
        if (pair == null) {
            return NULL;
        }
        //
        return pair.getValue();
    }
    
    MonkeyObject applyFunction(MonkeyObject fn, List<MonkeyObject> args) {
        if (fn instanceof MonkeyObjectFunction) {
            MonkeyObjectFunction f = (MonkeyObjectFunction)fn;
            MonkeyEnvironment extendedEnv = extendFunctionEnv(f, args);
            MonkeyObject evaluated = eval(f.getBody(), extendedEnv);
            return unwrapReturnValue(evaluated);
        } else if (fn instanceof MonkeyObjectBuiltin) {
            MonkeyObjectBuiltin f = (MonkeyObjectBuiltin)fn;
            MonkeyBuiltinCallable c = f.getFn();
            return c.call(this, args);
        }
        //
        return newError(String.format("not a function: %s", fn.getType()));
    }

    MonkeyEnvironment extendFunctionEnv(MonkeyObjectFunction fn, 
            List<MonkeyObject> args) {
        MonkeyEnvironment env = MonkeyEnvironment.newInstanceEnclosed(fn.getEnvironment());
        for (int i=0; i<fn.getParameters().size(); i++) {
            Identifier param = fn.getParameters().get(i);
            env.set(param.getValue(), args.get(i));
        }
        //
        return env;
    }

    MonkeyObject unwrapReturnValue(MonkeyObject obj) {
        if (obj instanceof MonkeyObjectReturnValue) {
            MonkeyObjectReturnValue o = (MonkeyObjectReturnValue)obj;
            return o.getValue();
        }
        //
        return obj;
    }
    
    boolean isTruthy(MonkeyObject obj) {
        if (obj == NULL) {
            return false;
        } else if (obj == TRUE) {
            return true;
        } else if (obj == FALSE) {
            return false;
        } else {
            return true;
        }
    }
    
    MonkeyObject newError(String message) {
        MonkeyObjectError ret = new MonkeyObjectError();
        ret.setMessage(message);
        return ret;
    }
    
    boolean isError(MonkeyObject obj) {
        if (obj != null) {
            return obj.getType().equals(MonkeyObject.ERROR_OBJ);
        }
        //
        return false;
    }

    public PrintStream getOutput() {
        return output;
    }

    public void setOutput(PrintStream output) {
        this.output = output;
    }
    
    public static MonkeyEvaluator newInstance() {
        return new MonkeyEvaluator();
    }        
}

class CompiUtil {
    public static String stringJoin(String delimiter, List list) {
        StringBuilder ret = new StringBuilder();
        //
        int max = list.size();
        for (int i=0; i<max; i++) {
            Object o = list.get(i);
            ret.append(o.toString());
            
            if (i < max-1) {
                ret.append(delimiter);
            }
        }
        //
        return ret.toString();
    }
    
    public static String readFile(File f) {
        StringBuilder ret = new StringBuilder();
        //
        try {
            Scanner scan = new Scanner(f);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                ret.append(line);
            }
            scan.close();
        } catch (Exception e) {
        }
        //
        return ret.toString();
    }
}

public class Compilador {
    public static final String VERSION = "0.6";
    public static final String TITLE = "Monkey.java " + VERSION;
    public static final String MESSAGE = "Press ENTER to quit";
    public static final String LINESEP = System.getProperty("line.separator");
    public static final String PROMPT = ">> ";
    
    public static String input(String s) {
        Scanner scan = new Scanner(System.in);
        System.out.print(s);
        try {
            return scan.nextLine();
        } catch (Exception e) {
            return "";
        }
    }
    
    public static void output(String s) {
        output(s, System.out);
    }

    public static void output(String s, PrintStream output) {
        output.println(s);
    }

    public static MonkeyEnvironment environmentFromMap(Map<String, Object> map) {
        MonkeyEnvironment e = new MonkeyEnvironment();

        for (String k: map.keySet()) {
            Object v = map.get(k);
            String key = null;
            MonkeyObject value = null;
            //
            if (k instanceof String) {
                key = k;
            } else {
                key = k.toString();
            }
            //
            if (v instanceof String) {
                value = new MonkeyObjectString((String) v);
            } else if (v instanceof Boolean) {
                value = new MonkeyObjectBoolean((Boolean) v);
            } else if (v instanceof Integer) {
                value = new MonkeyObjectInteger((Integer) v);
            } else {
                value = new MonkeyObjectString(v.toString());
            }
            //
            if (key != null && value != null) {
                e.set(key, value);
            }
        }
        return e;
    }
    
    public static MonkeyEnvironment environmentFromDictionary(Map<String, Object> dictionary) {
        return Compilador.environmentFromMap(dictionary);
    }

    public static void lexer() {
        Compilador.output(TITLE);
        Compilador.output(MESSAGE);
        while (true) {
            String inp = Compilador.input(PROMPT).trim();
            if (inp.length() == 0) {
                break;
            }
            Lexer l = Lexer.newInstance(inp);
            while (true) {
                Token t = l.nextToken();
                if (t.getType().equals(Token.EOF)) {
                    break;
                }
                Compilador.output(String.format("Type: %s, Literal: %s", 
                        t.getType(), t.getLiteral()));
            }
        }
    }

    public static void parser() {
        Compilador.output(TITLE);
        Compilador.output(MESSAGE);
        while (true) {
            String inp = Compilador.input(PROMPT).trim();
            if (inp.length() == 0) {
                break;
            }
            Lexer l = Lexer.newInstance(inp);
            MonkeyParser p = MonkeyParser.newInstance(l);
            MonkeyProgram program = p.parseProgram();
            //
            List<String> errors = p.getErrors();
            if (!errors.isEmpty()) {
                Compilador.printParseErrors(errors);
                continue;
            }
            //
            Compilador.output(program.toString());
        }
    }
    
    public static void printParseErrors(List<String> errors) {
        for (String e: errors) {
            Compilador.output(String.format("PARSER ERROR: %s", e));
        }
    }

    public static void printParseErrors(List<String> errors, PrintStream output) {
        for (String e: errors) {
            Compilador.output(String.format("PARSER ERROR: %s", e), output);
        }
    }
    
    public static void evaluator() {
        Compilador.output(MESSAGE);
        MonkeyEnvironment env = MonkeyEnvironment.newInstance();
        while (true) {
            String inp = Compilador.input(PROMPT).trim();
            if (inp.length() == 0) {
                break;
            }
            Lexer l = Lexer.newInstance(inp);
            MonkeyParser p = MonkeyParser.newInstance(l);
            MonkeyProgram program = p.parseProgram();
            //
            List<String> errors = p.getErrors();
            if (!errors.isEmpty()) {
                Compilador.printParseErrors(errors);
                continue;
            }
            MonkeyEvaluator evaluator = MonkeyEvaluator.newInstance();
            MonkeyObject evaluated = evaluator.eval(program, env);
            if (evaluated != null) {
                Compilador.output(evaluated.inspect());
            }
        }
    }
    
    public static void evaluatorString(String s, MonkeyEnvironment environ, PrintStream output) {
        MonkeyEnvironment env;
        if (environ == null || !(environ instanceof MonkeyEnvironment)) {
            env = MonkeyEnvironment.newInstance();
        } else {
            env = environ;
        }
        Lexer l = Lexer.newInstance(s);
        MonkeyParser p = MonkeyParser.newInstance(l);
        MonkeyProgram program = p.parseProgram();
        //
        List<String> errors = p.getErrors();
        if (!errors.isEmpty()) {
            Compilador.printParseErrors(errors, output);
            return;
        }
        //
        MonkeyEvaluator evaluator = MonkeyEvaluator.newInstance();
        evaluator.setOutput(output);
        MonkeyObject evaluated = evaluator.eval(program, env);
        if (evaluated != null) {
            Compilador.output(evaluated.inspect(), output);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            Compilador.evaluator();
        } else {
            String t = args[0];
            String s = t;
            File f = new File(t);
            if (f.exists()) {
                try {
                    s = CompiUtil.readFile(f);
                } catch (Exception e) {
                }
            }
            if (s.length() > 0) {
                Compilador.evaluatorString(s, null, System.out);
            }
        }
    }
}