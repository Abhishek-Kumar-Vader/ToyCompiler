package com.compiler.ast;

import com.compiler.token.Token;

public class AssignmentNode implements StatementNode{
    public final Token identifier;
    public final ExpressionNode expression;

    public AssignmentNode(Token identifier, ExpressionNode expression){
        this.identifier=identifier;
        this.expression=expression;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitAssignmentNode(this);
    }
}
