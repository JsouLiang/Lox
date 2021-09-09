package com.company.syntax;

import com.company.tokenizer.Token;

public abstract class Expression {

    public interface Visitor<R> {
        R visitBinaryExpression(Binary expression);

        R visitGroupingExpression(Grouping expression);

        R visitLiteralExpression(Literal expression);

        R visitUnaryExpression(Unary expression);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class Binary extends Expression {
        public Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public Expression getLeft() {
            return left;
        }

        public Token getOperator() {
            return operator;
        }

        public Expression getRight() {
            return right;
        }

        final Expression left;
        final Token operator;
        final Expression right;

        @Override
        public  <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }
    }

    public static class Grouping extends Expression {
        public Grouping(Expression expression) {
            this.expression = expression;
        }

        public Expression getExpression() {
            return expression;
        }

        final Expression expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }
    }

    public static class Literal extends Expression {
        public Literal(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        final Object value;

        @Override
       public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }
    }

    public static class Unary extends Expression {
        public Unary(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        public Token getOperator() {
            return operator;
        }

        public Expression getRight() {
            return right;
        }

        final Token operator;
        final Expression right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }
    }

}
