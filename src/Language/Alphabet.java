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
public class Alphabet {
    private String alphabet;
    private boolean pre_defined; // Se as palavras forem pre-definidas elas serão identificadas como pertencentes ao alfabeto apenas se a palavra completa existir
    private String type;
    
    public Alphabet(String alphabet, boolean pre_defined, String type) {
        this.alphabet = alphabet;
        this.pre_defined = pre_defined;
        this.type = type;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    public boolean isPreDefined() {
        return pre_defined;
    }

    public void setPreDefined(boolean pre_defined) {
        this.pre_defined = pre_defined;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
}
