package com.company.tokenizer;

public class Token {
    public final TokenType type;
    public final String lexeme;
    /// 词法运行时的值，比如输入的词法为"123" 此时对应的 literal 就是整形的 123
    public final Object literal;

    public final int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", literal=" + literal +
                ", line=" + line +
                '}';
    }
}
