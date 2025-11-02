package com.compiler.ast;

import com.compiler.token.Token;

public class UnaryOpNode implements ExpressionNode {
    public final Token operator;
    public final ExpressionNode operand;

    public UnaryOpNode(Token operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visitUnaryOpNode(this);
    }
}
