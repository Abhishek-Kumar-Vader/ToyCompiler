package com.compiler.ast;

import com.compiler.token.Token;
import java.util.List;

public class FunctionDeclNode implements Node {
    public final Token functionName;
    public final List<Token> parameters;
    public final BlockNode body;

    public FunctionDeclNode(Token functionName, List<Token> parameters, BlockNode body) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visitFunctionDeclNode(this);
    }
}
