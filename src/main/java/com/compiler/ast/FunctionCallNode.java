package com.compiler.ast;

import com.compiler.token.Token;

import java.util.List;

public class FunctionCallNode implements ExpressionNode{
    public final Token token;
    public final List<ExpressionNode> arguments;

    public FunctionCallNode(Token token, List<ExpressionNode> arguments){
        this.token=token;
        this.arguments=arguments;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitFunctionCallNode(this);
    }
}
