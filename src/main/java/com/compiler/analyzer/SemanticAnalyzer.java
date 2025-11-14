package com.compiler.analyzer;

import com.compiler.ast.*;
import com.compiler.token.*;


public class SemanticAnalyzer implements NodeVisitor<Void> {
    private final SymbolTable symbolTable = new SymbolTable();

    public void analyze(Node node) {
        node.accept(this);
    }

    @Override
    public Void visitProgramNode(ProgramNode node) {
        for (FunctionDeclNode function : node.functions) {
            function.accept(this);
        }
        return null;
    }

    @Override
    public Void visitFunctionDeclNode(FunctionDeclNode node) {
        // Declare the function name in the current (global) scope
        symbolTable.declare(node.functionName.getValue());
        symbolTable.initialize(node.functionName.getValue());

        symbolTable.enterScope(); // Enter function scope
        for (Token param : node.parameters) {
            symbolTable.declare(param.getValue());
            symbolTable.initialize(param.getValue()); // Parameters are considered initialized
        }
        node.body.accept(this);
        symbolTable.exitScope(); // Exit function scope
        return null;
    }

    @Override
    public Void visitBlockNode(BlockNode node) {
        symbolTable.enterScope(); // Enter block scope
        for (StatementNode statement : node.statements) {
            statement.accept(this);
        }
        symbolTable.exitScope(); // Exit block scope
        return null;
    }

    @Override
    public Void visitExpressionStatementNode(ExpressionStatementNode node) {
        node.expression.accept(this);
        return null;
    }

    @Override
    public Void visitAssignmentNode(AssignmentNode node) {
        String varName = node.identifier.getValue();
        // First, check if the variable on the left has been declared
        if (!symbolTable.isDeclared(varName)) {
            // If not, we'll treat this as a declaration
            symbolTable.declare(varName);
        }

        // Analyze the expression on the right
        node.expression.accept(this);

        // Now, mark the variable as initialized
        symbolTable.initialize(varName);
        return null;
    }

    @Override
    public Void visitVariableNode(VariableNode node) {
        // When a variable is *used*, check if it has been declared and initialized
        String varName = node.name;
        if (!symbolTable.isDeclared(varName)) {
            throw new RuntimeException("Semantic Error: Variable '" + varName + "' not declared.");
        }
        if (!symbolTable.isInitialize(varName)) {
            // This is more of a warning in some languages, but we can enforce it
            throw new RuntimeException("Semantic Error: Variable '" + varName + "' may not have been initialized.");
        }
        return null;
    }

    @Override
    public Void visitFunctionCallNode(FunctionCallNode node) {
        // Check if the function itself is declared
        if (!symbolTable.isDeclared(node.functionName.getValue())) {
            throw new RuntimeException("Semantic Error: Function '" + node.functionName.getValue() + "' not declared.");
        }
        // Analyze the arguments
        for (ExpressionNode arg : node.arguments) {
            arg.accept(this);
        }
        return null;
    }

    // --- Other Visitor Methods ---

    @Override
    public Void visitIfNode(IfNode node) {
        node.condition.accept(this);
        node.thenBlock.accept(this);
        if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }
        return null;
    }

    @Override
    public Void visitWhileNode(WhileNode node) {
        node.condition.accept(this);
        node.body.accept(this);
        return null;
    }

    @Override
    public Void visitReturnNode(ReturnNode node) {
        if (node.expression != null) {
            node.expression.accept(this);
        }
        return null;
    }

    @Override
    public Void visitPrintNode(PrintNode node) {
        node.expression.accept(this);
        return null;
    }

    @Override
    public Void visitBinaryOpNode(BinaryOpNode node) {
        node.left.accept(this);
        node.right.accept(this);
        return null;
    }

    @Override
    public Void visitUnaryOpNode(UnaryOpNode node) {
        node.operand.accept(this);
        return null;
    }

    // Literals don't need any semantic checks
    @Override
    public Void visitIntLiteralNode(IntLiteralNode node) { return null; }
    @Override
    public Void visitFloatLiteralNode(FloatLiteralNode node) { return null; }
}
