/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import Language.LanguageX;
import java.io.IOException;

/**
 *
 * @author Jorismar
 */
public class Compiler {
    private final LexicalAnalyzer lexico;
    private final SyntacticAnalyzer syntactic;
    //private final SemanticAnalyzer semantic;
    
    public Compiler(LanguageX lang, String filename) {
        this.lexico = new LexicalAnalyzer(lang, filename);
        this.syntactic = new SyntacticAnalyzer(lang);
        //this.semantic = new SemanticAnalyzer(lang, "$");
    }
    
    public void compile(boolean info) throws IOException {
        this.lexico.genTokenTable(info);
        this.syntactic.analyze(this.lexico.getTable());
        //this.semantic.analyze();
    }
}
