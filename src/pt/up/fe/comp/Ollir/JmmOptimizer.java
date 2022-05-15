package pt.up.fe.comp.Ollir;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;

public class JmmOptimizer implements JmmOptimization{

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {
        // TODO Auto-generated method stub
        var ollirGenerator= new OllirGenerator(semanticsResult.getSymbolTable());
        ollirGenerator.visit(semanticsResult.getRootNode());
        var ollirCode=ollirGenerator.getCode();
        return null;
    }

}