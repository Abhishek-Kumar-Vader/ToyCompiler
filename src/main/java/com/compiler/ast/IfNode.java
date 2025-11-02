package com.compiler.ast;

import com.compiler.token.Token;


public class IfNode implements StatementNode{
    public final Token token;
    public final ExpressionNode expression;
    public final BlockNode thenBlock;
    public final BlockNode elseBlock;

    public IfNode(Token token, ExpressionNode expression, BlockNode thenBlock, BlockNode elseBlock){
        this.token=token;
        this.expression=expression;
        this.thenBlock=thenBlock;
        this.elseBlock=elseBlock;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visitIfNode(this);
    }
}
