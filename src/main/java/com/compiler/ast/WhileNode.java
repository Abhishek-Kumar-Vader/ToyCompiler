package com.compiler.ast;

import com.compiler.token.Token;

public class WhileNode implements StatementNode{
    public final Token token;
    public final ExpressionNode condition;
    public final BlockNode body;

    public WhileNode(Token token, ExpressionNode condition, BlockNode body){
        this.token=token;
        this.condition=condition;
        this.body=body;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitWhileNode(this);
    }
}
