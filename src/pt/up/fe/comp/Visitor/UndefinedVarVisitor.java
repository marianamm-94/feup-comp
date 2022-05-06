package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.List;

public class UndefinedVarVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    public UndefinedVarVisitor(){
        addVisit("MethodDeclaration", this::visitMethodDeclaration);
        //addVisit("ObjectMethodParameters", this::visitMethodParameters);
        //addVisit("Return", this::visitReturn);
        //addVisit("VarDeclaration", this::visitVarDeclaration);
        //addVisit("Extends", this::visitExtends);
    }

    public Boolean visitMethodDeclaration(JmmNode methodNode, JmmAnalyser symbolTableReport){

        JmmNode methodScope = methodNode.getChildren().get(0);

        String methodName;
        if(methodScope.getKind().equals("OtherMethodDeclaration"))
            methodName = methodScope.getChildren().get(1).get("name");
        else
            methodName = "main";

        JmmNode methodBody = null;
        for(JmmNode node: methodScope.getChildren()){
            if(node.getKind().equals("methodBody")) {
                methodBody = node;
                break;
            }
        }

        for(JmmNode node: methodBody.getChildren()){
            if(node.getKind().equals("Assignment"))
                expressionValid(node, methodName, symbolTableReport);
        }

        return true;
    }

    public void expressionValid(JmmNode node, String methodName, JmmAnalyser symbolTableReport){

        JmmNode leftNode = node.getChildren().get(0);
        nodeValid(leftNode,methodName,symbolTableReport);

        if(node.getNumChildren()>1) {
            JmmNode rightNode = node.getChildren().get(1);
            nodeValid(rightNode, methodName, symbolTableReport);
        }

    }

    public void nodeValid(JmmNode node, String methodName, JmmAnalyser symbolTableReport){

        if(node.getKind().equals(("Identifier")))
            definedVar(node, methodName, symbolTableReport );
        else if (Utils.isOp(node.getKind()) || node.getKind().equals("Array"))
            expressionValid(node, methodName, symbolTableReport);

    }

    public void definedVar(JmmNode node, String methodName, JmmAnalyser symbolTableReport){

        String nameVar = node.get("name");

        List<Symbol> localVars = symbolTableReport.getSymbolTable().getLocalVariables(methodName);
        List<Symbol> classFields = symbolTableReport.getSymbolTable().getFields();
        List<Symbol> methodParams = symbolTableReport.getSymbolTable().getParameters(methodName);

        if(!symbolContain(localVars, nameVar) && !symbolContain(classFields, nameVar) && !symbolContain(methodParams, nameVar))
            symbolTableReport.newReport(node,"Variable \""+nameVar+ "\" is not defined");

    }

    public Boolean symbolContain(List<Symbol> vars, String nameVar){

        for(Symbol symb: vars){
            if(symb.getName().equals(nameVar))
                return true;
        }

        return false;
    }

}
