package com.company.syntax;

public class AstPrinter implements Expression.Visitor<String> {

    public String print(Expression exp) {
        return exp.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator.toString(), expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if (expression.value == null) return "nil";
        return expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        return parenthesize(expression.operator.toString(), expression.right);
    }

    private String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expression exp : expressions) {
            builder.append(" ");
            builder.append(exp.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}
