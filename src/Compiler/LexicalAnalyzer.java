package Compiler;

import Language.LanguageX;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LexicalAnalyzer {
    private final String filename;
    private final LanguageX language;
    private final ArrayList<Token> table;
    
    public LexicalAnalyzer(LanguageX lang, String filename) {
        this.filename = filename;
        this.language = lang;
        this.table = new ArrayList<>();
    }

    public ArrayList<Token> getTable() {
        return this.table;
    }
    
    //@SuppressWarnings("empty-statement")
    public void genTokenTable(boolean info) throws FileNotFoundException, IOException {
        String[] words;
        String line;
        boolean isComment = false;
        int nline = 0, open_comment_line = 0, i = 0;
        BufferedReader buffer = new BufferedReader(new FileReader(this.filename));
       
        if(info)
            System.out.print("Generating tokens table...\nTOKEN\t\tTYPE\t\t\tLINE\n--------------------------------------------\n");
        
        while(true) {
            line = buffer.readLine();
            
            if(line == null) break;
            
            nline++;
            words = line.split(" ");

            for(String word:words)
                if(!isComment)
                    isComment = identify(word, 0, nline, info);
                else if(word.contains(language.getCloseCommentSymbol())) 
                    isComment = identify(word, word.indexOf(language.getCloseCommentSymbol()) + 1, nline, info);
        }

        if(isComment)
            error("Error ln:" + open_comment_line + " - comment has been opened and not closed!");
       
        if(info)
            System.out.print("--------------------------------------------\n\n");
    }
                
    private boolean identify(String word, int start_pos, int nline, boolean info) {
        char c;
        int i = start_pos, l = 0;
        String aux = "", type = "";
        
        while(l < language.amountAlphabets() && i < word.length()) {
            do {
                c = word.charAt(i);
                
                if(c != '\t')
                    if(language.getAlphabet(l).contains(language.isPreDefined(l) ? aux + c : "" + c)) 
                        aux = aux + c;
                    else break;
                
                i++;
            } while(l < language.amountAlphabets() && i < word.length());

            if(!aux.equals("")) {
                /* IDENTIFICAÇÃO DE VALORES */
                if(type.equals("") && language.isValue(l)) {
                    if((i + 1 < word.length()) && language.getDoubleSeparator().contains("" + word.charAt(i)) && language.getAlphabet(l).contains("" + word.charAt(i + 1))) {
                        aux = aux + word.charAt(i++);
                        //l--;
                        type = language.getDoubleType();
                        continue;
                    } else type = language.getIntegerType();
                }
                
                /* DISTINÇÃO ENTRE OPERADORES (ATRIB-REALAT-DELIMT) */
                if(language.isAssignOperator(l) && !language.getAlphabet(l).equals(aux)) { 
                    i--;
                    l++;
                } else {
                    /* IDENTIFICAÇÃO DE PALAVRAS */
                    if(language.isWord(l)) 
                        type = language.identWord(aux);
                
                    table.add(new Token(aux, type.equals("") ? language.getType(l) : type, nline));
                    
                    if(info)
                        System.out.println("" + table.get(table.size() - 1).getName() + (table.get(table.size() - 1).getName().length() < 8 ? "\t\t" : "\t") + table.get(table.size() - 1).getType() + "\t\t" + table.get(table.size() - 1).getLine());

                    l = 0;
                    type = "";
                }

                aux = "";
            } else l++;
        }
        
        if(l >= language.amountAlphabets()) {
            if(language.getOpenCommentSymbol().contains("" + word.charAt(i)))
                return true;
            else
                error("Error ln:" + nline + " - invalid character '" + word.charAt(i) + "'");
        }
        return false;
    }

    private void error(String msg) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
        System.err.println(msg);
        System.exit(1);
    }
}