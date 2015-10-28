/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Language;

/**
 *
 * @author Jorismar
 */
public class LanguageX implements Syntax{
    private final String numbers = "0123456789";
    private final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    private final Alphabet values = new Alphabet(numbers, false, "VALUE");
    private final Alphabet permited_words = new Alphabet(letters + numbers + '_', false, "WORD");
    private final Alphabet add_operators = new Alphabet("+ -", true, "ADD OPERATOR");
    private final Alphabet mult_operators = new Alphabet("* /", true, "MULT OPERATOR");
    private final Alphabet assign_operators = new Alphabet(":=", true, "ASSIGN OPERATOR");
    private final Alphabet delimiters = new Alphabet(". , : ; ( )", true, "DELIMITER");
    private final Alphabet relat_operators = new Alphabet("= > < <= >= <>", true, "RELAT OPERATOR");
    private final String double_separator = ".";
    private final String open_comment = "{";
    private final String close_comment = "}";
    
    private final String[] reserved_words = {"if", "then", "end", "boolean", "procedure", "program", "integer", "else", "begin", "do", "var", "real", "not", "while"};
    private final String[] boolean_value_words = {"true", "false"};
    private final String add_operator_word = "or";
    private final String mult_operator_word = "and";
    
    private final Alphabet[] alphabet;

    public LanguageX() {
        Alphabet[] alph = { // Não alterar ordem
            this.values,
            this.permited_words,
            this.assign_operators,
            this.add_operators,
            this.mult_operators,
            this.relat_operators,
            this.delimiters
        };
        
        this.alphabet = alph;
    }
    
    private boolean isReservedWord(String word) {
        for(String aux: this.reserved_words)
            if(word.equals(aux)) return true;

        return false;
    }
    
    private boolean isBooleanValue(String word) {
        return word.equals(this.boolean_value_words[0]) || word.equals(this.boolean_value_words[1]);
    }
    
    private boolean isAddOperatorWord(String word) {
        return word.equals(this.add_operator_word);
    }
    
    private boolean isMultOperatorWord(String word) {
        return word.equals(this.mult_operator_word);
    }
    
    @Override 
    public String identWord(String word) {
        if(this.isReservedWord(word)) return this.getReservedType();
        else if(this.isAddOperatorWord(word)) return this.getAddOperatorType();
        else if(this.isMultOperatorWord(word)) return this.getMultOperatorType();
        else if(this.isBooleanValue(word)) return this.getBooleanType();
        
        return this.getIdentifierType();
    }
    
    @Override 
    public String getAlphabet(int index) {
        return this.alphabet[index].getAlphabet();
    }
    
    @Override 
    public boolean isPreDefined(int index) {
        return this.alphabet[index].isPreDefined();
    }
    
    @Override 
    public String getType(int index) {
        return this.alphabet[index].getType();
    }
    
    @Override 
    public String getDoubleSeparator() {
        return this.double_separator;
    }

    @Override 
    public String getOpenCommentSymbol() {
        return this.open_comment;
    }

    @Override 
    public String getCloseCommentSymbol() {
        return this.close_comment;
    }
    
    @Override 
    public String getDoubleType() {
        return "DOUBLE VALUE";
    }
    
    @Override 
    public String getIntegerType() {
        return "INTEGER VALUE";
    }
    
    @Override 
    public String getReservedType() {
        return "RESERVED WORD";
    }
    
    @Override 
    public String getAddOperatorType() {
        return "ADD OPERATOR";
    }
    
    @Override 
    public String getMultOperatorType() {
        return "MULT OPERATOR";
    }
    
    @Override 
    public String getAssignOperatorType() {
        return "ASSIGN OPERATOR";
    }
    
    @Override 
    public String getDelimiterType() {
        return "DELIMITER";
    }
    
    @Override 
    public String getRelationalType() {
        return "RELAT OPERATOR";
    }
    
    @Override 
    public String getIdentifierType() {
        return "IDENTIFIER";
    }
    
    public String getBooleanType() {
        return "BOOLEAN VALUE";
    }
    
    @Override 
    public int amountAlphabets() {
        return this.alphabet.length;
    }
    
    @Override 
    public boolean isValue(int index) {
        return this.alphabet[index] == this.values;
    }

    @Override 
    public boolean isWord(int index) {
        return this.alphabet[index] == this.permited_words;
    }
/*
    @Override 
    public boolean isAddOperator(int index) {
        return this.alphabet[index] == this.add_operators;
    }

    @Override 
    public boolean isMultOperator(int index) {
        return this.alphabet[index] == this.mult_operators;
    }
*/
    @Override 
    public boolean isAssignOperator(int index) {
        return this.alphabet[index] == this.assign_operators;
    }
/*
    @Override 
    public boolean isDelimiter(int index) {
        return this.alphabet[index] == this.delimiters;
    }

    @Override 
    public boolean isRelatOperator(int index) {
        return this.alphabet[index] == this.relat_operators;
    }
*/
}
