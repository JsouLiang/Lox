package com.company.parser;

import com.company.syntax.Expression;
import com.company.tokenizer.Token;
import com.company.tokenizer.TokenType;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    /**
     * Like the scanner, the parser consumes a flat input sequence, only now we're reading tokens instead of character
     *
     * We store the list of tokens and use current to point the next token eagerly waiting to be parsed
     * @param tokens
     */
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * expression: equality
     * @return
     */
    private Expression expression() {
        return equality();
    }

    /**
     * equality: comparison ( ("!=" | "==") comparison )*
     * @return
     */
    private Expression equality() {
        Expression expression = comparison();
        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    /**
     * comparison: term ( (">=" | "<=" | "<" | ">">) term )*
     * @return
     */
    private Expression comparison() {
        Expression left = term();
        while (match(TokenType.LESS_EQUAL, TokenType.LESS, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            left = new Expression.Binary(left, operator, right);
        }
        return left;
    }

    /**
     * term: factor (("+" | "-") factor)*
     * @return
     */
    private Expression term() {
        Expression left = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = factor();
            left = new Expression.Binary(left, operator, right);
        }
        return left;
    }

    /**
     * factor: unary (("*" |"/") unary)*
     * @return
     */
    private Expression factor() {
        Expression left = unary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = unary();
            left = new Expression.Binary(left, operator, right);
        }
        return left;
    }

    /**
     * unary: ("!" | "-") unary | primary
     * @return
     */
    private Expression unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expression unary = unary();
            return new Expression.Unary(operator, unary);
        }
        return primary();
    }

    /**
     * primary: NUMBER | STRING | "true" | "false" | "nil"
     *                | "(" expression ")" ;
     * @return
     */
    private Expression primary() {
        if (match(TokenType.TRUE)) return new Expression.Literal(true);
        if (match(TokenType.FALSE)) return new Expression.Literal(false);
        if (match(TokenType.NIL)) return new Expression.Literal(null);
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.Literal(previous().literal);
        }
        if (match(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            // TODO(weiguoliang) consume )
            return new Expression.Grouping(expression);
        }
        return null;
    }

    /**
     * Advance the token if it matches the types
     * @param tokenTypes
     * @return
     */
    private boolean match(TokenType... tokenTypes) {
        for (TokenType type: tokenTypes) {
            if (!check(type)) {
                advance();
                return false;
            }
        }
        return true;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() { return current == tokens.size();}

    private Token peek() { return tokens.get(current); }

    private Token previous() { return tokens.get(current - 1);}
}
