package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

public class JasminLoops {
    public static String gotoInstruction( GotoInstruction instruction){
        return "goto " + instruction.getLabel() + "\n";
    }
}
