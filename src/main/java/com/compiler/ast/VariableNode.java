package com.compiler.ast;

import com.compiler.token.Token;

public class VariableNode implements ExpressionNode{
    public final Token token;
    public final int value;

    public VariableNode(Token token, int value){
        this.token=token;
        this.value=value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitVariableNode(this);
    }
}
