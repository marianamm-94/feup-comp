package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class FunctionNotFoundVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    public FunctionNotFoundVisitor() {
        addVisit("Dot", this::visitDot);
        addVisit("NewOject", this::visitNewObj);
    }

    public Boolean visitDot(JmmNode node, JmmAnalyser symbolTableReport){

        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);

        if(rightNode.getKind().equals("Length")){
            //TODO
        }

        return true;
    }

    public Boolean visitNewObj(JmmNode jmmNode, JmmAnalyser symbolTableReport) {
        //TODO
        return true;
    }
}
