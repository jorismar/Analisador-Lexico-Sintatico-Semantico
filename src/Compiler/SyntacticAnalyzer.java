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
    private final LanguageX lang;
    private Iterator<Token> itable;
    
    /****************** SEMANTIC ******************/
        private final Stack scope_stack;
        private final Stack operat_stack;
        private int current_stack_pos;
        private final Token mark = new Token("$", "mark", 0);
    /**********************************************/
    
    public SyntacticAnalyzer(LanguageX lang)  {
        this.lang = lang;
        this.scope_stack = new Stack();
        this.operat_stack = new Stack();
    }
    
    public void analyze(ArrayList<Token> table) {
        Token aux;
        this.itable = table.iterator();
        
        aux = this.next();
        if(this.nameCmp(aux, "program")) {
        /****************** SEMANTIC ******************/
            this.push_to(this.scope_stack, this.mark);
        /**********************************************/
            aux = this.next();
            if(this.typeCmp(aux, this.lang.getIdentifierType())) {
            /****************** SEMANTIC ******************/
                this.push_to(this.scope_stack, new Token(aux.getName(), "program", aux.getLine()));
            /**********************************************/
                aux = this.next();
                if(this.nameCmp(aux, ";")) {
                    aux = this.next();
                    aux = this.var_declarations(aux);
                    aux = this.proc_declarations(aux);
                    aux = this.comp_command(aux);
                    if(this.nameCmp(aux, ".")) {
                        System.out.println("No syntax error detected!");
                    /****************** SEMANTIC ******************/
                        this.close_scope();
                    /**********************************************/
                    } else this.error("Error: expected a '.' delimiter at end of algoritm");
                } else this.error("Error ln" + aux.getLine() + ": expected a ';'");
            } else this.error("Error ln" + aux.getLine() + ": expected a identifier");
        } else this.error("Error ln" + aux.getLine() + ": program start error, expected a \"program\" word");
    }
    

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
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
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
            this.push_operat(this.mark);
            aux = this.opt_commands(this.next());
            if(!this.nameCmp(aux, "end")) // <-- caso begin-end sem instrução
                this.error("Error ln" + aux.getLine() + ": expected a \"end\"");
            
            aux = this.next();
        }
        
        return aux;
    }
    
    private Token var_declaration_list(Token token) {
        Token aux;
        
    /****************** SEMANTIC ******************/
        this.current_stack_pos = this.scope_stack.size();
    /**********************************************/

        aux = identifiers_list(token);

        if(this.nameCmp(aux, ":")) {
            aux = this.type(this.next());
            
            if(!this.nameCmp(aux, ";"))
                this.error("Error ln" + aux.getLine() + ": expected a ';'");
            
            aux = this.next();
            
            if(this.typeCmp(aux, this.lang.getIdentifierType()))
                aux = this.var_declaration_list(aux);
        } else this.error("Error ln" + aux.getLine() + ": expected a ':'");

        return aux;
    }
    
    private Token identifiers_list(Token token) {
        Token aux = token;

        if(this.typeCmp(aux, this.lang.getIdentifierType())) {
        /****************** SEMANTIC ******************/
            this.push_unique(new Token(aux.getName(), "", aux.getLine()));
        /**********************************************/
            
            aux = this.next();
            
            if(this.nameCmp(aux, ","))
                aux = this.identifiers_list(this.next());
        } else this.error("Error ln" + aux.getLine() + ": expected a identifier");

        return aux;
    }
    
    private Token type(Token token) {
        if(this.nameCmp(token, "integer"))
        /****************** SEMANTIC ******************/
            this.set_vars_type(this.lang.getIntegerType());
        /**********************************************/
        else if(this.nameCmp(token, "real"))
        /****************** SEMANTIC ******************/
            this.set_vars_type(this.lang.getDoubleType());
        /**********************************************/
        else if(this.nameCmp(token, "boolean"))
        /****************** SEMANTIC ******************/
            this.set_vars_type(this.lang.getBooleanType());
        /**********************************************/
        else this.error("Error ln" + token.getLine() + ": expected a var type");

        return this.next();
    }
    
    private Token proc_declaration(Token token) {
        Token aux;
        
        if(this.nameCmp(token, "procedure")) {
            aux = this.next();
            if(this.typeCmp(aux, this.lang.getIdentifierType())) {
                
            /****************** SEMANTIC ******************/
                this.push_unique(new Token(aux.getName(), "procedure", aux.getLine()));
                this.push_to(this.scope_stack, this.mark);
            /**********************************************/
                
                aux = this.argument(this.next());
                
                if(!this.nameCmp(aux, ";"))
                    this.error("Error ln" + aux.getLine() + ": expected a ';'");
                
                aux = this.var_declarations(this.next());
                aux = this.proc_declarations(aux);
                aux = this.comp_command(aux);
                
                if(!this.nameCmp(aux, ";"))
                    this.error("Error ln" + aux.getLine() + ": expected a ';'");
                
            /****************** SEMANTIC ******************/
                this.close_scope();
            /**********************************************/
        
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
        
    /****************** SEMANTIC ******************/
        this.current_stack_pos = this.scope_stack.size();
    /**********************************************/
    
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
        
        if(this.nameCmp(aux, ";")) {
            this.close_operat();
            aux = this.commands_list(this.next());
        }

        return aux;
    }
    
    private Token command(Token token) {
        Token aux = token;
        
        if(this.typeCmp(aux, this.lang.getIdentifierType())) {
        /****************** SEMANTIC ******************/
            this.push_operat(this.is_declared(aux), aux.getLine());
        /**********************************************/
            aux = this.next();
            if(this.typeCmp(aux, this.lang.getAssignOperatorType())) {
                this.push_operat(aux);
                aux = this.expression(this.next());
            } else this.error("Error ln" + aux.getLine() + ": expected a ':='");
        }
        
        aux = this.proc_activation(aux);
        aux = this.comp_command(aux);
        
        if(this.nameCmp(aux, "if")) {
            aux = this.expression(this.next());
            
            if(!this.nameCmp(aux, "then"))
                this.error("Error ln" + aux.getLine() + ": expected a \"then\"");
            
            this.close_operat();
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
        
        if(this.typeCmp(aux, this.lang.getRelationalType())) {
            this.push_operat(aux);
            aux = this.simple_expression(this.next());
        }

        return aux;
    }
    
    private Token proc_activation(Token token) {
        Token aux = token;
        
        if(this.typeCmp(aux, this.lang.getIdentifierType())) {
        /****************** SEMANTIC ******************/
            this.is_declared(aux);
        /**********************************************/
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
        
        if(this.typeCmp(aux, this.lang.getAddOperatorType())) {
            this.push_operat(aux);
            aux = this.simple_expression(aux);
        }
        
        return aux;
    }
    
    private Token term(Token token) {
        Token aux;
        
        aux = this.factor(token);
        
        if(this.typeCmp(aux, this.lang.getMultOperatorType())) {
            this.push_operat(aux);
            aux = this.term(this.next());
        }
        
        return aux;
    }
    
    private Token factor(Token token) {
        Token aux = token;
        
        if(this.typeCmp(aux, this.lang.getIdentifierType())) {
        /****************** SEMANTIC ******************/
            this.push_operat(this.is_declared(aux), aux.getLine());
            //this.semantic_is_expected_type(this.is_declared(aux));
        /**********************************************/
            aux = this.next();
            if(this.nameCmp(aux, "(")) {
                aux = this.expression_list(this.next());
                if(!this.nameCmp(aux, ")"))
                    this.error("Error ln" + aux.getLine() + ": expected a ')'");
                aux = this.next();
            }
        } else if(this.typeCmp(aux, this.lang.getIntegerType()) || this.typeCmp(aux, this.lang.getDoubleType())) {
        /****************** SEMANTIC ******************/
            //this.semantic_is_expected_type(aux);
            this.push_operat(aux);
        /**********************************************/
            aux = this.next();
        } else if(this.nameCmp(aux, "true") || this.nameCmp(aux, "false")) {
        /****************** SEMANTIC ******************/
            //this.semantic_is_expected_type(aux);
            this.push_operat(aux);
        /**********************************************/
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
        
        // tratar boolean
        
        return aux;
    }
    
/******************************* SEMANTIC *******************************/
    private void push_to(Stack stack, Token token) {
        stack.push(token);
    }
    
    private void push_operat(int i, long nline) {
        Token token = (Token) this.scope_stack.get(i);
        token.setLine(nline);
        this.push_to(this.operat_stack, token);
    }
    
    private void push_operat(Token token) {
        this.push_to(this.operat_stack, token);
    }
    
    private void push_unique(Token token) {
        if(this.contains_on_scope(token, "mark") >= 0)
            this.error("Error ln" + token.getLine() + ": identifier '" + token.getName() + "' is already defined above.");
        this.push_to(this.scope_stack, token);
    }
    
    private void set_vars_type(String type) {
        Token aux;
        
        for(int i = this.current_stack_pos; i < this.scope_stack.size(); i++) {
            aux = (Token) this.scope_stack.get(i);
            aux.setType(type);
        }
    }

    /* Percorre a pilha a partir do topo ate encontrar o tipo de parada especificado */
    private int contains_on_scope(Token token, String stop_at_fst_type) {
        Token aux;

        int i = this.scope_stack.size() - 1;
        
        do {
            aux = (Token) this.scope_stack.get(i--);
            if(aux.getName().equals(token.getName()))
                return i + 1;
        } while(!aux.getType().equals(stop_at_fst_type));
        
        return -1;
    }
    
    private int is_declared(Token token) {
        int i = this.contains_on_scope(token, "program");

        if(i < 0)
            this.error("Error ln" + token.getLine() + ": '" + token.getName() + "' identifier was not declared in this scope");
        else if(i == 1)
            this.error("Error ln" + token.getLine() + ": The program identifier may not be used in commands and keywords ");
                
        return i;
    }

    private void close_scope() {
        Token aux;
        do {
            aux = (Token) this.scope_stack.pop();
        } while(!aux.getName().equals(this.mark.getName()));
    }
    
    private void close_operat() {
        Token aux;
        String last, type, op;
        
        aux = (Token) this.operat_stack.pop();
        last = aux.getType();
        type = last;
        
        while(!aux.getName().equals(":=") && aux != this.mark) {
            aux = (Token) this.operat_stack.pop();
            
            if(!type.equals(lang.getBooleanType())) {
                if(aux.getName().equals("/")) {
                    type = lang.getDoubleType();
                } else if(aux.getType().equals(lang.getIntegerType()) || aux.getType().equals(lang.getDoubleType())) {
                    last = aux.getType();

                    if(last.equals(lang.getIntegerType()) && type.equals(lang.getIntegerType()))
                        type = lang.getIntegerType();
                    else if(last.equals(lang.getIntegerType()) && type.equals(lang.getDoubleType()))
                        type = lang.getDoubleType();
                    else if(last.equals(lang.getDoubleType()) && type.equals(lang.getIntegerType()))
                        type = lang.getDoubleType();
                    else if(last.equals(lang.getDoubleType()) && type.equals(lang.getDoubleType()))
                        type = lang.getDoubleType();
                }
            } 
            
            if(aux.getType().equals(lang.getRelationalType())) {
                aux = (Token) this.operat_stack.pop();
                if(!((aux.getType().equals(lang.getIntegerType()) || aux.getType().equals(lang.getDoubleType())) && (last.equals(lang.getIntegerType()) || last.equals(lang.getDoubleType()))))
                    this.error("Error ln" + aux.getLine() + ": invalid types - expected a number value");
                type = lang.getBooleanType();
            } else if(!type.equals(lang.getBooleanType()) && (aux.getName().equals("and") || aux.getName().equals("or") || aux.getName().equals("not"))) {
                this.error("Error ln" + aux.getLine() + ": invalid types - expected boolean value");
            } else if(aux.getType().equals(lang.getBooleanType())) {
                last = aux.getType();
                type = lang.getBooleanType();
            }
        }
        

        if(aux.getType().equals(lang.getAssignOperatorType())) {
            aux = (Token) this.operat_stack.pop();
            last = aux.getType();

            if(!last.equals(type)) {
                if(last.equals(lang.getIntegerType()) || 
                        (last.equals(lang.getDoubleType()) && type.equals(lang.getBooleanType())) ||
                                (last.equals(lang.getBooleanType()) && type.equals(lang.getDoubleType())) ||
                                    (last.equals(lang.getBooleanType()) && type.equals(lang.getIntegerType())))
                    this.error("Error ln" + aux.getLine() + ": invalid types - expected '" + aux.getType() + "' and received '" + type + "'");
            }
        }
    }
    
    public void printStack(Stack stack) {
        Token aux;
        
        for(int i = 0; i < stack.size(); i++) {
            aux = (Token) stack.get(i);
            System.err.println("Name: " + aux.getName() + "\t\tType: " + aux.getType() + "\t\tLine" + aux.getLine());
        }
    }
/************************************************************************/
}
