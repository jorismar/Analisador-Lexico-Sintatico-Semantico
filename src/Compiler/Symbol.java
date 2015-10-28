/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import java.util.Stack;
/**
 *
 * @author Jorismar
 */
public class Symbol {
    private final Token token;
    private final boolean is_scope;
    private final Stack father;
    private final Stack stack;
    
    public Symbol(Token token, Stack father) {
        this.token = token;
        this.father = father;
        this.is_scope = this.father != null;
        this.stack = is_scope ? new Stack() : null;
    }

    public Token getToken() {
        return token;
    }

    public boolean isIs_scope() {
        return is_scope;
    }

    public Stack getStack() {
        return stack;
    }
    
    public Stack getFather() {
        return father;
    }
    
    public Token push(Token token) {
        return (Token) this.stack.push(token);
    }
    
    public Token pop() {
        return (Token) this.stack.pop();
    }
    
    public Token peek() {
        return (Token) this.stack.pop();
    }
    
    public Token get(int index) {
        return (Token) this.stack.get(index);
    }
}
