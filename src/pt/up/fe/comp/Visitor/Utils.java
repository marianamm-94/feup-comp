package pt.up.fe.comp.Visitor;

import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class Utils {

    public static String getTypeVar(JmmNode node, JmmAnalyser symbolTableReport, String parentMethod){
        if (node.getKind().equals("Number")) return "int";
        if (node.getKind().equals("True") || node.getKind().equals("False")) return "boolean";
        if (node.getKind().equals("this")) return symbolTableReport.getSymbolTable().getClassName();


        //continue for other kinds

        return "undefined";
    }

    public static String getTypeNode(JmmNode node){

        //TODO

        return "undefined";
    }

    public static String getParentMethod(JmmNode node){
        //TODO
        return "main";
    }

    public static String getReturn(JmmNode node, JmmAnalyser symbolTableReport) {
        //TODO
        return "NOT DONE";
    }

    public static String getReturnType(JmmNode node, JmmAnalyser symbolTableReport){
        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);

        String type = Utils.getTypeNode(node);
        String method = rightNode.getChildren().get(0).get("name");
        String valueClass = symbolTableReport.getSymbolTable().getClassName();
        Boolean valueMethod = symbolTableReport.getSymbolTable().getMethods().contains(method);

        if(rightNode.getKind().equals("Length"))
            return "int";
        else if (valueMethod && (method.equals(valueClass) || node.getKind().equals("This"))){
            Type typeReturn = symbolTableReport.getSymbolTable().getReturnType((method));
            return typeReturn.getName() + (typeReturn.isArray() ? "[]" : "");
        }

        return "undefined";
    }

    public static Boolean isOp(String param){
        if(param.equals("Add")) return true;
        else if(param.equals("Sub")) return true;
        else if(param.equals("mult")) return true;
        else if(param.equals("Div")) return true;
        else if(param.equals("And")) return true;
        else if(param.equals("Not")) return true;
        else if(param.equals("Less")) return true;
        else if(param.equals("ExpressionArray")) return true;
        else if(param.equals("Array")) return true;
        return false;
    }

    public static boolean isMathOp(String param) {
        if(param.equals("Add") || param.equals("sub") || param.equals("Mul") || param.equals("Div"))
            return true;
        else
            return false;
    }

    public static boolean isBoolOp(String param){
        if (param.equals("Less") || param.equals("And") || param.equals("Not"))
            return true;
        else
            return false;
    }

    public static boolean importValid(String importValue, SymbolTable symbolTable){
        for(String importName : symbolTable.getImports()) {
            String[] splitImport = importName.split("\\.");
            if (splitImport[splitImport.length - 1].equals(importValue))
                return true;
        }
        return false;
    }

}
