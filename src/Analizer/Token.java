/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analizer;

/**
 *
 * @author Jorismar
 */
public class Token {
    private String name;
    private String type;
    private long line;

    public Token(String name, String type, long line) {
        this.name = name;
        this.type = type;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public long getLine() {
        return line;
    }

    public void setName(String token) {
        this.name = token;
    }

    public void setType(String  type) {
        this.type = type;
    }

    public void setLine(long line) {
        this.line = line;
    }
}
