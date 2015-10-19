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
    public static int MAX = 9999999;
    
    public static enum Type {
        RESERVED_WORD, IDENTIFIER, DELIMITER, INTEGER_NUMBER, DOUBLE_NUMBER,
        ASSIGN_OPERATOR, RELAT_OPERATOR, ADD_OPERATOR, MULT_OPERATOR, UNKNOWN
    }

    public static String check(int i, String from, String alph, int ret_max_len) {
        char c;
        int count = 0, size = from.length();
        String aux = "";
        
        while(i < size && count < ret_max_len) {
            c = from.charAt(i++);
            
            if(alph.contains("" + c)) {
                aux = aux + c;
                count++;
            } else break;
        }

        return (ret_max_len != MAX && i < ret_max_len) ? "" : aux;
    }
    
    public static boolean addToken(String name, Type tp, long nline) {
        return table.add(new Token(name, tp.name(), nline));
    }

    //@SuppressWarnings("empty-statement")
    public static boolean LexicalAnalyzer(String filename) throws FileNotFoundException, IOException {
        String[] words;
        String line, aux;
        boolean isComment = false;
        int nline = 0, open_comment_line = 0, i = 0;
        BufferedReader buffer = new BufferedReader(new FileReader(filename));
       
        while(true) {
            line = buffer.readLine();
            
            if(line == null) break;
            
            nline++;

            words = line.split(" ");

            for(String word:words) {
                while(i < word.length()) {
                    if(!isComment) {
                        aux = check(i, word, language.getNumbersAlphabet(), MAX);

                        if(!aux.equals("")) {
                            addToken(aux, (aux.contains(".") ? Type.DOUBLE_NUMBER : Type.INTEGER_NUMBER), nline);
                            i += aux.length();
                            continue; // Gargalo (porem permite deteccao de character invalido)
                        }

                        aux = check(i, word, language.getIdentifiersAlphabet(), MAX);

                        if(!aux.equals("")) {
                            addToken(aux, (
                                (language.getReservedWordsAlphabet().contains(aux)) ? (
                                    (language.getAddOperatorsAlphabet().contains(aux)) ? 
                                        Type.ADD_OPERATOR : 
                                        (language.getMultOperatorsAlphabet().contains(aux)) ? 
                                            Type.MULT_OPERATOR : 
                                                Type.RESERVED_WORD) : Type.IDENTIFIER), nline
                            );
                            i += aux.length();
                            continue; // Gargalo (porem permite deteccao de character invalido)
                        }

                        aux = check(i, word, language.getAssignOperatorsAlphabet(), language.getAssignOperatorsAlphabet().length());

                        if(!aux.equals("") && language.getAssignOperatorsAlphabet().contains(aux)) {
                            addToken(aux, Type.ASSIGN_OPERATOR, nline);
                            i += aux.length();
                            continue;
                        }

                        aux = check(i, word, language.getAddOperatorsAlphabet(), 1);

                        if(!aux.equals("")) {
                            if(!(aux.equals("o") || aux.equals("r"))) {
                                addToken(aux, Type.ADD_OPERATOR, nline);
                                i += aux.length();
                            }
                            continue;
                        }

                        aux = check(i, word, language.getMultOperatorsAlphabet(), 1);

                        if(!aux.equals("")) {
                            if(!(aux.equals("a") || aux.equals("n") || aux.equals("d"))) {
                                addToken(aux, Type.MULT_OPERATOR, nline);
                                i += aux.length();
                            }
                            continue;
                        }

                        aux = check(i, word, language.getDelimitersAlphabet(), 1);

                        if(!aux.equals("")) {
                            addToken(aux, Type.DELIMITER, nline);
                            i += aux.length();
                            continue;
                        }

                        aux = check(i, word, language.getRelatOperatorsAlphabet(), 1);

                        if(!aux.equals("")) {
                            aux += check(i+1, word, language.getRelatOperatorsAlphabet(), 1);
                            if(!language.getRelatOperatorsAlphabet().contains(aux))       // Caso =>, == ou =< (invalidos)
                                aux = "" + aux.charAt(0);

                            addToken(aux, Type.RELAT_OPERATOR, nline);
                            i += aux.length();
                            continue;
                        }

                        aux = check(i, word, language.getOpenCommentSimbol(), 1);

                        if(!aux.equals("")) {
                            isComment = true;
                            open_comment_line = nline;
                            continue;
                        }
                        
                        if(word.charAt(i++) == '\t') continue;

                        System.err.println("Error ln:" + nline + " - invalid character '" + word.charAt(i) + "'");
                        return false;
                    } else if(word.contains(language.getCloseCommentSimbol())) {
                            i = word.indexOf(language.getCloseCommentSimbol()) + 1;
                            isComment = false;
                    } else break;
                }
                i = 0;
            }
        }
        
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
        Token tk;
        
        if( isEquals(nextToken(itable), "program") && isEquals(nextToken(itable), Type.IDENTIFIER) && isEquals(nextToken(itable), ";")){
            tk = startProgram(itable);

            if(isEquals(tk, "end")) {
                tk = nextToken(itable);
                
                if(!isEquals(tk, "."))
                    err_stop("Error ln: " + tk.getLine() + " expected a .");
            } else err_stop("Error ln: " + tk.getLine() + " expected a end");
        } else err_stop("Error ln: 1 program start error");
        
        System.out.println("Syntax is correct!");
        
    }
    
    public static Token startProgram(Iterator<Token> it) {
        Token tk = null;
                    
        while(it.hasNext()) {
            tk = nextToken(it);
            
            if(isEquals(tk, "var"))
                tk = listVarDeclaration(it);

            if(isEquals(tk, "procedure")) {
                tk = subprogramDeclaration(it);
                if(isEquals(tk, "end")) 
                    tk = nextToken(it);
                else err_stop("Error ln: " + tk.getLine() + " expected a end");
            }
            
            if(isEquals(tk, "begin")) {
                tk = OptCommands(it);
                if(isEquals(tk, "end")) {
                    break;
                } else err_stop("Error ln: " + tk.getLine() + " expected a end");                
            }
        }

        return tk;
    }

    public static Token OptCommands(Iterator<Token> it) {
       return commandList(it);
    }
    
    public static Token commandList(Iterator<Token> it) {
        Token tk;
        
        tk = command(it);
        
        if(isEquals(tk, ";"))
            tk = commandList(it);
        
        return tk;
    }
    
    public static Token command(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        if(isEquals(tk, Type.IDENTIFIER)) {
            tk = nextToken(it);
            if(isEquals(tk, Type.ASSIGN_OPERATOR)) {
                tk = expression(it);
            } else if(isEquals(tk, "(")) {
                tk = expressionList(it);
                if(!isEquals(tk, ")"))
                    err_stop("Error ln: " + tk.getLine() + " expected a )");
            }
        } else if(isEquals(tk, "begin")) {
            tk = OptCommands(it);
            if(isEquals(tk, "end"))
                tk = command(it);
            else err_stop("Error ln: " + tk.getLine() + " expected a end");
        } else if(isEquals(tk, "if")) {
                tk = expression(it);
            if(isEquals(tk, "then")) {
                tk = command(it);
                if(isEquals(tk, "else"))
                    tk = command(it);
            } else err_stop("Error ln: " + tk.getLine() + " expected a then");
        } else if(isEquals(tk, "while")) {
            tk = expression(it);
            if(isEquals(tk, "do"))
                tk = command(it);
            else err_stop("Error ln: " + tk.getLine() + " expected a do");
        }

        return tk;
    }
    
    public static Token expressionList(Iterator<Token> it) {
        Token tk;
        
        tk = expression(it);
        
        if(isEquals(tk, ","))
            tk = expressionList(it);
        
        return tk;
    }
    
    public static Token expression(Iterator<Token> it) {
        Token tk;
        
        tk = simpleExpression(it);
        
        if(isEquals(tk, Type.RELAT_OPERATOR))
            tk = simpleExpression(it);
        
        return tk;
    }
    
    public static Token simpleExpression(Iterator<Token> it) {
        Token tk;
        
        tk = term(it);
        
        if(isEquals(tk, Type.ADD_OPERATOR))
            tk = simpleExpression(it);
        
        return tk;
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
        } else if(isEquals(tk, "+") || isEquals(tk, "-")) {
            tk = term(it);
        } else if(isEquals(tk, "(")) {
            tk = expression(it);
            if(isEquals(tk, ")"))
                tk = nextToken(it);
            else err_stop("Error ln: " + tk.getLine() + " expected a )");
        }else err_stop("Error ln: " + tk.getLine() + " invalid expression");
        
        return tk;
    }
    
    public static Token listVarDeclaration(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        while(isEquals(tk, Type.IDENTIFIER)) {
            if(varDeclaration(it))
                tk = nextToken(it);
        }
        
        return tk;
    }
    
    public static boolean varDeclaration(Iterator<Token> it) {
        Token tk = listIdentifiers(it);
        
        if(isEquals(tk, ":")) {
            if(isVarType(nextToken(it))) {
                tk = nextToken(it);
                
                if(isEquals(tk, ";")) {
                    return true;
                } else err_stop("Error ln: " + tk.getLine() + " expected a ;");
            }
        } else err_stop("Error ln: " + tk.getLine() + " expected a :");
        
        return false;
    }
    
    public static Token listIdentifiers(Iterator<Token> it) {
        Token tk = nextToken(it); // verificar
        
        while(isEquals(tk, ",")) {
            tk = nextToken(it);
            if(isEquals(tk, Type.IDENTIFIER))
                tk = nextToken(it);
            else err_stop("Error ln: " + tk.getLine() + " expected a identifier");
        }

        return tk;
    }
    
    public static boolean isVarType(Token tk) {
        if(!(isEquals(tk, "integer") || isEquals(tk, "boolean") || isEquals(tk, "real")))
            err_stop("Error ln: " + tk.getLine() + " invalid type.");
        
        return true;
    }

    public static Token subprogramDeclaration(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        if(isEquals(tk, Type.IDENTIFIER)) {
            if(isEquals(nextToken(it), "(")) {
                if(isEquals(argumentList(it), ")")) {
                    if(isEquals(nextToken(it),";")) {
                        return startProgram(it);
                    } else err_stop("Error ln: " + tk.getLine() + " expected a ;");
                } else err_stop("Error ln: " + tk.getLine() + " expected a )");
            } else err_stop("Error ln: " + tk.getLine() + " expected a (");
        } else err_stop("Error ln: " + tk.getLine() + " expected a identifier");
        
        return null;
    }
    
    public static Token argumentList(Iterator<Token> it) {
        Token tk = nextToken(it);
        
        while(isEquals(tk, Type.IDENTIFIER)) {
            tk = listIdentifiers(it);
            if(isEquals(tk, ":") && isVarType(nextToken(it))) {
                tk = nextToken(it);
                if(isEquals(tk, ";"))
                    tk = nextToken(it);
            } else err_stop("Error ln: " + tk.getLine() + " expected a :");
        }
        
        return tk;
    }
    
    public static Token nextToken(Iterator<Token> it) {
        Token tk = it.hasNext() ? it.next() : null;
        
        if(tk == null)
            err_stop("Error: reached the end of the list, the program is not completed");
        
        return tk;
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
