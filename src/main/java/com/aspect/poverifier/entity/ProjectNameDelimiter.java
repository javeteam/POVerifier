package com.aspect.poverifier.entity;

public enum ProjectNameDelimiter {
    COMA(','),
    SPACE(' '),
    BOTH('!');

    ProjectNameDelimiter(char symbol){
        this.symbol = symbol;
    }

    private final char symbol;

    public char getSymbol() {
        return symbol;
    }
}
