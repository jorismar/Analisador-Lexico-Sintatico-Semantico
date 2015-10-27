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
public class SyntacticAnalyzer {
    private final LanguageX language;
    private Iterator<Token> itable;
    //private Stack semant_stack;
    //private final Token semant_stack_mark = new Token("$", "mark", 0);
    
    public SyntacticAnalyzer(LanguageX lang)  {
        this.language = lang;
        //this.semant_stack = new Stack();
    }
    
    public void analyze(ArrayList<Token> table) {
        Token aux;
        this.itable = table.iterator();
        
        aux = this.next();
        if(this.nameCmp(aux, "program")) {
            aux = this.next();
            if(this.typeCmp(aux, this.language.getIdentifierType())) {
                aux = this.next();
                if(this.nameCmp(aux, ";")) {
                    //this.semant_stack.push(this.semant_stack_mark);
                    aux = this.next();
                    aux = this.var_declarations(aux);
                    aux = this.proc_declarations(aux);
                    aux = this.comp_command(aux);
                    if(this.nameCmp(aux, "."))
                        System.out.println("No syntax error detected!");
                    else this.error("Error: expected a '.' delimiter at end of algoritm");
                } else this.error("Error ln" + aux.getLine() + ": expected a ';'");
            } else this.error("Error ln" + aux.getLine() + ": expected a identifier");
        } else this.error("Error ln" + aux.getLine() + ": program start error, expected a \"program\" word");
    }
/*    
    public Stack getScopeTable() {
        return this.semant_stack;
    }
    
    public Token getScopeMark() {
        return this.semant_stack_mark;
    }
*/    
    private Token next() {
        return this.itable.hasNext() ? this.itable.next() : new Token("", "", 0);
    }
    
    private boolean typeCmp(Token tk, String str) {
        return tk != null ? tk.getType().equals(str) : false;
    }
    
    private boolean nameCmp(Token tk, String str) {
        return tk != null ? tk.getName().equals(str) : false;
    }
    
    private void error(String str) {
        System.err.println(str);
        System.exit(1);
    }
    
    private Token var_declarations(Token token) {
        if(this.nameCmp(token, "var"))
            return var_declaration_list(this.next());

        return token;
    }
    
    private Token proc_declarations(Token token) {
        return proc_declaration(token);
    }

    private Token comp_command(Token token) {
        Token aux = token;
        
        if(this.nameCmp(token, "begin")) {
            aux = this.opt_commands(this.next());
            if(!this.nameCmp(aux, "end")) // <-- caso begin-end sem instrução
                this.error("Error ln" + aux.getLine() + ": expected a \"end\"");
            aux = this.next();
        }
        
        return aux;
    }
    
    private Token var_declaration_list(Token token) {
        Token aux;
        
        aux = identifiers_list(token);
        
        if(this.nameCmp(aux, ":")) {
            aux = this.type(this.next());
            
            if(!this.nameCmp(aux, ";"))
                this.error("Error ln" + aux.getLine() + ": expected a ';'");
            
            aux = this.next();
            
            if(this.typeCmp(aux, this.language.getIdentifierType()))
                aux = this.var_declaration_list(aux);
        } else this.error("Error ln" + aux.getLine() + ": expected a ':'");

        return aux;
    }
    
    private Token identifiers_list(Token token) {
        Token aux = token;

        if(this.typeCmp(aux, this.language.getIdentifierType())) {
            //this.semant_stack.push(aux);
            
            aux = this.next();
            
            if(this.nameCmp(aux, ","))
                aux = this.identifiers_list(this.next());
        } else this.error("Error ln" + aux.getLine() + ": expected a identifier");

        return aux;
    }
    
    private Token type(Token token) {
        if(!(this.nameCmp(token, "integer") || 
             this.nameCmp(token, "real") || 
             this.nameCmp(token, "boolean")
            )) this.error("Error ln" + token.getLine() + ": expected a var type");

        return this.next();
    }
    
    private Token proc_declaration(Token token) {
        Token aux;
        
        if(this.nameCmp(token, "procedure")) {
            aux = this.next();
            if(this.typeCmp(aux, this.language.getIdentifierType())) {
                //this.semant_stack.push(aux);
                //this.semant_stack.push(this.semant_stack_mark);
                
                aux = this.argument(this.next());
                
                if(!this.nameCmp(aux, ";"))
                    this.error("Error ln" + aux.getLine() + ": expected a ';'");
                
                aux = this.var_declarations(this.next());
                aux = this.proc_declarations(aux);
                aux = this.comp_command(aux);
                
                if(!this.nameCmp(aux, ";"))
                    this.error("Error ln" + aux.getLine() + ": expected a ';'");
                
                return this.next();
            } else this.error("Error ln" + aux.getLine() + ": expected a identifier after \"procedure\"");
        }
        
        return token;
    }
    
    private Token argument(Token token) {
        Token aux;
        
        if(this.nameCmp(token, "(")) {
            aux = param_list(this.next());
            
            if(!this.nameCmp(aux, ")"))
                this.error("Error ln" + aux.getLine() + ": expected a '('");
            
            return this.next();
        }
        
        return token;
    }
    
    private Token param_list(Token token) {
        Token aux;
        
        aux = identifiers_list(token);
        
        if(this.nameCmp(aux, ":")) {
            aux = this.type(this.next());
            if(this.nameCmp(aux, ";"))
                aux = this.param_list(this.next());
        } else this.error("Error ln" + aux.getLine() + ": expected a ':'");

        return aux;
    }
    
    private Token opt_commands(Token token) {
        if(!this.nameCmp(token, "end"))
            return this.commands_list(token);

        return token;
    }
    
    private Token commands_list(Token token) {
        Token aux;
                
        aux = this.command(token);
        
        if(this.nameCmp(aux, ";"))
            aux = this.commands_list(this.next());

        return aux;
    }
    
    private Token command(Token token) {
        Token aux = token;
        
        if(this.typeCmp(aux, this.language.getIdentifierType())) {
            aux = this.next();
            if(this.typeCmp(aux, this.language.getAssignOperatorType())) {
                aux = this.expression(this.next());
            } else this.error("Error ln" + aux.getLine() + ": expected a ':='");
        }
        
        aux = this.proc_activation(aux);
        aux = this.comp_command(aux);
        
        if(this.nameCmp(aux, "if")) {
            aux = this.expression(this.next());
            
            if(!this.nameCmp(aux, "then"))
                this.error("Error ln" + aux.getLine() + ": expected a \"then\"");
            
            aux = this.command(this.next());
            
            if(this.nameCmp(aux, "else"))
                aux = this.command(this.next());
        }
        
        if(this.nameCmp(aux, "while")) {
            aux = this.expression(aux);

            if(!this.nameCmp(aux, "do"))
                this.error("Error ln" + aux.getLine() + ": expected a \"do\"");
            
            aux = this.command(aux);
        }
        
        return aux;
    }
    
    private Token expression(Token token) {
        Token aux;
        
        aux = this.simple_expression(token);
        
        if(this.typeCmp(aux, this.language.getRelationalType()))
            aux = this.simple_expression(this.next());

        return aux;
    }
    
    private Token proc_activation(Token token) {
        Token aux = token;
        
        if(this.typeCmp(aux, this.language.getIdentifierType())) {
            aux = this.next();
            
            if(this.nameCmp(aux, "(")) {
                aux = this.expression_list(aux);
                
                if(!this.nameCmp(aux, ")"))
                    this.error("Error ln" + aux.getLine() + ": expected a ')'");
                
                aux = this.next();
            }
            return aux;
        }
        return token;
    }
    
    private Token expression_list(Token token) {
        Token aux;
        
        aux = expression(token);
        
        if(this.nameCmp(aux, ",")) {
            this.expression_list(aux);
        }

        return aux;
    }
    
    private Token simple_expression(Token token) {
        Token aux;
        
        aux = signal(token);
        aux = term(aux);
        
        if(this.typeCmp(aux, this.language.getAddOperatorType()))
            aux = this.simple_expression(aux);
        
        return aux;
    }
    
    private Token term(Token token) {
        Token aux;
        
        aux = this.factor(token);
        
        if(this.typeCmp(aux, this.language.getMultOperatorType()))
            aux = this.term(this.next());
        
        return aux;
    }
    
    private Token factor(Token token) {
        Token aux = token;
        
        if(this.typeCmp(aux, this.language.getIdentifierType())) {
            aux = this.next();
            if(this.nameCmp(aux, "(")) {
                aux = this.expression_list(this.next());
                if(!this.nameCmp(aux, ")"))
                    this.error("Error ln" + aux.getLine() + ": expected a ')'");
                aux = this.next();
            }
        } else if(this.typeCmp(aux, this.language.getIntegerType()) || this.typeCmp(aux, this.language.getDoubleType())) {
            aux = this.next();
        } else if(this.nameCmp(aux, "true") || this.nameCmp(aux, "false")) {
            aux = this.next();
        } else if(this.nameCmp(aux, "(")) {
            aux = this.expression(this.next());
            if(!this.nameCmp(aux, ")"))
                this.error("Error ln" + aux.getLine() + ": expected a ')'");
            aux = this.next();
        } else if(this.nameCmp(aux, "not"))
            aux = this.factor(aux);
        
        return aux;
    }
    
    private Token signal(Token token) {
        Token aux = token;
        
        if(this.nameCmp(aux, "+") || this.nameCmp(aux, "-"))
            aux = this.next();
        
        return aux;
    }
}
