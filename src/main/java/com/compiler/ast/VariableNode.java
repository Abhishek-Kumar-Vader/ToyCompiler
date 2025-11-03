package com.compiler.ast;

import com.compiler.token.Token;

public class VariableNode implements ExpressionNode{
    public final Token token;
    public final String name;

    public VariableNode(Token token){
        this.token=token;
        this.name= token.getValue();
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitVariableNode(this);
    }
}
