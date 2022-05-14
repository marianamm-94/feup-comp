package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.List;

public class UndefinedVarVisitor extends PreorderJmmVisitor<Analysis, Boolean> {

    public UndefinedVarVisitor(){
        addVisit("OtherMethodDeclaration", this::visitMethodParameters);
        //addVisit("ObjectMethodParameters", this::visitMethodParameters);
        //addVisit("ReturnValue", this::visitReturn);
        //addVisit("VarDeclaration", this::visitVarDeclaration);
        //addVisit("Extends", this::visitExtends);
    }

    public Boolean visitOtherMethodDeclaration(JmmNode methodNode, Analysis symbolTableReport){

        JmmNode methodScope = methodNode.getChildren().get(0);
        String methodName = methodScope.get("name");

        JmmNode type = methodScope.getChildren().get(0);
        String typeVar = type.get("name");
        boolean isArray = "True" == type.get("isArray");

        JmmNode methodBodyPreview = methodScope.getChildren().get(1);
        JmmNode methodBody = null;
        for(JmmNode node: methodBody.getChildren()){
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

    public Boolean visitMethodParameters(JmmNode methodNode, Analysis analysis){
        String methodName = Utils.getParentMethod(methodNode);

        //Check all params
        for(JmmNode node: methodNode.getChildren()){
            if (node.getNumChildren() > 0 && !node.getKind().equals("VarDeclaration"))
                expressionValid(node, methodName, analysis);
            else if (node.getKind().equals("Assignment"))
                definedVar(node, methodName, analysis);
        }

        return true;
    }

    public void expressionValid(JmmNode node, String methodName, Analysis symbolTableReport){

        JmmNode leftNode = node.getChildren().get(0);
        nodeValid(leftNode,methodName,symbolTableReport);

        if(node.getNumChildren()>1) {
            JmmNode rightNode = node.getChildren().get(1);
            nodeValid(rightNode, methodName, symbolTableReport);
        }

    }

    public void nodeValid(JmmNode node, String methodName, Analysis symbolTableReport){

        if(node.getKind().equals(("Identifier")))
            definedVar(node, methodName, symbolTableReport );
        else if (Utils.isOp(node.getKind()) || node.getKind().equals("Array"))
            expressionValid(node, methodName, symbolTableReport);

    }

    public void definedVar(JmmNode node, String methodName, Analysis symbolTableReport){

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
