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
public interface Sintaxe {
    public String getNumbersAlphabet();
    public String getLettersAlphabet();
    public String getAddOperatorsAlphabet();
    public String getMultOperatorsAlphabet();
    public String getAssignOperatorsAlphabet();
    public String getRelatOperatorsAlphabet();
    public String getDelimitersAlphabet();
    public String getReservedWordsAlphabet();
    public String getIdentifiersAlphabet();
    public String getValueAlphabet();
    public String getOpenCommentSimbol();
    public String getCloseCommentSimbol();
    
}
