package Analizer;

import Language.LanguageX;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Compiler {
    public static LanguageX language = new LanguageX();
    public static ArrayList<Token> table = new ArrayList<>();
    
    public static enum Type {
        RESERVED_WORD, IDENTIFIER, DELIMITER, INTEGER_NUMBER, DOUBLE_NUMBER,
        ASSIGN_OPERATOR, RELAT_OPERATOR, ADD_OPERATOR, MULT_OPERATOR, UNKNOWN
    }

    public static boolean LexicalAnalyzer(String filename) throws FileNotFoundException, IOException {
        BufferedReader buffer = new BufferedReader(new FileReader(filename));
        char c;
        long nline = 0, open_comment_line = 0;
        boolean isComment = false;
        String line, aux;
        String[] words;
        
        do {
            line = buffer.readLine();
            
            if(line != null) {
                nline++;
                
                words = line.split(" ");

                for(String word:words) {
                    if(!isComment || word.contains(language.getCloseCommentSimbol())) { // <---- LINHA ADICIONADA
                        for(int i = 0; i < word.length(); i++) {
                            c = word.charAt(i);
                            if(c == '\t') continue;

                            if(!isComment) {
                                if(language.getNumbersAlphabet().contains("" + c)) { // Verifica se é um valor inteiro ou real
                                    aux = identify(word, language.getValueAlphabet(), nline, i);

                                    if(aux.contains("" + '.')) addToken(aux, Type.DOUBLE_NUMBER, nline);
                                        else addToken(aux, Type.INTEGER_NUMBER, nline);

                                    i += aux.length() - 1;
                                }
                                else if(language.getIdentifiersAlphabet().contains("" + c)) { // Verifica se é um identificador ou palavra reservada
                                    aux = identify(word, language.getIdentifiersAlphabet(), nline, i);

                                    if(language.getReservedWordsAlphabet().contains(aux)) {
                                        if(language.getAddOperatorsAlphabet().contains(aux))
                                            addToken(aux, Type.ADD_OPERATOR, nline);
                                        else if(language.getMultOperatorsAlphabet().contains(aux))
                                            addToken(aux, Type.MULT_OPERATOR, nline);
                                        else
                                            addToken(aux, Type.RESERVED_WORD, nline);
                                    } else addToken(aux, Type.IDENTIFIER, nline);

                                    i += aux.length() - 1;
                                }
                                else if(language.getAssignOperatorsAlphabet().contains("" + c)) {
                                    aux = identify(word, language.getAssignOperatorsAlphabet(), nline, i);

                                    if(aux.contains(language.getAssignOperatorsAlphabet()) && aux.length() == language.getAssignOperatorsAlphabet().length())
                                        addToken(aux, Type.ASSIGN_OPERATOR, nline);
                                    else if(language.getRelatOperatorsAlphabet().contains(aux))
                                        addToken(aux, Type.RELAT_OPERATOR, nline);
                                    else 
                                        addToken(aux, Type.DELIMITER, nline);

                                    i += aux.length() - 1;
                                }
                                else if(language.getDelimitersAlphabet().contains("" + c)) {
                                        //addToken("" + c, Type.DELIMITER, nline);
                                        aux = identify(word, language.getDelimitersAlphabet(), nline, i); // Não há necessidade da chamada de identify, porém caso o alfabeto mude, para mais de um símbolo em algum elemento
                                        addToken(aux, Type.DELIMITER, nline);

                                        i += aux.length() - 1;
                                }
                                else if(language.getAddOperatorsAlphabet().contains("" + c)) {
                                    //addToken("" + c, Type.ADD_OPERATOR, nline);
                                    aux = identify(word, language.getAddOperatorsAlphabet(), nline, i); // Não há necessidade da chamada de identify, porém caso o alfabeto mude, para mais de um símbolo em algum elemento
                                    addToken(aux, Type.ADD_OPERATOR, nline);

                                    i += aux.length() - 1;
                                }
                                else if(language.getMultOperatorsAlphabet().contains("" + c)) {
                                    //addToken("" + c, Type.MULT_OPERATOR, nline);
                                    aux = identify(word, language.getMultOperatorsAlphabet(), nline, i); // Não há necessidade da chamada de identify, porém caso o alfabeto mude, para mais de um símbolo em algum elemento
                                    addToken(aux, Type.MULT_OPERATOR, nline);

                                    i += aux.length() - 1;
                                }
                                else if(language.getRelatOperatorsAlphabet().contains("" + c)) {
                                    aux = identify(word, language.getRelatOperatorsAlphabet(), nline, i);

                                    if(language.getRelatOperatorsAlphabet().contains(aux))
                                        addToken(aux, Type.RELAT_OPERATOR, nline);
                                    // <--- SE A SEQUÊNCIA ESTIVER INCORRETA(=> =< >< ==)? TRATAR COMO DOIS OPERADORES
                                    
                                    i += aux.length() - 1;
                                }
                                else if(language.getOpenCommentSimbol().contains("" + c)) {
                                    isComment = true;
                                    open_comment_line = nline;
                                }
                                else {
                                    System.err.println("Error ln:" + nline + " - invalid character (" + c + ")");
                                    return false;
                                }

                                aux = "";
                            } else if(language.getCloseCommentSimbol().contains("" + c)) {
                                isComment = false;
                            }
                        }
                    }
                }
            }
        } while (line != null);
        
        if(isComment) {
            System.err.println("Error ln:" + open_comment_line + " - comment has been opened and not closed!");
            return false;
        }
        
        return true;
    }
    
    public static String identify(String word, String list, long nline, int current_pos) {
        String aux = "";
        char c;
        
        do {
            c = word.charAt(current_pos++);
            
            if(list.contains("" + c)) aux += c;
                else break;
        } while(current_pos < word.length());
        
        return aux;
    }
    
    public static void printTable() {
        System.out.print("TOKEN      TYPE       LINE\n---------------------------------\n");
        
        for(Token tk:table) {
            System.out.println("" + tk.getName() + "\t" + tk.getType() + "\t" + tk.getLine() + "\t|");
        }
    }

    public static void SyntaxAnalyzer() {
        Iterator<Token> itable = table.iterator();
        
        if( nextToken(itable).getName().equals("program") && nextToken(itable).getType().equals(Type.IDENTIFIER.name()) && nextToken(itable).getName().equals(";")){
            startProgram(itable);
        } else err_stop("Error ln: 1 program start error");
    }
    
    public static Token startProgram(Iterator<Token> it) {
        Token tk;
                    
        while(it.hasNext()) {
            tk = nextToken(it);
            if(tk.getName().equals("var"))
                tk = listVarDeclaration(it);

            if(tk.getName().equals("procedure")) {
                tk = subprogramDeclaration(it);
                if(tk.getName().equals("end"))
                    tk = nextToken(it);
                else err_stop("Error ln: " + tk.getLine() + " expected a end");
            }
            
            if(tk.getName().equals("begin"))
            
            
        }

        return tk;
    }
    
    /*
        variável := expressão
        | ativação_de_procedimento
        | comando_composto
        | if expressão then comando parte_else
        | while expressão do comando 
    */
    
    public static Token command(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        if(tk.getType().equals(Type.IDENTIFIER.name())) {
            tk = nextToken(it);
            if(tk.getType().equals(Type.ASSIGN_OPERATOR.name()))
                tk = expression(it);
        }
        
        if()
            
    }
    
    public static Token expression(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        
        
        
        return tk;
    }
    
    public static Token simpleExpression(Iterator<Token> it) {
        
    }
    
    public static Token term(Iterator<Token> it) {
        Token tk;
        
        tk = factor(it);
        
        if(isEquals(tk, Type.MULT_OPERATOR))
            tk = term(it);
        
        return tk;
    }
    
    public static Token factor(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        if(isEquals(tk, Type.IDENTIFIER)) {
            tk = nextToken(it);
            if(isEquals(tk, "(")) {
                tk = expressionList(it);
                if(!isEquals(tk, ")"))
                    err_stop("Error ln: " + tk.getLine() + " expected a )");
            }
        } else if(isEquals(tk, Type.INTEGER_NUMBER) || isEquals(tk, Type.DOUBLE_NUMBER) || isEquals(tk, "true") || isEquals(tk, "false")) {
            tk = nextToken(it);
        } else if(isEquals(tk, "not")) {
            tk = factor(it);
        }
        
        return tk;
    }
    
    public static Token listVarDeclaration(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        while(tk.getType().equals(Type.IDENTIFIER.name())) {
            if(varDeclaration(it))
                tk = nextToken(it);
        }
        
        return tk;
    }
    
    public static boolean varDeclaration(Iterator<Token> it) {
        Token tk = listIdentifiers(it);
        
        if(tk.getName().equals(":")) {
            if(isVarType(nextToken(it))) {
                tk = nextToken(it);
                
                if(tk.getName().equals(";")) {
                    return true;
                } else err_stop("Error ln: " + tk.getLine() + " expected a ;");
            }
        } else err_stop("Error ln: " + tk.getLine() + " expected a :");
        
        return false;
    }
    
    public static Token listIdentifiers(Iterator<Token> it) {
        Token tk = nextToken(it); // verificar
        
        while(tk.getName().equals(",")) {
            tk = nextToken(it);
            if(tk.getType().equals(Type.IDENTIFIER.name()))
                tk = nextToken(it);
            else err_stop("Error ln: " + tk.getLine() + " expected a identifier");
        }

        return tk;
    }
    
    public static boolean isVarType(Token tk) {
        if(!(tk.getName().equals("integer") || tk.getName().equals("boolean") || tk.getName().equals("real")))
            err_stop("Error ln: " + tk.getLine() + " invalid type.");
        
        return true;
    }

    public static Token subprogramDeclaration(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        if(tk.getType().equals(Type.IDENTIFIER.name())) {
            if(nextToken(it).getName().equals("(")) {
                if(argumentList(it).getName().equals(")")) {
                    if(nextToken(it).getName().equals(";")) {
                        return startProgram(it);
                    } else err_stop("Error ln: " + tk.getLine() + " expected a ;");
                } else err_stop("Error ln: " + tk.getLine() + " expected a )");
            } else err_stop("Error ln: " + tk.getLine() + " expected a (");
        } else err_stop("Error ln: " + tk.getLine() + " expected a identifier");
        
        return null;
    }
    
    public static Token argumentList(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        while(tk.getType().equals(Type.IDENTIFIER.name())) {
            tk = listIdentifiers(it);
            if(tk.getName().equals(":") && isVarType(nextToken(it))) {
                tk = nextToken(it);
                if(tk.getName().equals(";"))
                    tk = nextToken(it);
            } else err_stop("Error ln: " + tk.getLine() + " expected a :");
        }
        
        return tk;
    }
    
    public static boolean addToken(String name, Type tp, long nline) {
        return table.add(new Token(name, tp.name(), nline));
    }
    
    public static Token nextToken(Iterator<Token> it) {
        return it.hasNext() ? it.next() : null;
    }
    
    public static boolean isEquals(Token tk, String name) {
        return tk.getName().equals(name);
    }
    
    public static boolean isEquals(Token tk, Type tp) {
        return tk.getType().equals(tp.name());
    }
    
    public static void err_stop(String str) {
        System.err.println(str);
        System.exit(1);
    }
}
