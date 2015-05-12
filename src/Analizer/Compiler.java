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
    
    public static void SyntaxAnalyzer() {
        Iterator<Token> itable = table.iterator();
        
        if(nextName(itable, "program")){
            if(nextType(itable, Type.IDENTIFIER)) {
                if(nextName(itable, ";")) {
                    
                }
            }
        }
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
    
    public static boolean addToken(String name, Type tp, long nline) {
        return table.add(new Token(name, tp.name(), nline));
    }
    
    public static boolean nextName(Iterator<Token> it, String name) {
        return it.hasNext() && it.next().getName().equals(name);
    }
    
    public static boolean nextType(Iterator<Token> it, Type tp) {
        return it.hasNext() && it.next().getType().equals(tp.name());
    }

    public static void printTable() {
        System.out.print("TOKEN      TYPE       LINE\n---------------------------------\n");
        
        for(Token tk:table) {
            System.out.println("" + tk.getName() + "\t" + tk.getType() + "\t" + tk.getLine() + "\t|");
        }
    }
}
