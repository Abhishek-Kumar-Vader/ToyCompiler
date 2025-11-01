package com.compiler.lexer;

import com.compiler.token.Token;
import com.compiler.token.TokenType;
import com.compiler.utils.SourceReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final SourceReader reader;
    private final Map<String, TokenType> keywords;

    public Lexer(SourceReader reader) {
        this.reader = reader;
        this.keywords = initializeKeywords();
    }

    private Map<String, TokenType> initializeKeywords() {
        Map<String, TokenType> map = new HashMap<>();
        map.put("int", TokenType.INT);
        map.put("float", TokenType.FLOAT);
        map.put("if", TokenType.IF);
        map.put("else", TokenType.ELSE);
        map.put("while", TokenType.WHILE);
        map.put("function", TokenType.FUNCTION);
        map.put("return", TokenType.RETURN);
        map.put("print", TokenType.PRINT);
        return map;
    }

    public Token nextToken() throws IOException {
        skipWhitespace();

        int line = reader.getLine();
        int column = reader.getColumn();

        if (reader.isEof()) {
            return new Token(TokenType.EOF, "", line, column);
        }

        char current = reader.current();

        // Numbers
        if (Character.isDigit(current)) {
            return tokenizeNumber(line, column);
        }

        // Identifiers and keywords
        if (Character.isLetter(current) || current == '_') {
            return tokenizeIdentifierOrKeyword(line, column);
        }

        // Two-character operators
        if (current == '=' && reader.peek() == '=') {
            reader.advance();
            reader.advance();
            return new Token(TokenType.EQUALS, "==", line, column);
        }

        if (current == '<' && reader.peek() == '=') {
            reader.advance();
            reader.advance();
            return new Token(TokenType.LESS_EQUAL, "<=", line, column);
        }

        if (current == '>' && reader.peek() == '=') {
            reader.advance();
            reader.advance();
            return new Token(TokenType.GREATER_EQUAL, ">=", line, column);
        }

        if (current == '!' && reader.peek() == '=') {
            reader.advance();
            reader.advance();
            return new Token(TokenType.NOT_EQUAL, "!=", line, column);
        }

        // Single-character tokens
        TokenType type = getSingleCharTokenType(current);
        if (type != null) {
            reader.advance();
            return new Token(type, String.valueOf(current), line, column);
        }

        // Invalid character
        reader.advance();
        return new Token(TokenType.INVALID, String.valueOf(current), line, column);
    }

    private Token tokenizeNumber(int line, int column) throws IOException {
        StringBuilder value = new StringBuilder();
        boolean isFloat = false;

        while (!reader.isEof() && Character.isDigit(reader.current())) {
            value.append(reader.current());
            reader.advance();
        }

        if (!reader.isEof() && reader.current() == '.' && !reader.isEof() && Character.isDigit(reader.peek())) {
            isFloat = true;
            value.append(reader.current());
            reader.advance();

            while (!reader.isEof() && Character.isDigit(reader.current())) {
                value.append(reader.current());
                reader.advance();
            }
        }

        TokenType type = isFloat ? TokenType.FLOAT_LITERAL : TokenType.INT_LITERAL;
        return new Token(type, value.toString(), line, column);
    }

    private Token tokenizeIdentifierOrKeyword(int line, int column) throws IOException {
        StringBuilder value = new StringBuilder();

        while (!reader.isEof() && (Character.isLetterOrDigit(reader.current()) || reader.current() == '_')) {
            value.append(reader.current());
            reader.advance();
        }

        String identifier = value.toString();
        TokenType type = keywords.getOrDefault(identifier, TokenType.IDENTIFIER);

        return new Token(type, identifier, line, column);
    }

    private TokenType getSingleCharTokenType(char c) {
        return switch (c) {
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '*' -> TokenType.STAR;
            case '/' -> TokenType.SLASH;
            case '=' -> TokenType.ASSIGN;
            case '<' -> TokenType.LESS_THAN;
            case '>' -> TokenType.GREATER_THAN;
            case '(' -> TokenType.LPAREN;
            case ')' -> TokenType.RPAREN;
            case '{' -> TokenType.LBRACE;
            case '}' -> TokenType.RBRACE;
            case ';' -> TokenType.SEMICOLON;
            case ',' -> TokenType.COMMA;
            default -> null;
        };
    }

    private void skipWhitespace() throws IOException {
        while (!reader.isEof()) {
            char current = reader.current();

            if (Character.isWhitespace(current)) {
                reader.advance();
                continue;
            }

            if (current == '/' && !reader.isEof() && reader.peek() == '/') {
                reader.advance();
                reader.advance();

                while (!reader.isEof() && reader.current() != '\n') {
                    reader.advance();
                }
                continue;
            }

            break;
        }
    }

    public java.util.List<Token> tokenizeAll() throws IOException {
        java.util.List<Token> tokens = new java.util.ArrayList<>();
        Token token;

        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);

        return tokens;
    }
}
