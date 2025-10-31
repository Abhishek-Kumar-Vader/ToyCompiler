package com.compiler.token;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {
    @Test
    public void testTokenCreation(){
        Token token = new Token(TokenType.INT_LITERAL, "42", 1, 5);
        assertEquals(TokenType.INT_LITERAL, token.getType());
        assertEquals("42", token.getValue());
        assertEquals(1, token.getLine());
        assertEquals(5, token.getColumn());
    }

    @Test
    public void testTokenEquality(){
        Token token1 = new Token(TokenType.PLUS, "+", 1, 10);
        Token token2 = new Token(TokenType.PLUS, "+", 1, 10);
        Token token3 = new Token(TokenType.MINUS, "-", 1, 10);

        assertEquals(token1, token2);
        assertNotEquals(token1, token3);
    }

    @Test
    public void testAllTokenTypes(){
        Token intLiteral = new Token(TokenType.INT_LITERAL, "123", 1, 2);
        Token floatLiteral = new Token(TokenType.FLOAT_LITERAL, "3.14", 1, 15);
        Token identifier = new Token(TokenType.IDENTIFIER, "x", 1, 10);
        Token plus = new Token(TokenType.PLUS, "+", 1, 12);
        Token lparen = new Token(TokenType.LPAREN, "(", 1,14);
        Token eof = new Token(TokenType.EOF, "", 1, 15);

        assertNotNull(intLiteral);
        assertNotNull(floatLiteral);
        assertNotNull(identifier);
        assertNotNull(plus);
        assertNotNull(lparen);
        assertNotNull(eof);
    }

    @Test
    public void testKeywordTokens(){
        Token ifToken = new Token(TokenType.IF, "if", 1, 1);
        Token whileToken = new Token(TokenType.WHILE, "while", 2, 1);
        Token functionToken = new Token(TokenType.FUNCTION, "Function", 3, 1);

        assertEquals(TokenType.IF, ifToken.getType());
        assertEquals(TokenType.WHILE, whileToken.getType());
        assertEquals(TokenType.FUNCTION, functionToken.getType());
    }
}
