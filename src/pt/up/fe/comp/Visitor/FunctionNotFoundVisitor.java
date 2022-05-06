package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.List;

public class FunctionNotFoundVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    public FunctionNotFoundVisitor() {
        addVisit("FullStop", this::visitFullStop);
        addVisit("NewObject", this::visitNewObj);
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
        if(leftNode.getKind().equals("Identifier")){
            String nodeName = leftNode.get("name");

            if(Utils.importValid(nodeName, symbolTableReport.getSymbolTable()))
                if (isObject(node, nodeName, symbolTableReport))
                    symbolTableReport.newReport(leftNode, "\""+nodeName+"\" import invalid");
        }
        if(leftNode.getKind().equals("This") && rightNode.getKind().equals("FullStopMethod")){
            if(symbolTableReport.getSymbolTable().getSuper() != null)
                return true;
            String ident = leftNode.get("name");
            if(!symbolTableReport.getSymbolTable().getMethods().contains(ident))
                symbolTableReport.newReport(node,"Fuction \""+ ident + "\" is not defined");
        }

        return true;
    }

    public Boolean visitNewObj(JmmNode node, JmmAnalyser symbolTableReport) {

        JmmNode objNode = node.getChildren().get(0);
        String objName = objNode.get("name");
        Boolean returnValue = false;

        if(objName.equals(symbolTableReport.getSymbolTable().getClassName()))
            returnValue = true;
        else if(symbolTableReport.getSymbolTable().getSuper() != null && objName.equals((symbolTableReport.getSymbolTable().getSuper())))
                returnValue = true;
        else if(Utils.importValid(objName,symbolTableReport.getSymbolTable()))
                returnValue = true;
        else{
            symbolTableReport.newReport(objNode, "\""+objName+"\"  not object or import");
            }

        return returnValue;
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
           if(symb.getName().equals(nodeName)) {
               String typeSymb = symb.getType().getName();
               if (!typeSymb.equals("int") && !typeSymb.equals("String") && !typeSymb.equals("boolean")) {
                   if(typeSymb.equals(symbolTableReport.getSymbolTable().getClassName()))
                       if(symbolTableReport.getSymbolTable().getSuper() != null)
                           return true;
                       else if(!symbolTableReport.getSymbolTable().getMethods().contains(calledMethod.get("name")))
                           symbolTableReport.newReport(calledMethod, "\""+calledMethod.get("name")+"\" is not a class method");
               }
           }
           return true;
       }

        for(Symbol symb: methodParams){
            if(symb.getName().equals(nodeName)) {
                String typeSymb = symb.getType().getName();
                if (!typeSymb.equals("int") && !typeSymb.equals("String") && !typeSymb.equals("boolean")) {
                    if(typeSymb.equals(symbolTableReport.getSymbolTable().getClassName()))
                        if(symbolTableReport.getSymbolTable().getSuper() != null)
                            return true;
                        else if(!symbolTableReport.getSymbolTable().getMethods().contains(calledMethod.get("name")))
                            symbolTableReport.newReport(calledMethod, "\""+calledMethod.get("name")+"\" is not a class method");
                }
            }
            return true;
        }

       return false;
    }
}
