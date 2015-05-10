package Analizer;

import Language.LanguageX;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LexicalAnalyzer {
    public static LanguageX language = new LanguageX();
    public static ArrayList<Token> table = new ArrayList<>();
    
    public static boolean analyzeFile(String filename) throws FileNotFoundException, IOException {
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

                                    if(aux.contains("" + '.')) table.add(new Token(aux, Token.Type.DOUBLE_NUMBER.name(), nline));
                                        else table.add(new Token(aux, Token.Type.INTEGER_NUMBER.name(), nline));

                                    i += aux.length() - 1;
                                }
                                else if(language.getIdentifiersAlphabet().contains("" + c)) { // Verifica se é um identificador ou palavra reservada
                                    aux = identify(word, language.getIdentifiersAlphabet(), nline, i);

                                    if(language.getReservedWordsAlphabet().contains(aux)) {
                                        if(language.getAddOperatorsAlphabet().contains(aux))
                                            table.add(new Token(aux, Token.Type.ADD_OPERATOR.name(), nline));
                                        else if(language.getMultOperatorsAlphabet().contains(aux))
                                            table.add(new Token(aux, Token.Type.MULT_OPERATOR.name(), nline));
                                        else
                                            table.add(new Token(aux, Token.Type.RESERVED_WORD.name(), nline));
                                    } else table.add(new Token(aux, Token.Type.IDENTIFIER.name(), nline));

                                    i += aux.length() - 1;
                                }
                                else if(language.getAssignOperatorsAlphabet().contains("" + c)) {
                                    aux = identify(word, language.getAssignOperatorsAlphabet(), nline, i);

                                    if(aux.contains(language.getAssignOperatorsAlphabet()) && aux.length() == language.getAssignOperatorsAlphabet().length())
                                        table.add(new Token(aux, Token.Type.ASSIGN_OPERATOR.name(), nline));
                                    else if(language.getRelatOperatorsAlphabet().contains(aux))
                                        table.add(new Token(aux, Token.Type.RELAT_OPERATOR.name(), nline));
                                    else 
                                        table.add(new Token(aux, Token.Type.DELIMITER.name(), nline));

                                    i += aux.length() - 1;
                                }
                                else if(language.getDelimitersAlphabet().contains("" + c)) {
                                        //table.add(new Token("" + c, Token.Type.DELIMITER.name(), nline));
                                        aux = identify(word, language.getDelimitersAlphabet(), nline, i); // Não há necessidade da chamada de identify, porém caso o alfabeto mude, para mais de um símbolo em algum elemento
                                        table.add(new Token(aux, Token.Type.DELIMITER.name(), nline));

                                        i += aux.length() - 1;
                                }
                                else if(language.getAddOperatorsAlphabet().contains("" + c)) {
                                    //table.add(new Token("" + c, Token.Type.ADD_OPERATOR.name(), nline));
                                    aux = identify(word, language.getAddOperatorsAlphabet(), nline, i); // Não há necessidade da chamada de identify, porém caso o alfabeto mude, para mais de um símbolo em algum elemento
                                    table.add(new Token(aux, Token.Type.ADD_OPERATOR.name(), nline));

                                    i += aux.length() - 1;
                                }
                                else if(language.getMultOperatorsAlphabet().contains("" + c)) {
                                    //table.add(new Token("" + c, Token.Type.MULT_OPERATOR.name(), nline));
                                    aux = identify(word, language.getMultOperatorsAlphabet(), nline, i); // Não há necessidade da chamada de identify, porém caso o alfabeto mude, para mais de um símbolo em algum elemento
                                    table.add(new Token(aux, Token.Type.MULT_OPERATOR.name(), nline));

                                    i += aux.length() - 1;
                                }
                                else if(language.getRelatOperatorsAlphabet().contains("" + c)) {
                                    aux = identify(word, language.getRelatOperatorsAlphabet(), nline, i);

                                    if(language.getRelatOperatorsAlphabet().contains(aux))
                                        table.add(new Token(aux, Token.Type.RELAT_OPERATOR.name(), nline));
                                    // <--- SE A SEQUÊNCIA ESTIVER INCORRETA(=> =< >< ==)?
                                    
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
            System.out.println("" + tk.getToken() + "\t" + tk.getType() + "\t" + tk.getLine() + "\t|");
        }
    }
}
