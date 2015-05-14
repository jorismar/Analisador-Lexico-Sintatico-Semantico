import Analizer.Compiler;
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
            if(Compiler.LexicalAnalyzer("D:/teste.txt")) {
                Compiler.printTable();
                Compiler.SyntaxAnalyzer();
            }
                
        } catch (IOException ex) {
            System.err.println("Error: File cannot opened!");
        }
    }
    
}
