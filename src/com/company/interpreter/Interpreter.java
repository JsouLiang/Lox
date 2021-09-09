package com.company.interpreter;

import com.company.syntax.Expression;

import java.io.DataOutput;
import java.io.ObjectStreamException;

/**
 * 当解析完字符串生成语法树之后，接下来就是对每个语法节点进行解释计算
 * 事实上，我们可以把这个操作看成每个节点解释其自己的语义
 * 比如 Literal 节点本事就是一个值节点，所以执行该节点就是直接返回其值，对于 binary 节点则是根据 operator 计算它 left 和 right 在该操作下的结果值
 * <p>
 * 这里我们使用访问者模式，来对每种节点指定操作逻辑
 */
public class Interpreter implements Expression.Visitor<Object> {
    public Object interpreter(Expression exp) {
        return exp.accept(this);
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

}
