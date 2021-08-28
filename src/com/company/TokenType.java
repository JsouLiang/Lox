package com.company;

public enum TokenType {

    // Single-character tokens.
    LEFT_PAREN, // (
    RIGHT_PAREN, // )
    LEFT_BRACE, // {
    RIGHT_BRACE, // }
    COMMA,  // ,
    DOT, // .
    MINUS, // -
    PLUS, // +
    SEMICOLON, // ;
    SLASH, // /
    STAR, // *

    BANG,        // !
    BANG_EQUAL, // !=
    EQUAL,      // =
    EQUAL_EQUAL, // ==
    GREATER, // >
    GREATER_EQUAL, //>=
    LESS, // <
    LESS_EQUAL, // <=

    STRING,
    NUMBER,

    IDENTIFIER,
    AND,  // "and"
    CLASS,  // "class"
    ELSE,  // "else"
    FALSE,  // "false"
    FOR,  // "for"
    FUN,  // "fun"
    IF,  // "if"
    NIL,  // "nil"
    OR,  // "or"
    PRINT,  // "print"
    RETURN,  // "return"
    SUPER,  // "super"
    THIS,  // "this"
    TRUE,  // "true"
    VAR,  // "var"
    WHILE,  // "while"
}
