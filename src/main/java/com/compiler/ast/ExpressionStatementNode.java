package com.compiler.ast;

public class ExpressionStatementNode implements StatementNode{
    public final ExpressionNode expression;

    public ExpressionStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        // We need to add this to the visitor interface
        return visitor.visitExpressionStatementNode(this);
    }
}
