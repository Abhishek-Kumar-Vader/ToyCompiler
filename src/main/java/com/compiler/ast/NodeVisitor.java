package com.compiler.ast;

public interface NodeVisitor<T> {

    T visitIntLiteralNode(IntLiteralNode node);
    T visitFloatLiteralNode(FloatLiteralNode node);
    T visitVariableNode(VariableNode node);
    T visitBinaryOpNode(BinaryOpNode node);
    T visitUnaryOpNode(UnaryOpNode node);
    T visitFunctionCallNode(FunctionCallNode node);

    // Statements
    T visitAssignmentNode(AssignmentNode node);
    T visitPrintNode(PrintNode node);
    T visitIfNode(IfNode node);
    T visitWhileNode(WhileNode node);
    T visitReturnNode(ReturnNode node);
    T visitBlockNode(BlockNode node);

    // Declarations
    T visitFunctionDeclNode(FunctionDeclNode node);
    T visitProgramNode(ProgramNode node);

}
