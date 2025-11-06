package com.compiler.ast;

import com.compiler.token.Token;


public class IfNode implements StatementNode{
    public final Token token;
    public final ExpressionNode condition;;
    public final BlockNode thenBlock;
    public final BlockNode elseBlock;

    public IfNode(Token token, ExpressionNode condition, BlockNode thenBlock, BlockNode elseBlock){
        this.token=token;
        this.condition=condition;
        this.thenBlock=thenBlock;
        this.elseBlock=elseBlock;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visitIfNode(this);
    }
}
