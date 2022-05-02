package pt.up.fe.comp.Visitor;

import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class Utils {

    public static String getTypeVar(JmmNode node){
        if (node.getKind().equals("Number")) return "int";
        if (node.getKind().equals("True") || node.getKind().equals("False")) return "boolean";
        if (node.getKind().equals("this")) return JmmAnalyser.getSymbolTable().getClassName();


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

}
