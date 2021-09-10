package com.company.tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.utils.*;

import static com.company.utils.Helpers.isDigit;

public class Scanner {
    private final List<Token> tokens = new ArrayList<>();
    private final String source;

    // 当前解析 Token 词法的开始位置
    // 解析到合法 token 时，其对应的字符串描述为：source.substring(start, current)
    private int start = 0;
    // 当前词法解析遍历到那个字符
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }


    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        return tokens;
    }

    private void scanToken() {
        // get current character
        char c = advance();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '/': {
                if (match('/')) { // commands
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }
            }
            break;

            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            case '"':
                string();
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (Helpers.isAlpha(c)) {
                    identifier();
                }
                // TODO(weiguoliang): Lox error
//                Lox.error
        }
    }


    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(tokenType, text, literal, line));
    }

    /**
     * current character is match the excepted
     * if match consume it
     *
     * @param excepted
     * @return
     */
    private boolean match(char excepted) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) == excepted) {
            advance();
            return true;
        }
        return false;
    }

    /**
     * character is at end
     *
     * @return
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * get the current character but doesn't consume the character
     * also called "lookahead"
     *
     * @return
     */
    private char peek(int offset) {
        if (isAtEnd()) {
            return '\0';
        }
        if (current + offset >= source.length()) {
            return '\0';
        }
        return source.charAt(current + offset);
    }

    private char peek() {
        return peek(0);
    }

    /**
     * Consumes the next character in the source and return it
     * <p>
     * advance() is for input, addToken() is for output
     *
     * @return
     */
    private char advance() {
        return source.charAt(current++);
    }


    private void string() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
//            Lox.error(line, "Unterminated string.");
            return;
        }
        // advance the closing "
        advance();
        // start is the begin ", current is the end "
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // number.number
        if (peek() == '.' && isDigit(peek(1))) {
            advance(); // .
            while (isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (Helpers.isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);

    }
}
