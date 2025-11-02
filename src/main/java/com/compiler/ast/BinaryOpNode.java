package com.compiler.ast;

import com.compiler.token.Token;

public class BinaryOpNode implements ExpressionNode{
    public final ExpressionNode left;
    public final Token operator;
    public final ExpressionNode right;

    public BinaryOpNode(ExpressionNode left, Token operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitBinaryOpNode(this);
    }
}
