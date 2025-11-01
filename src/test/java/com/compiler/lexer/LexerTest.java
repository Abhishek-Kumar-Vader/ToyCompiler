package com.compiler.lexer;

import com.compiler.token.Token;
import com.compiler.token.TokenType;
import com.compiler.utils.SourceReader;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    private Lexer createLexer(String source) throws IOException{
        return new Lexer(SourceReader.fromString(source));
    }

    @Test
    public void testTokenizeIntegers() throws IOException{
        Lexer lexer = createLexer("42 123 0 999");

        Token t1 = lexer.nextToken();
        assertEquals(TokenType.INT_LITERAL, t1.getType());
        assertEquals("42", t1.getValue());

        Token t2 = lexer.nextToken();
        assertEquals(TokenType.INT_LITERAL, t2.getType());
        assertEquals("123", t2.getValue());

        Token t3 = lexer.nextToken();
        assertEquals(TokenType.INT_LITERAL, t3.getType());
        assertEquals("0", t3.getValue());

        Token t4 = lexer.nextToken();
        assertEquals(TokenType.INT_LITERAL, t4.getType());
        assertEquals("999", t4.getValue());

        Token eof = lexer.nextToken();
        assertEquals(TokenType.EOF, eof.getType());
    }

    @Test
    public void testTokenizeFloats() throws IOException{
        Lexer lexer = createLexer("3.14 0.5 100.0");

        Token t1 = lexer.nextToken();
        assertEquals(TokenType.FLOAT_LITERAL, t1.getType());
        assertEquals("3.14", t1.getValue());

        Token t2 = lexer.nextToken();
        assertEquals(TokenType.FLOAT_LITERAL, t2.getType());
        assertEquals("0.5", t2.getValue());

        Token t3 = lexer.nextToken();
        assertEquals(TokenType.FLOAT_LITERAL, t3.getType());
        assertEquals("100.0", t3.getValue());
    }

    @Test
    public void testTokenizeIdentifiers() throws IOException{
        Lexer lexer = createLexer("x myVar _temp var123");

        Token t1 = lexer.nextToken();
        assertEquals(TokenType.IDENTIFIER, t1.getType());
        assertEquals("x", t1.getValue());

        Token t2 = lexer.nextToken();
        assertEquals(TokenType.IDENTIFIER, t2.getType());
        assertEquals("myVar", t2.getValue());

        Token t3 = lexer.nextToken();
        assertEquals(TokenType.IDENTIFIER, t3.getType());
        assertEquals("_temp", t3.getValue());

        Token t4 = lexer.nextToken();
        assertEquals(TokenType.IDENTIFIER, t4.getType());
        assertEquals("var123", t4.getValue());
    }

    @Test
    public void testTokenizeKeywords() throws IOException {
        Lexer lexer = createLexer("if else while function return print int float");

        assertEquals(TokenType.IF, lexer.nextToken().getType());
        assertEquals(TokenType.ELSE, lexer.nextToken().getType());
        assertEquals(TokenType.WHILE, lexer.nextToken().getType());
        assertEquals(TokenType.FUNCTION, lexer.nextToken().getType());
        assertEquals(TokenType.RETURN, lexer.nextToken().getType());
        assertEquals(TokenType.PRINT, lexer.nextToken().getType());
        assertEquals(TokenType.INT, lexer.nextToken().getType());
        assertEquals(TokenType.FLOAT, lexer.nextToken().getType());
    }

    @Test
    public void testTokenizeOperators() throws IOException {
        Lexer lexer = createLexer("+ - * / = == < > <= >= !=");

        assertEquals(TokenType.PLUS, lexer.nextToken().getType());
        assertEquals(TokenType.MINUS, lexer.nextToken().getType());
        assertEquals(TokenType.STAR, lexer.nextToken().getType());
        assertEquals(TokenType.SLASH, lexer.nextToken().getType());
        assertEquals(TokenType.ASSIGN, lexer.nextToken().getType());
        assertEquals(TokenType.EQUALS, lexer.nextToken().getType());
        assertEquals(TokenType.LESS_THAN, lexer.nextToken().getType());
        assertEquals(TokenType.GREATER_THAN, lexer.nextToken().getType());
        assertEquals(TokenType.LESS_EQUAL, lexer.nextToken().getType());
        assertEquals(TokenType.GREATER_EQUAL, lexer.nextToken().getType());
        assertEquals(TokenType.NOT_EQUAL, lexer.nextToken().getType());
    }

    @Test
    public void testTokenizeDelimiters() throws IOException {
        Lexer lexer = createLexer("( ) { } ; ,");

        assertEquals(TokenType.LPAREN, lexer.nextToken().getType());
        assertEquals(TokenType.RPAREN, lexer.nextToken().getType());
        assertEquals(TokenType.LBRACE, lexer.nextToken().getType());
        assertEquals(TokenType.RBRACE, lexer.nextToken().getType());
        assertEquals(TokenType.SEMICOLON, lexer.nextToken().getType());
        assertEquals(TokenType.COMMA, lexer.nextToken().getType());
    }

    @Test
    public void testTokenizeSimpleExpression() throws IOException {
        Lexer lexer = createLexer("x = 10 + 20;");

        List<Token> tokens = lexer.tokenizeAll();

        assertEquals(7, tokens.size()); // x, =, 10, +, 20, ;, EOF
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
        assertEquals("x", tokens.get(0).getValue());
        assertEquals(TokenType.ASSIGN, tokens.get(1).getType());
        assertEquals(TokenType.INT_LITERAL, tokens.get(2).getType());
        assertEquals("10", tokens.get(2).getValue());
        assertEquals(TokenType.PLUS, tokens.get(3).getType());
        assertEquals(TokenType.INT_LITERAL, tokens.get(4).getType());
        assertEquals("20", tokens.get(4).getValue());
        assertEquals(TokenType.SEMICOLON, tokens.get(5).getType());
        assertEquals(TokenType.EOF, tokens.get(6).getType());
    }

    @Test
    public void testTokenizeIfStatement() throws IOException {
        Lexer lexer = createLexer("if (x < 10) { print(x); }");

        List<Token> tokens = lexer.tokenizeAll();

        assertEquals(TokenType.IF, tokens.get(0).getType());
        assertEquals(TokenType.LPAREN, tokens.get(1).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(2).getType());
        assertEquals(TokenType.LESS_THAN, tokens.get(3).getType());
        assertEquals(TokenType.INT_LITERAL, tokens.get(4).getType());
        assertEquals(TokenType.RPAREN, tokens.get(5).getType());
        assertEquals(TokenType.LBRACE, tokens.get(6).getType());
        assertEquals(TokenType.PRINT, tokens.get(7).getType());
        assertEquals(TokenType.LPAREN, tokens.get(8).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(9).getType());
        assertEquals(TokenType.RPAREN, tokens.get(10).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(11).getType());
        assertEquals(TokenType.RBRACE, tokens.get(12).getType());
        assertEquals(TokenType.EOF, tokens.get(13).getType());
    }

    @Test
    public void testSkipWhitespace() throws IOException {
        Lexer lexer = createLexer("  x   =   5  ;  ");

        assertEquals(TokenType.IDENTIFIER, lexer.nextToken().getType());
        assertEquals(TokenType.ASSIGN, lexer.nextToken().getType());
        assertEquals(TokenType.INT_LITERAL, lexer.nextToken().getType());
        assertEquals(TokenType.SEMICOLON, lexer.nextToken().getType());
        assertEquals(TokenType.EOF, lexer.nextToken().getType());
    }

    @Test
    public void testSkipComments() throws IOException {
        Lexer lexer = createLexer("x = 5; // this is a comment\ny = 10;");

        List<Token> tokens = lexer.tokenizeAll();

        // Should tokenize: x, =, 5, ;, y, =, 10, ;, EOF
        assertEquals(9, tokens.size());
        assertEquals("x", tokens.get(0).getValue());
        assertEquals("5", tokens.get(2).getValue());
        assertEquals("y", tokens.get(4).getValue());
        assertEquals("10", tokens.get(6).getValue());
    }

    @Test
    public void testKeywordVsIdentifier() throws IOException {
        // Test that keywords are recognized vs similar identifiers
        Lexer lexer = createLexer("if ifx xif");

        Token t1 = lexer.nextToken();
        assertEquals(TokenType.IF, t1.getType());
        assertEquals("if", t1.getValue());

        Token t2 = lexer.nextToken();
        assertEquals(TokenType.IDENTIFIER, t2.getType());
        assertEquals("ifx", t2.getValue());

        Token t3 = lexer.nextToken();
        assertEquals(TokenType.IDENTIFIER, t3.getType());
        assertEquals("xif", t3.getValue());
    }

    @Test
    public void testComplexProgram() throws IOException {
        String program = """
            function main() {
                x = 10;
                y = 20.5;
                if (x < y) {
                    print(x);
                } else {
                    print(y);
                }
            }
            """;

        Lexer lexer = createLexer(program);
        List<Token> tokens = lexer.tokenizeAll();

        // Verify we got a reasonable number of tokens
        assertTrue(tokens.size() > 20);

        // Verify first few tokens
        assertEquals(TokenType.FUNCTION, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("main", tokens.get(1).getValue());
        assertEquals(TokenType.LPAREN, tokens.get(2).getType());
        assertEquals(TokenType.RPAREN, tokens.get(3).getType());
        assertEquals(TokenType.LBRACE, tokens.get(4).getType());
    }

    @Test
    public void testInvalidCharacters() throws IOException {
        Lexer lexer = createLexer("x @ y");

        assertEquals(TokenType.IDENTIFIER, lexer.nextToken().getType());
        assertEquals(TokenType.INVALID, lexer.nextToken().getType());
        assertEquals(TokenType.IDENTIFIER, lexer.nextToken().getType());
    }

    @Test
    public void testEmptyInput() throws IOException {
        Lexer lexer = createLexer("");

        Token token = lexer.nextToken();
        assertEquals(TokenType.EOF, token.getType());
    }

    @Test
    public void testMultilineProgram() throws IOException {
        String program = "x = 5;\ny = 10;\nz = x + y;";

        Lexer lexer = createLexer(program);
        List<Token> tokens = lexer.tokenizeAll();

        // x = 5 ; y = 10 ; z = x + y ; EOF
        assertEquals(15, tokens.size());
    }

    @Test
    public void debugKeywords() throws IOException {
        Lexer lexer = createLexer("int float");

        Token t1 = lexer.nextToken();
        System.out.println("Token 1: " + t1.getType() + " = " + t1.getValue());

        Token t2 = lexer.nextToken();
        System.out.println("Token 2: " + t2.getType() + " = " + t2.getValue());
    }
}
