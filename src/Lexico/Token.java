/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lexico;

/**
 *
 * @author Jorismar
 */
public class Token {
    private String token;
    private String type;
    private long line;

    public Token(String token, String type, long line) {
        this.token = token;
        this.type = type;
        this.line = line;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public long getLine() {
        return line;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String  type) {
        this.type = type;
    }

    public void setLine(long line) {
        this.line = line;
    }

    public static enum Type {
        RESERVED_WORD, IDENTIFIER, DELIMITER, INTEGER_NUMBER, DOUBLE_NUMBER,
        ASSIGN_OPERATOR, RELAT_OPERATOR, ADD_OPERATOR, MULT_OPERATOR
    }
}
