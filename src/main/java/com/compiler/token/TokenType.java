package com.compiler.token;

public enum TokenType {
    INT_LITERAL,
    FLOAT_LITERAL,

    //Identifiers
    IDENTIFIER,
    INT,
    FLOAT,
    IF,
    ELSE,
    WHILE,
    FUNCTION,
    RETURN,
    PRINT,

    //Operators
    PLUS,
    MINUS,
    STAR,
    SLASH,
    ASSIGN,
    EQUALS,
    LESS_THAN,
    GREATER_THAN,
    NOT_EQUAL,

    //Delimiters
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    SEMICOLON,
    COMMA,

    //Special
    EOF,
    INVALID
}
