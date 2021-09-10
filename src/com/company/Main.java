package com.company;

import com.company.interpreter.Interpreter;
import com.company.parser.Parser;
import com.company.syntax.AstPrinter;
import com.company.syntax.Statement;
import com.company.tokenizer.Scanner;
import com.company.tokenizer.Token;
import com.company.tokenizer.TokenType;

import com.company.syntax.Expression;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // write your code here
        // -123 * (45.67)
//        Expression expression = new Expression.Binary(
//                new Expression.Unary(
//                        new Token(TokenType.MINUS, "-", null, 1),
//                        new Expression.Literal(123)),
//                new Token(TokenType.STAR, "*", null, 1),
//                new Expression.Grouping(
//                        new Expression.Literal(45.67)));
//
//        System.out.println(new AstPrinter().print(expression));

        Scanner scanner = new Scanner("{ var a = 1; {var b = 1; {var c = 2;}}}");

        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parser();
        Interpreter interpreter = new Interpreter();
        interpreter.interpreter(statements);
//        Parser parser = new Parser();
//        parser.parser();
    }
}
