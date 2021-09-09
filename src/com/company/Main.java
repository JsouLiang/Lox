package com.company;

import com.company.interpreter.Interpreter;
import com.company.syntax.AstPrinter;
import com.company.tokenizer.Token;
import com.company.tokenizer.TokenType;

import com.company.syntax.Expression;

public class Main {

    public static void main(String[] args) {
        // write your code here
        // -123 * (45.67)
        Expression expression = new Expression.Binary(
                new Expression.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expression.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expression.Grouping(
                        new Expression.Literal(45.67)));

        System.out.println(new AstPrinter().print(expression));
        new Interpreter().interpreter(expression);
    }
}
