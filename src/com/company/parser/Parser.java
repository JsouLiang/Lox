package com.company.parser;

import com.company.syntax.Expression;
import com.company.syntax.Statement;
import com.company.tokenizer.Token;
import com.company.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    /**
     * Like the scanner, the parser consumes a flat input sequence, only now we're reading tokens instead of character
     *
     * We store the list of tokens and use current to point the next token eagerly waiting to be parsed
     * @param tokens
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * program: declaration* EOF ;
     *
     * @return
     */
    public List<Statement> parser() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    /**
     * declaration: varDeclaration | statement
     *
     * Any place where a declaration is allowed also allows non-declaring statements,
     * so the declaration rule falls through to statement.
     * Obviously, you can declare stuff at the top level of a script, so program routes to the new rule.
     * @return
     */
    private Statement declaration() {
        try {
            if (advanceIfMatch(TokenType.VAR)) {
                return varDeclaration();
            }
            return statement();
        } catch ( ParseError error) {
//            synchronize();
            return null;
        }
    }

    /**
     * statement: exprStatement | ifStatement | printStatement | blockStatement
     * @return
     */
    private Statement statement() {
        if (advanceIfMatch(TokenType.PRINT)) {
            return printStatement();
        }
        if (advanceIfMatch(TokenType.LEFT_BRACE)) {
            return blockStatement();
        }
        return expressionStatement();
    }

    /**
     * exprStatement: expression ";"
     * @return
     */
    private Statement expressionStatement() {
        Expression expression = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Statement.ExprStatement(expression);
    }

    private Statement ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after 'if' condition.");

        Statement thenBranch = statement();
        Statement elseBranch = null;
        if (advanceIfMatch(TokenType.ELSE)) {
            elseBranch = statement();
        }
        return new Statement.IfStatement(condition, thenBranch, elseBranch);
    }

    /**
     * printStatement: "print" expression ";"
     * @return
     */
    private Statement printStatement() {
        Expression expression = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Statement.PrintStatement(expression);
    }

    /**
     * blockStatement: "{" declaration* "}"
     * @return
     */
    private Statement blockStatement() {
        List<Statement> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block");
        return new Statement.BlockStatement(statements);
    }

    /**
     * varStatement: "var" IDENTIFIER ("=" expression)? ";"
     * @return
     */
    private Statement varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
        Expression initial = null;
        // matched the =
        if (advanceIfMatch(TokenType.EQUAL)) {
            initial = expression();
        }
        consume(TokenType.SEMICOLON, "Except ';' after variable declaration.");
        return new Statement.VarDeclaration(name, initial);
    }

    /**
     * expression: assignment
     * @return
     */
    private Expression expression() {
        return assignment();
    }


    /**
     * assignment: IDENTIFIER "=" assignment | equality
     * 上面的语义表示赋值表达式要么是一个
     * 1. 标识符后面跟一个=在跟一个值或者表达式
     * 2. 判等表达式
     * @return
     */
    private Expression assignment() {
        Expression expr = equality();
        if (advanceIfMatch(TokenType.EQUAL)) {
            Token equals = previous();
            Expression value = assignment();
            if (expr instanceof Expression.Variable) {
                Token name = ((Expression.Variable)expr).getName();
                return new Expression.Assign(name, value);
            }
            error(equals, "Invalid assignment target");
        }
        return expr;
    }


    /**
     * equality: comparison ( ("!=" | "==") comparison )*
     * equality is left associative
     * a == b == c == e
     * 当解析到 (a == b )== c 时，a == b 是作为 第二个 == 的左子树
     * 这样可以确保他们的左结合性
     * @return
     */
    private Expression equality() {
        Expression expression = comparison();
        while (advanceIfMatch(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
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
        while (advanceIfMatch(TokenType.LESS_EQUAL, TokenType.LESS, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
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
        while (advanceIfMatch(TokenType.PLUS, TokenType.MINUS)) {
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
        while (advanceIfMatch(TokenType.STAR, TokenType.SLASH)) {
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
        if (advanceIfMatch(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expression unary = unary();
            return new Expression.Unary(operator, unary);
        }
        return primary();
    }

    /**
     * primary: NUMBER | STRING | "true" | "false" | "nil"
     *                | "(" expression ")" | IDENTIFIER;
     * @return
     */
    private Expression primary() {
        if (advanceIfMatch(TokenType.TRUE)) return new Expression.Literal(true);
        if (advanceIfMatch(TokenType.FALSE)) return new Expression.Literal(false);
        if (advanceIfMatch(TokenType.NIL)) return new Expression.Literal(null);
        if (advanceIfMatch(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.Literal(previous().literal);
        }
        if (advanceIfMatch(TokenType.IDENTIFIER)) {
            return new Expression.Variable(previous());
        }
        if (advanceIfMatch(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            // TODO(weiguoliang) consume )
            return new Expression.Grouping(expression);
        }
        throw error(peek(), "Expect expression.");
    }

    /**
     * Advance the token if it matches the types
     * @param tokenTypes
     * @return
     */
    private boolean advanceIfMatch(TokenType... tokenTypes) {
        for (TokenType type: tokenTypes) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
//        Lox.error(token, message);
        return new ParseError();
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
