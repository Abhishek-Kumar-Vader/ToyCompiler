package com.compiler.parser;

import com.compiler.ast.*;
import com.compiler.lexer.Lexer;
import com.compiler.token.Token;
import com.compiler.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(Lexer lexer) throws IOException {
        this.tokens = lexer.tokenizeAll();
    }

    // Main entry point
    public ProgramNode parse() {
        List<FunctionDeclNode> functions = new ArrayList<>();
        while (!isAtEnd()) {
            if (match(TokenType.FUNCTION)) {
                functions.add(functionDeclaration());
            } else {
                throw new RuntimeException("Parse Error: Expect 'function' at top level. Found: " + peek().getValue());
            }
        }
        return new ProgramNode(functions);
    }

    private FunctionDeclNode functionDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect function name.");
        consume(TokenType.LPAREN, "Expect '(' after function name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expect ')' after parameters.");
        consume(TokenType.LBRACE, "Expect '{' before function body.");
        BlockNode body = new BlockNode(block());
        return new FunctionDeclNode(name, parameters, body);
    }

    private List<StatementNode> block() {
        List<StatementNode> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(statement());
        }
        consume(TokenType.RBRACE, "Expect '}' after block.");
        return statements;
    }

    private StatementNode statement() {
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.RETURN)) return returnStatement();
        if (match(TokenType.LBRACE)) return new BlockNode(block());
        return expressionStatement();
    }

    private StatementNode expressionStatement() {
        ExpressionNode expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression or assignment.");
        if (expr instanceof BinaryOpNode && ((BinaryOpNode) expr).operator.getType() == TokenType.ASSIGN) {
            BinaryOpNode binOp = (BinaryOpNode) expr;
            if (binOp.left instanceof VariableNode) {
                Token name = ((VariableNode) binOp.left).token;
                return new AssignmentNode(name, binOp.right);
            }
        }
        return new ExpressionStatementNode(expr);
    }

    private StatementNode ifStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'if'.");
        ExpressionNode condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after if condition.");

        StatementNode thenBranch = statement();
        StatementNode elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        BlockNode thenBlock = (thenBranch instanceof BlockNode) ? (BlockNode) thenBranch : new BlockNode(Collections.singletonList(thenBranch));
        BlockNode elseBlockNode = null;
        if (elseBranch != null) {
            elseBlockNode = (elseBranch instanceof BlockNode) ? (BlockNode) elseBranch : new BlockNode(Collections.singletonList(elseBranch));
        }

        return new IfNode(previous(), condition, thenBlock, elseBlockNode);
    }

    private StatementNode whileStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'while'.");
        ExpressionNode condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after while condition.");
        StatementNode body = statement();

        BlockNode bodyBlock = (body instanceof BlockNode) ? (BlockNode) body : new BlockNode(Collections.singletonList(body));

        return new WhileNode(previous(), condition, bodyBlock);
    }

    private StatementNode printStatement() {
        Token keyword = previous();
        consume(TokenType.LPAREN, "Expect '(' after 'print'.");
        ExpressionNode value = expression();
        consume(TokenType.RPAREN, "Expect ')' after value.");
        consume(TokenType.SEMICOLON, "Expect ';' after print statement.");
        return new PrintNode(keyword, value);
    }

    private StatementNode returnStatement() {
        Token keyword = previous();
        ExpressionNode value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new ReturnNode(keyword, value);
    }

    private ExpressionNode expression() {
        return assignment();
    }

    private ExpressionNode assignment() {
        ExpressionNode expr = equality();
        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            ExpressionNode value = assignment();
            if (expr instanceof VariableNode) {
                return new BinaryOpNode(expr, equals, value);
            }
            throw new RuntimeException("Invalid assignment target at line " + equals.getLine());
        }
        return expr;
    }

    private ExpressionNode equality() {
        ExpressionNode expr = comparison();
        while (match(TokenType.EQUALS, TokenType.NOT_EQUAL)) {
            Token operator = previous();
            ExpressionNode right = comparison();
            expr = new BinaryOpNode(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode comparison() {
        ExpressionNode expr = term();
        while (match(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL, TokenType.LESS_THAN, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            ExpressionNode right = term();
            expr = new BinaryOpNode(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode term() {
        ExpressionNode expr = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ExpressionNode right = factor();
            expr = new BinaryOpNode(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode factor() {
        ExpressionNode expr = unary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            ExpressionNode right = unary();
            expr = new BinaryOpNode(expr, operator, right);
        }
        return expr;
    }

    private ExpressionNode unary() {
        if (match(TokenType.NOT_EQUAL, TokenType.MINUS)) {
            Token operator = previous();
            ExpressionNode right = unary();
            return new UnaryOpNode(operator, right);
        }
        return call();
    }

    private ExpressionNode call() {
        ExpressionNode expr = primary();
        while (true) {
            if (match(TokenType.LPAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }
        return expr;
    }

    private ExpressionNode finishCall(ExpressionNode callee) {
        List<ExpressionNode> arguments = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        Token paren = consume(TokenType.RPAREN, "Expect ')' after arguments.");
        if (callee instanceof VariableNode) {
            return new FunctionCallNode(((VariableNode) callee).token, arguments);
        }
        throw new RuntimeException("Invalid call target at line " + paren.getLine());
    }

    private ExpressionNode primary() {
        if (match(TokenType.INT_LITERAL)) {
            return new IntLiteralNode(previous(), Integer.parseInt(previous().getValue()));
        }
        if (match(TokenType.FLOAT_LITERAL)) {
            return new FloatLiteralNode(previous(), Float.parseFloat(previous().getValue()));
        }
        if (match(TokenType.IDENTIFIER)) {
            return new VariableNode(previous());
        }
        if (match(TokenType.LPAREN)) {
            ExpressionNode expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return expr;
        }
        throw new RuntimeException("Parse Error: Unexpected token " + peek().getValue() + " at line " + peek().getLine());
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException(message + " at line " + peek().getLine());
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
