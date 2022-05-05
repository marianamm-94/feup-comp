package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class UndefinedVarVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    public UndefinedVarVisitor(){
        addVisit("MethodDeclaration", this::visitMethodDeclaration);
        addVisit("ObjectMethodParameters", this::visitMethodParameters);
        addVisit("Return", this::visitReturn);
        addVisit("VarDeclaration", this::visitVarDeclaration);
        addVisit("Extends", this::visitExtends);
    }

    //TODO

}
