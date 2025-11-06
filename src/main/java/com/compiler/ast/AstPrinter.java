package com.compiler.ast;

import java.util.List;

public class AstPrinter implements NodeVisitor<String> {

    public String print(Node node) {
        return node.accept(this);
    }

    private String parenthesize(String name, Node... nodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Node node : nodes) {
            builder.append(" ");
            if (node == null) {
                builder.append("null");
            } else {
                builder.append(node.accept(this));
            }
        }
        builder.append(")");
        return builder.toString();
    }

    private String parenthesizeBlock(String name, List<? extends Node> nodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Node node : nodes) {
            builder.append("\n  ").append(node.accept(this));
        }
        builder.append("\n)");
        return builder.toString();
    }

    @Override
    public String visitIntLiteralNode(IntLiteralNode node) {
        return String.valueOf(node.value);
    }

    @Override
    public String visitFloatLiteralNode(FloatLiteralNode node) {
        return String.valueOf(node.value);
    }

    @Override
    public String visitVariableNode(VariableNode node) {
        return node.name;
    }

    @Override
    public String visitBinaryOpNode(BinaryOpNode node) {
        return parenthesize(node.operator.getValue(), node.left, node.right);
    }

    @Override
    public String visitUnaryOpNode(UnaryOpNode node) {
        return parenthesize(node.operator.getValue(), node.operand);
    }

    @Override
    public String visitFunctionCallNode(FunctionCallNode node) {
        StringBuilder builder = new StringBuilder();
        builder.append("(call ").append(node.functionName.getValue());
        for (Node arg : node.arguments) {
            builder.append(" ").append(arg.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitAssignmentNode(AssignmentNode node) {
        return parenthesize("= " + node.identifier.getValue(), node.expression);
    }

    @Override
    public String visitPrintNode(PrintNode node) {
        return parenthesize("print", node.expression);
    }

    @Override
    public String visitIfNode(IfNode node) {
        if (node.elseBlock == null) {
            return parenthesize("if", node.condition, node.thenBlock);
        }
        return parenthesize("if-else", node.condition, node.thenBlock, node.elseBlock);
    }

    @Override
    public String visitWhileNode(WhileNode node) {
        return parenthesize("while", node.condition, node.body);
    }

    @Override
    public String visitReturnNode(ReturnNode node) {
        return parenthesize("return", node.expression);
    }

    @Override
    public String visitBlockNode(BlockNode node) {
        return parenthesizeBlock("block", node.statements);
    }

    @Override
    public String visitExpressionStatementNode(ExpressionStatementNode node) {
        return parenthesize("expr-stmt", node.expression);
    }

    @Override
    public String visitFunctionDeclNode(FunctionDeclNode node) {
        StringBuilder builder = new StringBuilder();
        builder.append("(fun ").append(node.functionName.getValue()).append(" (");
        for (int i = 0; i < node.parameters.size(); i++) {
            builder.append(node.parameters.get(i).getValue());
            if (i < node.parameters.size() - 1) builder.append(" ");
        }
        builder.append(") ");
        builder.append(node.body.accept(this));
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitProgramNode(ProgramNode node) {
        return parenthesizeBlock("program", node.functions);
    }
}