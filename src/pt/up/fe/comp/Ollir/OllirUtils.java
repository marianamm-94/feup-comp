package pt.up.fe.comp.Ollir;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class OllirUtils {

    public static String getCode(Symbol symbol){
        return symbol.getName()+"."+getCode(symbol.getType());
    }
    public static String getCode(Type type){
        StringBuilder ollircode=new StringBuilder();

        if(type.isArray())
            ollircode.append("array.");

        ollircode.append(getOllirType(type.getName()));
        return ollircode.toString();
    }

    public static String getOllirType(String jmmType){
        switch (jmmType){
            case "void":
                return "V";
            case "int":
            case "EEInt":
                return"i32";
            case "EETrue":
                return"0.bool";
            case "EEFalse":
                return"1.bool";
            case "boolean":
                return"bool";
            default:
                return jmmType;
        }
    }
    public static String getOllirOperator(JmmNode node){
        switch (node.getKind()){
            case "Add":
                return " +.i32 ";
            case "Less":
                return " <.i32 ";
            case "Sub":
                return " -.i32 ";
            case "Mult":
                return " *.i32 ";
            case "Div":
                return " /.i32 ";
            case "And":
                return " &&.bool ";
            case "Not":
                return " !.bool ";
        }
        throw new NotImplementedException(node.getKind());
    }


    public static void binaryOperation() {
    }
}
