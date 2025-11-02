package com.compiler.ast;

import com.compiler.token.Token;

public class PrintNode implements StatementNode{
    public final Token token;
    public final ExpressionNode expression;

    public PrintNode(Token token, ExpressionNode expression){
        this.token=token;
        this.expression=expression;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitPrintNode(this);
    }
}
