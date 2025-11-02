package com.compiler.ast;

import java.util.List;

public class BlockNode implements Node{
    public final List<StatementNode> statements;

    public BlockNode(List<StatementNode> statements){
        this.statements=statements;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor){
        return visitor.visitBlockNode(this);
    }
}
