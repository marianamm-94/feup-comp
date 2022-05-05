package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.List;

public class FunctionNotFoundVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    public FunctionNotFoundVisitor() {
        addVisit("FullStop", this::visitFullStop);
        addVisit("NewOject", this::visitNewObj);
    }

    public Boolean visitFullStop(JmmNode node, JmmAnalyser symbolTableReport){

        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);

        if(rightNode.getKind().equals("Length")){
            if(lengthValidator(leftNode)) {
                symbolTableReport.newReport(node, "Length invalid");
                return false;
            }
            return true;
        }
        else if(leftNode.getKind().equals("Identifier")){
            String nodeName = leftNode.get("name");

            if(Utils.importValid(nodeName, symbolTableReport.getSymbolTable()))
                if ()
                symbolTableReport.newReport(leftNode, "\""+nodeName+"\" import invalid");
        }

        return true;
    }

    public Boolean visitNewObj(JmmNode jmmNode, JmmAnalyser symbolTableReport) {
        //TODO
        return true;
    }

    public Boolean lengthValidator(JmmNode node){
        String value = Utils.getTypeNode(node);
        if(!value.equals("int[]") && !value.equals("String[]"))
            return false;
        return true;
    }

    public Boolean isObject(JmmNode node, String nodeName, JmmAnalyser symbolTableReport) {
        String method = Utils.getParentMethod(node);

        JmmNode calledMethod = node.getChildren().get(1).getChildren().get(0);

        List<Symbol> localVariables = symbolTableReport.getSymbolTable().getLocalVariables(method);
        List<Symbol> classFields = symbolTableReport.getSymbolTable().getFields();
        List<Symbol> methodParams = symbolTableReport.getSymbolTable().getParameters(method);

       for(Symbol symb: localVariables){
           if(symb.getName().equals(nodeName))
       }
    }

}
