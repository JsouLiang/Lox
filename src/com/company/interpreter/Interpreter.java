package com.company.interpreter;

import com.company.environment.Environment;
import com.company.syntax.Expression;
import com.company.syntax.Statement;

import java.util.List;

/**
 * 当解析完字符串生成语法树之后，接下来就是对每个语法节点进行解释计算
 * 事实上，我们可以把这个操作看成每个节点解释其自己的语义
 * 比如 Literal 节点本事就是一个值节点，所以执行该节点就是直接返回其值，对于 binary 节点则是根据 operator 计算它 left 和 right 在该操作下的结果值
 * <p>
 * 这里我们使用访问者模式，来对每种节点指定操作逻辑
 */
public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {

    private Environment environment = new Environment();

    public void interpreter(List<Statement> statements) {
        try {
            for (Statement stmt: statements) {
                execute(stmt);
            }
        } catch (Exception e/*RuntimeError error*/) {

        }
    }

    private void execute(Statement statement) {
        statement.accept(this);
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        // 计算表达式左子树和右子树的值
        Object left = evaluate(expression.getLeft());
        Object right = evaluate(expression.getRight());

        switch (expression.getOperator().type) {
            case MINUS:
                return (double) left - (double) right;
            case PLUS: {
                if (left instanceof Number && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                break;
            }
            case SLASH:
                return (double) left / (double) right;
            case STAR:
                return (double) left * (double) right;
            case GREATER:
                return (double) left > (double) right;
            case GREATER_EQUAL:
                return (double) left >= (double) right;
            case LESS:
                return (double) left < (double) right;
            case LESS_EQUAL:
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL:
                return isEqual(left, right);
        }
        return null;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) { return true; }
        if (a == null) { return false; }
        if (b == null) { return false; }
        return a.equals(b);
    }

    /**
     * 对于括号的表达式，括号内部是其真实的表达式，所以为了计算该括号表达式值，我们需要递归的对其子表达式进行计算
     *
     * @param expression
     * @return
     */
    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        return evaluate(expression.getExpression());
    }

    private Object evaluate(Expression exp) {
        return exp.accept(this);
    }


    /**
     * 值节点，直接返回其值即可
     */
    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.getValue();
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = expression.getRight();
        switch (expression.getOperator().type) {
            case MINUS: {
                return -(double) right;
            }
            case BANG: {
                return !isTruthy(right);
            }
        }
        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    /**
     * 获取声明变量的值
     * @param expression
     * @return
     */
    @Override
    public Object visitVariableExpression(Expression.Variable expression) {
        return environment.get(expression.getName());
    }

    @Override
    public Object visitAssignExpression(Expression.Assign expression) {
        Object value = evaluate(expression.getValue());
        environment.assign(expression.getName(), value);
        return value;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExprStatement statement) {
        // evaluate the statement expression
        evaluate(statement.getExpr());
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.PrintStatement statement) {
        Object value = evaluate(statement.getExpr());
        System.out.println("");

        return null;
    }

    @Override
    public Void visitVarDeclaration(Statement.VarDeclaration declaration) {
        Object value = null;
        // if the declaration has an expression, evaluate this
        if (declaration.getExpression() != null) {
            value = evaluate(declaration.getExpression());
        }
        environment.define(declaration.getName().lexeme, value);
        return null;
    }

    @Override
    public Void visitBlockStatement(Statement.BlockStatement blockStatement) {
        // 当访问到 block scope 时，创建一个新的 Environment 同时当前的environment 作为新 Environment 的 enclosing
        executeBlock(blockStatement.getStatements(), new Environment(environment));
        return null;
    }

    void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }
}
