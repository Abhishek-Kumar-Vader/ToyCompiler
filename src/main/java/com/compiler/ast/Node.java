package com.compiler.ast;

public interface Node {
    <T> T accept (NodeVisitor<T> visitor);
}
