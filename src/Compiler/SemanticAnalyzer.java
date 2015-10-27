/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import Language.LanguageX;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author Jorismar
 */
public class SemanticAnalyzer {
    private Stack stack;
    private Token mark;
    private Iterator<Token> itktable;
    private LanguageX language;
    
    public SemanticAnalyzer(LanguageX lang, String mark) {
        this.stack = new Stack();
        this.mark = new Token(mark, "mark", 0);
        this.language = lang;
    }
    
    private Token push(Token symbol) {
        return (Token) this.stack.push(symbol);
    }
    
    private Token get(int index) {
        return (Token) this.stack.get(index);
    }
    
    private Token peek() {
        return (Token) this.stack.peek();
    }
    
    private Token pop() {
        return (Token) this.stack.pop();
    }
    
    public Token next() {
        return this.itktable.hasNext() ? this.itktable.next() : new Token("", "", 0);
    }
    
    public boolean typeCmp(Token tk, String str) {
        return tk != null ? tk.getType().equals(str) : false;
    }
    
    public boolean nameCmp(Token tk, String str) {
        return tk != null ? tk.getName().equals(str) : false;
    }
    
    private void error(String str) {
        System.err.println(str);
        System.exit(1);
    }
    
    public void genSymbolsTable(ArrayList<Token> table) {

    }
    
    public void analyze() {
        
    }
}
