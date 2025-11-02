package com.compiler.ast;

import com.compiler.token.Token;

public class IntLiteralNode implements ExpressionNode{
    public final Token token;
    public final int value;

    public IntLiteralNode(Token token, int value){
        this.token=token;
        this.value=value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitIntLiteralNode(this);
    }
}
