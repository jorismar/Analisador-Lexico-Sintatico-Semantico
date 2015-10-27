import Compiler.Compiler;
import Language.LanguageX;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jorismar
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Compiler compiler = new Compiler(new LanguageX(), "teste.txt");
            compiler.compile(true);
        } catch (IOException ex) {
            System.err.println("Error: File cannot opened!");
        }
    }
    
}
