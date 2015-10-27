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
interface Syntax {
    String getAlphabet(int index);
    boolean isPreDefined(int index);
    String getType(int index);
    
    String getDoubleType();
    String getDoubleSeparator();
    String getIntegerType();
    public String getReservedType();
    public String getAddOperatorType();
    public String getMultOperatorType();
    public String getAssignOperatorType();
    public String getDelimiterType();
    public String getRelationalType();
    public String getIdentifierType();


    String identWord(String word);
    
    String getOpenCommentSymbol();
    String getCloseCommentSymbol();
    
    int amountAlphabets();
    
    boolean isValue(int index);
    boolean isWord(int index);
    boolean isAssignOperator(int index);
    //boolean isAddOperator(int index);
    //boolean isMultOperator(int index);
    //boolean isDelimiter(int index);
    //boolean isRelatOperator(int index);
}
