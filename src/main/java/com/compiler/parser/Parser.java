package com.compiler.parser;

import com.compiler.ast.*;
import com.compiler.lexer.Lexer;
import com.compiler.token.Token;
import com.compiler.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private final List<Token> tokens;
    private int current = 0;

    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        this.tokens = lexer.tokenizeAll();
    }

    public ExpressionNode parseExpression() {
        return equality();
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
        return primary();
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
            ExpressionNode expr = parseExpression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return expr;
        }

        throw new RuntimeException("Parse Error: Unexpected token " + peek().getValue());
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
        throw new RuntimeException("Parse Error: " + message);
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
