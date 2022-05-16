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
                return"i32";
            case "boolean":
                return"bool";
            default:
                return ( jmmType);
        }
    }
//TODO:: VERIFICAR SE FUNCIONA
    public static int getLastParamIndex(JmmNode methodDecl){
        int lastParamIndex=-1;
        for(int i=0;i<methodDecl.getNumChildren();i++){
            if(methodDecl.getJmmChild(i).getKind().equals("Argument")){
                lastParamIndex=i;
            }
        }
        return lastParamIndex;
    }

    public static String varDeclaration(JmmNode varDecl,String type) {
        StringBuilder ollirCode=new StringBuilder();
        if(varDecl.getJmmChild(0).get("isArray").equals("True")){
            ollirCode.append("array.");
            ollirCode.append(getOllirType(type));
        }else{
            ollirCode.append(getOllirType(type));
        }
        return ollirCode.toString();
    }
}
