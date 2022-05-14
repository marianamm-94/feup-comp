package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

/*
public class BadArgsVisitor extends PreorderJmmVisitor<Analysis, Boolean> {
    private final JmmSymbolTable symbolTable = new JmmSymbolTable();
    public BadArgsVisitor(){
        addVisit("DotMethod", this::visitDotMethod);
    }

    public JmmSymbolTable getSymbolTable(){
        return symbolTable;
    }


    public Boolean visitDotMethod(JmmNode node, Analysis analysis){

        String methodName = node.getChildren().get(0).get("name");
        JmmNode parametersNode = node.getChildren().get(1);

        if (!analysis.getSymbolTable().getMethods().contains(methodName)) return true;

        // Check left side
        JmmNode parentNode = node.getJmmParent();
        JmmNode left = parentNode.getChildren().get(0);

        if (!Utils.getNodeType(left, analysis).equals(analysis.getSymbolTable().getClassName())) return true;

        // Check arguments of method calls
        hasCorrectParameters(parametersNode, analysis , methodName);

        return true;
    }

    public void hasCorrectParameters(JmmNode node, Analysis analysis, String methodName){
        List<Symbol> parameters = analysis.getSymbolTable().getParameters(methodName);
        boolean hasSuper = analysis.getSymbolTable().getSuper() != null;
        int providedArgs = node.getNumChildren();
        int requiredArgs = parameters.size();
        if(providedArgs != requiredArgs){
            if(!hasSuper)
                analysis.newReport(node,"Wrong number of arguments. " + "Provided: " + providedArgs + " Required: " + requiredArgs);
            return;
        }

        for (int i = 0 ; i < requiredArgs; i++){
            Type type = parameters.get(i).getType();
            String requiredType = type.getName() + (type.isArray() ? "[]" : "");

            String providedType = Utils.getNodeType(node.getChildren().get(i), analysis);
            if (!providedType.equals(requiredType) && !providedType.equals("undefined")){
                analysis.newReport(node,"Parameter at position " + i + " has invalid type." +
                        " Provided: " + providedType + " Required: " + requiredType);
            }
        }
    }


}

 */