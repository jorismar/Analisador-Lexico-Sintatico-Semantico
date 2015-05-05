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
public class LanguageX implements Sintaxe {
    private final String numAlphabet = "0123456789";
    private final String lettAlphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final String addOperators = "+-or";
    private final String assignOperators = ":=";
    private final String multOperators = "*/and";
    private final String delimitOperators = ".,:;()";
    private final String relationOperators = "= <> <= >= > <";
    
    private final String reservedWords = "if then end boolean procedure program integer else begin do var and or real not while";
    private final String identifWords =  lettAlphabet + numAlphabet + '_';
    private final String valueAlphabet = numAlphabet + '.';
    private final String openComment = "{";
    private final String closeComment = "}";
    
    @Override
    public String getNumbersAlphabet() {
        return numAlphabet;
    }
    
    @Override
    public String getLettersAlphabet() {
        return lettAlphabet;
    }
    @Override
    public String getAddOperatorsAlphabet() {
        return addOperators;
    }
    
    @Override
    public String getMultOperatorsAlphabet() {
        return multOperators;
    }
    
    @Override
    public String getAssignOperatorsAlphabet() {
        return assignOperators;
    }
    
    @Override
    public String getRelatOperatorsAlphabet() {
        return relationOperators;
    }
    
    @Override
    public String getDelimitersAlphabet() {
        return delimitOperators;
    }
    
    @Override
    public String getReservedWordsAlphabet() {
        return reservedWords;
    }

    @Override
    public String getIdentifiersAlphabet() {
        return identifWords;
    }
    
    @Override
    public String getValueAlphabet() {
        return valueAlphabet;
    }
    
    @Override
    public String getOpenCommentSimbol() {
        return openComment;
    }

    @Override
    public String getCloseCommentSimbol() {
        return closeComment;
    }

    
}
