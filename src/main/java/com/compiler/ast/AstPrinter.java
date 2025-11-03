package com.compiler.ast;

public class AstPrinter implements NodeVisitor<String>{
    public String print(Node node){
        return node.accept(this);
    }

    private String parenthesize(String name, Node... nodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Node node : nodes) {
            builder.append(" ");
            builder.append(node.accept(this));
        }
        builder.append(")");
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
    public String visitFunctionCallNode(FunctionCallNode node) { return ""; }

    @Override
    public String visitAssignmentNode(AssignmentNode node) { return ""; }

    @Override
    public String visitPrintNode(PrintNode node) { return ""; }

    @Override
    public String visitIfNode(IfNode node) { return ""; }

    @Override
    public String visitWhileNode(WhileNode node) { return ""; }

    @Override
    public String visitReturnNode(ReturnNode node) { return ""; }

    @Override
    public String visitBlockNode(BlockNode node) { return ""; }

    @Override
    public String visitFunctionDeclNode(FunctionDeclNode node) { return ""; }

    @Override
    public String visitProgramNode(ProgramNode node) { return ""; }
}
