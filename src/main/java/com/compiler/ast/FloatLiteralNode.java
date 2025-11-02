package com.compiler.ast;

import com.compiler.token.Token;

public class FloatLiteralNode implements ExpressionNode{
    public final Token token;
    public final int value;

    public FloatLiteralNode(Token token, int value){
        this.token=token;
        this.value=value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitFloatLiteralNode(this);
    }
}
