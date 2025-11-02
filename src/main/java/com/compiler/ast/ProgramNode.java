package com.compiler.ast;

import java.util.List;

public class ProgramNode implements Node {
    public final List<FunctionDeclNode> functions;

    public ProgramNode(List<FunctionDeclNode> functions) {
        this.functions = functions;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visitProgramNode(this);
    }
}
