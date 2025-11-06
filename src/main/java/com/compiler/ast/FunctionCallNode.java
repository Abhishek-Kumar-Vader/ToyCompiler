package com.compiler.ast;

import com.compiler.token.Token;

import java.util.List;

public class FunctionCallNode implements ExpressionNode{
    public final Token functionName;
    public final List<ExpressionNode> arguments;

    public FunctionCallNode(Token functionName, List<ExpressionNode> arguments){
        this.functionName=functionName;
        this.arguments=arguments;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitFunctionCallNode(this);
    }
}
