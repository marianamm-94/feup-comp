package pt.up.fe.comp.Ollir;

import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;

public class OllirAssignment {
    public static void assignmentStatement(String methodName, JmmNode statement, SymbolTable symbolTable) {
        //TODO:: symbol table private??
        JmmNode left= statement.getChildren().get(0);
        JmmNode right= statement.getChildren().get(1);

        if(left.getKind().equals("Array")){
            JmmNode arrayId = left.getChildren().get(0);
            JmmNode index = left.getChildren().get(1);
            arrayAssignment(methodName,arrayId,index,right,symbolTable);
        }

        else{
            String name=left.get("name");
            identifierAssignment(methodName,name,right);

        }
    }

    private static void identifierAssignment(String methodName, String name, JmmNode right) {

    }

    private static String arrayAssignment(String methodName, JmmNode arrayId, JmmNode index, JmmNode right,SymbolTable symbolTable) {
        //TODO::
        StringBuilder ollirCode=new StringBuilder();
        String name=arrayId.get("name");
        Type type= symbolTable.getReturnType(methodName);
        String ollirType=OllirUtils.getCode(type);

       return ollirCode.toString();

    }
}
