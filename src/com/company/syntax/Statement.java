package com.company.syntax;
import com.company.syntax.Expression;
import com.company.tokenizer.Token;

import java.util.List;

public abstract class Statement {

    public interface Visitor<R> {
        R visitExpressionStatement(ExprStatement statement);

        R visitPrintStatement(PrintStatement statement);

        R visitVarDeclaration(VarDeclaration declaration);

        R visitBlockStatement(BlockStatement blockStatement);

        R visitIfStatement(IfStatement ifStatement);
    }

    public abstract <R> R accept(Visitor<R> statement);

    /**
     * Expression Statement
     */
    public static class ExprStatement extends Statement {
        public ExprStatement(Expression expr) {
            this.expr = expr;
        }

        public Expression getExpr() {
            return expr;
        }

        final Expression expr;

        @Override
        public <R> R accept(Visitor<R> statement) {
            return statement.visitExpressionStatement(this);
        }
    }

    /**
     * Print Statement
     */
    public static class PrintStatement extends Statement {
        public PrintStatement(Expression expr) {
            this.expr = expr;
        }

        public Expression getExpr() {
            return expr;
        }

        final Expression expr;

        @Override
        public <R> R accept(Visitor<R> statement) {
            return statement.visitPrintStatement(this);
        }
    }

    /**
     * Variable declaration statement
     *
     * A variable declaration statement brings a new variable into the world
     * Once that's done, a variable expression accesses that binding.
     */
    public static class VarDeclaration extends Statement {
        public VarDeclaration(Token name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }

        public Token getName() {
            return name;
        }

        public Expression getExpression() {
            return expression;
        }

        final Token name;
        final Expression expression;

        @Override
        public <R> R accept(Visitor<R> statement) {
            return statement.visitVarDeclaration(this);
        }
    }

    public static class BlockStatement extends Statement {
        public BlockStatement(List<Statement> statements) {
            this.statements = statements;
        }

        public List<Statement> getStatements() {
            return statements;
        }

        final List<Statement> statements;

        @Override
        public <R> R accept(Visitor<R> statement) {
            return statement.visitBlockStatement(this);
        }
    }

    public static class IfStatement extends Statement {
        public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        public Expression getCondition() {
            return condition;
        }

        public Statement getThenBranch() {
            return thenBranch;
        }

        public Statement getElseBranch() {
            return elseBranch;
        }

        final Expression condition;
        final Statement thenBranch;
        final Statement elseBranch;

        @Override
        public <R> R accept(Visitor<R> statement) {
            return statement.visitIfStatement(this);
        }
    }
}
